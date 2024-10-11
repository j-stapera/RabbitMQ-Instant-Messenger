package org.IRCtest;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length <= 0){
            System.out.println("Missing arg, Please use \"send\" or \"recv\"");
            return;
        }

        if (args[0].equalsIgnoreCase("send")){
            new Send();
        } else if (args[0].equalsIgnoreCase("recv")){
            new Recv();
        } else {
            System.out.println("Arg not recognized. Please use \"send\" or \"recv\"");
        }
    }
}
