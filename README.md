sphero-bluecove
===============

A Java SDK for Orbotix's Sphero using BlueCove.

Supported functionality:

+ Send Sphero command packets synchronously, asynchronously, and/or with or without resetting the inactivity timer.
+ Receive response packet from Sphero to any synchronous command packet, and asynchronous packets from data streaming.
+ Can set RGB LED color, back LED brightness, and relative heading.
+ Enable/disable asynchronous data streaming.
+ Detect if Sphero has disconnected using a heartbeat.
+ Parse data in asynchronous packets given the options that were enabled for data streaming.

Unsupported functionality:

- Everything else.

Known issues:

+ Null pointer exception when calling Sphero functions after failed connect.
