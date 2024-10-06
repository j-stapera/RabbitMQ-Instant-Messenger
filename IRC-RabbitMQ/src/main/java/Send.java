import com.rabbitmq.stream.*;

import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.io.File;
import java.util.stream.Collectors;

public class Send {
    private static ArrayList<String> streams = new ArrayList<>();
    private static Map<String, String> commandHelp = new HashMap<>();
    private static String currStream;
    private static Producer currProducer; // provided to reduce lookup time
    private static Map<String, Producer> producers = new HashMap<>();
    private static Environment environment;
    private static final Path StreamListPath = Path.of("src\\main\\resources\\StreamList.txt");
    private static final Path CommandsDocPath = Path.of("src\\main\\resources\\CommandsDoc.txt");
    private static String username;
    public static void main(String[] args) throws IOException {
        // ------------- Initialize Sender Class ----------------
        Scanner input = new Scanner(System.in); //user input scanner

        // TODO: Determine if this needs to be changed when placed
        //       in a docker container
        environment = Environment.builder().build();

        // Load list of streams file, read in data
        // I don't like this but it does the job
        try {
            var in = new Scanner(StreamListPath);
            // file delimited by \n
            in.useDelimiter("\n");
            // file will be split into two tokens
            // tkn1 - CurrStream:<stream>
            // tkn2 - Streams:<stream>,<stream>,...

            // slightly excessive to use a stream (Java) type here but future-proof
            // also from my victim picker code so easy copy
            // (see https://github.com/j-stapera/victim-picker/blob/main/src/FileHandler.java)
            var tokens = in.tokens()
                    .map(e-> e.replaceFirst("\r", "")) //remove weird \r that appears
                    .collect(Collectors.toCollection(ArrayList::new)); //collects to ArrayList

            String[] currStreamFromFile = tokens.get(0).split(":");
            // check if correct var
            if (currStreamFromFile[0].equalsIgnoreCase("CurrStream")){
                currStream = currStreamFromFile[1];
            }
            else {
                throw new FileNotFoundException();
            }
            String[] streamsFromFile = tokens.get(1).split(":");
            // check if correct var
            if (streamsFromFile[0].equalsIgnoreCase("Streams")){
                streams.addAll(Arrays.asList(streamsFromFile[1].split(",")));
            } else {
                throw new FileNotFoundException();
            }
        } catch (FileNotFoundException | ArrayIndexOutOfBoundsException e){

            System.out.println("StreamList.txt not found or is improper. Creating file...");
            // create StreamList.txt
            File newFile = new File(StreamListPath.toString());
            System.out.println("Please enter the name of a stream: ");
            String newStream = input.nextLine();
            currStream = newStream;
            streams.add(newStream);

            // write newStream to StreamList.txt
            try {
                FileWriter fileWriter = new FileWriter(newFile);
                // writes: CurrStream:<newStream>
                //         Streams:<newStream>
                fileWriter.write((String.format("CurrStream:%s\nStreams:%s", newStream, newStream)));
                fileWriter.close();
            } catch (IOException ex) {
                throw new RuntimeException(ex);
            }
        }

        // Load command help into memory
        // This will be removed in future iterations when it is determined to be too memory heavy
        try {
            var in = new Scanner(CommandsDocPath);
            // file delimited by \n
            in.useDelimiter("\n");

            var cmdTokens = in.tokens()
                    .map(e -> e.replaceFirst("\r", "")) //remove weird \r that appears
                    .collect(Collectors.toCollection(ArrayList::new)); //collects to ArrayList

            for (String token : cmdTokens){
                String[] cmd_HelpPair = token.split("-");
                commandHelp.put(cmd_HelpPair[0],cmd_HelpPair[1]);
            }
        } catch (FileNotFoundException | ArrayIndexOutOfBoundsException e){
            System.out.println("CommandsDoc.txt not found or is improper. /help will not provide any information." +
                                "\nPlease retrieve the file from the repo and restart the program");
            commandHelp.put("null", "CommandsDoc.txt missing");
        }
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
        System.out.println("Please enter username for session: ");
        username = input.nextLine(); // Proper action would have this verified for security issues, not doing that for now

        System.out.println("Connected to #"+currStream);
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


                // get curr date and time
                DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy HH:mm");
                Date date = new Date();
                // Append additional data to message
                // End message will be formatted as so: <#Stream> [MM/dd/yyyy HH:mm] | User: msgContent
                String formattedMsg = String.format("[%s] #%s | %s: %s", dateFormat.format(date), currStream, username, userInput);

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
            if (cmdTkns.length > 2){
             username = cmdTkns[1];
            } else {
                //else print help context
            }
        }

        // if /leave
        else if (cmdTkns[0].equalsIgnoreCase("/leave")){
            String streamToLeave;
            if (cmdTkns.length > 2 ){
                if (streams.contains(cmdTkns[1])){
                    streamToLeave = cmdTkns[1];
                } else {
                    System.out.println("Cannot leave Stream as you have not joined it");
                }
            // else /leave curr stream
            } else {
                streamToLeave = currStream;
            }
            // switch to random stream
            // close producer
            // delete stream from file
        }

        // if /join <stream name>
        else if (cmdTkns[0].equalsIgnoreCase("/join")) {
            //if second arg exists and stream is valid
            if (cmdTkns.length > 2){
                // determine if stream already exists
                // if not, reject command and prompt user to use /create
                // if it does, join stream and switch to it
            }
            // else print out help context
        }

        // if /switch <stream name>
        else if (cmdTkns[0].equalsIgnoreCase("/switch")){
            //if second arg exists
            if (cmdTkns.length > 2) {
                switchStream(cmdTkns[1]);
            }
        }

        // if /create <stream name>
        else if (cmdTkns[0].equalsIgnoreCase("/create")) {
            // if second arg exists
            // else print help context
        }
        // if /help
        else if (cmdTkns[0].equalsIgnoreCase("/help")){
            help();
        }
        else {
            System.out.println("Command not recognized, recommend using /help");
        }

    }

    private static void switchStream(String streamToSwitch){
        // if stream is valid
        // write to file to announce change
        // sout("Connecting to <stream>")
        // else print out help context
    }

    private static void help(){
        //print help context for all cmds
    }

    // creates a new producer when user joins a new stream
    // a unique producer is required per stream
    // Provided as a function for reducing code bulk
    private static Producer newProducer(String newStream){
        return environment.producerBuilder().stream(newStream).build();

    }
}