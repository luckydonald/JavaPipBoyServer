package de.luckydonald.pipboyserver.PipBoyServer;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Created by luckydonald on 09.02.16.
 */
public class CommandInput extends Thread implements Runnable {
    private HashMap<String, Function<String, Void>> commandCallbacks = new HashMap<>();

    public CommandInput(String command, Function<String, Void> callback) {
        super("CommandInput Thread");
        this.commandCallbacks.put(command, callback);
    }
    public CommandInput(String[] commands, Function<String, Void>[] callbacks) {
        this(Arrays.asList(commands), Arrays.asList(callbacks));
    }
    public CommandInput(List<String> commands, List<Function<String, Void>> callbacks) {
        super("CommandInput Thread");
        do_assert(commands.size() == callbacks.size());
        for (int i = 0; i < commands.size(); i++) {
            this.commandCallbacks.put(commands.get(i), callbacks.get(i));
        }
    }

    public CommandInput(HashMap<String, Function<String, Void>> commandCallbacks) {
        super("CommandInput Thread");
        this.commandCallbacks = commandCallbacks;
    }

    static void do_assert(boolean test) {
        if(!test) {
            throw new RuntimeException("Test Failed.");
        }
    }

    public Function<String, Void> get(String command) {
        return commandCallbacks.get(command);
    }

    public void add(String command, Function<String, Void> callback) {
        this.commandCallbacks.put(command, callback);
    }
    public void put(String command, Function<String, Void> callback){
        this.add(command, callback);
    }
    public void process(String line) {
        Map.Entry<String, Function<String, Void>> longestHit = null;
        for (Map.Entry<String, Function<String, Void>> entry : this.commandCallbacks.entrySet()) {
            if (line.startsWith(entry.getKey())) {
                if (longestHit == null || longestHit.getKey().length() < entry.getKey().length()) {
                    longestHit = entry;
                }
            }
        }
        if (longestHit != null) {
            longestHit.getValue().apply(line);
        }

    }

    /**
     * When an object implementing interface <code>Runnable</code> is used
     * to create a thread, starting the thread causes the object's
     * <code>run</code> method to be called in that separately executing
     * thread.
     * <p>
     * The general contract of the method <code>run</code> is that it may
     * take any action whatsoever.
     *
     * @see Thread#run()
     */
    public void run() {
        Scanner scanner = new Scanner(System.in);
        String line = "";
        while ( line != null) {
            System.out.println("Enter command.");
            line = scanner.next();
            try {
                this.process(line);
            } catch (RuntimeException e) {
                e.printStackTrace();
            }
        }
    }
}
