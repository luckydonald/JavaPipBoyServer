package de.luckydonald.pipboyserver.PipBoyServer;

import de.luckydonald.pipboyserver.Messages.DataUpdate;
import de.luckydonald.pipboyserver.Messages.IDataUpdateListener;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Created by luckydonald on 15.01.16.
 */
public class Database {
    final ReentrantReadWriteLock entriesLock = new ReentrantReadWriteLock();
    private List<DBEntry> entries = new ArrayList<>();

    final ReentrantReadWriteLock updateListenerLock = new ReentrantReadWriteLock();
    private List<IDataUpdateListener> updateListener = new LinkedList<>();

    //Queue<DataUpdate> updates = new ConcurrentLinkedQueue<DataUpdate>();
    public Database() {
        //do nothing.
    }
    /*
    public Database<T>(T obj) {
        this.load(obj,0);
    }
    private void load<T>(T obj, int i) {

    }
    */
    public DBEntry add(DBDict.DictEntry entry) {
        if (entry.getDBEntry() == null) {
            throw new NullPointerException("entry.getDBEntry() is null. Did you create the DictEntry with a DBEntry?");
        }
        return this.add(entry.getDBEntry());
    }
    public DBEntry add(DBEntry e) {
        int nextFreeInt = this.getNextFreeIndex();
        this.entriesLock.writeLock().lock();
        e._setID(nextFreeInt);
        e._setDatabase(this);
        this.entries.add(nextFreeInt, e);
        this.entriesLock.writeLock().unlock();
        //DatabasePlace place = new DatabasePlace(nextFreeInt, e);
        DataUpdate update = new DataUpdate(e);
        //this.updates.add(update);
        this.updateListenerLock.readLock().lock();
        for (IDataUpdateListener listener : this.updateListener) {
            try {
                listener.onDataUpdate(update);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.updateListenerLock.readLock().unlock();
        return e;
    }
    public DataUpdate initialDump() {
        ByteArrayOutputStream allDatShit = new ByteArrayOutputStream();
        this.entriesLock.readLock().lock();
        for (DBEntry entry : this.entries) {
            try {
                allDatShit.write(entry.getBytes());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.entriesLock.readLock().unlock();
        DataUpdate update = new DataUpdate(allDatShit.toByteArray());
        return update;
    }
    public int getNextFreeIndex() {
        this.entriesLock.readLock().lock();
        int size = this.entries.size();
        for (int i = 0; i < size; i++) {
            if (this.entries.get(i) == null) {
                return i;
            }
        }
        this.entriesLock.readLock().unlock();
        return size;
    }
    public DBEntry get(int id) {
        this.entriesLock.readLock().lock();
        DBEntry entry = this.entries.get(id);
        this.entriesLock.readLock().unlock();
        if (entry.getID() == -1) {
            throw new AssertionError("Id of element in db is undefined (-1)!");
        }
        if (entry.getID() != id) {
            throw new AssertionError("Id in db differs with id of element!");
        }
        return entry;
    }


    public static void main(String[] args) {
        Database db = new Database();
        fillWithDefault(db);

        System.out.println(db.toString());
        boolean quit = false;
        while (!quit) {
            /*DataUpdate update = this.updates.poll();
            if (update == null) {
                quit = true;
                break;
            }
            byte[] bytes = update.toBytes();
            for (byte b : bytes) {
                System.out.printf("%02X ", b);
            }
            System.out.println("");
            */
        }

        System.out.println("--");
    }

    public static Database newWithDefault() {
        Database db = new Database();
        return db.fillWithDefault();
    }
    public Database fillWithDefault() {
        return fillWithDefault(this);
    }
    public static Database fillWithDefault(Database db) {
        DBDict rootDict = new DBDict(null);
        db.add(rootDict);

        DBDict invDict = new DBDict(null); //Inventory
        db.add(invDict);
        rootDict.add("Inventory", invDict);

        DBList logList = new DBList(null);      // Log
        db.add(logList);
        rootDict.add("Log", logList);

        DBDict mapDict = new DBDict(null);      // Map
        db.add(mapDict);
        rootDict.add("Map", mapDict);

        DBList perksList = new DBList(null);    // Perks
        db.add(perksList);
        rootDict.add("Perks", perksList);

        DBDict playerDict = new DBDict(null);   // PlayerInfo
        db.add(playerDict);
        rootDict.add("PlayerInfo", playerDict);

        DBList questList = new DBList(null);    // Quests
        db.add(questList);
        rootDict.add("Quests", questList);

        DBList radioList = new DBList(null);    // Radio
        db.add(radioList);
        rootDict.add("Radio", radioList);

        DBList specialList = new DBList(null);  // Special
        db.add(specialList);
        rootDict.add("Special", specialList);

        DBList statsList = new DBList(null);    // Stats
        db.add(statsList);
        rootDict.add("Stats", statsList);

        DBDict statusDict = new DBDict(null);  // Status
        db.add(statusDict);
        rootDict.add("Status", statusDict);

        DBList effectColorList = new DBList();  // EffectColor
        db.add(effectColorList);
        statusDict.add("EffectColor", effectColorList);


        DBFloat effectColorRedFloat = new DBFloat(0.08);       // 1st color
        db.add(effectColorRedFloat);
        effectColorList.append(effectColorRedFloat);

        DBFloat effectColorGreenFloat = new DBFloat(1);  // 2nd color
        db.add(effectColorGreenFloat);
        effectColorList.append(effectColorGreenFloat);

        DBFloat effectColorBlueFloat = new DBFloat(0.08);   // 3rd color
        db.add(effectColorBlueFloat);
        effectColorList.append(effectColorBlueFloat);


        DBBoolean isDataUnavailableBool = new DBBoolean(false);  // IsDataUnavailable
        db.add(isDataUnavailableBool);
        statusDict.add("IsDataUnavailable", isDataUnavailableBool);

        DBBoolean isInAnimationBool = new DBBoolean(false);  // IsInAnimation
        db.add(isInAnimationBool);
        statusDict.add("IsInAnimation", isInAnimationBool);

        DBBoolean isInAutoVanityBool = new DBBoolean(false);  // IsInAutoVanity
        db.add(isInAutoVanityBool);
        statusDict.add("IsInAutoVanity", isInAutoVanityBool);

        DBBoolean isInVatsBool = new DBBoolean(false);  // IsInVats
        db.add(isInVatsBool); // IsInVats
        statusDict.add("IsInVats", isInVatsBool);

        DBBoolean isInVatsPlaybackBool = new DBBoolean(false);  // IsInVatsPlayback
        db.add(isInVatsPlaybackBool); // IsInVatsPlayback
        statusDict.add("IsInVatsPlayback", isInVatsBool);

        DBBoolean isLoadingBool = new DBBoolean(false);  // IsLoading
        db.add(isLoadingBool); // IsLoading
        statusDict.add("IsLoading", isLoadingBool);

        DBBoolean isMenuOpenBool = new DBBoolean(false);  // IsMenuOpen
        db.add(isMenuOpenBool); // IsMenuOpen
        statusDict.add("IsMenuOpen", isMenuOpenBool);

        DBBoolean isPipboyNotEquippedBool = new DBBoolean(false);  // IsPipboyNotEquipped
        db.add(isPipboyNotEquippedBool); // IsPipboyNotEquipped
        statusDict.add("IsPipboyNotEquipped", isPipboyNotEquippedBool);

        DBBoolean isPlayerDeadBool = new DBBoolean(false);  // IsPlayerDead
        db.add(isPlayerDeadBool); // IsPlayerDead
        statusDict.add("IsPlayerDead", isPlayerDeadBool);

        DBBoolean isPlayerInDialogueBool = new DBBoolean(false);  // IsPlayerInDialogue
        db.add(isPlayerInDialogueBool);
        statusDict.add("IsPlayerInDialogue", isPlayerInDialogueBool);

        DBBoolean isPlayerMovementLockedBool = new DBBoolean(false);  // IsPlayerMovementLocked
        db.add(isPlayerMovementLockedBool); // IsPlayerMovementLocked
        statusDict.add("IsPlayerMovementLocked", isPlayerMovementLockedBool);

        DBBoolean isPlayerPipboyLockedBool = new DBBoolean(false);  // IsPlayerPipboyLocked
        db.add(isPlayerPipboyLockedBool); // IsPlayerPipboyLocked
        statusDict.add("IsPlayerPipboyLocked", isPlayerPipboyLockedBool);

        DBList workshopList = new DBList();  // Workshop
        db.add(workshopList); // Workshop
        rootDict.add("Workshop", workshopList);
        return db;
    }

    public void registerDataUpdateListener(IDataUpdateListener listener) {
        this.updateListenerLock.writeLock().lock();
        this.updateListener.add(listener);
        this.updateListenerLock.writeLock().unlock();
        listener.onDataUpdate(this.initialDump());
    }
    public void unregisterDataUpdateListener(IDataUpdateListener listener) {
        this.updateListenerLock.writeLock().lock();
        this.updateListener.remove(listener);
        this.updateListenerLock.writeLock().unlock();
    }

    @Override
    public String toString() {
        String s =  "Database(entries=[";
        int end = -1;
        this.entriesLock.readLock().lock();
        end = this.entries.size() - 1;
        if (end >= 0) {

            for (int i = 0; i < end; i++) {
                DBEntry entry = this.entries.get(i);
                if (entry == null) {
                    continue;
                }
                s += "" + i + ": " + entry.toString() + ", ";
            }
            s += "" + end + ": " + this.entries.get(end).toString();
        }
        this.entriesLock.readLock().unlock();
        s+="], updateListener=[";
        this.updateListenerLock.readLock().lock();
        end = this.updateListener.size() - 1;
        if (end >= 0) {
            for (int i = 0; i < end; i++) {
                IDataUpdateListener entry = this.updateListener.get(i);
                s += entry.toString() + ", ";
            }
            s+= "" + end + ": " + this.updateListener.get(end).toString();
        }
        this.updateListenerLock.readLock().unlock();
        s+="])";
        return s;
    }

    public String toSimpleString() {
        String s =  "Database(entries=[";
        int end = -1;
        this.entriesLock.readLock().lock();
        end = this.entries.size() - 1;
        if (end >= 0) {

            for (int i = 0; i < end; i++) {
                DBEntry entry = this.entries.get(i);
                if (entry == null) {
                    continue;
                }
                s += "" + i + ": " + entry.toSimpleString() + ", ";
            }
            s += "" + end + ": " + this.entries.get(end).toString();
        }
        this.entriesLock.readLock().unlock();
        s+="], updateListener=[";
        this.updateListenerLock.readLock().lock();
        end = this.updateListener.size() - 1;
        if (end >= 0) {
            for (int i = 0; i < end; i++) {
                IDataUpdateListener entry = this.updateListener.get(i);
                s += entry.toString() + ", ";
            }
            s+= "" + end + ": " + this.updateListener.get(end).toString();
        }
        this.updateListenerLock.readLock().unlock();
        s+="])";
        return s;
    }
}
class DatabasePlace {
    int id;
    DBEntry value;
    public DatabasePlace(int id, DBEntry value) {
        this.id = id;
        this.value = value;
    }
}




/*
        3: 'INT_32', // 4: signed
        4: 'UINT_32', // 4: unsigned
        5: 'FLOAT', // 4: float
        6: 'STRING', // n: null terminated, dynamic length
        7: 'ARRAY', // 2: element count; then $n 4 byte nodeId
        8: 'OBJECT', // 2: element count; then
                     // $n 4 byte nodeId with null terminated string following; then
                     // 2: removed element count; then
                     // $n 4 byte removed nodeId with null terminated string following
*/