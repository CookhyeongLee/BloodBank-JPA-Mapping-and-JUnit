/***************************************************************************
 * File: BloodBank.java Course materials (22W) CST 8277
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
import java.util.Objects;
import java.util.Set;

/**
 * The persistent class for the blood_bank database table.
 */
// [xTODO] BB01 - add the missing annotations.
// [xTODO] BB02 - BloodBank has subclasses PrivateBloodBank and PublicBoodBank.  Look at week 9 slides for InheritanceType.
// [xTODO] BB03 - do we need a mapped super class? which one?
@Entity
@Table(name = "blood_bank")
@NamedQuery(name = "BloodBank.findAll", query = "SELECT b FROM BloodBank b")
@AttributeOverride(name = "id", column = @Column(name = "bank_id"))
@Inheritance(strategy=InheritanceType.SINGLE_TABLE)
@DiscriminatorColumn(name="privately_owned", columnDefinition="BIT(1)", discriminatorType = DiscriminatorType.INTEGER)
public abstract class BloodBank extends PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	// [xTODO] BB04 - add the missing annotations.
	@Basic(optional = false)
	@Column(name = "name", nullable = false, length = 100)
	private String name;

	// [xTODO] BB05 - add the 1:M annotation.  This list should be effected by changes to this object (cascade).
	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, mappedBy = "bank")
	private Set<BloodDonation> donations = new HashSet<>();

	public BloodBank() {
	}

	public Set< BloodDonation> getDonations() {
		return donations;
	}

	public void setDonations( Set< BloodDonation> donations) {
		this.donations = donations;
	}

	public void setName( String name) {
		this.name = name;
	}

	public String getName() {
		return name;
	}

	//Inherited hashCode/equals is NOT sufficient for this Entity class
	
	/**
	 * Very important: use getter's for member variables because JPA sometimes needs to intercept those calls<br/>
	 * and go to the database to retrieve the value
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		// only include member variables that really contribute to an object's identity
		// i.e. if variables like version/updated/name/etc. change throughout an object's lifecycle,
		// they shouldn't be part of the hashCode calculation
		
		// the database schema for the BLOOD_BANK table has a UNIQUE constraint for the NAME column,
		// so we should include that in the hash/equals calculations
		
		return prime * result + Objects.hash(getId(), getName());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		
		if (obj instanceof BloodBank otherBloodBank) {
			// see comment (above) in hashCode(): compare using only member variables that are
			// truely part of an object's identity
			return Objects.equals(this.getId(), otherBloodBank.getId()) &&
				Objects.equals(this.getName(), otherBloodBank.getName());
		}
		return false;
	}
}