
package acme.entities.Bookings;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.validation.Mandatory;
import acme.entities.Passengers.Passenger;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
public class BookingRecord extends AbstractEntity {

	//Serialisation version 

	private static final long	serialVersionUID	= 1L;

	//Relationships

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Booking				booking;

	@Mandatory
	@Valid
	@ManyToOne(optional = false)
	private Passenger			passenger;

}
