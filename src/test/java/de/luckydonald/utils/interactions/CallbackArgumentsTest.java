package de.luckydonald.utils.interactions;

import de.luckydonald.utils.ObjectWithLogger;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Scanner;

import static org.junit.Assert.assertEquals;

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
        CommandInput.CallbackArguments foo = new CommandInput.CallbackArguments(s, o);

        assertEquals("Scanner", s, foo.scanner);
        assertEquals("Output", o, foo.output);

    }
}