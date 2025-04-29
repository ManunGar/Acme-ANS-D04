
package acme.features.technicians.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Tasks.Task;
import acme.entities.Tasks.TaskType;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskShowService extends AbstractGuiService<Technician, Task> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int technicianId;
		Collection<Task> task;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();
		task = this.repository.findTasksByTechnicianId(technicianId);
		status = task.stream().allMatch(mr -> mr.getTechnician().getUserAccount().getId() == technicianId) && super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		int id;
		Task task;

		id = super.getRequest().getData("id", int.class);
		task = this.repository.findTaskById(id);

		super.getBuffer().addData(task);
	}

	@Override
	public void unbind(final Task Task) {
		SelectChoices choices;
		SelectChoices technicianChoices;
		Dataset dataset;
		Collection<Technician> technicians;

		choices = SelectChoices.from(TaskType.class, Task.getType());

		technicians = this.repository.findAllTechnicians();
		technicianChoices = SelectChoices.from(technicians, "licenseNumber", Task.getTechnician());

		dataset = super.unbindObject(Task, "technician.identity.name", "type", "description", "priority", "estimatedDuration", "draftMode");
		dataset.put("type", choices);
		dataset.put("technician", technicianChoices.getSelected().getKey());
		dataset.put("technicians", technicianChoices);

		super.getResponse().addData(dataset);
	}
}
