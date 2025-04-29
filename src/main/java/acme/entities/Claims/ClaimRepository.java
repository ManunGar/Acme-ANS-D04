
package acme.entities.Claims;

import java.util.List;

import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import acme.client.repositories.AbstractRepository;
import acme.entities.TrackingLogs.TrackingLog;

public interface ClaimRepository extends AbstractRepository {

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId order by t.resolutionPercentage desc,t.secondTrackingLog desc, t.createdMoment desc")
	List<TrackingLog> findAllByClaimId(@Param("claimId") Integer claimId);

	@Query("SELECT t FROM TrackingLog t WHERE t.claim.id = :claimId AND t.draftMode = false order by t.resolutionPercentage desc,t.secondTrackingLog desc, t.createdMoment desc")
	List<TrackingLog> findAllTrackingLogsPublishedByClaimId(@Param("claimId") Integer claimId);

}
