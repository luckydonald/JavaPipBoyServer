package de.luckydonald.pipboyserver.PipBoyServer.input;

import de.luckydonald.utils.ObjectWithLogger;

import static de.luckydonald.utils.Functions.getMethodName;

/**
 * @author luckydonald
 * @since 18.02.2016
 **/
public class BinFileReadLogger extends ObjectWithLogger {
    private long startPosition;
    private long endPosition;
    private final String name;

    // Constructor
    public BinFileReadLogger(long startPosition) {
        this(getMethodName(), startPosition);
    }
    public BinFileReadLogger(String methodName, long startPosition) {
        this.name = methodName;
        this.startPosition = startPosition;
    }

    // Setter
    public BinFileReadLogger setStartPosition(int startPosition) {
        this.startPosition = startPosition;
        return this;
    }

    public BinFileReadLogger setEndPosition(long endPosition) {
        this.endPosition = endPosition;
        return this;
    }

    // Getter
    public long getStartPosition() {
        return startPosition;
    }

    public long getEndPosition() {
        return endPosition;
    }

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return  ""+
                //"BinFileReadLogger" +
                name +
                "[" +
                "" + startPosition +
                ", " + endPosition +
                ']';
    }
}