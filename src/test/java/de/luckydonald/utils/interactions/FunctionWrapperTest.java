package de.luckydonald.utils.interactions;

import de.luckydonald.utils.ObjectWithLogger;
import static de.luckydonald.utils.interactions.CommandInput.FunctionWrapper;
import static de.luckydonald.utils.interactions.CommandInput.CallbackArguments;
import org.junit.Before;
import org.junit.Test;

import java.util.Scanner;
import java.util.function.Function;

import static org.junit.Assert.*;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 12.05.2016
 **/
public class FunctionWrapperTest extends ObjectWithLogger {
    private FunctionWrapper wrapper = null;
    private String command0 = "test0";
    private String command1 = "test1";
    private String help0 = "this is the help0";
    private String help1 = "this is the help1";
    private Function<CallbackArguments, Void> callback0 = this::helper_callback0;
    private Function<CallbackArguments, Void> callback1 = this::helper_callback1;

    @Before
    public void setUp() throws Exception {
        wrapper = new FunctionWrapper(callback0);
    }

    @Test
    public void testGetFunction() throws Exception {
        assertEquals("getFunction", callback0, wrapper.getFunction());
    }

    @Test
    public void testSetFunction() throws Exception {
        testGetFunction();
        wrapper.setFunction(callback1);
        assertEquals("setFunction", callback1, wrapper.getFunction());
    }

    @Test
    public void testGetCmd() throws Exception {
        assertNull("getCmd, not set.", wrapper.getCmd());
        wrapper = new FunctionWrapper(callback0, command0);
        assertEquals("getCmd, set.", command0, wrapper.getCmd());
    }

    @Test
    public void testSetCmd() throws Exception {
        testGetCmd();
        wrapper.setCmd(command1);
        assertEquals("setCmd", command1, wrapper.getCmd());
    }

    @Test
    public void testHasHelp() throws Exception {
        assertFalse("hasHelp w/o help set", wrapper.hasHelp());
        wrapper.setHelp(help0);
        assertTrue("hasHelp with help set", wrapper.hasHelp());
    }

    @Test
    public void testGetHelp() throws Exception {
        assertEquals("getHelp, help not set", "", wrapper.getHelp());
        wrapper.setHelp(help0);
        assertEquals("getHelp, help set", help0, wrapper.getHelp());
    }

    @Test
    public void testSetHelp() throws Exception {
        wrapper.setHelp(help1);
        assertEquals("setHelp, help set", help1, wrapper.getHelp());
    }

    @Test
    public void testInits() {
        FunctionWrapper wrappers[] = new FunctionWrapper[3];
        wrappers[0] = wrapper;
        wrappers[0].setCmd(command0);
        wrappers[0].setHelp(help0);
        wrappers[1] = new FunctionWrapper(callback0, command0);
        wrappers[1].setHelp(help0);
        wrappers[2] = new FunctionWrapper(callback0, command0, help0);
        for (FunctionWrapper testedWrapper : wrappers) {
            assertEquals("Wrapper callback function", callback0, testedWrapper.getFunction());
            assertEquals("Wrapper command string", command0, testedWrapper.getCmd());
            assertEquals("Wrapper help text", help0, testedWrapper.getHelp());
        }
    }

    public Void helper_callback0(CallbackArguments args) {
        this.getLogger().info("called callback0");
        return null;
    }

    public Void helper_callback1(CallbackArguments args) {
        this.getLogger().info("called callback1");
        return null;
    }

}