@REM ----------------------------------------------------------------------------
@REM Maven Wrapper Startup Script for Algorithm Explorer (Group 13)
@REM Automatically resolves JAVA_HOME and downloads Apache Maven if not present.
@REM ----------------------------------------------------------------------------

@Echo Off
@setlocal

if "%JAVA_HOME%"=="" (
  for /f "tokens=2 delims==" %%I in ('java -XshowSettings:properties -version 2^>^&1 ^| findstr "java.home"') do (
    for /f "tokens=* delims= " %%J in ("%%I") do set "JAVA_HOME=%%J"
  )
)

set MAVEN_VERSION=3.9.9
set M2_WRAPPER_HOME=%USERPROFILE%\.m2\wrapper\dists\apache-maven-%MAVEN_VERSION%
set MVN_CMD=%M2_WRAPPER_HOME%\apache-maven-%MAVEN_VERSION%\bin\mvn.cmd

if not exist "%MVN_CMD%" (
  echo Downloading Apache Maven %MAVEN_VERSION%...
  powershell -NoProfile -ExecutionPolicy Bypass -Command ^
    "$ErrorActionPreference = 'Stop'; " ^
    "[Net.ServicePointManager]::SecurityProtocol = [Net.SecurityProtocolType]::Tls12; " ^
    "New-Item -ItemType Directory -Force -Path '%M2_WRAPPER_HOME%' | Out-Null; " ^
    "$zip = '%M2_WRAPPER_HOME%\maven.zip'; " ^
    "Invoke-WebRequest -Uri 'https://repo.maven.apache.org/maven2/org/apache/maven/apache-maven/%MAVEN_VERSION%/apache-maven-%MAVEN_VERSION%-bin.zip' -OutFile $zip; " ^
    "Expand-Archive -Path $zip -DestinationPath '%M2_WRAPPER_HOME%' -Force; " ^
    "Remove-Item $zip -Force"
)

call "%MVN_CMD%" %*
exit /b %ERRORLEVEL%
