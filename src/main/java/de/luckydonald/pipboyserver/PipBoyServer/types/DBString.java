package de.luckydonald.pipboyserver.PipBoyServer.types;

import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.EntryType;
import java.util.logging.Logger;

import java.nio.ByteBuffer;

public class DBString extends DBEntry {
    public static final EntryType TYPE = EntryType.STRING;
    private String value;

    public DBString(Database db, String value) {
        super(db);
        this.value = value;
    }

    @Override
    public EntryType getType() {
        return TYPE;
    }

    @Override
    public ByteBuffer putValueIntoBuffer(ByteBuffer b) {
        b.put(this.value.getBytes());
        b.put((byte) 0); // Null Termination
        return b;
    }

    @Override
    public int getRequiredValueBufferLength() {
        return this.value.length()+1; //+1 because Null-Termination.
    }

    @Override
    public String toString(boolean showId) {
        return "DBString(\"" + value + "\")";
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
        this.dirty = true;
    }

    /**
     * Presents the data in a simple non-recursive way, printing the ids instead.
     * This should only differ to the {@link #toString()} for {@link DBList} and {@link DBDict}.
     *
     * @param showId
     * @return
     */
    @Override
    public String toSimpleString(boolean showId) {
        return super.toSimpleString(showId);
    }

    /**
     * Method to use for accessing String values.
     * Does <b>NOT</b> do any conversions for non-String value nodes;
     * for non-String values (ones for which {@link #isTextual} returns
     * false) null will be returned.
     * For String values, null is never returned (but empty Strings may be)
     *
     * @return Textual value this node contains, iff it is a textual
     * JSON node (comes from JSON String value entry)
     */
    @Override
    public String textValue() {
        return this.getValue();
    }
}
