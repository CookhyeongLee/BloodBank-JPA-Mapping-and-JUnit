/***************************************************************************
 * File: Assignment3Driver.java Course materials (22W) CST 8277
 * 
 * @author Shariar (Shawn) Emami
 * @date Mar 9, 2021
 * 
 * @author Mike Norman
 * @date 2020 04
 */
package bloodbank;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import bloodbank.entity.Address;
import bloodbank.entity.BloodBank;
import bloodbank.entity.BloodDonation;
import bloodbank.entity.BloodType;
import bloodbank.entity.Contact;
import bloodbank.entity.ContactPK;
import bloodbank.entity.DonationRecord;
import bloodbank.entity.Person;
import bloodbank.entity.Phone;
import bloodbank.entity.PrivateBloodBank;
import bloodbank.entity.PublicBloodBank;

/**
 * used as starting point of application to simply create the DB on Server or refresh it if needs be.
 * 
 * @author Shariar (Shawn) Emami
 * @version Mar 12, 2021
 */
public class BloodBankDriver {

	protected static final Logger LOG = LogManager.getLogger();

	public static final String PERSISTENCE_UNIT = "bloodbank-PU";

	public static void main( String[] args) {
		EntityManagerFactory emf = Persistence.createEntityManagerFactory( PERSISTENCE_UNIT);
		EntityManager em = emf.createEntityManager();
		addSampleData(em);
		printData(em);
		cleanData(em);
		em.close();
		emf.close();
	}
	
	private static void cleanData(EntityManager em) {
		/* clean up data: instead of removing the schema, remove all the Entities
		 * 
		 * JPQL has a String-based DELETE query, but it gets complicated when an Entity
		 * has a composity primary key, like Contact with ContactPK. The JPQL look like:
		 *   DELETE from c Contact c WHERE c.phoneId = :x and c.personId = y
		 * If the WHERE clause gets more complicated, say for example only delete
		 * Contact's from a particular province ... not nice!
		 * 
		 * We use the CriteriaBuilder API ... it also is complicated, but the benefit
		 * is that once you figure it out, changing the query later doesn't increase
		 * the complexity
		 * 
		*/
		CriteriaBuilder cb = em.getCriteriaBuilder();

		CriteriaDelete<Contact>        q1 = cb.createCriteriaDelete(Contact.class);
		q1.from(Contact.class);
		CriteriaDelete<Phone>          q2 = cb.createCriteriaDelete(Phone.class);
		q2.from(Phone.class);
		CriteriaDelete<Address>        q3 = cb.createCriteriaDelete(Address.class);
		q3.from(Address.class);
		CriteriaDelete<DonationRecord> q4 = cb.createCriteriaDelete(DonationRecord.class);
		q4.from(DonationRecord.class);
		CriteriaDelete<BloodDonation>  q5 = cb.createCriteriaDelete(BloodDonation.class);
		q5.from(BloodDonation.class);
		CriteriaDelete<BloodDonation>  q6 = cb.createCriteriaDelete(BloodDonation.class);
		q6.from(BloodDonation.class);
		CriteriaDelete<BloodBank>      q7 = cb.createCriteriaDelete(BloodBank.class);
		q7.from(BloodBank.class);
		CriteriaDelete<Person>         q8 = cb.createCriteriaDelete(Person.class);
		q8.from(Person.class);

		EntityTransaction et = em.getTransaction();
		et.begin();
		em.createQuery(q1).executeUpdate();
		em.createQuery(q2).executeUpdate();
		em.createQuery(q3).executeUpdate();
		em.createQuery(q4).executeUpdate();
		em.createQuery(q5).executeUpdate();
		em.createQuery(q6).executeUpdate();
		em.createQuery(q7).executeUpdate();
		em.createQuery(q8).executeUpdate();
		et.commit();
		
		em.close();
	}

