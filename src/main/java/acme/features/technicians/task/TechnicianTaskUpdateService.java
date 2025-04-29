
package acme.features.technicians.task;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Tasks.Task;
import acme.entities.Tasks.TaskType;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskUpdateService extends AbstractGuiService<Technician, Task> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		int id;
		Task task;
		int technicianId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);
		boolean status = task.getTechnician().getUserAccount().getId() == technicianId && super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		super.getResponse().setAuthorised(status);
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

		Task t = this.repository.findTaskById(task.getId());
		t.setType(task.getType());
		t.setDescription(task.getDescription());
		t.setPriority(task.getPriority());
		t.setEstimatedDuration(task.getEstimatedDuration());
		t.setDraftMode(task.isDraftMode());
		t.setTechnician(task.getTechnician());

		this.repository.save(t);
	}

	@Override
	public void unbind(final Task task) {
		SelectChoices choices;
		Dataset dataset;

		choices = SelectChoices.from(TaskType.class, task.getType());

		dataset = super.unbindObject(task, "technician.identity.name", "type", "description", "priority", "estimatedDuration", "draftMode");
		dataset.put("type", choices);

		super.getResponse().addData(dataset);
	}
}
