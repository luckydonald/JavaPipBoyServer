package de.luckydonald.utils.interactions;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Scanner;
import java.util.function.Function;

/**
 * Creates a new thread to wait for userinput
 * Created by luckydonald on 09.02.16.
 */
public class CommandInput extends Thread implements Runnable {
    private HashMap<String, FunctionWrapper> commandCallbacks = new HashMap<>();
    public InputStream input;
    public CommandInput() {
        this(System.in);
    }
    public CommandInput(InputStream input) {
        super("CommandInput Thread");
        this.input = input;
    }
    public CommandInput(String command, Function<Scanner, Void> callback) {
        this();
        this.commandCallbacks.put(command, new FunctionWrapper(callback, command));
    }
    public CommandInput(InputStream input, String command, Function<Scanner, Void> callback) {
        this(input);
        this.commandCallbacks.put(command, new FunctionWrapper(callback, command));
    }
    public CommandInput(String[] commands, Function<Scanner, Void>[] callbacks) {
        this(Arrays.asList(commands), Arrays.asList(callbacks));
    }
    public CommandInput(InputStream input, String[] commands, Function<Scanner, Void>[] callbacks) {
        this(input, Arrays.asList(commands), Arrays.asList(callbacks));
    }
    public CommandInput(List<String> commands, List<Function<Scanner, Void>> callbacks) {
        this();
        do_assert(commands.size() == callbacks.size());
        this.input = System.in;
        for (int i = 0; i < commands.size(); i++) {
            this.commandCallbacks.put(commands.get(i), new FunctionWrapper(callbacks.get(i), commands.get(i)));
        }
    }
    public CommandInput(InputStream input, List<String> commands, List<Function<Scanner, Void>> callbacks) {
        this(input);
        do_assert(commands.size() == callbacks.size());
        this.input = System.in;
        for (int i = 0; i < commands.size(); i++) {
            this.commandCallbacks.put(commands.get(i), new FunctionWrapper(callbacks.get(i), commands.get(i)));
        }
    }

    static void do_assert(boolean test) {
        if(!test) {
            throw new RuntimeException("Test Failed.");
        }
    }

    public Function<Scanner, Void> getFunction(String command) {
        return commandCallbacks.get(command).getFunction();
    }

    public void add(String command, Function<Scanner, Void> callback) {
        this.commandCallbacks.put(command, new FunctionWrapper(callback, command));
    }
    public void put(String command, Function<Scanner, Void> callback){
        this.add(command, callback);
    }
    public void process(Scanner scanner, String line) {
        //todo commands with spaces
        Map.Entry<String, FunctionWrapper> longestHit = null;
        for (Map.Entry<String, FunctionWrapper> entry: this.commandCallbacks.entrySet()) {
            if (line.startsWith(entry.getKey())) {
                if (longestHit == null || longestHit.getKey().length() < entry.getKey().length()) {
                    longestHit = entry;
                }
            }
        }
        if (longestHit != null) {
            longestHit.getValue().getFunction().apply(scanner);
        } else if ("help".equals(line.trim())){
            this.printHelp();
        }

    }

    /**
     * Prints the help about this commands.
     */
    public void printHelp() {
        for (Map.Entry<String, FunctionWrapper> cmd : this.commandCallbacks.entrySet()) {
            System.out.println(" - " + cmd.getKey() + "\t" + (cmd.getValue().hasHelp() ? cmd.getValue().getHelp() : cmd.getValue().getFunction().toString()));
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
        Scanner scanner = new Scanner(input);
        String line = "";
        while ( line != null) {
            System.out.println("Enter command, end with ^D");
            line = scanner.next();
            try {
                try {
                    this.process(scanner, line);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    System.out.println("Continuing.");
                }
            } catch (Exception | StackOverflowError e) {
                e.printStackTrace();
                System.out.println("Continuing.");
            }
        }
    }
    class FunctionWrapper{
        Function<Scanner, Void> func;
        String cmd;
        String help;

        public FunctionWrapper(Function<Scanner, Void> callback, String cmd, String help) {
            this.func = callback;
            this.cmd = cmd;
            this.help = help;
        }
        public FunctionWrapper(Function<Scanner, Void> callback, String cmd) {
            this.func = callback;
            this.cmd = cmd;
        }

        public FunctionWrapper(Function<Scanner, Void> callback) {
            this.func = func;
        }

        public Function<Scanner, Void> getFunction() {
            return func;
        }

        public void setFunction(Function<Scanner, Void> func) {
            this.func = func;
        }

        public String getCmd() {
            return cmd;
        }

        public void setCmd(String cmd) {
            this.cmd = cmd;
        }

        /**
         * returns whether it has a value different to {@code null} or a empty String ({@code ""} when {@link String#trim() trimmed})
         * @return if it its Help string has a value.
         */
        public boolean hasHelp() {
            return this.help != null && !("".equals(this.help.trim()));
        }
        /**
         * Returns the help string, or an empty string if not set.
         * @return the help string or {@code ""}
         */
        public String getHelp() {
            if (help == null) {
                return "";
            }
            return help;
        }

        public void setHelp(String help) {
            this.help = help;
        }
    }
}
