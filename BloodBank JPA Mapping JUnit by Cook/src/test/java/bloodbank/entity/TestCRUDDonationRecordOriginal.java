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
public class TestCRUDDonationRecordOriginal extends JUnitBase {

	private EntityManager em;
	private EntityTransaction et;
	private static Person person;
	private static DonationRecord record;
	private static BloodDonation donation;
	private static BloodType type;
	private static BloodBank bank;

	@BeforeAll
	static void setupAllInit() {
		person = new Person();
		person.setFullName("Cookhyeong", "Lee");

		bank = new PublicBloodBank();
		bank.setName("Public Blood Bank");

		type = new BloodType();
		type.setType("O", "+");

		donation = new BloodDonation();
		donation.setBloodType(type);
		donation.setMilliliters(10);
		donation.setBank(bank);
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
		assertThat(getTotalCount(DonationRecord.class, em), is(comparesEqualTo(0L)));
	}

	@Test
	void test02_Create() {
		et.begin();
		record = new DonationRecord();
		record.setOwner(person);
		record.setTested(true);
		record.setDonation(donation);
		em.persist(record);
		et.commit();

		assertThat(getCountWithId(DonationRecord.class, Integer.class, DonationRecord_.id, donation.getId(), em), is(greaterThanOrEqualTo(1L)));
	}

	@Test
	void test03_CreateInvalid() {
		et.begin();
		DonationRecord r = new DonationRecord();
		r.setDonation(donation);
		r.setTested(false);
		assertThrows(PersistenceException.class, () -> em.persist(r));
		et.commit();
	}

	@Test
	void test04_Read() {
		assertThat(getAll(DonationRecord.class, em), contains(equalTo(donation)));
	}

	@Test
	void test05_ReadDependencies() {
		DonationRecord returnedRecord = getWithId(DonationRecord.class, Integer.class, DonationRecord_.id, donation.getId(), em);

		assertThat(returnedRecord.getOwner(), equalTo(person));
		assertThat(returnedRecord.getTested(), equalTo((byte)0b0001));
		assertThat(returnedRecord.getDonation(), equalTo(donation));
		assertThat(returnedRecord.getDonation().getBloodType(), equalTo(type));
		assertThat(returnedRecord.getDonation().getBank(), equalTo(bank));
		assertThat(returnedRecord.getCreated(), equalTo(returnedRecord.getUpdated()));
		assertThat(returnedRecord.getVersion(), equalTo(1));
	}

	@Test
	void test06_Update() {
		DonationRecord returnedRecord = getWithId(DonationRecord.class, Integer.class, DonationRecord_.id, donation.getId(), em);

		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			throw new RuntimeException(e);
		}

		et.begin();
		returnedRecord.setTested(false);
		em.merge(returnedRecord);
		et.commit();

		returnedRecord = getWithId(DonationRecord.class, Integer.class, DonationRecord_.id, donation.getId(), em);

		assertThat(returnedRecord.getTested(), equalTo((byte)0b0000));
		assertThat(returnedRecord.getCreated(), lessThan(returnedRecord.getUpdated()));
		assertThat(returnedRecord.getVersion(), equalTo(2));
	}

	@Test
	void test07_UpdateDependencies() {
		DonationRecord returnedRecord = getWithId(DonationRecord.class, Integer.class, DonationRecord_.id, donation.getId(), em);

		person = returnedRecord.getOwner();
		person.setFullName("Hans", "Michael");

		bank = returnedRecord.getDonation().getBank();
		bank.setName("Public Bank");

		type = returnedRecord.getDonation().getBloodType();
		type.setType("A", "+");

		donation = returnedRecord.getDonation();
		donation.setMilliliters(1337);

		et.begin();
		returnedRecord.setOwner(person);
		returnedRecord.setDonation(donation);
		em.merge(returnedRecord);
		et.commit();

		returnedRecord = getWithId(DonationRecord.class, Integer.class, DonationRecord_.id, donation.getId(), em);

		assertThat(returnedRecord.getOwner(), equalTo(person));
		assertThat(returnedRecord.getDonation(), equalTo(donation));
		assertThat(returnedRecord.getDonation().getBloodType(), equalTo(type));
		assertThat(returnedRecord.getDonation().getBank(), equalTo(bank));
	}

	@Test
	void test08_DeleteDependency() {
		DonationRecord returnedRecord = getWithId(DonationRecord.class, Integer.class, DonationRecord_.id, donation.getId(), em);

		int donationId = returnedRecord.getDonation().getId();

		et.begin();
		returnedRecord.setDonation(null);
		em.merge(returnedRecord);
		et.commit();

		returnedRecord = getWithId(DonationRecord.class, Integer.class, DonationRecord_.id, record.getId(), em);

		assertThat(returnedRecord.getDonation(), is(nullValue()));

		assertThat(getCountWithId(BloodDonation.class, Integer.class, BloodDonation_.id, donationId, em), is(equalTo( 1L)));
	}

	@Test
	void test09_Delete() {
		DonationRecord returnedRecord = getWithId(DonationRecord.class, Integer.class, DonationRecord_.id, donation.getId(), em);

		et.begin();
		DonationRecord r = new DonationRecord();
		BloodBank b = new PrivateBloodBank();
		b.setName("Bloody Bank");
		BloodType t = new BloodType();
		t.setType("AB", "-");
		BloodDonation d = new BloodDonation();
		d.setMilliliters(1337);
		d.setBloodType(t);
		d.setBank(b);
		r.setOwner(returnedRecord.getOwner());
		em.persist(r);
		et.commit();

		et.begin();
		em.remove(returnedRecord);
		et.commit();

		assertThat(getCountWithId(DonationRecord.class, Integer.class, DonationRecord_.id, record.getId(), em), is(equalTo(0L)));

		assertThat(getCountWithId(DonationRecord.class, Integer.class, DonationRecord_.id, r.getId(), em), is(equalTo(1L)));
	}

}
