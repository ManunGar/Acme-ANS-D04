
package acme.features.assistanceAgent.TrackingLog;

import org.springframework.beans.factory.annotation.Autowired;

import acme.client.components.models.Dataset;
import acme.client.components.views.SelectChoices;
import acme.client.helpers.MomentHelper;
import acme.client.services.AbstractGuiService;
import acme.client.services.GuiService;
import acme.entities.Claims.AcceptedIndicator;
import acme.entities.Claims.Claim;
import acme.entities.Claims.ClaimTypes;
import acme.entities.TrackingLogs.TrackingLog;
import acme.realms.AssistanceAgent.AssistanceAgent;

@GuiService
public class AssistanceAgentTrackingLogPublishService extends AbstractGuiService<AssistanceAgent, TrackingLog> {

	@Autowired
	private AssistanceAgentTrackingLogRepository repository;


	@Override
	public void authorise() {
		int trackingLogId = super.getRequest().getData("id", int.class);
		int agentId = super.getRequest().getPrincipal().getActiveRealm().getId();
		boolean status = this.repository.isPublishableTrackingLogOwnedByAgent(trackingLogId, agentId);

		if (super.getRequest().hasData("accepted", String.class)) {
			String accepted = super.getRequest().getData("accepted", String.class);

			if (!"0".equals(accepted))
				try {
					ClaimTypes.valueOf(accepted);
				} catch (IllegalArgumentException | NullPointerException e) {
					status = false;
				}
		}

		super.getResponse().setAuthorised(status);
	}

	@Override
	public void load() {
		Claim claim;
		int trackingLogId;
		TrackingLog trackingLog;

		trackingLogId = super.getRequest().getData("id", int.class);
		trackingLog = this.repository.findTrackingLogById(trackingLogId);

		claim = this.repository.findClaimByTrackingLogId(trackingLogId);
		trackingLog.setClaim(claim);

		super.getBuffer().addData(trackingLog);

	}

	@Override
	public void bind(final TrackingLog trackingLog) {

		AcceptedIndicator accepted;
		accepted = super.getRequest().getData("accepted", AcceptedIndicator.class);
		accepted = accepted == null ? AcceptedIndicator.PENDING : accepted;

		super.bindObject(trackingLog, "step", "resolutionPercentage", "resolution");
		trackingLog.setAccepted(accepted);

	}

	@Override
	public void validate(final TrackingLog trackingLog) {
		;
	}

	@Override
	public void perform(final TrackingLog trackingLog) {
		trackingLog.setLastUpdateMoment(MomentHelper.getCurrentMoment());
		trackingLog.setDraftMode(false);

		this.repository.save(trackingLog);
	}

	@Override
	public void unbind(final TrackingLog trackingLog) {
		SelectChoices statusChoices;
		boolean claimDraftMode;
		Dataset dataset;

		statusChoices = SelectChoices.from(AcceptedIndicator.class, trackingLog.getAccepted());

		claimDraftMode = trackingLog.getClaim().isDraftMode();

		dataset = super.unbindObject(trackingLog, "lastUpdateMoment", "step", "resolutionPercentage", "accepted", "draftMode", "resolution", "createdMoment", "secondTrackingLog");
		dataset.put("claim", trackingLog.getClaim().getDescription());
		dataset.put("status", statusChoices);
		dataset.put("claimDraftMode", claimDraftMode);
		dataset.put("secondTrackingLogReadOnly", true);

		super.getResponse().addData(dataset);

	}

}
