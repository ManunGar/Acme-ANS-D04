
package acme.features.technicians.task;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Tasks.Task;
import acme.realms.Technician;

@GuiService
public class TechnicianTaskListMaintenanceRecordService extends AbstractGuiService<Technician, Task> {

	// Internal state ------------------------------------------------------------

	@Autowired
	private TechnicianTaskRepository repository;

	// AbstractGuiService interface ----------------------------------------------


	@Override
	public void authorise() {
		boolean status;
		int technicianId;
		Collection<Task> tasks;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getUserAccount().getId();
		tasks = this.repository.findTasksByTechnicianId(technicianId);
		status = tasks.stream().allMatch(mr -> mr.getTechnician().getUserAccount().getId() == technicianId) && super.getRequest().getPrincipal().hasRealmOfType(Technician.class);

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Collection<Task> task;
		int maintenanceRecordId;

		maintenanceRecordId = this.getRequest().getData("maintenanceRecordId", int.class);
		task = this.repository.findTasksByMaintenanceRecordId(maintenanceRecordId);

		super.getBuffer().addData(task);
	}

	@Override
	public void unbind(final Task task) {
		Dataset dataset;

		dataset = super.unbindObject(task, "type", "description", "priority");
		super.addPayload(dataset, task, "estimatedDuration", "technician.identity.name");

		super.getResponse().addData(dataset);
	}
}