	private static void printData( EntityManager em) {

		Person p = em.find( Person.class, 1);
		int sizeContacts = p.getContacts().size();
		int sizeDonations = p.getDonations().size();

		DonationRecord dr = em.find( DonationRecord.class, 1);
		int ownerId = dr.getOwner().getId();
		int donationId = dr.getDonation().getId();

		BloodDonation bd = em.find( BloodDonation.class, 1);
		int bankId = bd.getBank().getId();
		int recordId = bd.getRecord().getId();

		BloodBank bb = em.find( BloodBank.class, 1);
		int sizeBBDonations = bb.getDonations().size();

		Phone phone = em.find( Phone.class, 1);
		int sizePContacts = phone.getContacts().size();

		Address ad = em.find( Address.class, 1);
		int sizeAdContacts = ad.getContacts().size();

		Contact con = em.find( Contact.class, new ContactPK( 1, 1));
		int ownerConId = con.getOwner().getId();
		int addId = con.getAddress().getId();
		int phoneId = con.getPhone().getId();

		LOG.info( "Person.ID: {} has donations: {} and contacts: {}", p.getId(), sizeContacts, sizeDonations);
		LOG.info( "DonationRecord.ID: {} has Owner.id: {} and donation.id: {}", dr.getId(), ownerId, donationId);
		LOG.info( "BloodDonation.ID: {} has bank.id: {} and record.id: {}", bd.getId(), bankId, recordId);
		LOG.info( "BloodBank.ID: {} has donations: {}", bb.getId(), sizeBBDonations);
		LOG.info( "Phone.ID: {} has contacts: {}", phone.getId(), sizePContacts);
		LOG.info( "Address.ID: {} has contacts: {}", ad.getId(), sizeAdContacts);
		LOG.info( "Contact.ID: {} has Owner.id: {}, Address.id: {}, and Phone.id: {}", con.getId(), ownerConId, addId,
				phoneId);
	}

	private static void addSampleData( EntityManager em) {
		EntityTransaction et = em.getTransaction();
		et.begin();

		BloodBank bankPrivate = new PrivateBloodBank();
		bankPrivate.setName( "Bloody Bank");
		em.persist( bankPrivate);

		BloodBank bankPublic = new PublicBloodBank();
		bankPublic.setName( "Cheap Bloody Bank");
		em.persist( bankPublic);

		Phone phoneHome = new Phone();
		phoneHome.setNumber( "0", "234", "5678900");

		Phone phoneWork = new Phone();
		phoneWork.setNumber( "1", "432", "0098765");

		Address address = new Address();
		address.setAddress( "123", "abcd Dr.W", "ottawa", "ON", "CA", "A1B2C3");

		Person p1 = new Person();
		p1.setFullName( "Teddy", "Yap");

		BloodType type = new BloodType();
		type.setType( "A", "+");

		BloodType type2 = new BloodType();
		type2.setType( "B", "-");

		Contact contactHome = new Contact();
		contactHome.setAddress( address);
		contactHome.setPhone( phoneHome);
		contactHome.setEmail( "test@test.com");
		contactHome.setContactType( "Home");
		contactHome.setOwner( p1);
		em.persist( contactHome);

		Contact contactWork = new Contact();
		contactWork.setOwner( p1);
		contactWork.setPhone( phoneWork);
		contactWork.setContactType( "Work");
		contactWork.setOwner( p1);
		em.persist( contactWork);

		BloodDonation donation = new BloodDonation();
		donation.setBloodType( type);
		donation.setMilliliters( 10);
		donation.setBank( bankPublic);
		em.persist(donation);

		BloodDonation donation2 = new BloodDonation();
		donation2.setBloodType( type2);
		donation2.setMilliliters( 10);
		donation2.setBank( bankPrivate);
		em.persist( donation2);

		DonationRecord record = new DonationRecord();
		record.setOwner( p1);
		record.setTested( true);
		record.setDonation( donation);
		em.persist( record);

		DonationRecord record2 = new DonationRecord();
		record2.setOwner( p1);
		record2.setTested( false);
		record2.setDonation( donation);
		em.persist( record2);

		DonationRecord record3 = new DonationRecord();
		record3.setOwner( p1);
		record3.setTested( true);
		record3.setDonation( donation2);
		em.persist( record3);

		et.commit();
	}

}
