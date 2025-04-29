
package acme.features.technicians.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.MaintenanceRecords.MaintenanceRecordTask;
import acme.entities.Tasks.Task;
import acme.entities.Tasks.TaskType;
import acme.features.technicians.maintenanceRecordTask.TechnicianMaintenanceRecordTaskRepository;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskDeleteService extends AbstractGuiService<Technician, Task> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository					repository;

	@Autowired
	private TechnicianMaintenanceRecordTaskRepository	mrtRepository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean isTechnician = super.getRequest().getPrincipal().hasRealmOfType(Technician.class);
		super.getResponse().setAuthorised(isTechnician);
	}

	@Override
	public void load() {
		Task task;
		int id;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);

		super.getBuffer().addData(task);

	}

	@Override
	public void bind(final Task task) {

		super.bindObject(task, "type", "description", "priority", "estimatedDuration");
	}

	@Override
	public void validate(final Task task) {
		;
	}

	@Override
	public void perform(final Task task) {

		for (MaintenanceRecordTask mrt : this.mrtRepository.findMaintenanceRecordTaskByTaskId(task.getId()))
			this.mrtRepository.delete(mrt);

		this.repository.delete(task);
	}

	@Override
	public void unbind(final Task task) {
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(TaskType.class, task.getType());

		dataset = super.unbindObject(task, "technician.identity.name", "type", "description", "priority", "estimatedDuration");
		dataset.put("type", choices);

		super.getResponse().addData(dataset);
	}
}
