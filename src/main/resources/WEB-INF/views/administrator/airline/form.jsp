<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
		<acme:input-textarea code="administrator.airline.list.label.name" path="name" />
		<acme:input-textarea code="administrator.airline.list.label.iata-code" path="IATAcode"/>
		<acme:input-url code="administrator.airline.list.label.website" path="website"/>
		<acme:input-select code="administrator.airline.list.label.type" path="type" choices="${types}"/>
		<acme:input-moment code="administrator.airline.list.label.foundationMoment" path="foundationMoment"/>
		<acme:input-email code="administrator.airline.list.label.emailAddress" path="emailAddress" />
		<acme:input-textarea code="administrator.airline.list.label.phoneNumber" path="phoneNumber"/>
		
		<jstl:choose>
			<jstl:when test="${acme:anyOf(_command, 'show')}">
				<acme:input-checkbox code="administrator.airline.form.label.confirmation.update" path="confirmation"/>
				<acme:submit code="administrator.airline.form.button.update" action="/administrator/airline/update"/>
			</jstl:when>
			<jstl:when test="${_command == 'create'}">
				<acme:input-checkbox code="administrator.airline.form.label.confirmation.create" path="confirmation"/>	
				<acme:submit code="administrator.airline.form.button.create" action="/administrator/airline/create"/>
			</jstl:when>		
	</jstl:choose>					
</acme:form>