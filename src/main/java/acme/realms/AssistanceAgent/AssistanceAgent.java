
package acme.realms.AssistanceAgent;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.Valid;

import acme.client.components.basis.AbstractRole;
import acme.client.components.datatypes.Money;
import acme.client.components.mappings.Automapped;
import acme.client.components.validation.Mandatory;
import acme.client.components.validation.Optional;
import acme.client.components.validation.ValidMoment;
import acme.client.components.validation.ValidMoney;
import acme.client.components.validation.ValidUrl;
import acme.constraints.ValidAssistanceAgent;
import acme.constraints.ValidIdentifier;
import acme.constraints.ValidLongText;
import acme.entities.Airlines.Airline;
import lombok.Getter;
import lombok.Setter;

@Entity
@Setter
@Getter
@ValidAssistanceAgent
public class AssistanceAgent extends AbstractRole {

	private static final long	serialVersionUID	= 1L;

	//Attributes

	@Mandatory
	@ValidIdentifier
	@Column(unique = true)
	private String				employeeCode;

	@Mandatory
	@ValidLongText		//It is a List delimitated by comas
	@Automapped
	private String				spokenLanguages;

	@Mandatory
	@ValidMoment(past = true)
	@Temporal(TemporalType.TIMESTAMP)
	private Date				moment;

	@Optional
	@ValidLongText
	@Automapped
	private String				briefBio;

	@Optional
	@ValidMoney
	@Automapped
	private Money				salary;

	@Optional
	@ValidUrl
	@Automapped
	private String				photo;

	// Relationships

	@Mandatory
	@Valid
	@ManyToOne
	private Airline				airline;

}
