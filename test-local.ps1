
$ErrorActionPreference = "Stop";

if (-NOT ([Security.Principal.WindowsPrincipal][Security.Principal.WindowsIdentity]::GetCurrent()).IsInRole([Security.Principal.WindowsBuiltInRole] "Administrator"))
{    
    throw "This script needs to be run As Admin"
}

# If you followed the instructions in Read.me then this script should just work.
# If you need to run the script against a different version of the JDK then change the path below:  
$JdkHome = "C:\Program Files\Java\jdk1.6.0_38"

#
# TeamCity directories. First is where you installed TeamCity to. Second is the TeamCity data directory.
$TeamCityDistribution = "C:\TeamCity"
$TeamCityData = "C:\ProgramData\JetBrains\TeamCity"


Stop-Service TeamCity
Stop-Service TCBuildAgent

pushd .\source

& ant "-Dteamcity.distribution=$TeamCityDistribution" "-Djdk.home.1.6=$JdkHome"
Copy-Item ".\dist\Octopus.TeamCity.zip" "$TeamCityData\plugins\Octopus.TeamCity.zip" -Force

popd

Start-Service TeamCity
Start-Service TCBuildAgent

