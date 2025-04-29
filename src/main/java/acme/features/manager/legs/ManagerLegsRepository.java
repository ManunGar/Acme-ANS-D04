
package acme.features.manager.legs;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Legs.Legs;

@Repository
public interface ManagerLegsRepository extends AbstractRepository {

	@Query("select l from Legs l where l.flight.id = :flightId")
	Collection<Legs> findAllByFlightId(@Param("flightId") Integer flightId);

	@Query("select l from Legs l where l.id = :legId")
	Legs findLegById(@Param("legId") Integer legId);

}
