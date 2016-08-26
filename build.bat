@echo off
cd /d "%~dp0"
title Xena Builder
call gradlew installDist
echo.
pause