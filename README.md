[![Build Status](https://travis-ci.org/Jonatino/Xena.svg?branch=master)](https://travis-ci.org/Jonatino/Xena)
[![Release](https://jitpack.io/v/Jonatino/Xena.svg)](https://jitpack.io/#Jonatino/Xena)
![license](https://img.shields.io/github/license/Jonatino/Xena.svg)

# Xena.
Xena is a CS:GO (Counter Strike Global Offensive) cheat made from scratch written in Java. Xena is built from the ground up with garbage-free, high performance, low resource usage programming practices in mind. Xena works thanks to the power on JNA (https://github.com/java-native-access/jna) to provide very easy direct mapping to native classes.

### Instructions
Before doing anything, make sure you have Java Development Kit (JDK) 8 or later installed. This is NOT the same thing as the regular Java Runtime (JRE), and you MUST install the JDK in order to build Xena, as well as any other Java software.

1. Download zip here: https://codeload.github.com/Jonatino/Xena/zip/master
2. Extract the contents from the archive to any folder (to your desktop, for example)
3. Make sure you have Java Development Kit 8 or later installed
4. Double click on the "build" (build.bat) script and wait for it to complete
5. Click run.bat and you're good to go!

### Dependencies
- [JMM](https://github.com/Jonatino/Java-Memory-Manipulation) for our high-level cross-platform interfacing with processes
- [Java Native Access (JNA)](https://github.com/java-native-access/jna) as the backbone for interfacing with native libraries

### So... what's to come?
Xena is more of a side project I seem to fall back into every couple of months. While I 'do' have plans for it, I can not give any ETA. So without further ado, here are some features you can look forward to (possibly contribute yourself <3)
- Cross-Platform (Note: the cheat it's self is cross platform, but I need the patterns for offsets for the Offset Scanner)
- Some type of configuration tool to change esp color/aim settings etc.

### Authors and Contributors
@Jonatino - Started this project with @Jire, made my cross-platform memory editing library, also made the offset/netvar scanner that Xena uses

@Jire - We both have worked on and maintained another CS:GO cheat by the name of 'Abendigo'

