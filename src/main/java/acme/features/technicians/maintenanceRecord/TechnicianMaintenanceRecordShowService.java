
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
public class TechnicianMaintenanceRecordShowService extends AbstractGuiService<Technician, MaintenanceRecord> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int technicianId;
		Collection<MaintenanceRecord> maintenanceRecords;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();
		maintenanceRecords = this.repository.findMaintenanceRecordsByTechnicianId(technicianId);
		status = maintenanceRecords.stream().allMatch(mr -> mr.getTechnician().getUserAccount().getId() == technicianId) && super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id;
		MaintenanceRecord maintenanceRecord;

		id = super.getRequest().getData("id", int.class);
		maintenanceRecord = this.repository.findMaintenanceRecordById(id);

		super.getBuffer().addData(maintenanceRecord);
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
