<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<jsp:useBean id="keys" class="octopus.teamcity.common.OctopusConstants"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<div class="parameter">
    Run OctoPack:
    <strong><props:displayValue name="${keys.runOctoPack}" emptyValue="not specified"/></strong>
</div>

<div class="parameter">
    OctoPack package version:
    <strong><props:displayValue name="${keys.octoPackPackageVersion}" emptyValue="not specified"/></strong>
</div>
