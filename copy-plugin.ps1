$TeamCityData = "C:\ProgramData\JetBrains\TeamCity"
Copy-Item ".\octopus-distribution\target\Octopus.TeamCity.zip" "$TeamCityData\plugins\Octopus.TeamCity.zip" -Force