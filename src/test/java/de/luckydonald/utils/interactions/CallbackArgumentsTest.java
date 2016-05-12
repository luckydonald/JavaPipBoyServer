package de.luckydonald.utils.interactions;

import de.luckydonald.utils.ObjectWithLogger;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;
import static de.luckydonald.utils.interactions.CommandInput.CallbackArguments;

/**
 * Created by luckydonald
 *
 * @author luckydonald
 * @since 12.05.2016
 **/
public class CallbackArgumentsTest extends ObjectWithLogger {
    @Test
    public void testInit() throws Exception {
        Scanner s = new Scanner("ololol");
        OutputStream o = new OutputStream() {
            @Override
            public void write(int b) throws IOException {
            }
        };
        PrintStream p = new PrintStream(o);
        CallbackArguments foo = new CallbackArguments(s, p);

        assertEquals("Scanner", s, foo.scanner);
        assertEquals("Output", p, foo.output);

    }
}