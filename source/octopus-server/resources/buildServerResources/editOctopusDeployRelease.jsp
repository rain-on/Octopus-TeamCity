<%@ include file="/include-internal.jsp"%>
<%@ taglib prefix="props" tagdir="/WEB-INF/tags/props" %>
<%@ taglib prefix="forms" tagdir="/WEB-INF/tags/forms" %>
<%@ taglib prefix="l" tagdir="/WEB-INF/tags/layout" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

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
    <th>Octopus version:<l:star/></th>
    <td>
        <props:selectProperty name="${keys.octopusVersion}" multiple="false">
            <c:set var="selected" value="false"/>
            <c:forEach var="version" items="${keys.octopusVersions}">
                <c:set var="selected" value="false"/>
                <c:if test="${selectedOctopusVersion == version}">
                    <c:set var="selected" value="true"/>
                </c:if>
                <props:option value="${version}"
                              selected="${selected}"><c:out value="${version}"/></props:option>
            </c:forEach>
        </props:selectProperty>

        <span class="error" id="error_${keys.octopusVersion}"></span>
        <span class="smallNote">Which version of the Octopus Deploy server are you using?</span>
    </td>
</tr>
</l:settingsGroup>

<l:settingsGroup title="Deployment">
    <tr>
      <th>Project:<l:star/></th>
      <td>
        <props:textProperty name="${keys.projectNameKey}" className="longField"/>
        <span class="error" id="error_${keys.projectNameKey}"></span>
        <span class="smallNote">Enter the name of the Octopus project to deploy</span>
      </td>
    </tr>
    <tr>
      <th>Release number:<l:star/></th>
      <td>
        <props:textProperty name="${keys.releaseNumberKey}" className="longField"/>
        <span class="error" id="error_${keys.releaseNumberKey}"></span>
        <span class="smallNote">The number of the release to deploy, e.g., <code>latest</code>, <code>%octo.releaseNumber%</code> or <code>%build.number%</code>.</span>
      </td>
    </tr>
    <tr>
      <th>Deploy to:<l:star/></th>
      <td>
        <props:textProperty name="${keys.deployToKey}" className="longField"/>
        <span class="error" id="error_${keys.deployToKey}"></span>
        <span class="smallNote">Comma separated list of environments to deploy to. Leave empty to create a release without deploying it.</span>
      </td>
    </tr>
    <tr class="advancedSetting">
        <th><label for="${keys.tenantsKey}">Deploy for tenant:</label></th>
        <td>
            <props:textProperty name="${keys.tenantsKey}" className="longField"/>
            <span class="error" id="error_${keys.tenantsKey}"></span>
            <span class="smallNote">Comma separated list of tenants to deploy to. Wildcard '*' will deploy to all tenants currently able to deploy to the above provided environment.
            <br /> Note that when supplying tenant filters then only one environment may be provided above.</span>
        </td>
    </tr>
    <tr class="advancedSetting">
        <th><label for="${keys.tenantTagsKey}">Deploy for tenant tags:</label></th>
        <td>
            <props:textProperty name="${keys.tenantTagsKey}" className="longField"/>
            <span class="error" id="error_${keys.tenantTagsKey}"></span>
            <span class="smallNote">Comma separated list of tenant tags to deploy to.
            <br /> Note that when supplying tag filters then only one environment may be provided above.</span>
        </td>
    </tr>
    <tr>
        <th>Show deployment progress:</th>
        <td>
            <props:checkboxProperty name="${keys.waitForDeployments}" />
            <span class="error" id="error_${keys.waitForDeployments}"></span>
            <span class="smallNote">If checked, the build process will only succeed if the deployment is successful.</span>
        </td>
    </tr>
</l:settingsGroup>


<l:settingsGroup title="Advanced">
<tr>
  <th>Additional command line arguments:</th>
  <td>
    <props:textProperty name="${keys.commandLineArgumentsKey}" className="longField"/>
    <span class="error" id="error_${keys.commandLineArgumentsKey}"></span>
    <span class="smallNote">Additional arguments to be passed to <a href="https://github.com/OctopusDeploy/Octopus-Tools">Octo.exe</a></span>
  </td>
</tr>
</l:settingsGroup>