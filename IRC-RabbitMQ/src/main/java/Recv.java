import com.rabbitmq.stream.ByteCapacity;
import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import java.io.IOException;

public class Recv {

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