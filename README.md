# RabbitMQ Instant Messenger
In development...

Developed by: Josh Stapera

This "thing" is being developed with "love." It will leverage the exchange and stream services provided by the RabbitMQ framework.

==
IMPORTANT NOTE: SENDER MUST BE INITIALIZED BEFORE RECV. THINGS GET COMPLICATED WHEN SENDER DOESN'T INITIALIZE CERTAIN FILES

== Requirements
Maven 
Java 21

== running
cd to dir
`mvn package`
`mvn exec:java -Dexec.args="send"`
Open another terminal
`mvn exec:java -Dexec.args="recv"`
