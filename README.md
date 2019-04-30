This plug-in allows TeamCity builds to trigger deployments in Octopus Deploy. 

**Please raise and track issues for this project [here](https://github.com/OctopusDeploy/Issues/issues/).**

## Get the plugin

Download the plugin from [the Octopus Deploy downloads page](http://octopusdeploy.com/downloads) or the [JetBrains plugins downloads](<https://plugins.jetbrains.com/plugin/9038-octopus-deploy>).

Installation and usage instructions are available in [the Octopus Deploy documentation](http://octopusdeploy.com/documentation/integration/teamcity). 

## Building

To build the plugin from code:

 1. Install the latest version of the JDK
 2. Install TeamCity
 4. Run `mvnw -Dteamcity.distribution=C:\TeamCity clean package` (set the TeamCity
    directory to the location where you extracted or installed TeamCity locally). 
    The `mvnw` script will download Maven for you if it is not already installed.
 5. The plugin is available at `octopus-distribution/target/Octopus.TeamCity.zip`

## Editing and debugging in IntelliJ

1. Install TeamCity locally to `C:\TeamCity`. Allow the service to start for the first time, and add an 
   admin user. Then stop the service so it is not running.
2. Give yourself full permissions to the Teamcity Data folder (usually `C:\ProgramData\JetBrains\TeamCity`). 
   This folder may be hidden.
3. Import the Maven project into IntelliJ.
4. Create a Tomcat Configuration:
    1. Click Run -> Edit Configurations.
    2. Click the plus button and select Tomcat -> Local
       ![Run Configuration - Startup](https://raw.githubusercontent.com/OctopusDeploy/Octopus-TeamCity/master/TomcatLocal.PNG)
    3. Click the `Configure...` button next to the `Application Server:` option.
    4. Set the `Tomcat Home` option to the location of your TeamCity installation (probably `C:\TeamCity`).
        ![Run Configuration - Startup](https://raw.githubusercontent.com/OctopusDeploy/Octopus-TeamCity/master/TomcatServer.PNG)
    5. Click the `OK` button.
6. Under the `Startup/Connection` tab, set the `Startup script` to `C:\TeamCity\bin\teamcity-server.bat run` and 
   the `Shutdown script` to `C:\TeamCity\bin\teamcity-server.bat stop`. Do this for both the `Run` and `Debug` 
   environments.
5. Ensure that the `Before launch` list includes the step `Run Maven goal 'Octopus Deploy TeamCity plugin: package'`.
6. Run the `copy-plugin.cmd` script as an external tool after the Maven goal. This will copy the plugin to TeamCity.
6. Ignore the `Warning: No artifacts configured` message.
   ![Run Configuration - Startup](https://raw.githubusercontent.com/OctopusDeploy/Octopus-TeamCity/master/TomcatStartup.PNG)
7. Click the `OK` button to save your changes.

At this point the you can run TeamCity from IntelliJ. If need be you can run  TeamCity in debug mode and step 
through the plugin code as it is executed by TeamCity.