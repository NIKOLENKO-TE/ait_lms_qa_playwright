@echo off
cd /d "%~dp0..\"
echo //////////////////////////////////////////////////////////////////////////////////////////////////////
echo 			Start allcom_qa tests in Chrome [ABOUTUS, HEADLESS MODE]
echo //////////////////////////////////////////////////////////////////////////////////////////////////////
call gradlew clean login_users -PbrowserType=chrome -PheadlessMode=true
pause