import com.rabbitmq.stream.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Recv {
    private static ArrayList<String> streams = new ArrayList<>();
    private static String currStream;
    private static Consumer currConsumer; // provided to reduce lookup time
    private static Map<String, Consumer> consumerMap = new HashMap<>();
    private static Environment environment;
    private static final Path StreamListPath = Path.of("src\\main\\resources\\StreamList.txt");
    private static final Path CommandsDocPath = Path.of("src\\main\\resources\\CommandsDoc.txt");

    public static void main(String[] args) throws IOException {
        // -------------- Initialize Receiver ----------
        // TODO: Determine if this needs to be changed when placed
        //       in a docker container
        Environment environment = Environment.builder().build();

        // TODO: Load list of streams file
        // TODO: Create Streams
        String stream = "hello-java-stream";
        environment.streamCreator().stream(stream).maxLengthBytes(ByteCapacity.GB(5)).create();

        // TODO: Create consumers for each stream
        // TODO: Create map linking stream name to consumer

        // TODO: Load Consumer for "current stream"
        // current stream - item in streams file
        Consumer consumer = environment.consumerBuilder()
                .stream(stream)
                .offset(OffsetSpecification.first())
                .messageHandler((unused, message) -> {
                    System.out.println("Received message: " + new String(message.getBodyAsBinary()));
                }).build();

        // TODO: Setup FileWatch to observe for changes made by Sender class
        // Changes such as: Indicating Stream switch via current stream
        //                  Adding a new stream (Joining a stream)
        //                  Removing an existing stream (Leaving a stream)
        // TODO: When stream switch happens call clearTerminal!!

        // TODO: Determine best way for user to close receiver
        System.out.println(" [x]  Press Enter to close the consumer...");
        System.in.read();
        consumer.close();
        environment.close();
    }

    // creates a new consumer when the user joins a new stream
    // each stream requires a unique consumer
    private static Consumer newConsumer(String newStream, Environment environment){

        return null;
    }


    private static void readInStreamFile(){

    }

    // clears the terminal of all data
    // Primarily used when switching between streams
    private static void clearTerminal(){
        try
        {
            final String os = System.getProperty("os.name");

            if (os.contains("Windows"))
            {
                Runtime.getRuntime().exec("cls");
            }
            else
            {
                Runtime.getRuntime().exec("clear");
            }
        }
        catch (final Exception e)
        {
            System.out.println("Terminal failed to clear" + e.getMessage());
        }
    }
}