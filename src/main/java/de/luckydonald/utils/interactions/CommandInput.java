package de.luckydonald.utils.interactions;

import de.luckydonald.utils.ObjectWithLogger;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
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
    public static final String HELP_COMMAND = "help";
    private HashMap<String, FunctionWrapper> commandCallbacks = new HashMap<>();
    public InputStream input;
    /**
     * Set a custom output where to print.
     */
    public PrintStream output;

    public CommandInput(InputStream input) {
        super("CommandInput Thread");
        this.input = input;
        this.output = System.out;
    }

    public CommandInput() {
        this(System.in);
    }

    public CommandInput(String command, Function<CallbackArguments, Void> callback) {
        this();
        this.commandCallbacks.put(command, new FunctionWrapper(callback, command));
    }
    public CommandInput(InputStream input, String command, Function<CallbackArguments, Void> callback) {
        this(input);
        this.commandCallbacks.put(command, new FunctionWrapper(callback, command));
    }
    public CommandInput(String[] commands, Function<CallbackArguments, Void>[] callbacks) {
        this(Arrays.asList(commands), Arrays.asList(callbacks));
    }
    public CommandInput(InputStream input, String[] commands, Function<CallbackArguments, Void>[] callbacks) {
        this(input, Arrays.asList(commands), Arrays.asList(callbacks));
    }
    public CommandInput(List<String> commands, List<Function<CallbackArguments, Void>> callbacks) {
        this();
        do_assert(commands.size() == callbacks.size());
        this.input = System.in;
        for (int i = 0; i < commands.size(); i++) {
            this.commandCallbacks.put(commands.get(i), new FunctionWrapper(callbacks.get(i), commands.get(i)));
        }
    }
    public CommandInput(InputStream input, List<String> commands, List<Function<CallbackArguments, Void>> callbacks) {
        this(input);
        do_assert(commands.size() == callbacks.size());
        for (int i = 0; i < commands.size(); i++) {
            this.commandCallbacks.put(commands.get(i), new FunctionWrapper(callbacks.get(i), commands.get(i)));
        }
    }

    static void do_assert(boolean test) {
        if(!test) {
            throw new RuntimeException("Test Failed.");
        }
    }

    public Function<CallbackArguments, Void> getFunction(String command) {
        return commandCallbacks.get(command).getFunction();
    }

    public void add(String command, Function<CallbackArguments, Void> callback) {
        this.commandCallbacks.put(command, new FunctionWrapper(callback, command));
    }
    public void put(String command, Function<CallbackArguments, Void> callback){
        this.add(command, callback);
    }

    /**
     * Process a line. Searches for the longest fitting command and calls that callback.
     *
     * If no command registers for {@link #HELP_COMMAND "help"}, {@link #printHelp()} will be called.
     *
     * @param scanner A scanner to give to the callback.
     * @param line The line to process, starts with the command.
     */
    public void process(Scanner scanner, String line) {
        //todo commands with spaces
        Map.Entry<String, FunctionWrapper> longestHit = null;
        for (Map.Entry<String, FunctionWrapper> entry: this.commandCallbacks.entrySet()) {
            if (line.startsWith(entry.getKey()) && (longestHit == null || longestHit.getKey().length() < entry.getKey().length())) {
                longestHit = entry;
            }
        }
        if (longestHit != null) {
            CallbackArguments argumentsObject = new CallbackArguments(scanner, output);
            longestHit.getValue().getFunction().apply(argumentsObject);
        } else if (HELP_COMMAND.equals(line.trim())){
            this.printHelp();
        }

    }

    /**
     * Prints the help about this commands.
     */
    public void printHelp() {
        boolean isHelpOverridden = false;
        for (Map.Entry<String, FunctionWrapper> cmd : this.commandCallbacks.entrySet()) {
            this.output.println(" - " + cmd.getKey() + ":\t" + (cmd.getValue().hasHelp() ? cmd.getValue().getHelp() : cmd.getValue().getFunction()));
            if (HELP_COMMAND.equals(cmd.getKey())) {
                isHelpOverridden = true;
            }
        }
        if (!isHelpOverridden) {
            this.output.println(" - " + HELP_COMMAND + ":\tDisplays a list of available commands.");
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
            this.output.println("Enter command, end with ^D");
            line = scanner.next();
            try {
                try {
                    this.process(scanner, line);
                } catch (RuntimeException e) {
                    e.printStackTrace();
                    this.output.println("Continuing.");
                }
            } catch (Exception | StackOverflowError e) {
                e.printStackTrace();
                this.output.println("Continuing.");
            }
        }
    }
    public static class FunctionWrapper{
        private Function<CallbackArguments, Void> func;
        private String cmd;
        private String help;

        /**
         * @param callback the callback function
         * @param cmd the command string
         * @param help the help text
         */
        public FunctionWrapper(Function<CallbackArguments, Void> callback, String cmd, String help) {
            this.func = callback;
            this.cmd = cmd;
            this.help = help;
        }
        public FunctionWrapper(Function<CallbackArguments, Void> callback, String cmd) {
            this.func = callback;
            this.cmd = cmd;
        }

        public FunctionWrapper(Function<CallbackArguments, Void> callback) {
            this.func = callback;
        }

        public Function<CallbackArguments, Void> getFunction() {
            return func;
        }

        public void setFunction(Function<CallbackArguments, Void> func) {
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
            if (hasHelp()) {
                return help;
            }
            return "";
        }

        public void setHelp(String help) {
            this.help = help;
        }
    }

    /**
     * Class to give multible things as one argument to the callback functions.
     */
    public static class CallbackArguments extends ObjectWithLogger {
        public final PrintStream output;
        public final Scanner scanner;

        /**
         * Class to give multible things as one argument to the callback functions.
         *  @param scanner The scanner
         * @param output The output to print to. Use this instead of {@link System#out}.
         */
        public CallbackArguments(Scanner scanner, PrintStream output) {
            this.scanner = scanner;
            this.output = output;
        }
    }
}
