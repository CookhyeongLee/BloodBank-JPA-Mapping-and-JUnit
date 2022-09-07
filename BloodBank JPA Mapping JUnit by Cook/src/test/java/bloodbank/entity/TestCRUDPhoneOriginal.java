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
public class TestCRUDPhoneOriginal extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;
	private static Phone phone;
	private static final String COUNTRY_CODE = "1";
	private static final String AREA_CODE = "613";
	private static final String NUMBER = "808-0870";

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
		assertThat(getTotalCount(Phone.class, em), is(comparesEqualTo(0L)));
	}

	@Test
	void test02_Create() {
		et.begin();
		phone = new Phone();
		phone.setCountryCode(COUNTRY_CODE);
		phone.setAreaCode(AREA_CODE);
		phone.setNumber(NUMBER);
		em.persist(phone);
		et.commit();

		assertThat(getCountWithId(Phone.class, Integer.class, Phone_.id, phone.getId(), em), is(greaterThanOrEqualTo(1L)));
	}

	@Test
	void test03_CreateInvalid() {
		et.begin();
		Phone p = new Phone();
		p.setCountryCode(generateRandomString(11));
		p.setAreaCode(AREA_CODE);
		p.setNumber(NUMBER);
		assertThrows(PersistenceException.class, () -> em.persist(p));
		et.commit();

		et.begin();
		p.setCountryCode(null);
		assertThrows(PersistenceException.class, () -> em.persist(p));
		et.commit();

		et.begin();
		p.setCountryCode(COUNTRY_CODE);
		p.setAreaCode(generateRandomString(11));
		assertThrows(PersistenceException.class, () -> em.persist(p));
		et.commit();

		et.begin();
		p.setAreaCode(null);
		assertThrows(PersistenceException.class, () -> em.persist(p));
		et.commit();

		et.begin();
		p.setAreaCode(AREA_CODE);
		p.setNumber(generateRandomString(11));
		assertThrows(PersistenceException.class, () -> em.persist(p));
		et.commit();

		et.begin();
		p.setNumber(null);
		assertThrows(PersistenceException.class, () -> em.persist(p));
		et.commit();
	}

	@Test
	void test04_Read() {
		assertThat(getAll(Phone.class, em), contains(equalTo(phone)));
	}

	@Test
	void test05_ReadDependencies() {
		Phone returnPhone = getWithId(Phone.class, Integer.class, Phone_.id, phone.getId(), em);

		assertThat(returnPhone.getCountryCode(), equalTo(COUNTRY_CODE));
		assertThat(returnPhone.getAreaCode(), equalTo(AREA_CODE));
		assertThat(returnPhone.getNumber(), equalTo(NUMBER));
		assertThat(returnPhone.getCreated(), equalTo(returnPhone.getUpdated()));
		assertThat(returnPhone.getVersion(), equalTo(1));
	}

	@Test
	void test06_Update() {
		Phone returnPhone = getWithId(Phone.class, Integer.class, Phone_.id, phone.getId(), em);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		String newCountryCode = generateRandomString(10);
		String newAreaCode = generateRandomString(10);
		String newNumber = generateRandomString(10);

		et.begin();
		returnPhone.setNumber(newCountryCode, newAreaCode, newNumber);
		em.merge(returnPhone);
		et.commit();

		returnPhone = getWithId(Phone.class, Integer.class, Phone_.id, phone.getId(), em);

		assertThat(returnPhone.getCountryCode(), equalTo(newCountryCode));
		assertThat(returnPhone.getAreaCode(), equalTo(newAreaCode));
		assertThat(returnPhone.getNumber(), equalTo(newNumber));
		assertThat(returnPhone.getCreated(), lessThan(returnPhone.getUpdated()));
		assertThat(returnPhone.getVersion(), equalTo(2));
	}

	@Test
	void test07_Delete() {
		
		Phone returnPhone = getWithId(Phone.class, Integer.class, Phone_.id, phone.getId(), em);
		
		et.begin();
		Phone p = new Phone();
		p.setAreaCode("000");
		p.setNumber("000-0000");
		em.persist(p);
		et.commit();

		et.begin();
		em.remove(returnPhone);
		et.commit();

		assertThat(getCountWithId(Phone.class, Integer.class, Phone_.id, phone.getId(), em), is(comparesEqualTo(0L)));

		assertThat(getCountWithId(Phone.class, Integer.class, Phone_.id, p.getId(), em), is(equalTo(1L)));
	}
}
