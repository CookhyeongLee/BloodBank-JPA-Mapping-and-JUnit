/***************************************************************************
 * File: PrivateBloodBank.java Course materials (22W) CST 8277
 * 
 * @author Teddy Yap
 * @author Shariar (Shawn) Emami
 * @date Mar 9, 2021
 * 
 */
package bloodbank.entity;

import javax.persistence.DiscriminatorValue;
import javax.persistence.Entity;
import java.io.Serializable;

// [xTODO] PBB01 - add missing annotations, week 9 slides page 15.  value 1 is private and value 0 is public.
@Entity
@DiscriminatorValue(value = "1")
public class PrivateBloodBank extends BloodBank implements Serializable {
	private static final long serialVersionUID = 1L;

	public PrivateBloodBank() {
	}
}
