<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<%--
  ~ Copyright 2000-2012 Octopus Deploy Pty. Ltd.
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

<jsp:useBean id="keys" class="octopus.teamcity.common.OctopusConstants" />
<jsp:useBean id="propertiesBean" scope="request" type="jetbrains.buildServer.controllers.BasePropertiesBean"/>

<l:settingsGroup title="Pack">

  <tr>
    <th>Metadata Output path:<l:star/></th>
    <td>
      <props:textProperty name="${keys.metadataOutputPathKey}" className="longField" />
      <span class="error" id="error_${keys.metadataOutputPathKey}"></span>
      <span class="smallNote">
        Directory to write the metadata file to.
      </span>
    </td>
  </tr>

  <tr>
    <th>Commit processing:<l:star/></th>
    <td>
      <props:selectProperty name="${keys.commentParserKey}" multiple="false">
        <props:option value="" selected="${keys.commentParserKey == ''}"></props:option>
        <props:option value="Jira" selected="${keys.commentParserKey == 'Jira'}">Jira</props:option>
      </props:selectProperty>
      <span class="error" id="error_${keys.commentParserKey}"></span>
      <span class="smallNote">
        Process the commit messages looking for work item references and include them in the package
      </span>
    </td>
  </tr>

</l:settingsGroup>
