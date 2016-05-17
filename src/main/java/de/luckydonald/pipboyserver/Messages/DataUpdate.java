package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;
import de.luckydonald.pipboyserver.PipBoyServer.types.DBEntry;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataUpdate extends Message{
    private List<DBEntry> entries = new ArrayList<DBEntry>();

    public DataUpdate(DBEntry e) {
        super();
        this.entries.add(e); //TODO: deep copy
    }

    public DataUpdate(List<DBEntry> entries) {
        super();
        this.entries.addAll(entries);

    }
    public DataUpdate(DBEntry[] entries) {
        this(Arrays.asList(entries));
    }
    public DataUpdate addEntry(DBEntry e) {
        this.entries.add(e);
        return this;
    }

    @Override
    public MESSAGE_CHANNEL getType() {
        return MESSAGE_CHANNEL.DataUpdate;
    }

    @Override
    public byte[] toBytes() {
        int size = 0;
        for (DBEntry entry : this.entries) {
            size += entry.getRequiredBufferLength();
        }
        getLogger().info("Preparing buffer with "+ size + " bytes.");
        ByteArrayOutputStream allContent = new ByteArrayOutputStream(size);
        int i = 1;
        for (DBEntry entry : this.entries) {
            getLogger().fine("Packaging Entry " + (i++) + " of " + this.entries.size() + " (ID: " + entry.getID() + ")");
            try {
                allContent.write(entry.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.setContent(allContent.toByteArray());
        getLogger().info("Data is ready to send.");
        return super.toBytes();
    }

    public String toString(boolean simple) {
        StringBuilder sb = new StringBuilder("DataUpdate(");
        boolean notFirst = false;
        for (DBEntry entry : this.entries) {
            if (notFirst) {
                sb.append(", ");
            } else {
                notFirst = true;
            }
            if (simple) {
                sb.append(entry.toSimpleString());
            } else {
                sb.append(entry.toString());
            }
        }
        return sb.append(")").toString();
    }
    public String toSimpleString() {
        return this.toString(true);
    }
    @Override
    public String toString() {
        return this.toString(false);
    }
}

/*
struct Entry {
  uint8_t type; // 8 bits
  uint32_t id;
  switch (type) {
    case 0:
      uint8_t boolean;
      break;
    case 1:
      sint8_t integer;
      break;
    case 2:
      uint8_t integer;
      break;
    case 3:
      sint32_t integer;
      break;
    case 4:
      uint32_t integer;
      break;
    case 5:
      float32_t floating_point;
      break;
    case 6:
      char_t *string; // zero-terminated
      break;
    case 7: // list
      uint16_t count;
      uint32_t references[count];
      break;
    case 8:
      uint16_t insert_count;
      DictEntry[insert_count];
      uint16_t remove_count;
      uint32_t references[remove_count];
      break;
  }
};
struct DictEntry {
      uint32_t reference;
      char_t *name; // zero-terminated
};
 */