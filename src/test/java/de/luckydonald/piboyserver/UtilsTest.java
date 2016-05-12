package de.luckydonald.piboyserver;

import de.luckydonald.utils.ObjectWithLogger;
import org.junit.Test;

import java.util.logging.Level;

import static de.luckydonald.utils.Functions.getMethodName;
import static org.junit.Assert.assertEquals;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 18.02.2016
 **/
public class UtilsTest extends ObjectWithLogger {
    @Test
    public void testMethodName() {
        assertEquals("getMethodName()", "testMethodName", getMethodName());
        assertEquals("get(static)Logger() name", getLogger().getName(), getStaticLogger().getName());
    }

    @Test
    public void testAddLogConsoleHandler(){
        addLogConsoleHandler();
        assertEquals("LogLevel ALL", Level.ALL, getLogger().getHandlers()[0].getLevel());
    }
}