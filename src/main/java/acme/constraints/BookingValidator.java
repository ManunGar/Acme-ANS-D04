
package acme.constraints;

import javax.validation.ConstraintValidatorContext;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.validation.AbstractValidator;
import acme.client.components.validation.Validator;
import acme.entities.Bookings.Booking;
import acme.features.customer.booking.CustomerBookingRepository;

@Validator
public class BookingValidator extends AbstractValidator<ValidBooking, Booking> {

	@Autowired
	private CustomerBookingRepository repository;


	@Override
	protected void initialise(final ValidBooking annotation) {
		assert annotation != null;
	}

	@Override
	public boolean isValid(final Booking booking, final ConstraintValidatorContext context) {
		assert context != null;

		boolean result;

		if (booking == null)
			super.state(context, false, "*", "acme.validation.NotNull.message");
		else if (booking.getLocatorCode() == null || !booking.getLocatorCode().matches("^[A-Z0-9]{6,8}$"))
			super.state(context, false, "locatorCode", "acme.validation.confirmation.message.booking.locator-code.pattern");
		else {
			Booking b = this.repository.findBookingByLocatorCode(booking.getLocatorCode());
			if (b != null && b.getId() != booking.getId())
				super.state(context, false, "locatorCode", "acme.validation.confirmation.message.booking.locator-code");

		}

		result = !super.hasErrors(context);

		return result;
	}

}
