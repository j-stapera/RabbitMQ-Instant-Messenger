import com.rabbitmq.stream.ByteCapacity;
import com.rabbitmq.stream.Consumer;
import com.rabbitmq.stream.Environment;
import com.rabbitmq.stream.OffsetSpecification;

import java.io.IOException;

public class Recv {

    public static void main(String[] args) throws IOException {
        Environment environment = Environment.builder().build();
        String stream = "hello-java-stream";
        environment.streamCreator().stream(stream).maxLengthBytes(ByteCapacity.GB(5)).create();

        Consumer consumer = environment.consumerBuilder()
                .stream(stream)
                .offset(OffsetSpecification.first())
                .messageHandler((unused, message) -> {
                    System.out.println("Received message: " + new String(message.getBodyAsBinary()));
                }).build();

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