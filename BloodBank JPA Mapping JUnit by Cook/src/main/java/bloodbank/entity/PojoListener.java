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

public class PojoListener {

	// [xTODO] - What annotation is used when we want to do something just before object is INSERT'd in the database?
	@PrePersist
	public void setCreatedOnDate( PojoBase pojoBase) {
		LocalDateTime now = LocalDateTime.now();
		// [xTODO] - what member field(s) do we wish to alter just before object is INSERT'd in the database?
		pojoBase.setCreated(now);
		pojoBase.setUpdated(now);
	}

	// [xTODO] - What annotation is used when we want to do something just before object is UPDATE'd in the database?
	@PreUpdate
	public void setUpdatedDate( PojoBase pojoBase) {
		// [xTODO] - what member field(s) do we wish to alter just before object is UPDATE'd in the database?
		pojoBase.setUpdated(LocalDateTime.now());
	}

}