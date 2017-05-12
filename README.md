This plug-in allows TeamCity builds to trigger deployments in Octopus Deploy. 

**Please raise and track issues for this project [here](https://github.com/OctopusDeploy/Issues/issues/).**

## Get the plugin

Download the plugin from [the Octopus Deploy downloads page](http://octopusdeploy.com/downloads).

Installation and usage instructions are available in [the Octopus Deploy documentation](http://octopusdeploy.com/documentation/integration/teamcity). 

## Building

To build the plugin from code:

 1. Install the latest version of the JDK
 2. Install [Maven](https://maven.apache.org/download.cgi)
 3. Install TeamCity
 4. Run `mvn -Dteamcity.distribution=C:\TeamCity clean package` (set the TeamCity
    directory to the location where you extracted or installed TeamCity locally)
 5. The plugin is available at `octopus-distribution/target/Octopus.TeamCity.zip`