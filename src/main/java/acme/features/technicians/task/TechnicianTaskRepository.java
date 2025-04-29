
package acme.features.technicians.task;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.MaintenanceRecords.MaintenanceRecordTask;
import acme.entities.Tasks.Task;
import acme.realms.Technician;

@Repository
public interface TechnicianTaskRepository extends AbstractRepository {

	@Query("SELECT t FROM Task t WHERE t.draftMode = false")
	Collection<Task> findPublishedTasks();

	@Query("SELECT t FROM Task t WHERE t.technician.id = :id")
	Collection<Task> findTasksByTechnicianId(int id);

	@Query("SELECT t FROM Task t WHERE t.id = :id")
	Task findTaskById(int id);

	@Query("SELECT mrt FROM MaintenanceRecordTask mrt WHERE mrt.task.id = :id")
	Collection<MaintenanceRecordTask> findMaintenanceRecordTasksFromTaskId(int id);

	@Query("SELECT t FROM Technician t")
	Collection<Technician> findAllTechnicians();

	@Query("SELECT t FROM Technician t WHERE t.id = :id")
	Technician findTechnicianById(int id);

	@Query("SELECT mrt.task FROM MaintenanceRecordTask mrt WHERE mrt.maintenanceRecord.id = :maintenanceRecordId")
	Collection<Task> findTasksByMaintenanceRecordId(int maintenanceRecordId);

}
