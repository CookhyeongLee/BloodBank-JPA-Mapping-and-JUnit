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
public class TestCRUDPersonOriginal extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;
	private static Person person;
	private static final String FIRST_NAME = "Cookhyeong";
	private static final String LAST_NAME = "Lee";

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
		assertThat(getTotalCount(Person.class, em), is(comparesEqualTo(0L)));
	}

	@Test
	void test02_Create() {
		et.begin();
		person = new Person();
		person.setFirstName(FIRST_NAME);
		person.setLastName(LAST_NAME);
		em.persist(person);
		et.commit();

		assertThat(getCountWithId(Person.class, Integer.class, Person_.id, person.getId(), em), is(greaterThanOrEqualTo(1L)));
	}

	@Test
	void test03_CreateInvalid() {
		et.begin();
		Person p = new Person();
		p.setFirstName(generateRandomString(51));
		p.setLastName(LAST_NAME);
		assertThrows(PersistenceException.class, () -> em.persist(p));
		et.commit();

		et.begin();
		p.setFirstName(null);
		assertThrows(PersistenceException.class, () -> em.persist(p));
		et.commit();

		et.begin();
		p.setFirstName(FIRST_NAME);
		p.setLastName(generateRandomString(51));
		assertThrows(PersistenceException.class, () -> em.persist(p));
		et.commit();

		et.begin();
		p.setLastName(null);
		assertThrows(PersistenceException.class, () -> em.persist(p));
		et.commit();
	}

	@Test
	void test04_Read() {
		assertThat(getAll(Person.class, em), contains(equalTo(person)));
	}

	@Test
	void test05_ReadDependencies() {
		Person returnPerson = getWithId(Person.class, Integer.class, Person_.id, person.getId(), em);

		assertThat(returnPerson.getFirstName(), equalTo(FIRST_NAME));
		assertThat(returnPerson.getLastName(), equalTo(LAST_NAME));
		assertThat(returnPerson.getCreated(), equalTo(returnPerson.getUpdated()));
		assertThat(returnPerson.getVersion(), equalTo(1));
	}

	@Test
	void test06_Update() {
		Person returnPerson = getWithId(Person.class, Integer.class, Person_.id, person.getId(), em);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}
		String newFirstName = generateRandomString(50);
		String newLastName = generateRandomString(50);

		et.begin();
		returnPerson.setFullName(newFirstName, newLastName);
		em.merge(returnPerson);
		et.commit();

		returnPerson = getWithId(Person.class, Integer.class, Person_.id, person.getId(), em);

		assertThat(returnPerson.getFirstName(), equalTo(newFirstName));
		assertThat(returnPerson.getLastName(), equalTo(newLastName));
		assertThat(returnPerson.getCreated(), lessThan(returnPerson.getUpdated()));
		assertThat(returnPerson.getVersion(), equalTo(2));
	}

	@Test
	void test07_Delete() {
		Person returnPerson = getWithId(Person.class, Integer.class, Person_.id, person.getId(), em);

		et.begin();
		Person p = new Person();
		p.setFullName("Person1", "Person2");
		em.persist(p);
		et.commit();

		et.begin();
		em.remove(returnPerson);
		et.commit();

		assertThat(getCountWithId(Person.class, Integer.class, Person_.id, person.getId(), em), is(comparesEqualTo(0L)));

		assertThat(getCountWithId(Person.class, Integer.class, Person_.id, p.getId(), em), is(equalTo(1L)));
	}
}
