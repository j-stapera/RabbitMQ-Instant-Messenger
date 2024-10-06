import com.rabbitmq.stream.*;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;
import java.util.Scanner;

public class Send {
    private static ArrayList<String> streams;
    private static Map<String, String> commandHelp;
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

        // TODO: Load command help into memory
        // This will be removed in future iterations when it is determined to be too memory heavy
        // if CommandsDoc.txt is present
        // for String command : file.getnextline
        // place commands into a map

        // Create Streams
        for (String stream : streams) {
            environment.streamCreator().stream(stream).maxLengthBytes(ByteCapacity.GB(5)).create();

            // Create producers for each stream
            // Create map linking stream name to producer
            producers.put(stream, newProducer(stream));
        }
        // Load Producer for "current stream"
        // current stream - item in streams file
        currProducer = producers.get(currStream);

        // ask user for username
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
                // check if user input is an exit cmd
                // has to happen here due to the isActive var
                 if (userInput.toLowerCase().startsWith("/exit")){
                      System.out.println("Exiting Session");
                      isActive = false;
                 } else {
                      UserCommands(userInput);
                }
            // if not a command, user is sending a message
            } else {

                // Append additional data to message

                // get curr date and time
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                Date date = new Date();
                // End message will be formatted as so: <#Stream> [MM/dd/yyyy HH:mm] | User: msgContent
                String formattedMsg = String.format("<#%s> [%s] | %s: %s", currStream, dateFormat.format(date), username, userInput);

                // send user message
                currProducer.send(
                        currProducer.messageBuilder()
                                .addData(formattedMsg.getBytes())
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

        // close all producers
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
    // TODO: finish command execution behavior
    private static void UserCommands(String userCmd){
        // apparently can be refactored using Extract method technique?
        // likely not needed, however acknowledging the possibility
        String[] cmdTkns = userCmd.split(" ");
        // if /nick <name>
        if (cmdTkns[0].equalsIgnoreCase("/nick")){
            //if second arg exists
            //else print help context
        }

        // if /leave
        if (cmdTkns[0].equalsIgnoreCase("/leave")){
            //      if /leave <stream name>
            //      else /leave curr stream
        }

        // if /join <stream name>
        if (cmdTkns[0].equalsIgnoreCase("/join")) {
            //if second arg exists and stream is valid
            // else print out help context
        }

        // if /switch <stream name>
        if (cmdTkns[0].equalsIgnoreCase("/switch")){
            //if second arg exists and stream is valid
            // else print out help context
        }

        // if /create <stream name>
        if (cmdTkns[0].equalsIgnoreCase("/create")) {
            // if second arg exists
            // else print help context
        }
        // if /help
        if (cmdTkns[0].equalsIgnoreCase("/help")){
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