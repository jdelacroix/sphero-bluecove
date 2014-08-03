sphero-bluecove
===============

A Java SDK for Orbotix's Sphero using BlueCove.

Supported functionality:

+ Send Sphero command packets synchronously, asynchronously, and/or with or without resetting the inactivity timer.
+ Receive Sphero response to any synchronous (only) command packet.
+ Can set RGB LED color, back LED brightness, and relative heading.

Unsupported functionality:

- Everything else.

Known issues:

+ Synchronous responses are only received and parsed after the next synchronous command.
