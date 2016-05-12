import static de.luckydonald.utils.interactions.CommandInput.CallbackArguments;
import de.luckydonald.utils.interactions.CommandInput;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.Scanner;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * @author luckydonald
 * @since 12.02.2016
 **/
public class CommandInputTests {
    private String var_test_commands[] = {"test", "foobar"};
    private boolean[] use_callback_was_called = {false, false};
    private Function<CallbackArguments, Void>[] var_callback_functions = new Function[2];
    private InputStream exp_in_stream;
    private CommandInput use_cmd_in;
    private CommandInput cmd_in;
    private Scanner exp_scanner;

    @Before
    public void setUp() throws IOException {
        use_cmd_in = null;

        exp_in_stream = new InputStream() {
            @Override
            public int read() throws IOException {
                return 0;
            }
        };
        var_callback_functions[0] = this::helper_callback0;
        var_callback_functions[1] = this::helper_callback1;
        exp_scanner = new Scanner(this.exp_in_stream);
    }

    @Test
    public void testInit_noArgs() {
        if (use_cmd_in == null) {
            this.use_cmd_in = new CommandInput();
        }
        assertEquals("CommandInput() InputStream is System.in", System.in, use_cmd_in.input);
    }

    @Test
    public void testInit_Stream() {
        if (use_cmd_in == null) {
            this.use_cmd_in = new CommandInput(exp_in_stream);
        }
        assertEquals("CommandInput(InputStream) InputStream", exp_in_stream, use_cmd_in.input);
    }

    @Test
    public void testInit_Command() {
        for (int i = 0; i < var_test_commands.length; i++) {
            this.use_cmd_in = new CommandInput(var_test_commands[i], var_callback_functions[i]);
            testInit_noArgs();
            assertEquals("getFunction() [" + i + "]", use_cmd_in.getFunction(var_test_commands[i]), var_callback_functions[i]);
        }
    }

    @Test
    public void testInit_Command_Stream() {
        for (int i = 0; i < var_test_commands.length; i++) {
            this.use_cmd_in = new CommandInput(exp_in_stream, var_test_commands[i], var_callback_functions[i]);
            testInit_Stream();
            assertEquals("getFunction() [" + i + "]", use_cmd_in.getFunction(var_test_commands[i]), var_callback_functions[i]);
        }
    }

    @Test
    public void testInit_Commands() {
        if (use_cmd_in == null) {
            this.use_cmd_in = new CommandInput(var_test_commands, var_callback_functions);
            testInit_noArgs();
        }
        for (int i = 0; i < var_test_commands.length; i++) {
            assertEquals("getFunction() [" + i + "]", use_cmd_in.getFunction(var_test_commands[i]), var_callback_functions[i]);
        }
    }

    @Test
    public void testInit_Commands_Stream() {
        this.use_cmd_in = new CommandInput(exp_in_stream, var_test_commands, var_callback_functions);
        testInit_Stream();
        testInit_Commands();
    }

    @Test
    public void testAdd() {
        this.use_cmd_in = new CommandInput();
        testInit_noArgs();
        for (int i = 0; i < var_test_commands.length; i++) {
            if (i%2 == 0) { //even -> test add(); odd -> test put()
                this.use_cmd_in.add(var_test_commands[i], var_callback_functions[i]);
            } else {
                this.use_cmd_in.put(var_test_commands[i], var_callback_functions[i]);
            }
        }
        testInit_Commands();
    }

    @Test
    public void testProcess_Commands() {
        testInit_Commands();
        for (int i = 0; i < var_test_commands.length; i++) {
            assertFalse("process() [i]", use_callback_was_called[i]);
            this.use_cmd_in.process(this.exp_scanner, var_test_commands[i]);
            assertTrue("process() [i]", use_callback_was_called[i]);
        }
    }

    @Test
    public void testProcess_Help() {
        if (this.use_cmd_in == null) {
            this.testInit_Commands();
        }
        assertNotNull("setup", this.use_cmd_in);
        ByteArrayOutputStream outContent = new ByteArrayOutputStream();
        PrintStream out = new PrintStream(outContent);
        this.use_cmd_in.output = out;
        System.setOut(out);
        this.use_cmd_in.printHelp();
        String exp_help = outContent.toString();
        out.flush();  // reset
        outContent.reset();
        use_cmd_in.process(exp_scanner, "help");
        String result = outContent.toString();
        System.setOut(System.out);
        assertNotEquals("printHelp not empty", "", exp_help);
        assertTrue("printHelp().length > 0", exp_help.length() > 0);
        assertTrue("process() \"help\" > 0", result.length() > 0);
        assertEquals("process() \"help\"", exp_help, result);
    }

    @Test
    public void testProcess_Help_Empty() {
        testInit_noArgs();
        testProcess_Help();
    }

    public Void helper_callback0(CallbackArguments args) {
        use_callback_was_called[0] = true;
        return null;
    }
    public Void helper_callback1(CallbackArguments args) {
        use_callback_was_called[1] = true;
        return null;
    }
}