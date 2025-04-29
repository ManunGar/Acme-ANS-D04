
package acme.entities.Claims;

import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;

import acme.client.components.basis.AbstractEntity;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.ValidEmail;
import acme.client.components.validation.ValidMoment;
import acme.client.helpers.SpringHelper;
import acme.constraints.ValidClaim;
import acme.constraints.ValidLongText;
import acme.entities.Legs.Legs;
import acme.entities.TrackingLogs.TrackingLog;
import acme.realms.AssistanceAgent.AssistanceAgent;
import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@ValidClaim
public class Claim extends AbstractEntity {

	private static final long	serialVersionUID	= 1L;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				registrationMoment;

	@Mandatory
	@ValidEmail
	@Automapped
	private String				passengerEmail;

	@Mandatory
	@ValidLongText
	@Automapped
	private String				description;

	@Mandatory
	@Valid
	@Automapped
	private ClaimTypes			claimType;

	@Mandatory
	@Automapped
	private boolean				draftMode;

	// Relationships

	@Mandatory
	@Valid
	@ManyToOne
	private AssistanceAgent		assistanceAgent;

	@Mandatory
	@Valid
	@ManyToOne
	private Legs				leg;


	@Transient
	public AcceptedIndicator accepted() {
		List<TrackingLog> trackingLogs;
		AcceptedIndicator accepted;

		ClaimRepository repository = SpringHelper.getBean(ClaimRepository.class);

		trackingLogs = repository.findAllTrackingLogsPublishedByClaimId(this.getId());
		accepted = trackingLogs.size() == 0 ? AcceptedIndicator.PENDING : trackingLogs.get(0).getAccepted();

		return accepted;
	}

}
