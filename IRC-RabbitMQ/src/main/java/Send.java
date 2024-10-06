import com.rabbitmq.stream.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

public class Send {
    private static ArrayList<String> streams;
    private static String currStream;
    private static Producer currProducer; // provided to reduce lookup time
    private static Map<String, Producer> producers;
    private static Environment environment;
    public static void main(String[] args) throws IOException {
        // ------------- Initialize Sender Class ----------------
        // TODO: Determine if this needs to be changed when placed
        //       in a docker container
        environment = Environment.builder().build();

        // TODO: Load list of streams file, read in data
        // TODO: Determine steps for when StreamList is not present
        // if StreamList.isPresent()
        // for (String stream : file.getNextLine)
        // else
        // ???



        // Create Streams
        for (String stream : streams) {
            environment.streamCreator().stream(stream).maxLengthBytes(ByteCapacity.GB(5)).create();

            // Create producers for each stream
            // Create map linking stream name to producer
            producers.put(stream, newProducer(stream));
        }
        // TODO: Load Producer for "current stream"
        // current stream - item in streams file
        currProducer = producers.get(currStream);
        // TODO: ask user for username
        Scanner input = new Scanner(System.in);
        System.out.println("Please enter username for session: ");
        String username = input.nextLine(); // Proper action would have this verified for security issues, not doing that for now
        // ----------------- Initialization Complete ------------



        // TODO: Get user input and detect if command
        // Command denoted by a / at the beginning of a input
        Boolean isActive = true;
        while (isActive) {
            // get user message
            String userInput = input.nextLine();

            // Process message
            if (userInput.toCharArray()[0] == '/'){ // determine if user is invoking a command
                // TODO: actually process userinput
                // if userInput == "/exit"{
                //      sout("Exiting Session");
                //      isActive = false;
                // } else {
                //      sout(UserCommands(userInput));
                //
            } else { // if not command, user is sending a message
                // Append additional data to message
                // End message will be formatted as so: <Stream> [Time] | User: msgContent

                // send user message
                currProducer.send(
                        currProducer.messageBuilder()
                                .addData(userInput.getBytes())
                                .build()
                        , null);

                // Confirm sending to user
                System.out.println(" [x] Sending: " + userInput);
            }
        }

        // Exit while loop via user command
        // confirm user exit
        System.out.println(" [x] Press Enter to close the producer...");
        System.in.read();

        // TODO: close all producers
        for (Producer producer : producers.values()) {
            producer.close();
        }
        environment.close();
    }

    /* List of User Commands:
        Change nickname - changes username
        Join Stream - Join a pre-existing stream
        Leave Stream - Disconnect from an existing stream
        Switch Stream - Switch recv messages to a different stream
        Create new Stream - Create a new Stream (other users will need to join)
        Help - Displays the list of possible commands and their invocation
     */
    private static void UserCommands(String userCmd){
        String[] cmdTkns = userCmd.split(" ");
        // if /nick <name>
        if (cmdTkns[0].toLowerCase().equals("/nick")){
            //if second arg exists
            //else print help context
        }

        // if /leave
        if (cmdTkns[0].toLowerCase().equals("/leave")){
            //      if /leave <stream name>
            //      else /leave curr stream
        }

        // if /join <stream name>
        if (cmdTkns[0].toLowerCase().equals("/join")) {
            //if second arg exists and stream is valid
            // else print out help context
        }

        // if /switch <stream name>
        if (cmdTkns[0].toLowerCase().equals("/switch")){
            //if second arg exists and stream is valid
            // else print out help context
        }

        // if /create <stream name>
        if (cmdTkns[0].toLowerCase().equals("/create")) {
            // if second arg exists
            // else print help context
        }
        // if /help
        if (cmdTkns[0].toLowerCase().equals("/help")){
            //print help context for all cmds
        }

    }

    // creates a new producer when user joins a new stream
    // a unique producer is required per stream
    // Provided as a function for reducing code bulk
    private static Producer newProducer(String newStream){
        return environment.producerBuilder().stream(newStream).build();

    }
}