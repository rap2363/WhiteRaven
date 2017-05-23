# Introduction
WhiteRaven is an ongoing project to build an NES emulator written in Java. In addition to emulating the NES environment, WhiteRaven can also support online multiplayer so you can play with friends.

![alt text](http://imgur.com/OGRGdd4.png)
##### Screenshot from Balloon Fight

# Dependencies
The only dependency is Java 8. If you want to build WhiteRaven, get the JDK: http://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html

If you just want to run it using the latest release, get a JRE:
http://www.oracle.com/technetwork/java/javase/downloads/jre8-downloads-2133155.html

# Building WhiteRaven
You can use IntelliJ IDEA + Apache Ant to build WhiteRaven. Load the project into IntelliJ and run:

`ant -buildfile whiteraven.xml`

If this builds successfully, you should have 3 jar files under `out/artifacts`: `WhiteRaven.jar`, `WhiteRavenServer.jar` and `WhiteRavenClient.jar` which correspond to the stand alone emulator, server application, and client application respectively.

|  Main Class | Executable  |
|---|---|
|  `main.java.screen.WhiteRavenLauncher`  | `WhiteRaven.jar`  |
|  `main.java.web.WhiteRavenServer` | `WhiteRavenServer.jar`  |
|  `main.java.web.WhiteRavenClient` | `WhiteRavenClient.jar`  |

# Running the Emulator
You can find the appropriate jar files under the `Releases` tab.

## Stand Alone Application
You can run WhiteRaven as a stand alone java app on your machine via WhiteRaven.jar:

`java -jar WhiteRaven.jar <rom_file>`

## Launching a WhiteRaven Server
If you want to play online, you'll need to launch WhiteRaven on a server via WhiteRavenServer.jar:

`java -jar WhiteRavenServer.jar <rom_file>`

This command will launch a running server listening on port 8888.

Once you've launched your server, clients can connect to it via the WhiteRavenClient.jar:

`java -jar WhiteRavenClient.jar <server_ip> <server_port>`

The first client to connect will automatically be assigned as `FIRST_PLAYER`. The second to connect will be assigned as `SECOND_PLAYER`. Any other clients to connect will be assigned as `VIEWER`, which means they can watch the game, but not participate.

# Future Extensions
- Add mapper support
- Fix bugs
