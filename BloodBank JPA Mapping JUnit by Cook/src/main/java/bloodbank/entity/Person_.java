package bloodbank.entity;

import javax.annotation.Generated;
import javax.persistence.metamodel.SetAttribute;
import javax.persistence.metamodel.SingularAttribute;
import javax.persistence.metamodel.StaticMetamodel;

@Generated(value="Dali", date="2022-07-24T21:24:33.714-0400")
@StaticMetamodel(Person.class)
public class Person_ extends PojoBase_ {
	public static volatile SingularAttribute<Person, String> firstName;
	public static volatile SingularAttribute<Person, String> lastName;
	public static volatile SetAttribute<Person, DonationRecord> donations;
	public static volatile SetAttribute<Person, Contact> contacts;
}
