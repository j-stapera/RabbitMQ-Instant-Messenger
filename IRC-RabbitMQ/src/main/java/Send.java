import com.rabbitmq.stream.*;
import java.io.IOException;
import java.util.Scanner;

public class Send {

    public static void main(String[] args) throws IOException {
        Environment environment = Environment.builder().build();
        String stream = "hello-java-stream";
        environment.streamCreator().stream(stream).maxLengthBytes(ByteCapacity.GB(5)).create();

        Producer producer = environment.producerBuilder().stream(stream).build();
        Scanner input = new Scanner(System.in);
        for (int i = 0; i < 5; i++) {
            String userInput = input.nextLine();
            producer.send(producer.messageBuilder().addData(userInput.getBytes()).build(), null);
            System.out.println(" [x] " + userInput);
        }
        System.out.println(" [x] Press Enter to close the producer...");
        System.in.read();
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