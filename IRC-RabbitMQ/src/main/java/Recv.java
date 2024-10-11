package org.IRCtest;

import com.rabbitmq.stream.*;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.util.*;

// WatchService related code retrieved from: https://www.baeldung.com/java-nio2-watchservice
public class Recv {
    private static String currStream;
    private static Environment environment;
    //hacky bullshit
    private static Path StreamListPath;
    private static Path WatchPath;
    private static Path IsActivePath;
    //private static final Path StreamListPath = Path.of("IRC-RabbitMQ/src/main/resources/StreamList.txt");
    //private static final Path WatchPath = Path.of("IRC-RabbitMQ/src/main/resources");
    //private static final Path IsActivePath = Path.of("IRC-RabbitMQ/src/main/resources/isActive");
    private static boolean isProducerActive;
    private static Consumer currConsumer;
    private static boolean consumerIsActive; //used when closing consumer since it may throw an exception

    Recv() throws IOException, InterruptedException, URISyntaxException {
        StreamListPath = Paths.get(getClass().getResource("/"+"StreamList.txt").toURI());
        IsActivePath = Paths.get(getClass().getResource("/"+"isActive").toURI());
        WatchPath = Path.of(IsActivePath.toString().substring(0,IsActivePath.toString().length()-8));
        // -------------- Initialize Receiver ----------
        // TODO: Determine if this needs to be changed when placed
        //       in a docker container
        environment = Environment.builder().build();

        // start watchService
        WatchService watchService = FileSystems.getDefault().newWatchService();
        WatchPath.register(
                watchService,
                StandardWatchEventKinds.ENTRY_DELETE,
                StandardWatchEventKinds.ENTRY_MODIFY);

        WatchKey key;

        // Load curr stream from file
        currStream = readInStreamFile();

        //determine if isActive file is present
        if(Files.isRegularFile(IsActivePath)){
            isProducerActive = true;
        } else {
            System.out.println("Producer has not been started. Start sender then restart consumer");
        }

        // initialize consumer
        if (currStream != null) {
            createConsumer();
        } else {
            consumerIsActive = false;
            System.out.println("No active stream, please join a stream");
        }

        // use presence of isActive file to determine if producer is running
        // receiver exits when it is not
        while(isProducerActive) {

            // ============== File Watch =====================
            // FileWatch to observe for changes made by Sender class
            // Changes such as: Indicating Stream switch via current stream
            //                  Closing producer aka Sender process exiting
            String fileEvent = "";
            if ((key = watchService.take()) != null) {
                for (WatchEvent<?> event : key.pollEvents()) {
                    fileEvent = event.context().toString();
                }

                key.reset();
            }

            // if file event relates to StreamList.txt
            if (fileEvent.contains("StreamList.txt")){
                // Two events could have happened: currStream got changed or the list of subscribed streams was updated
                // we only care about currStream
                String fileCurrStream = readInStreamFile();


                // check if currStream != fileCurrStream
                // AKA currStream got changed
                // or if currStream is null, meaning fileCurrStream will definitely not equal currStream
                if ((currStream == null || (!currStream.equals(fileCurrStream))) && fileCurrStream != null) {
                    // if currStream has changed then close current consumer
                    currStream = fileCurrStream;
                    if (consumerIsActive){
                        currConsumer.close();
                        consumerIsActive = false;
                    }
                    createConsumer();
                    // When stream switch happens clear terminal
                    // NOTE: This method only works in linux
                    //FIXME: FIND A SOLUTION THAT WORKS IN WINDOWS
                    System.out.print("\033[H\033[2J");
                } else if (fileCurrStream == null){
                    if (consumerIsActive) {
                        currConsumer.close();
                        consumerIsActive = false;
                    }
                    System.out.print("\033[H\033[2J");
                    currStream = null;
                    System.out.println("No active stream, please join a stream");
                }
            } else if (fileEvent.contains("isActive")){ //if it relates to the isActive file
                // simple check, just check if file isn't a file
                if (!Files.isRegularFile(IsActivePath)) {
                    isProducerActive = false;
                } else {// else log error because it shouldn't have an update
                    System.err.println("isActive was changed but was not deleted");
                }
            }

            // ========= File Watch ====================

        }
        System.out.println("Producer is closed");
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
        consumerIsActive = true;
    }


    private static String readInStreamFile(){

        try (var in = new Scanner(StreamListPath)){
            String[] fileCurrStream = in.nextLine().split(":");
            if (fileCurrStream.length >= 2) {
                String returnString;
                // check if there is no currStream
                if (fileCurrStream[1].equals("null")){
                    returnString = null;
                } else {
                    returnString = fileCurrStream[1];
                }
                return(returnString);
            } else {
                throw new IOException("currStream param missing");
            }
        } catch (IOException e){
            e.printStackTrace();
            throw new RuntimeException();
        }
    }

}