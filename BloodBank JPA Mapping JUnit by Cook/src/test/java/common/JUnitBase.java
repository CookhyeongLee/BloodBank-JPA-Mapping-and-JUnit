package common;

import java.util.List;
import java.util.Objects;
import java.util.Random;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaDelete;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;
import javax.persistence.metamodel.SingularAttribute;

import bloodbank.entity.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;

/**
 * super class for all junit tests, holds common methods for creating {@link EntityManagerFactory} and truncating the DB
 * before all.
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @version Mar 12, 2021
 */
public class JUnitBase {

	protected static final Logger LOG = LogManager.getLogger();

	/**
	 * default name of Persistence Unit = "bloodbank-PU"
	 */
	private static final String PERSISTENCE_UNIT = "bloodbank-PU";

	/**
	 * static instance of {@link EntityManagerFactory} for subclasses
	 */
	protected static EntityManagerFactory emf;

	/**
	 * create an instance of {@link EntityManagerFactory} using {@link JUnitBase#PERSISTENCE_UNIT}.<br>
	 * redirects to {@link JUnitBase#buildEMF(String)}.
	 * 
	 * @return an instance of EntityManagerFactory
	 */
	protected static EntityManagerFactory buildEMF() {
		return buildEMF(PERSISTENCE_UNIT);
	}

	/**
	 * create an instance of {@link EntityManagerFactory} using provided Persistence Unit name.
	 * 
	 * @return an instance of EntityManagerFactory
	 */
	protected static EntityManagerFactory buildEMF(String persistenceUnitName) {
		Objects.requireNonNull(persistenceUnitName, "Persistence Unit name cannot be null");
		if (persistenceUnitName.isBlank()) {
			throw new IllegalArgumentException("Persistence Unit name cannot be empty or just white space");
		}
		return Persistence.createEntityManagerFactory(PERSISTENCE_UNIT);
	}

	/**
	 * create a new instance of {@link EntityManager}.<br>
	 * must call {@link JUnitBase#buildEMF()} or {@link JUnitBase#buildEMF(String)} first.
	 * 
	 * @return an instance of {@link EntityManager}
	 */
	protected static EntityManager getEntityManager() {
		if (emf == null) {
			throw new IllegalStateException( " EntityManagerFactory is null, must call JUnitBase::buildEMF first");
		}
		return emf.createEntityManager();
	}

	/**
	 * Delete all Entities. Order of delete matters.
	 */
	protected static void deleteAllData() {
		EntityManager em = getEntityManager();

		em.getTransaction().begin();
		deleteAllFrom(Person.class, em);
		deleteAllFrom(BloodBank.class, em);
		deleteAllFrom(BloodDonation.class, em);
		deleteAllFrom(DonationRecord.class, em);
		deleteAllFrom(Address.class, em);
		deleteAllFrom(Phone.class, em);
		em.getTransaction().commit();
	}

	/**
	 * Delete all instances of provided type form the DB. Same operation as Truncate.
	 * 
	 * @see <a href = "https://stackoverflow.com/questions/23269885/truncate-delete-from-given-the-entity-class">
	 *      StackOverflow: Truncate with JPA</a>
	 * @param <T>        - Type of entity to delete, can be inferred by JVM when method is being executed.
	 * @param entityType - class type of entity, like Address.class
	 * @param em         - EntityManager to be used
	 * @return the number of entities updated or deleted
	 */
	public static <T> int deleteAllFrom(Class<T> entityType, EntityManager em) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaDelete<T> query = builder.createCriteriaDelete(entityType);
		query.from(entityType);
		return em.createQuery(query).executeUpdate();
	}

	protected static <T> long getTotalCount(Class<T> entityType, EntityManager em) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery( Long.class);
		Root<T> root = query.from(entityType);
		query.select( builder.count( root));
		TypedQuery<Long> tq = em.createQuery( query);
		return tq.getSingleResult();
	}

	protected static <T> List<T> getAll(Class<T> entityType, EntityManager em) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(entityType);
		Root<T> root = query.from(entityType);
		query.select(root);
		TypedQuery<T> tq = em.createQuery(query);
		return tq.getResultList();
	}

	protected static <T, R> T getWithId(Class<T> entityType, Class<R> classPK, SingularAttribute<? super T, R> sa, R id, EntityManager em) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> query = builder.createQuery(entityType);
		Root<T> root = query.from(entityType);
		query.select(root);
		query.where(builder.equal(root.get(sa), builder.parameter(classPK, "id")));
		TypedQuery<T> tq = em.createQuery(query);
		tq.setParameter("id", id);
		return tq.getSingleResult();
	}

	protected static <T, R> long getCountWithId(Class<T> entityType, Class<R> classPK, SingularAttribute<? super T, R> sa, R id, EntityManager em) {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<Long> query = builder.createQuery(Long.class);
		Root<T> root = query.from(entityType);
		query.select(builder.count(root));
		query.where(builder.equal(root.get(sa), builder.parameter(classPK, "id")));
		TypedQuery< Long> tq = em.createQuery(query);
		tq.setParameter("id", id);
		return tq.getSingleResult();
	}

	protected static String generateRandomString(int lengthOfString) {
		int leftLimit = 48;
		int rightLimit = 122;
		Random random = new Random();

		return random.ints(leftLimit, rightLimit + 1)
				.filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
				.limit(lengthOfString)
				.collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
				.toString();
	}

	@BeforeAll
	static void setupAll() {
		emf = buildEMF();
		deleteAllData();
	}

	@AfterAll
	static void tearDownAll() {
		emf.close();
	}
}
