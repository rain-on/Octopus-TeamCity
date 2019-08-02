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

<l:settingsGroup title="Octopus Connection">
  <tr>
    <th>Octopus URL:<l:star/></th>
    <td>
      <props:textProperty name="${keys.serverKey}" className="longField"/>
      <span class="error" id="error_${keys.serverKey}"></span>
      <span class="smallNote">Specify Octopus web portal URL</span>
    </td>
  </tr>
  <tr>
    <th>API key:<l:star/></th>
    <td>
      <props:passwordProperty name="${keys.apiKey}" className="longField"/>
      <span class="error" id="error_${keys.apiKey}"></span>
      <span class="smallNote">Specify Octopus API key. You can get this from your user page in the Octopus web portal.</span>
    </td>
  </tr>
  <tr>
    <th>Space name:</th>
    <td>
      <props:textProperty name="${keys.spaceName}" className="longField"/>
      <span class="error" id="error_${keys.spaceName}"></span>
      <span class="smallNote">Specify Octopus Space name. Leave blank to use the default space.</span>
    </td>
  </tr>
</l:settingsGroup>

<l:settingsGroup title="Metadata">

  <tr>
    <th>Package ID:<l:star/></th>
    <td>
      <props:textProperty name="${keys.packageIdKey}" className="longField" />
      <span class="error" id="error_${keys.packageIdKey}"></span>
      <span class="smallNote">
        The package's identifier.
      </span>
    </td>
  </tr>
  <tr>
    <th>Package version:<l:star/></th>
    <td>
      <props:textProperty name="${keys.packageVersionKey}" className="longField" />
      <span class="error" id="error_${keys.packageVersionKey}"></span>
      <span class="smallNote">
        The package's version.
      </span>
    </td>
  </tr>

  <tr>
    <th>Commit processing:<l:star/></th>
    <td>
      <props:selectProperty name="${keys.commentParserKey}" multiple="false">
        <props:option value=""></props:option>
        <props:option value="Jira">Jira</props:option>
        <props:option value="GitHub">GitHub</props:option>
      </props:selectProperty>
      <span class="error" id="error_${keys.commentParserKey}"></span>
      <span class="smallNote">
        Process the commit messages looking for work item references and include them in the metadata
      </span>
    </td>
  </tr>

  <tr class="advancedSetting">
    <th>Overwrite Mode:</th>
    <td>
      <props:selectProperty name="${keys.forcePushKey}">
        <props:option value="false">Fail If Exists</props:option>
        <props:option value="true">Overwrite Existing</props:option>
        <props:option value="IgnoreIfExists">Ignore If Exists</props:option>
      </props:selectProperty>
      <span class="error" id="error_${keys.forcePushKey}"></span>
      <span class="smallNote">Normally, if the same package metadata already exists on the server, the server will reject the push. This is a good practice as it ensures metadata isn't accidentally overwritten. Enable this setting to override this behavior.</span>
    </td>
  </tr>
  <tr>
    <th>Verbose logging:</th>
    <td>
      <props:checkboxProperty name="${keys.verboseLoggingKey}" />
      <span class="error" id="error_${keys.verboseLoggingKey}"></span>
      <span class="smallNote">Set this to get more verbose logging.</span>
    </td>
  </tr>

</l:settingsGroup>
