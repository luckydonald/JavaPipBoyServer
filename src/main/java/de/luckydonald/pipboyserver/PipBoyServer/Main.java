package de.luckydonald.pipboyserver.PipBoyServer;

/**
 * Main class, startes the Server.
 *
 * Created by luckydonald on 14.01.16.
 */
public class Main {
    public static void main(String[] args) {
        String machineType = typeFromArg(args);
        Discovery d = new Discovery(machineType);
        Thread t = new Thread(d, "Discovery Thread");
        t.start();
        Server s = new Server();
        s.run();
    }
    public static String typeFromArg(String[] args) {
        String machineType = "PC";
        if (args.length == 0) {
            return machineType;
        } else if ("PS4".equals(args[0].toLowerCase().trim())) {
            machineType = "PS4";
        } else if ("PC".equals(args[0].toLowerCase().trim())) {
            machineType = "PC";
        }
        System.out.println("Serving as " + machineType); //TODO: move into Discovery, as logger.
        return machineType;
    }
}

