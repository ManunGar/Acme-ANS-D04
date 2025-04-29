
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
import acme.entities.Tasks.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordPublishService extends AbstractGuiService<Technician, MaintenanceRecord> {

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
		{
			boolean status;
			status = maintenanceRecord.getStatus().equals(MaintenanceStatus.COMPLETED);

			super.state(status, "status", "technician.maintenance-record.publish.status");
		}

	}

	@Override
	public void perform(final MaintenanceRecord maintenanceRecord) {
		MaintenanceRecord mr = this.repository.findMaintenanceRecordById(maintenanceRecord.getId());
		mr.setMaintenanceMoment(maintenanceRecord.getMaintenanceMoment());
		mr.setStatus(maintenanceRecord.getStatus());
		mr.setNextInspection(maintenanceRecord.getNextInspection());
		mr.setEstimatedCost(maintenanceRecord.getEstimatedCost());
		mr.setNotes(maintenanceRecord.getNotes());
		mr.setDraftMode(false);
		mr.setTechnician(maintenanceRecord.getTechnician());
		mr.setAircraft(maintenanceRecord.getAircraft());

		this.repository.save(mr);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		SelectChoices choices;
		SelectChoices aircraftChoices;
		Dataset dataset;
		Collection<Aircraft> aircrafts;

		choices = SelectChoices.from(MaintenanceStatus.class, maintenanceRecord.getStatus());

		aircrafts = this.repository.findAllAircrafts();
		aircraftChoices = SelectChoices.from(aircrafts, "model", maintenanceRecord.getAircraft());

		Collection<Task> tasksNumber = this.repository.findTasksByMaintenanceRecordId(maintenanceRecord.getId());
		Collection<String> tasks = tasksNumber.stream().map(Task::getDescription).toList();

		dataset = super.unbindObject(maintenanceRecord, "aircraft", "technician.identity.name", "maintenanceMoment", "nextInspection", "status", "estimatedCost", "notes", "draftMode");
		dataset.put("status", choices);
		dataset.put("tasks", tasks);
		dataset.put("aircraft", aircraftChoices.getSelected().getKey());
		dataset.put("aircrafts", aircraftChoices);

		super.getResponse().addData(dataset);
	}
}
