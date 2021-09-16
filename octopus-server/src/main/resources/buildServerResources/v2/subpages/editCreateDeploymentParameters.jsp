<%@ include file="/include-internal.jsp" %>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  ~ Copyright 2000-2021 Octopus Deploy Pty. Ltd.
  ~
  ~ Licensed under the Apache License, Version 2.0 (the "License");
  ~ you may not use this file except in compliance with the License.
  ~ You may obtain a copy of the License at
  ~
  ~    http://www.apache.org/licenses/LICENSE-2.0
  ~
  ~ Unless required by applicable law or agreed to in writing, software
  ~ distributed under the License is distributed on an "AS IS" BASIS,
  ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  ~ See the License for the specific language governing permissions and
  ~ limitations under the License.
  --%>

<jsp:useBean id="params" class="octopus.teamcity.server.generic.BuildStepCollection"/>
<jsp:useBean id="keys" class="octopus.teamcity.common.createdeployment.CreateDeploymentPropertyNames"/>
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<l:settingsGroup title="Create Deployment">
    <tr>
        <th>Project name/ID:<l:star/></th>
        <td>
            <props:textProperty name="${keys.projectNameOrIdPropertyName}" className="longField" />
            <span class="error" id="error_${keys.projectNameOrIdPropertyName}"></span>
            <span class="smallNote">The project's name or ID.</span>
        </td>
    </tr>
    <tr>
        <th>Environment:<l:star/></th>
        <td>
            <props:multilineProperty name="${keys.environmentIdsOrNamesPropertyName}" rows="5" cols="55" expanded="true" linkTitle="Enter names or IDs"/>
            <span class="error" id="error_${keys.environmentIdsOrNamesPropertyName}"></span>
            <span class="smallNote">Newline separated environment names or IDs.</span>
        </td>
    </tr>
    <tr>
        <th>Release version:<l:star/></th>
        <td>
            <props:textProperty name="${keys.releaseVersionPropertyName}" />
            <span class="error" id="error_${keys.releaseVersionPropertyName}"></span>
            <span class="smallNote">Release version, eg. 1.0.2-SNAPSHOT</span>
        </td>
    </tr>
</l:settingsGroup>