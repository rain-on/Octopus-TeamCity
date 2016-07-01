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
