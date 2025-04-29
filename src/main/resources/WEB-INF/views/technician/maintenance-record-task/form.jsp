<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:form>
		<acme:input-select code="technician.maintenance-record-task.list.label.maintenance-record" path="maintenanceRecord" choices="${maintenanceRecords}"/>
		<acme:input-select code="technician.maintenance-record-task.list.label.task" path="task" choices="${tasks}"/>
		
	<jstl:choose>
			
			<jstl:when test="${_command == 'create'}">
				<acme:submit code="technician.maintenance-record-task.form.button.create" action="/technician/maintenance-record-task/create"/>
			</jstl:when>
					
	</jstl:choose>	
		
</acme:form>