
package acme.features.manager.legs;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Legs.Legs;
import acme.realms.AirlineManager;

@GuiService
public class ManagerLegsListService extends AbstractGuiService<AirlineManager, Legs> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegsRepository repository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		super.getResponse().setAuthorised(true);
	}

	@Override
	public void load() {
		int flightId = super.getRequest().getData("flightId", int.class);
		super.getResponse().addGlobal("flightId", flightId);
		Collection<Legs> legs = this.repository.findAllByFlightId(flightId);
		super.getBuffer().addData(legs);
	}

	@Override
	public void unbind(final Legs leg) {
		Dataset dataset;

		dataset = super.unbindObject(leg, "departure", "arrival", "status");
		dataset.put("flightId", leg.getFlight().getId());

		super.getResponse().addData(dataset);
	}

}
