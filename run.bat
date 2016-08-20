@echo off
title Xena

set bat="./build/install/Xena/bin/Xena.bat"

:loop
if exist %bat% (
    call %bat%
    pause
) else (
    call build.bat
    cls
    title Xena
    goto loop
)