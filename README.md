# RabbitMQ Instant Messenger
RabbitMQ Instant Messenger is a Java-based IRC-style CLI program that utilizes RabbitMQ's Stream Queues with the AMPQ 1.0 Protocol. This program supports the joining of multiple chats and switching between them. In addition, the user will always have a complete call back of the entire chat so context of a conversation will never be lost! 

Instant Messenger also features several chat commands: /nick, /leave, /join, /switch, /create, /list, /help, and /exit. Please see CommandsDoc.txt for more information. 

## Development Notes
- Due to the lack of experience by the developer, a binary distribution has not been created and will require manually packaging the maven project and executing it. This is will be fixed in the future to allow a better user experience.
- The IP the consumer and producer utilize is currently hard-coded and will require changing if your RabbitMQ Server has a different IP.
- This is not a completed product and is primarily in its alpha state
- THE CLIENT IS LINUX ONLY, this will change in future releases

# The Server
The server is your typical RabbitMQ server with the Stream plugin enabled and port <???> opened. If you are already familar with running a RabbitMQ server then you can safely skip this section. 
## Server Requirements
- RabbitMQ with Stream plugin enabled

## Running the server
<<Refer to rabbit docs>>

---
# The Client
The client is where the actual chatting happens and primarily where the user is going to interact. There are two processes started in order for the client to be fully launched: the sender and the receiver. The sender window is where the user will input their messages to send and the receiver window is where they will see incoming chats and past conversations that have taken place in the chat. Whenever the user switches between chats, the receiver window will be cleared in order to reduce clutter.  
## Client Requirements
- Maven 
- Java 21

## Running the client
*IMPORTANT NOTE: SENDER MUST BE INITIALIZED BEFORE THE RECEIVER. Receiver will not launch if Sender is not active*

cd to dir with pom.xml file

`mvn package`

`mvn exec:java -Dexec.args="send"`

Open another terminal

`mvn exec:java -Dexec.args="recv"`


---
Developed by: Josh Stapera

