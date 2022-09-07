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
public class TestCRUDBloodBankOriginal extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;

	private static BloodBank bank;
	private static final String NAME = "Blood Bank";

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
		assertThat(getTotalCount(BloodBank.class, em), is(comparesEqualTo(0L)));
	}

	@Test
	void test02_Create() {
		et.begin();
		bank = new PrivateBloodBank();
		bank.setName(NAME);
		em.persist(bank);
		et.commit();

		assertThat(getCountWithId(BloodBank.class, Integer.class, BloodBank_.id, bank.getId(), em), is(greaterThanOrEqualTo(1L)));
	}

	@Test
	void test03_CreateInvalid() {
		et.begin();
		BloodBank b1 = new PrivateBloodBank();
		b1.setName(generateRandomString(101));
		assertThrows(PersistenceException.class, () -> em.persist(b1));
		et.commit();

		et.begin();
		b1.setName(null);
		assertThrows(PersistenceException.class, () -> em.persist(b1));
		et.commit();

		et.begin();
		BloodBank b2 = new PublicBloodBank();
		b2.setName(generateRandomString(101));
		assertThrows(PersistenceException.class, () -> em.persist(b2));
		et.commit();

		et.begin();
		b2.setName(null);
		assertThrows(PersistenceException.class, () -> em.persist(b2));
		et.commit();
	}

	@Test
	void test04_Read() {
		assertThat(getAll(BloodBank.class, em), contains(equalTo(bank)));
	}

	@Test
	void test05_ReadDependencies() {
		BloodBank returnBank = getWithId(BloodBank.class, Integer.class, BloodBank_.id, bank.getId(), em);

		assertThat(returnBank.getName(), equalTo(NAME));
		assertThat(returnBank.getCreated(), equalTo(returnBank.getUpdated()));
		assertThat(returnBank.getVersion(), equalTo(1));
	}

	@Test
	void test06_Update() {
		BloodBank returnBank = getWithId(BloodBank.class, Integer.class, BloodBank_.id, bank.getId(), em);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		String newName = generateRandomString(100);

		et.begin();
		returnBank.setName(newName);
		em.merge(returnBank);
		et.commit();

		returnBank = getWithId(BloodBank.class, Integer.class, BloodBank_.id, bank.getId(), em);

		assertThat(returnBank.getName(), equalTo(newName));
		assertThat(returnBank.getCreated(), lessThan(returnBank.getUpdated()));
		assertThat(returnBank.getVersion(), equalTo(2));
	}

	@Test
	void test07_Delete() {
		BloodBank returnBank = getWithId(BloodBank.class, Integer.class, BloodBank_.id, bank.getId(), em);

		et.begin();
		BloodBank b = new PublicBloodBank();
		b.setName("Public Blood Bank");
		em.persist(b);
		et.commit();

		et.begin();
		em.remove(returnBank);
		et.commit();

		assertThat(getCountWithId(BloodBank.class, Integer.class, BloodBank_.id, bank.getId(), em), is(comparesEqualTo(0L)));

		assertThat(getCountWithId(BloodBank.class, Integer.class, BloodBank_.id, b.getId(), em), is(equalTo(1L)));
	}
}
