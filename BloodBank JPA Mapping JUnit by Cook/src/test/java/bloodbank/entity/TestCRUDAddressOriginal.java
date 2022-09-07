package bloodbank.entity;

import common.JUnitBase;
import org.junit.jupiter.api.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertThrows;

@TestMethodOrder(MethodOrderer.MethodName.class)
public class TestCRUDAddressOriginal extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;

	private static Address address;
	private static final String STREET_NUMBER = "2258";
	private static final String STREET = "Cotters Crecents";
	private static final String CITY = "HuntClub";
	private static final String PROVINCE = "Ontario";
	private static final String COUNTRY = "Canada";
	private static final String ZIPCODE = "2KG 1ER";

	@BeforeAll
	static void setupAllInit() {
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
		assertThat(getTotalCount(Address.class, em), is(comparesEqualTo(0L)));
	}

	@Test
	void test02_Create() {
		et.begin();
		address = new Address();
		address.setStreetNumber(STREET_NUMBER);
		address.setStreet(STREET);
		address.setCity(CITY);
		address.setProvince(PROVINCE);
		address.setCountry(COUNTRY);
		address.setZipcode(ZIPCODE);
		em.persist(address);
		et.commit();

		assertThat(getCountWithId(Address.class, Integer.class, Address_.id, address.getId(), em), is(greaterThanOrEqualTo(1L)));
	}

	@Test
	void test03_CreateInvalid() {
		et.begin();
		Address a = new Address();
		a.setStreetNumber(generateRandomString(11));
		a.setStreet(STREET);
		a.setCity(CITY);
		a.setProvince(PROVINCE);
		a.setCountry(COUNTRY);
		a.setZipcode(ZIPCODE);
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();

		et.begin();
		a.setStreetNumber(null);
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();

		et.begin();
		a.setStreetNumber(STREET_NUMBER);
		a.setStreet(generateRandomString(101));
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();

		et.begin();
		a.setStreet(null);
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();

		et.begin();
		a.setStreet(STREET);
		a.setCity(generateRandomString(101));
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();

		et.begin();
		a.setCity(null);
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();

		et.begin();
		a.setCity(CITY);
		a.setProvince(generateRandomString(101));
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();

		et.begin();
		a.setProvince(null);
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();

		et.begin();
		a.setProvince(PROVINCE);
		a.setCountry(generateRandomString(101));
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();

		et.begin();
		a.setCountry(null);
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();

		et.begin();
		a.setCountry(COUNTRY);
		a.setZipcode(generateRandomString(101));
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();

		et.begin();
		a.setZipcode(null);
		assertThrows(PersistenceException.class, () -> em.persist(a));
		et.commit();
	}

	@Test
	void test04_Read() {
		assertThat(getAll(Address.class, em), contains(equalTo(address)));
	}

	@Test
	void test05_ReadDependencies() {
		Address returnAddress = getWithId(Address.class, Integer.class, Address_.id, address.getId(), em);

		assertThat(returnAddress.getStreetNumber(), equalTo(STREET_NUMBER));
		assertThat(returnAddress.getStreet(), equalTo(STREET));
		assertThat(returnAddress.getCity(), equalTo(CITY));
		assertThat(returnAddress.getProvince(), equalTo(PROVINCE));
		assertThat(returnAddress.getCountry(), equalTo(COUNTRY));
		assertThat(returnAddress.getZipcode(), equalTo(ZIPCODE));
		assertThat(returnAddress.getCreated(), equalTo(returnAddress.getUpdated()));
		assertThat(returnAddress.getVersion(), equalTo(1));
	}

	@Test
	void test06_Update() {
		Address returnAddress = getWithId(Address.class, Integer.class, Address_.id, address.getId(), em);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		String newStreetNumber = generateRandomString(10);
		String newStreet = generateRandomString(100);
		String newCity = generateRandomString(100);
		String newProvince = generateRandomString(100);
		String newCountry = generateRandomString(100);
		String newZipcode = generateRandomString(100);

		et.begin();
		returnAddress.setAddress(newStreetNumber, newStreet, newCity, newProvince, newCountry, newZipcode);
		em.merge(returnAddress);
		et.commit();

		returnAddress = getWithId(Address.class, Integer.class, Address_.id, address.getId(), em);

		assertThat(returnAddress.getStreetNumber(), equalTo(newStreetNumber));
		assertThat(returnAddress.getStreet(), equalTo(newStreet));
		assertThat(returnAddress.getCity(), equalTo(newCity));
		assertThat(returnAddress.getProvince(), equalTo(newProvince));
		assertThat(returnAddress.getCountry(), equalTo(newCountry));
		assertThat(returnAddress.getZipcode(), equalTo(newZipcode));
		assertThat(returnAddress.getCreated(), lessThan(returnAddress.getUpdated()));
		assertThat(returnAddress.getVersion(), equalTo(2));
	}

	@Test
	void test07_Delete() {
		Address returnAddress = getWithId(Address.class, Integer.class, Address_.id, address.getId(), em);

		et.begin();
		Address a = new Address();
		a.setAddress("1", "Hello", "World", "Cool", "Awesome","00000");
		em.persist(a);
		et.commit();

		et.begin();
		em.remove(returnAddress);
		et.commit();

		assertThat(getCountWithId(Address.class, Integer.class, Address_.id, address.getId(), em), is(comparesEqualTo(0L)));

		assertThat(getCountWithId(Address.class, Integer.class, Address_.id, a.getId(), em), is(equalTo(1L)));
	}
}
