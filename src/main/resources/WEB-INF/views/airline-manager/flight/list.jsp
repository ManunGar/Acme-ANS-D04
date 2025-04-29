<%@page%>

<%@taglib prefix="jstl" uri="http://java.sun.com/jsp/jstl/core"%>
<%@taglib prefix="acme" uri="http://acme-framework.org/"%>

<acme:list>
	<acme:list-column code="manager.flight.list.label.description" path="description" width="80%"/>
	<acme:list-column code="manager.flight.list.label.cost" path="cost" width="20%"/>
</acme:list>

<acme:button code="manager.flight.list.button.create" action="/airline-manager/flight/create"/>