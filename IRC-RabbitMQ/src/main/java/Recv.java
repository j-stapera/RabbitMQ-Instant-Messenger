import com.rabbitmq.stream.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.*;
import java.util.*;

public class Recv {
    private static String currStream;
    private static Environment environment;
    private static final Path StreamListPath = Path.of("src\\main\\resources\\StreamList.txt");
    private static boolean isProducerActive;
    private static Consumer currConsumer;

    public static void main(String[] args) throws IOException, InterruptedException {
        // -------------- Initialize Receiver ----------
        // TODO: Determine if this needs to be changed when placed
        //       in a docker container
        environment = Environment.builder().build();

        // start watchService
        WatchService watchService = FileSystems.getDefault().newWatchService();
        Path watchPath = Path.of("src\\main\\resources");
        watchPath.register(
                watchService,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;
        // Load curr stream from file
        readInStreamFile();

        //determine if isActive file is present
        isProducerActive = new File("src\\main\\resources\\isActive").isFile();

        // initialize consumer
        createConsumer();

        // use presence of isActive file to determine if producer is running
        // receiver exits when it is not
        while(isProducerActive) {

            // ============== File Watch =====================
            // FileWatch to observe for changes made by Sender class
            // Changes such as: Indicating Stream switch via current stream
            //                  Closing producer aka Sender process exiting
            String deleteme = "";
            if ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    deleteme = event.context().toString();
                }

                key.reset();
            }

            System.out.println(deleteme);


            // ========= File Watch ====================
            String fileCurrStream = "newStream";

            // TODO: determine edge case when currStream = null
            // When stream switch happens call clearTerminal
            if (!currStream.equals(fileCurrStream)) {
                // if currStream has changed then close current consumer
                currStream = fileCurrStream;
                currConsumer.close();
                createConsumer();
                clearTerminal();
            }
        }

        System.out.println(" [x]  Press Enter to close the consumer...");
        System.in.read();
        currConsumer.close();
        environment.close();
    }

    // Load Consumer for "current stream"
    // current stream - item in streams file
    // NOTE: a new consumer will be made each time a stream switch happens.
    //       This is the only way I could think to do achieve stream switching without multithreading consumers
    private static void createConsumer(){

        currConsumer = environment.consumerBuilder()
                .stream(currStream)
                .offset(OffsetSpecification.first())
                .messageHandler((unused, message) -> {
                    System.out.println(new String(message.getBodyAsBinary()));
                }).build();
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