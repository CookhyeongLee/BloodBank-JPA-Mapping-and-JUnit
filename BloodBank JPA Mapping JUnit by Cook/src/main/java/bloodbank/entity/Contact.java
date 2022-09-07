/***************************************************************************
 * File: Contact.java Course materials (22W) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date Mar 9, 2021
 * 
 */
package bloodbank.entity;

import java.io.Serializable;
import java.util.Objects;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.MapsId;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * The persistent class for the contact database table.
 */
@Entity
@Table(name = "contact")
@Access(AccessType.FIELD)
@NamedQuery( name = "Contact.findAll", query = "SELECT c FROM Contact c")
public class Contact extends PojoBaseCompositeKey< ContactPK> implements Serializable {
	private static final long serialVersionUID = 1L;

	// Hint - what Annotation is used for a composite primary key type
	@EmbeddedId
	private ContactPK id;

	// @MapsId is used to map a part of composite key to an entity.
	@MapsId("personId")
	@ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "person_id", referencedColumnName = "id", nullable = false)
	private Person owner;

	// [xTODO] - add missing annotations
	@MapsId("phoneId")
	@ManyToOne(cascade = CascadeType.ALL, optional = false, fetch = FetchType.LAZY)
	@JoinColumn(name = "phone_id", referencedColumnName = "phone_id", nullable = false)
	private Phone phone;

	// [xTODO] - add missing annotations
	@ManyToOne(cascade = CascadeType.ALL, optional = true, fetch = FetchType.LAZY)
	@JoinColumn(name = "address_id", referencedColumnName = "address_id", nullable = true)
	private Address address;

	@Column(length = 100, name = "email")
	private String email;

	@Basic(optional = false)
	@Column(length = 10, name = "contact_type", nullable = false)
	private String contactType;

	public Contact() {
		id = new ContactPK();
	}

	@Override
	public ContactPK getId() {
		return id;
	}

	@Override
	public void setId( ContactPK id) {
		this.id = id;
	}

	public Person getOwner() {
		return owner;
	}

	public void setOwner( Person owner) {
		this.owner = owner;
		//we must manually set the 'other' side of the relationship (JPA does not 'do' auto-management of relationships)
		if (owner != null) {
			owner.getContacts().add(this);
		}
	}

	public Phone getPhone() {
		return phone;
	}

	public void setPhone( Phone phone) {
		this.phone = phone;
		//we must manually set the 'other' side of the relationship (JPA does not 'do' auto-management of relationships)
		if (phone != null) {
			phone.getContacts().add(this);
		}
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress( Address address) {
		this.address = address;
		//we must manually set the 'other' side of the relationship (JPA does not 'do' auto-management of relationships)
		if (address != null) {
			address.getContacts().add(this);
		}
	}

	public String getEmail() {
		return email;
	}

	public void setEmail( String email) {
		this.email = email;
	}

	public String getContactType() {
		return contactType;
	}

	public void setContactType( String contactType) {
		this.contactType = contactType;
	}

	//Inherited hashCode/equals is sufficient for this Entity class

}