
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.principals.Administrator;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Aircrafts.Aircraft;
import acme.entities.Aircrafts.AircraftStatus;
import acme.entities.Airlines.Airline;

@GuiService
public class AdministratorAircraftEnableService extends AbstractGuiService<Administrator, Aircraft> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private AdministratorAircraftRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean status = super.getRequest().getPrincipal().hasRealmOfType(Administrator.class);

		try {

			if (super.getRequest().hasData("id")) {

				Integer airlineId = super.getRequest().getData("airline", int.class);

				if (airlineId != 0) {
					Airline airline = this.repository.findAirlineById(airlineId);
					if (airline == null)
						status = false;
				}
			}
		} catch (Throwable E) {
			status = false;
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Aircraft aircraft;
		int id;

		id = super.getRequest().getData("id", int.class);
		aircraft = this.repository.findAircraftById(id);

		super.getBuffer().addData(aircraft);
	}

	@Override
	public void bind(final Aircraft aircraft) {

		super.bindObject(aircraft, "airline", "model", "registrationNumber", "capacity", "cargoWeight", "details");
	}

	@Override
	public void validate(final Aircraft aircraft) {
		boolean confirmation;

		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

	}

	@Override
	public void perform(final Aircraft aircraft) {

		Aircraft a = this.repository.findAircraftById(aircraft.getId());

		System.out.println("Current status: " + a.getStatus());

		if (a.getStatus() == AircraftStatus.MAINTENANCE) {
			a.setStatus(AircraftStatus.ACTIVE);
			System.out.println("Status changed to: " + a.getStatus());
		}

		a.setModel(aircraft.getModel());
		a.setRegistrationNumber(aircraft.getRegistrationNumber());
		a.setCapacity(aircraft.getCapacity());
		a.setCargoWeight(aircraft.getCargoWeight());
		a.setDetails(aircraft.getDetails());
		a.setAirline(aircraft.getAirline());

		this.repository.save(a);
	}

	@Override
	public void unbind(final Aircraft aircraft) {
		Dataset dataset;
		SelectChoices choices;
		SelectChoices airlineChoices;

		Collection<Airline> airlines = this.repository.findAllAirline();
		airlineChoices = SelectChoices.from(airlines, "id", aircraft.getAirline());
		choices = SelectChoices.from(AircraftStatus.class, aircraft.getStatus());

		dataset = super.unbindObject(aircraft, "airline", "model", "registrationNumber", "capacity", "cargoWeight", "status", "details");
		dataset.put("aircraftStatus", choices);
		dataset.put("confirmation", false);
		dataset.put("airlines", airlineChoices);
		dataset.put("airline", airlineChoices.getSelected().getKey());
		dataset.put("readOnlyStatus", true);

		super.getResponse().addData(dataset);
	}
}
