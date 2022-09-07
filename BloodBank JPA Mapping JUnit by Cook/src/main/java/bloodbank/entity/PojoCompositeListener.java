/***************************************************************************
 * File: PojoListener.java Course materials (22W) CST 8277
 *
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @author (original) Mike Norman
 *
 */
package bloodbank.entity;

import java.time.LocalDateTime;

import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

public class PojoCompositeListener {

	// [xTODO] - What annotation is used when we want to do something just before object is INSERT'd into database?
	@PrePersist
	public void setCreatedOnDate( PojoBaseCompositeKey< ?> pojoBaseComposite) {
		LocalDateTime now = LocalDateTime.now();
		// [xTODO] - what member field(s) do we wish to alter just before object is INSERT'd in the database?
		pojoBaseComposite.setCreated(now);
		pojoBaseComposite.setUpdated(now);
	}

	// [xTODO] - What annotation is used when we want to do something just before object is UPDATE'd into database?
	@PreUpdate
	public void setUpdatedDate( PojoBaseCompositeKey< ?> pojoBaseComposite) {
		// [xTODO] - what member field(s) do we wish to alter just before object is UPDATE'd in the database?
		pojoBaseComposite.setUpdated(LocalDateTime.now());
	}

}