This plug-in allows TeamCity builds to trigger deployments in Octopus Deploy.

**Please raise and track issues for this project [here](https://github.com/OctopusDeploy/Issues/issues/).**

## Get the plugin

Download the plugin from [the Octopus Deploy downloads page](http://octopusdeploy.com/downloads).

Installation and usage instructions are available in [the Octopus Deploy documentation](http://octopusdeploy.com/documentation/integration/teamcity).

## Building

To build the plugin from code:

 1. Install a [Java JDK 6 u38, 64 bit](http://www.oracle.com/technetwork/java/javase/downloads/java-archive-downloads-javase6-419409.html#jdk-6u38-oth-JPR) (don't use 7 or 8 or newer) from Oracle.
 2. Install [Ant](http://archive.apache.org/dist/ant/binaries/apache-ant-1.8.4-bin.zip) from Apache.
 3. Add the JDK `bin` folder and ant `bin` to your path
 4. Navigate to the source directory, and run `ant`.

The TeamCity plugin will be packaged and added to a `/source/dist` folder.

To edit the code, you'll probably want to install IntelliJ community edition. JetBrains provide [instructions for configuring IntelliJ](http://confluence.jetbrains.com/display/TCD7/Bundled+Development+Package) for TeamCity plugin development.  

## Versions
The version of the addin corresponds to the bundled [Octo.exe](https://github.com/OctopusDeploy/OctopusClients) version.

## Editing and debugging in IntelliJ

1. Install TeamCity locally to `C:\TeamCity`. Allow the service to start for the first time, and add an admin user. Then stop the service so it is not running.
2. Give yourself full permissions to the Teamcity Data folder (usually `C:\ProgramData\JetBrains\TeamCity`). This folder may be hidden.
3. Open the existing IntelliJ project.
4. You will have a `server` run configuration already defined. However, the TeamCity server it references will need to be configured.
  1. Click Run -> Edit Configurations.
  2. Select the Tomcat Server configuration (called `server`).
  3. Click the `Configure...` button next to the `Application Server: TeamCity Tomcat` option.
  4. Set the `Tomcat Home` option to the location of your TeamCity installation (probably `C:\TeamCity`).
  5. Click the `OK` button.
5. Under the `Startup/Connection` tab, set the `Startup script` to `C:\TeamCity\bin\teamcity-server.bat run` and the `Shutdown script` to `C:\TeamCity\bin\teamcity-server.bat stop`. Do this for both the `Run` and `Debug` environments.
6. Ensure that the `Before launch` list includes the step `Run Ant target 'deploy'`.
7. Ignore the `Warning: No artifacts configured` message
8. Click the `OK` button to save your changes.

At this point the you can run TeamCity from IntelliJ. If need be you can run  TeamCity in debug mode and step through the plugin code as it is executed by TeamCity.
