
package acme.features.manager.legs;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.constraints.LegsValidator;
import acme.entities.Aircrafts.Aircraft;
import acme.entities.Aircrafts.AircraftRepository;
import acme.entities.Airports.Airport;
import acme.entities.Airports.AirportRepository;
import acme.entities.Flight.Flight;
import acme.entities.Flight.FlightRepository;
import acme.entities.Legs.Legs;
import acme.entities.Legs.LegsStatus;
import acme.realms.AirlineManager;

@GuiService
public class ManagerLegsUpdateService extends AbstractGuiService<AirlineManager, Legs> {
	// Internal state ---------------------------------------------------------

	@Autowired
	private ManagerLegsRepository	repository;

	@Autowired
	private FlightRepository		flightRepository;

	@Autowired
	private AirportRepository		airportRepository;

	@Autowired
	private AircraftRepository		aircraftRepository;


	@Override
	public void authorise() {
		int userId;
		int legId;
		Legs legs;
		boolean autorhorise;

		legId = super.getRequest().getData("id", int.class);
		userId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();
		legs = this.repository.findLegById(legId);
		autorhorise = legs.getFlight().getManager().getUserAccount().getId() == userId;
		super.getResponse().setAuthorised(autorhorise);
	}

	@Override
	public void load() {
		int legId;
		Legs leg;

		legId = super.getRequest().getData("id", int.class);
		leg = this.repository.findLegById(legId);

		super.getBuffer().addData(leg);
	}

	@Override
	public void bind(final Legs leg) {
		super.bindObject(leg, "departure", "arrival", "status", "departureAirport", "arrivalAirport", "aircraft", "flight", "flightNumber");
	}

	@Override
	public void validate(final Legs leg) {

		boolean draftMode = leg.getDraftMode();
		super.state(draftMode, "*", "acme.validation.confirmation.message");

		boolean confirmation;
		confirmation = super.getRequest().getData("confirmation", boolean.class);
		super.state(confirmation, "confirmation", "acme.validation.confirmation.message");

		if (leg == null || leg.getAircraft() == null || leg.getDeparture() == null || leg.getArrival() == null || leg.getDepartureAirport() == null || leg.getArrivalAirport() == null)
			super.state(false, "*", "acme.validation.NotNull.message");
		else {
			boolean valid = leg.getArrival().before(leg.getDeparture());
			super.state(!valid, "departure", "acme.validation.legs.dates.message");

			Airport departureAirport = leg.getDepartureAirport();
			Airport arrivalAirport = leg.getArrivalAirport();
			super.state(!departureAirport.equals(arrivalAirport), "departureAirport", "acme.validation.leg.airport.message");

			boolean correctLeg = true;
			List<Legs> legs = (List<Legs>) this.repository.findAllByFlightId(leg.getFlight().getId());
			legs.add(leg);
			legs = LegsValidator.sortLegsByDeparture(legs);
			for (int i = 0; i < legs.size() - 1 && correctLeg && legs.size() >= 2; i++) {
				if (legs.get(i).getArrival().after(legs.get(i + 1).getDeparture())) {
					correctLeg = false;
					super.state(correctLeg, "departure", "acme.validation.legs.departure.message");
				}
				if (!legs.get(i).getArrivalAirport().getCity().equals(legs.get(i + 1).getDepartureAirport().getCity())) {
					correctLeg = false;
					super.state(correctLeg, "departureAirport", "acme.validation.legs.departureAirportmessage");
				}
			}
		}
	}

	@Override
	public void perform(final Legs leg) {
		Legs l = this.repository.findLegById(leg.getId());
		l.setFlight(leg.getFlight());
		l.setDeparture(leg.getDeparture());
		l.setArrival(leg.getArrival());
		l.setStatus(leg.getStatus());
		l.setArrivalAirport(leg.getArrivalAirport());
		l.setDepartureAirport(leg.getDepartureAirport());
		l.setAircraft(leg.getAircraft());
		l.setDraftMode(true);
		this.repository.save(l);
	}

	@Override
	public void unbind(final Legs leg) {
		Dataset dataset;
		SelectChoices choices;
		SelectChoices flightChoices;
		SelectChoices departureAirportChoices;
		SelectChoices arrivalAirportChoices;
		SelectChoices aircraftChoices;

		Collection<Flight> flights = this.flightRepository.findAllFlight();
		Collection<Airport> airports = this.airportRepository.findAllAirport();
		Collection<Aircraft> aircrafts = this.aircraftRepository.findAllAircarftByAirlineId(leg.getFlight().getManager().getAirline().getId());
		flightChoices = SelectChoices.from(flights, "description", leg.getFlight());
		departureAirportChoices = SelectChoices.from(airports, "city", leg.getDepartureAirport());
		arrivalAirportChoices = SelectChoices.from(airports, "city", leg.getArrivalAirport());
		aircraftChoices = SelectChoices.from(aircrafts, "model", leg.getAircraft());
		choices = SelectChoices.from(LegsStatus.class, leg.getStatus());

		dataset = super.unbindObject(leg, "departure", "arrival", "draftMode", "flightNumber");
		dataset.put("status", choices);
		dataset.put("flight", flightChoices.getSelected().getKey());
		dataset.put("flights", flightChoices);
		dataset.put("departureAirport", departureAirportChoices.getSelected().getKey());
		dataset.put("departureAirports", departureAirportChoices);
		dataset.put("arrivalAirport", arrivalAirportChoices.getSelected().getKey());
		dataset.put("arrivalAirports", arrivalAirportChoices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("legId", leg.getId());

		super.getResponse().addData(dataset);
	}

	public static List<Legs> sortLegsByDeparture(final List<Legs> legs) {
		List<Legs> sortedLegs = new ArrayList<>(legs);
		sortedLegs.sort(Comparator.comparing(Legs::getDeparture));
		return sortedLegs;
	}

}
