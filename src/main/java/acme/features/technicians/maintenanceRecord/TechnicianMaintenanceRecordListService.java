
package acme.features.technicians.maintenanceRecord;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.MaintenanceRecords.MaintenanceRecord;
import acme.realms.Technician;

@GuiService
public class TechnicianMaintenanceRecordListService extends AbstractGuiService<Technician, MaintenanceRecord> {

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
		Collection<MaintenanceRecord> maintenanceRecords;
		int technicianId;

		technicianId = super.getRequest().getPrincipal().getActiveRealm().getId();
		maintenanceRecords = this.repository.findMaintenanceRecordsByTechnicianId(technicianId);

		super.getBuffer().addData(maintenanceRecords);
	}

	@Override
	public void unbind(final MaintenanceRecord maintenanceRecord) {
		Dataset dataset;

		dataset = super.unbindObject(maintenanceRecord, "aircraft.model", "maintenanceMoment", "nextInspection", "status");
		super.addPayload(dataset, maintenanceRecord, "estimatedCost", "technician.identity.name");

		super.getResponse().addData(dataset);
	}
}
