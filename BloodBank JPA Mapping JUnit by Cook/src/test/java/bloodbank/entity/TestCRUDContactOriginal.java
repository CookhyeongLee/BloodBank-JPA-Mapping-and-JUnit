package bloodbank.entity;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import org.junit.jupiter.api.*;

import common.JUnitBase;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDContactOriginal extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;
	private static Phone phone;
	private static Address address;
	private static Person person;
	private static Contact contact;
	private static final String EMAIL = "test@test.com";
	private static final String CONTACT_TYPE = "Home";

	@BeforeAll
	static void setupAllInit() {
		phone = new Phone();
		phone.setNumber( "0", "234", "5678900");

		address = new Address();
		address.setAddress( "123", "abcd Dr.W", "ottawa", "ON", "CA", "A1B2C3");

		person = new Person();
		person.setFullName( "Teddy", "Yap");
	}

	@BeforeEach
	void setup() {
		em = getEntityManager();
		et = em.getTransaction();
	}

	@AfterEach
	void tearDown() {
		em.close();
	}

	@Test
	void test01_Empty() {
		assertThat(getTotalCount(Contact.class, em), is(comparesEqualTo(0L)));
	}

	@Test
	void test02_Create() {
		et.begin();
		contact = new Contact();
		contact.setAddress(address);
		contact.setPhone(phone);
		contact.setEmail(EMAIL);
		contact.setContactType(CONTACT_TYPE);
		contact.setOwner(person);
		em.persist(contact);
		et.commit();

		assertThat(getCountWithId(Contact.class, ContactPK.class, Contact_.id, contact.getId(), em), is(greaterThanOrEqualTo(1L)));
	}

	@Test
	void test03_CreateInvalid() {
		et.begin();
		Contact contactHome = new Contact();
		contactHome.setAddress( address);
		contactHome.setEmail( "test@test.com");
		contactHome.setContactType( "Home");
		contactHome.setOwner( person);
		assertThrows(PersistenceException.class, () -> em.persist(contactHome));
		et.commit();
	}

	@Test
	void test04_Read() {
		assertThat(getAll(Contact.class, em), contains(equalTo(contact)));
	}

	@Test
	void test05_ReadDependencies() {
		Contact returnedContact = getWithId(Contact.class, ContactPK.class, Contact_.id, contact.getId(), em);

		assertThat(returnedContact.getOwner(), equalTo(person));
		assertThat(returnedContact.getEmail(), equalTo(EMAIL));
		assertThat(returnedContact.getContactType(), equalTo(CONTACT_TYPE));
		assertThat(returnedContact.getPhone(), equalTo(phone));
		assertThat(returnedContact.getAddress(), equalTo(address));
		assertThat(returnedContact.getCreated(), equalTo(returnedContact.getUpdated()));
		assertThat(returnedContact.getVersion(), equalTo(1));
	}

	@Test
	void test06_Update() {
		Contact returnedContact = getWithId(Contact.class, ContactPK.class, Contact_.id, contact.getId(), em);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		String newEmail = generateRandomString(100);
		String newContactType = generateRandomString(10);

		et.begin();
		returnedContact.setEmail(newEmail);
		returnedContact.setContactType(newContactType);
		em.merge(returnedContact);
		et.commit();

		returnedContact = getWithId(Contact.class, ContactPK.class, Contact_.id, contact.getId(), em);

		assertThat(returnedContact.getEmail(), equalTo(newEmail));
		assertThat(returnedContact.getContactType(), equalTo(newContactType));
		assertThat(returnedContact.getCreated(), lessThan(returnedContact.getUpdated()));
		assertThat(returnedContact.getVersion(), equalTo(2));
	}

	@Test
	void test07_UpdateDependencies() {
		Contact returnedContact = getWithId(Contact.class, ContactPK.class, Contact_.id, contact.getId(), em);

		phone = returnedContact.getPhone();
		phone.setNumber("9", "876", "5432100");

		address = returnedContact.getAddress();
		address.setAddress("7654", "zxcv Dr.E", "Vancouver", "BS", "CA", "Z9Y8X7W");

		person = returnedContact.getOwner();
		person.setFullName("Jack", "Jackson");

		et.begin();
		returnedContact.setAddress(address);
		returnedContact.setPhone(phone);
		returnedContact.setOwner(person);
		em.merge(returnedContact);
		et.commit();

		returnedContact = getWithId(Contact.class, ContactPK.class, Contact_.id, contact.getId(), em);

		assertThat(returnedContact.getOwner(), equalTo(person));
		assertThat(returnedContact.getPhone(), equalTo(phone));
		assertThat(returnedContact.getAddress(), equalTo(address));
	}

	@Test
	void test08_DeleteDependency() {
		Contact returnedContact = getWithId(Contact.class, ContactPK.class, Contact_.id, contact.getId(), em);

		int addressId = returnedContact.getAddress().getId();

		et.begin();
		returnedContact.setAddress(null);
		em.merge(returnedContact);
		et.commit();

		returnedContact = getWithId(Contact.class, ContactPK.class, Contact_.id, contact.getId(), em);

		assertThat(returnedContact.getAddress(), is(nullValue()));

		assertThat(getCountWithId(Address.class, Integer.class, Address_.id, addressId, em), is(equalTo( 1L)));
	}

	@Test
	void test09_Delete() {
		Contact returnedContact = getWithId(Contact.class, ContactPK.class, Contact_.id, contact.getId(), em);

		et.begin();
		Contact contactHome = new Contact();
		contactHome.setPhone(new Phone().setNumber("2", "673", "9845385"));
		contactHome.setContactType("Work");
		contactHome.setOwner(returnedContact.getOwner());
		em.persist(contactHome);
		et.commit();

		et.begin();
		em.remove(returnedContact);
		et.commit();

		assertThat(getCountWithId(Contact.class, ContactPK.class, Contact_.id, contact.getId(), em), is(equalTo(0L)));

		assertThat(getCountWithId(Contact.class, ContactPK.class, Contact_.id, contactHome.getId(), em), is(equalTo(1L)));
	}

}
