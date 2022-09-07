/***************************************************************************
 * File: PojoBase.java Course materials (22W) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date Mar 9, 2021
 * @author Mike Norman
 * @date 2020 04
 */
package bloodbank.entity;

import javax.persistence.*;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Objects;

/**
 * Abstract class that is base of (class) hierarchy for all @Entity classes
 */
// [xTODO] PB01 - add annotation to define this class as superclass of all entities.  Week 9 slides.
// [xTODO] PB02 - add annotation to place all JPA annotations on fields.
// [xTODO] PB03 - add annotation to for listener.
@MappedSuperclass
@Access(AccessType.FIELD)
@EntityListeners({PojoListener.class})
public abstract class PojoBase implements Serializable {
	private static final long serialVersionUID = 1L;

	// [xTODO] PB04 - add missing annotations.
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	protected int id;

	// [xTODO] PB05 - add missing annotations.
	@Version
	protected int version = 1;

	// [xTODO] PB06 - add missing annotations (hint, is this column on DB).
	@Basic(optional = true)
	@Column(name = "created", nullable = true)
	protected LocalDateTime created;

	// [xTODO] PB07 - add missing annotations (hint, is this column on DB).
	@Basic(optional = true)
	@Column(name = "updated", nullable = true)
	protected LocalDateTime updated;

	public int getId() {
		return id;
	}

	public void setId( int id) {
		this.id = id;
	}

	public int getVersion() {
		return version;
	}

	public void setVersion( int version) {
		this.version = version;
	}

	public LocalDateTime getCreated() {
		return created;
	}
	public void setCreated( LocalDateTime created) {
		this.created = created;
	}

	public LocalDateTime getUpdated() {
		return updated;
	}
	
	public void setUpdated( LocalDateTime updated) {
		this.updated = updated;
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
		return prime * result + Objects.hash(getId());
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		/* enhanced instanceof - yeah!
		 * As of JDK 14, no need for additional 'silly' cast:
		    if (animal instanceof Cat) {
		        Cat cat = (Cat)animal;
		        cat.meow();
                // other class Cat operations ...
            }
		 */
		if (obj instanceof PojoBase otherPojoBase) {
			// see comment (above) in hashCode():  compare using only member variables that are
			// truely part of an object's identity
			return Objects.equals(this.getId(), otherPojoBase.getId());
		}
		return false;
	}
}