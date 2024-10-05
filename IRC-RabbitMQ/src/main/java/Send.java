import com.rabbitmq.stream.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Send {
    private static ArrayList<String> streams;
    private static Environment environment;
    public static void main(String[] args) throws IOException {
        // ------------- Initialize Sender Class ----------------
        // TODO: Determine if this needs to be changed when placed
        //       in a docker container
        environment = Environment.builder().build();

        // TODO: Load list of streams file
        // TODO: Determine steps for when StreamList is not present
        // TODO: Create Streams
        // for (String stream : file.getNextLine)
        String stream = "hello-java-stream";
        environment.streamCreator().stream(stream).maxLengthBytes(ByteCapacity.GB(5)).create();

        // TODO: Create producers for each stream
        // TODO: Create map linking stream name to producer
        Producer producer = environment.producerBuilder().stream(stream).build();

        // TODO: Load Producer for "current stream"
        // current stream - item in streams file

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
                producer.send(
                        producer.messageBuilder()
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
        producer.close();
        environment.close();
    }

    /* List of User Commands:
        Join Stream - Join a pre-existing stream
        Leave Stream - Disconnect from an existing stream
        Switch Stream - Switch recv messages to a different stream
        Create new Stream - Create a new Stream (other users will need to join)
        Help - Displays the list of possible commands and their invocation
     */
    private static String UserCommands(String userCmd){

        return "";
    }

    // creates a new producer when user joins a new stream
    // a unique producer is required per stream
    private static Producer newProducer(String newStream, Environment environment){

        return null;
    }
}