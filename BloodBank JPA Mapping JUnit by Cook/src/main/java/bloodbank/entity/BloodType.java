/***************************************************************************
 * File: BloodType.java Course materials (22W) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date Mar 9, 2021
 * 
 */
package bloodbank.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Objects;

// [xTODO] BT01 - this class is not an entity however it can be embedded in other entities.  add missing annotation.
@Embeddable
@Access(AccessType.FIELD)
public class BloodType implements Serializable {
	private static final long serialVersionUID = 1L;

	// [xTODO] BT02 - add annotations
	@Basic(optional = false)
	@Column(name = "blood_group", nullable = false, length = 2)
	private String bloodGroup;

	// [xTODO] BT03 - add annotations
	@Basic(optional = false)
	@Column(name = "rhd", nullable = false, length = 1)
	private byte rhd;

	public BloodType() {
	}

	public String getBloodGroup() {
		return bloodGroup;
	}

	public void setBloodGroup( String bloodGroup) {
		this.bloodGroup = bloodGroup;
	}

	public void setType( String group, String rhd) {
		setBloodGroup( group);
		byte p = 0b1;
		byte n = 0b0;
		setRhd( ( "+".equals( rhd) ? p : n));
	}

	public byte getRhd() {
		return rhd;
	}

	public void setRhd( byte rhd) {
		this.rhd = rhd;
	}

	/**
	 * Very important:  use getter's for member variables because JPA sometimes needs to intercept those calls<br/>
	 * and go to the database to retrieve the value
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		// only include member variables that really contribute to an object's identity
		// i.e. if variables like version/updated/name/etc.  change throughout an object's lifecycle,
		// they shouldn't be part of the hashCode calculation
		return prime * result + Objects.hash(getBloodGroup(), getRhd());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		if (obj instanceof BloodType otherBloodType) {
			// see comment (above) in hashCode():  compare using only member variables that are
			// truely part of an object's identity
			return Objects.equals(this.getBloodGroup(), otherBloodType.getBloodGroup()) &&
					Objects.equals(this.getRhd(), otherBloodType.getRhd());
		}
		return false;
	}

}