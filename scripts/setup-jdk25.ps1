$ErrorActionPreference = "Stop"

$archivePath = Join-Path $env:USERPROFILE "Downloads\OpenJDK25U-jdk_x64_windows_hotspot_25.0.3_9.zip"
$installRoot = Join-Path $env:USERPROFILE ".jdks"
$jdkHome = Join-Path $installRoot "jdk-25.0.3+9"
$repoRoot = Split-Path -Parent $PSScriptRoot

if (-not (Test-Path $archivePath)) {
    throw "No encuentro el ZIP del JDK 25 en: $archivePath"
}

if (-not (Test-Path (Join-Path $jdkHome "bin\javac.exe"))) {
    New-Item -ItemType Directory -Path $installRoot -Force | Out-Null
    Expand-Archive -Path $archivePath -DestinationPath $installRoot -Force
}

$env:JAVA_HOME = $jdkHome
$env:Path = "$env:JAVA_HOME\bin;$env:Path"

Write-Host "JAVA_HOME=$env:JAVA_HOME"
java -version
javac -version
& (Join-Path $repoRoot "mvnw.cmd") -version
