/***************************************************************************
 * File: Phone.java Course materials (22W) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date Mar 9, 2021
 * 
 */
package bloodbank.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;

/**
 * The persistent class for the phone database table.
 */
// [xTODO] PH01 - add the missing annotations.
// [xTODO] PH02 - do we need a mapped super class? which one?
@Entity
@Table(name = "phone")
@NamedQuery(name = "Phone.findAll", query = "SELECT p FROM Phone p")
@AttributeOverride(name= "id", column = @Column(name = "phone_id"))
public class Phone extends PojoBase implements Serializable{
	private static final long serialVersionUID = 1L;

	// [xTODO] PH03 - add missing annotations.
	@Basic(optional = false)
	@Column(name = "area_code", nullable = false, length = 10)
	private String areaCode;

	// [xTODO] PH04 - add missing annotations.
	@Basic(optional = false)
	@Column(name = "country_code", nullable = false, length = 10)
	private String countryCode = "1";

	// [xTODO] PH05 - add missing annotations.
	@Basic(optional = false)
	@Column(name = "number", nullable = false, length = 10)
	private String number;

	// [sTODO] PH06 - add annotations for 1:M relation.  remove should not cascade.
	@OneToMany(cascade = CascadeType.MERGE, fetch = FetchType.LAZY, mappedBy = "phone")
	private Set< Contact> contacts = new HashSet<>();

	public Phone() {
		super();
	}

	public Phone(String areaCode, String countryCode, String number) {
		this();
		this.areaCode = areaCode;
		this.countryCode = countryCode;
		this.number = number;
	}

	public Phone setNumber( String countryCode, String areaCode, String number) {
		setCountryCode( countryCode);
		setAreaCode( areaCode);
		setNumber( number);
		return this;
	}

	public String getAreaCode() {
		return areaCode;
	}

	public void setAreaCode( String areaCode) {
		this.areaCode = areaCode;
	}

	public String getCountryCode() {
		return countryCode;
	}

	public void setCountryCode( String countryCode) {
		this.countryCode = countryCode;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber( String number) {
		this.number = number;
	}

	public Set< Contact> getContacts() {
		return contacts;
	}

	public void setContacts( Set< Contact> contacts) {
		this.contacts = contacts;
	}

	//Inherited hashCode/equals is sufficient for this Entity class

}