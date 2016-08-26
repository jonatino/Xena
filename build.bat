@echo off
cd "%~dp0"
title Xena Builder
call gradlew installDist
echo.
pause