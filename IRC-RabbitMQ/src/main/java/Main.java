package org.IRCtest;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {
        if (args.length <= 0){
            System.out.println("Missing arg, Please use \"send\" or \"recv\"");
            return;
        }

        if (args[0].equalsIgnoreCase("send")){
            System.out.println("attempt to load send");
            new org.IRCtest.Send();
        } else if (args[0].equalsIgnoreCase("recv")){
            new org.IRCtest.Recv();
        } else {
            System.out.println("Arg not recognized. Please use \"send\" or \"recv\"");
        }
    }
}
