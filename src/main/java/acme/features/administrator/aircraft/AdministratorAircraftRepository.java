
package acme.features.administrator.aircraft;

import java.util.Collection;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import acme.client.repositories.AbstractRepository;
import acme.entities.Aircrafts.Aircraft;
import acme.entities.Airlines.Airline;

@Repository
public interface AdministratorAircraftRepository extends AbstractRepository {

	@Query("SELECT a FROM Aircraft a")
	Collection<Aircraft> findAllAircraft();

	@Query("SELECT a.id FROM Aircraft a")
	Collection<Integer> findAllAircraftId();

	@Query("SELECT a FROM Aircraft a WHERE a.id = :id")
	Aircraft findAircraftById(int id);

	@Query("SELECT a FROM Aircraft a WHERE a.registrationNumber = :registrationNumber")
	Aircraft findAircraftByRegistrationNumber(@Param("registrationNumber") String registrationNumber);

	@Query("SELECT a FROM Airline a ")
	Collection<Airline> findAllAirline();

	@Query("SELECT a FROM Airline a WHERE a.id = :id")
	Airline findAirlineById(@Param("id") int id);
}
