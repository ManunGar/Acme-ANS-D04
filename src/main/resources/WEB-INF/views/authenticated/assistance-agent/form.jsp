<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
		<acme:input-textbox code="authenticated.assistanceAgent.list.label.employeeCode" path="employeeCode"/>
		<acme:input-textbox code="authenticated.assistanceAgent.list.label.spokenLanguages" path="spokenLanguages"/>
		<acme:input-money code="authenticated.assistanceAgent.list.label.moment" path="moment"/>
		<acme:input-textbox code="authenticated.assistanceAgent.list.label.briefBio" path="briefBio"/>
		<acme:input-money code="authenticated.assistanceAgent.list.label.salary" path="salary" readonly="true"/>
		<acme:input-url code="authenticated.assistanceAgent.list.label.photo" path="photo" />
		<acme:input-select code="authenticated.assistanceAgent.list.label.airline" path="airline" choices="airlines"/>
		
	<jstl:if test="${_command == 'create'}">	
		<acme:submit code="authenticated.assistanceAgent.form.button.create" action="/authenticated/customer/create"/>
	</jstl:if>
	<jstl:if test="${_command == 'update'}">
		<acme:submit code="authenticated.consumer.form.button.update" action="/authenticated/customer/update"/>
	</jstl:if>
</acme:form>