# RabbitMQ Instant Messenger
RabbitMQ Instant Messenger is a Java-based IRC-style CLI program that utilizes RabbitMQ's Stream Queues with the AMPQ 1.0 Protocol. This program supports the joining of multiple chats and switching between them. In addition, the user will always have a complete call back of the entire chat so context of a conversation will never be lost! 

Instant Messenger also features several chat commands: /nick, /leave, /join, /switch, /create, /list, /help, and /exit. Please see CommandsDoc.txt for more information. 

## Development Notes
- Due to the lack of experience with maven, a binary distribution has not been created and will require manually packaging the maven project and executing it. This is will be fixed in the future to allow a better user experience.
- The IP the consumer and producer utilize is currently hard-coded and will require changing if your RabbitMQ Server has a different IP.
- This is not a completed product and is primarily in its alpha state
- The current setup of the clients is unsafe for a prod implementation, this will be fixed in the future

# The Server
The server is your typical RabbitMQ server with the Stream plugin enabled and port 5552 opened. If you are already familar with running a RabbitMQ server then you can safely skip this section.

NOTE: The firewall may need to be configured in order to allow clients to communicate with server 
## Server Requirements
- RabbitMQ with Stream plugin enabled

## Running the server
1. Install RabbitMQ server according to the [install documentation](https://www.rabbitmq.com/docs/download) 
2. Enable Stream plugin via `rabbitmq-plugins enable rabbitmq_stream.` or `rabbitmq-plugins.bat enable rabbitmq_stream.`
If you have issues with this step see the [stream documentation](https://rabbitmq.github.io/rabbitmq-stream-java-client/stable/htmlsingle/#with-a-rabbitmq-package-running-on-the-host)
3. Start RabbitMQ server via `rabbitmqctl start` or `rabbitmqctl.bat start_app`
4. Go to `rabbitmq.conf` location and add `loopback_users = none`. If the file does not exist in the labeled location, you will have to create it. See [RabbitMQ Documentation](https://www.rabbitmq.com/docs/configure#config-location) for details on where it is located

Adding this line to the config file allows clients to connect to the server without having a proper login. (This is very unsafe, however ease of use for our purposes)

---
# The Client
The client is where the actual chatting happens and primarily where the user is going to interact. There are two processes started in order for the client to be fully launched: the sender and the receiver. The sender window is where the user will input their messages to send and the receiver window is where they will see incoming chats and past conversations that have taken place in the chat. Whenever the user switches between chats, the receiver window will be cleared in order to reduce clutter.  
## Client Requirements
- Maven 
- Java 21

## Configuring the client
Inside the Send.java and Recv.java files, there is a variable called environment. This will need to be configured so that the client connects properly to the RabbitMQ server.

To do so:
1. Determine RabbitMQ Server hostname
2. Edit the variable entryPoint and change the current hostname to the hostname of your RabbitMQ server. The port should remain the same unless the port on the server was explicitly changed
3. Your new entryPoint variable should look something like this `Address entryPoint = new Address("<hostname>", 5552);`
## Running the client
*IMPORTANT NOTE: SENDER MUST BE INITIALIZED BEFORE THE RECEIVER. Receiver will not launch if Sender is not active*

1. cd to dir with pom.xml file
2. `mvn package`
3. `mvn exec:java -Dexec.args="send"`
4. Open another terminal
5. `mvn exec:java -Dexec.args="recv"`


---
Developed by: Josh Stapera

