import com.rabbitmq.stream.*;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class Recv {
    private static String currStream;
    private static Environment environment;
    private static final Path StreamListPath = Path.of("src\\main\\resources\\StreamList.txt");

    public static void main(String[] args) throws IOException {
        // -------------- Initialize Receiver ----------
        // TODO: Determine if this needs to be changed when placed
        //       in a docker container
        environment = Environment.builder().build();

        // Load curr stream from file
        readInStreamFile();


            // Load Consumer for "current stream"
            // current stream - item in streams file
            // NOTE: a new consumer will be made each time a stream switch happens.
            //       This is the only way I could think to do achieve stream switching without multithreading consumers
            Consumer Consumer = environment.consumerBuilder()
                    .stream(currStream)
                    .offset(OffsetSpecification.first())
                    .messageHandler((unused, message) -> {
                        System.out.println(new String(message.getBodyAsBinary()));
                    }).build();

        // TODO: Setup FileWatch to observe for changes made by Sender class
        // Changes such as: Indicating Stream switch via current stream
        //                  Adding a new stream (Joining a stream)
        //                  Removing an existing stream (Leaving a stream)
        // TODO: When stream switch happens call clearTerminal!!

        // TODO: Determine best way for user to close receiver
        System.out.println(" [x]  Press Enter to close the consumer...");
        System.in.read();
        Consumer.close();
        environment.close();
    }



    private static void readInStreamFile(){
        try (var in = new Scanner(StreamListPath)){
            String[] fileCurrStream = in.nextLine().split(":");
            if (fileCurrStream.length >= 2) {
                currStream = fileCurrStream[1];
            } else {
                throw new IOException("currStream param missing");
            }
        } catch (IOException e){
            e.printStackTrace();
        }
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