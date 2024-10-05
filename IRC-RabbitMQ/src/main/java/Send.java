import com.rabbitmq.stream.*;
import java.io.IOException;
import java.util.Scanner;

public class Send {

    public static void main(String[] args) throws IOException {
        // ------------- Initialize Sender Class ----------------
        // TODO: Determine if this needs to be changed when placed
        //       in a docker container
        Environment environment = Environment.builder().build();

        // TODO: Load list of streams file
        // TODO: Create Streams
        String stream = "hello-java-stream";
        environment.streamCreator().stream(stream).maxLengthBytes(ByteCapacity.GB(5)).create();

        // TODO: Create producers for each stream
        // TODO: Create map linking stream name to producer
        // current stream - item in streams file
        Producer producer = environment.producerBuilder().stream(stream).build();

        // TODO: Load Producer for "current stream"

        // ----------------- Initialization Complete ------------
        // TODO: place into while loop
        // TODO: Get user input and detect if command
        // Command denoted by a / at the beginning of a input
        //
        Scanner input = new Scanner(System.in);
        for (int i = 0; i < 5; i++) {
            // get user message
            String userInput = input.nextLine();

            // send user message
            producer.send(producer.messageBuilder().addData(userInput.getBytes()).build(), null);

            // Confirm sending to user
            System.out.println(" [x] " + userInput);
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
     */
    private static String UserCommands(){

        return "";
    }

    // creates a new producer when user joins a new stream
    // a unique producer is required per stream
    private static Producer newProducer(String newStream, Environment environment){

        return null;
    }
}