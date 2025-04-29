
package acme.features.technicians.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Aircrafts.Aircraft;
import acme.entities.MaintenanceRecords.MaintenanceRecord;
import acme.entities.MaintenanceRecords.MaintenanceStatus;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordUpdateService extends AbstractGuiService<Technician, MaintenanceRecord> {
	// Internal state ------------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		int id;
		MaintenanceRecord maintenanceRecord;
		int technicianId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(id);
		boolean status = maintenanceRecord.getTechnician().getUserAccount().getId() == technicianId && super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		MaintenanceRecord maintenanceRecord;
		int id;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(id);

		super.getBuffer().addData(maintenanceRecord);
	}

	@Override
	public void bind(final MaintenanceRecord maintenanceRecord) {

		super.bindObject(maintenanceRecord, "aircraft", "maintenanceMoment", "nextInspection", "status", "estimatedCost", "notes");
	}

	@Override
	public void validate(final MaintenanceRecord maintenanceRecord) {
		;
	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {

		MaintenanceRecord mr = this.repository.findMaintenanceRecordById(maintenanceRecord.getId());
		mr.setMaintenanceMoment(maintenanceRecord.getMaintenanceMoment());
		mr.setStatus(maintenanceRecord.getStatus());
		mr.setNextInspection(maintenanceRecord.getNextInspection());
		mr.setEstimatedCost(maintenanceRecord.getEstimatedCost());
		mr.setNotes(maintenanceRecord.getNotes());
		mr.setDraftMode(maintenanceRecord.isDraftMode());
		mr.setTechnician(maintenanceRecord.getTechnician());
		mr.setAircraft(maintenanceRecord.getAircraft());

		this.repository.save(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		SelectChoices choices;
		SelectChoices aircraftChoices;
		SelectChoices technicianChoices;
		Dataset dataset;
		Collection<Aircraft> aircrafts;
		Collection<Technician> technicians;

		choices = SelectChoices.from(MaintenanceStatus.class, maintenanceRecord.getStatus());

		aircrafts = this.repository.findAllAircrafts();
		aircraftChoices = SelectChoices.from(aircrafts, "model", maintenanceRecord.getAircraft());

		technicians = this.repository.findAllTechnicians();
		technicianChoices = SelectChoices.from(technicians, "licenseNumber", maintenanceRecord.getTechnician());

		dataset = super.unbindObject(maintenanceRecord, "technician.identity.name", "maintenanceMoment", "nextInspection", "status", "estimatedCost", "notes", "draftMode");
		dataset.put("status", choices);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);
		dataset.put("technician", technicianChoices.getSelected().getKey());
		dataset.put("technicians", technicianChoices);
		dataset.put("maintenanceRecordId", maintenanceRecord.getId());

		super.getResponse().addData(dataset);
	}
}
