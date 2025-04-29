
package acme.features.technicians.maintenanceRecordTask;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.MaintenanceRecords.MaintenanceRecord;
import acme.entities.MaintenanceRecords.MaintenanceRecordTask;
import acme.entities.Tasks.Task;
import acme.features.technicians.maintenanceRecord.TechnicianMaintenanceRecordRepository;
import acme.features.technicians.task.TechnicianTaskRepository;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordTaskCreateService extends AbstractGuiService<Technician, MaintenanceRecordTask> {

	// Internal state ---------------------------------------------------------

	@Autowired
	private TechnicianMaintenanceRecordTaskRepository	repository;

	@Autowired
	private TechnicianMaintenanceRecordRepository		maintenanceRecordRepository;

	@Autowired
	private TechnicianTaskRepository					taskRepository;

	// AbstractGuiService interface -------------------------------------------


	@Override
	public void authorise() {
		boolean isTechnician = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		super.getResponse().setAuthorised(isTechnician);
	}

	@Override
	public void load() {
		MaintenanceRecordTask maintenanceRecordTask;

		maintenanceRecordTask = new MaintenanceRecordTask();

		super.getBuffer().addData(maintenanceRecordTask);
	}

	@Override
	public void bind(final MaintenanceRecordTask maintenanceRecordTask) {

		super.bindObject(maintenanceRecordTask, "task", "maintenanceRecord");

	}

	@Override
	public void validate(final MaintenanceRecordTask maintenanceRecordTask) {

		;
	}

	@Override
	public void perform(final MaintenanceRecordTask maintenanceRecordTask) {
		this.repository.save(maintenanceRecordTask);
	}

	@Override
	public void unbind(final MaintenanceRecordTask maintenanceRecordTask) {
		Dataset dataset;
		SelectChoices taskChoices;
		SelectChoices maintenanceRecordChoices;

		int technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		Collection<MaintenanceRecord> maintenanceRecords = this.maintenanceRecordRepository.findMaintenanceRecordsByTechnicianId(technicianId);
		Collection<Task> tasks = this.taskRepository.findPublishedTasks();
		maintenanceRecordChoices = SelectChoices.from(maintenanceRecords, "id", maintenanceRecordTask.getMaintenanceRecord());
		taskChoices = SelectChoices.from(tasks, "description", maintenanceRecordTask.getTask());

		dataset = super.unbindObject(maintenanceRecordTask);
		dataset.put("maintenanceRecord", maintenanceRecordChoices.getSelected().getKey());
		dataset.put("maintenanceRecords", maintenanceRecordChoices);
		dataset.put("task", taskChoices.getSelected().getKey());
		dataset.put("tasks", taskChoices);

		super.getResponse().addData(dataset);
	}
}
