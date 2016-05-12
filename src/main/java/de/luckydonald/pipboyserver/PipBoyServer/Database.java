package de.luckydonald.pipboyserver.PipBoyServer;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ContainerNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import de.luckydonald.pipboyserver.Messages.DataUpdate;
import de.luckydonald.pipboyserver.Messages.IDataUpdateListener;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyInsertedException;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyTakenException;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.KeyDoesNotExistsException;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.ParserException;
import de.luckydonald.pipboyserver.PipBoyServer.input.BinFileReader;
import de.luckydonald.pipboyserver.PipBoyServer.types.*;
import de.luckydonald.utils.interactions.CommandInput;
import de.luckydonald.utils.ObjectWithLogger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.net.URL;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Function;
import java.util.logging.Level;

/**
 * This is the Database for the Pip Boy.
 *
 * Created by luckydonald on 15.01.16.
 */
public class Database extends ObjectWithLogger {
    public static final String DEFAULT_JSON_URL = "https://raw.githubusercontent.com/NimVek/pipboy/1087a1c820fae6265fbce2a614e62e85cd146442/DemoMode.json";
    public static final String DEFAULT_BIN_URL = "http://luckydonald.github.io/OfflineData.bin";
    final ReentrantReadWriteLock updateListenerLock = new ReentrantReadWriteLock();
    private final ReentrantReadWriteLock entriesLock = new ReentrantReadWriteLock();

    private HashMap<Integer, DBEntry> entries = new HashMap<>();
    private List<IDataUpdateListener> updateListener = new LinkedList<>();

    public ReentrantReadWriteLock getEntriesLock() {
        return entriesLock;
    }

    public DBEntry add(DBDict.DictEntry entry) {
        if (entry.getDBEntry() == null) {
            throw new IllegalArgumentException("entry.getDBEntry() is null. Did you create the DictEntry with a DBEntry?");
        }
        return this.add(entry.getDBEntry());
    }

    public DBEntry add(DBEntry e) {
        this.entriesLock.writeLock().lock();
        int nextFreeInt = this.getNextFreeIndex();
        this.add(nextFreeInt, e);
        this.entriesLock.writeLock().unlock();
        return e;
    }

    public DBEntry add(int id, DBEntry e) {
        this.entriesLock.writeLock().lock();
        if (has(id)) {
            this.entriesLock.writeLock().unlock();
            throw new IllegalArgumentException("Id already existing.");
        }
        this.entries.put(id, e);
        e._setID(id);
        e._setDatabase(this);
        this.entriesLock.writeLock().unlock();
        getLogger().finest("Added on id " + id + ": " + e.toSimpleString(false));
        DataUpdate update = new DataUpdate(e);
        queueDataUpdate(update);
        return e;
    }
    public boolean has(int id) {
        this.entriesLock.readLock().lock();
        boolean contains = this.entries.containsKey(id);
        this.entriesLock.readLock().unlock();
        return contains;
    }
    public boolean has(DBEntry entry) {
        this.entriesLock.readLock().lock();
        if (this.entries.containsValue(entry)) {
            this.entriesLock.readLock().unlock();
            return true;
        }
        if (this.has(entry.getID())) {
            getLogger().warning("But the Database has a element at the same ID.");
        }
        this.entriesLock.readLock().unlock();
        return false;
    }

    public synchronized void queueDataUpdate(DataUpdate update) {
        this.updateListenerLock.readLock().lock();
        for (IDataUpdateListener listener : this.updateListener) {
            try {
                listener.onDataUpdate(update);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        this.updateListenerLock.readLock().unlock();
    }

    public Void cmdList(Scanner command) {
        print();
        return null;
    }
    public Void cmdTest(Scanner command) {
        System.out.println("command: \"" + command.nextLine() + "\".");
        return null;
    }
    public Void cmdGet(Scanner scanner) {
        cmdGetter(scanner);
        return null;
    }
    public LinkedList<String> cmdGetter(Scanner scanner) {
        LinkedList<String> levels = new LinkedList<>();
        boolean isJustStarted = true;
        DBEntry e;
        while (scanner.hasNextLine()) {
            String lineInput = scanner.nextLine();
            if (isJustStarted) {
                isJustStarted = false;
                e = this.get("");
                System.out.println("(" + e.getID() + "):\t" + e.toSimpleString(false));
            } else if ("".equals(lineInput.trim())){
                // empty row => done
                break;
            }
            Scanner ss = new Scanner(lineInput);
            while (ss.hasNext()) {
                String input = ss.next();
                if ("..".equals(input.trim())) {
                    levels.removeLast();
                } else {
                    for (String part : input.split("\\.")) {
                        if (!("".equals(part.trim()))) {
                            levels.add(part);
                            try {
                                this.get(String.join(".", levels));  // just try to see if it raises an exception.
                            } catch (IndexOutOfBoundsException | NumberFormatException ex) {
                                levels.removeLast();
                                System.out.println("Failed to go further as \"" + String.join(".", levels) + "\". Ignoring rest. (" + ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage() + ")");
                                getLogger().log(Level.FINE,ex.toString(), ex);
                                break;
                            }
                        }
                        getLogger().fine("Skipped empty path part.");
                    }
                }
                String key = String.join(".", levels);
                try {
                    e = this.get(key);
                    System.out.println(key + " (" + e.getID() + "):\t" + e.toSimpleString(false));
                } catch (IndexOutOfBoundsException | NumberFormatException ex) {
                    System.out.println("Failed with " + ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage());
                    getLogger().log(Level.FINE,ex.toString(), ex);
                    levels.removeLast();
                }
            }
            System.out.println(" _");
            System.out.println("| Give a key/index where to go next or \"..\" to move up agan.");
            System.out.println("| Blank line exits.");
            System.out.print("'>");
        }
        return levels;
    }
    //Radio.1.text Günters Radio
    public Void cmdSet(Scanner scanner) {
        boolean isSimple = false;
        String key = null;
        DBEntry e = null;
        while (!isSimple) {
            LinkedList<String> levels = cmdGetter(scanner);
            key = String.join(".", levels);
            e = this.get(key);
            if (e.isContainer()) {
                System.out.println("Selected element is a container. Please choose a simple element.");
            } else {
                isSimple = true;
            }
        }
        System.out.println(key + " (" + e.getID() + "):\t" + e.toSimpleString(false));
        boolean didUpdate = false;
        while (!didUpdate) {
            System.out.println(" _");
            System.out.println("| Enter your new value.");
            System.out.print("'>");
            String line = scanner.nextLine();
            try {
                e = ((DBSimple) e).setValueFromString(line);
                didUpdate = true;
            } catch (ParserException ex) {
                System.out.println("Failed with " + ex.getClass().getSimpleName() + ": " + ex.getLocalizedMessage());
                getLogger().log(Level.FINE,ex.toString(), ex);
            }
        }
        System.out.println(key + " (" + e.getID() + "):\t" + e.toSimpleString(false));
        return null;
    }

    public Void cmdImport(Scanner scanner) {
        String s = scanner.nextLine();
        s = ("".equals(s.trim()) ? "OfflineData.bin" : s);
        while (!("".equals(s.trim()))) {
            File f = new File("OfflineData.bin");
            if (f.exists() && !f.isDirectory()) {
                // do something
                try {
                    BinFileReader binFileReader = new BinFileReader(new File("OfflineData.bin"));
                    Database db = new Database();
                    binFileReader.readNextEntry(db);
                } catch (IOException e) {
                    e.printStackTrace();
                    s = scanner.nextLine().trim();
                }
                this.print();
            }
        }
        return null;
    }

    /**
     * Print the database in a readable format.
     * Formatted like {@code "<id>:  <value>"}.
     */
    public void print() {
        this.entriesLock.readLock().lock();
        for (Map.Entry<Integer, DBEntry> entry : this.entries.entrySet()) {
            System.out.println(entry.getKey() + ":\t" + entry.getValue().toSimpleString(false));
            if (!entry.getKey().equals(entry.getValue().getID())) {
                getLogger().severe("IDs are different! DB says " + entry.getKey() + ", while DBEntry says " + entry.getValue().getID());
            }
        }
        entriesLock.readLock().unlock();
    }

    /**
     * Starts a new {@link CommandInput} instance, and registers the
     * "{@link #cmdList(Scanner) list}", "{@link #cmdGet(Scanner) get}", "{@link #cmdSet(Scanner) set}",
     * "{@link #cmdTest(Scanner) test}" and "{@link #cmdImport(Scanner) import}"
     * commands.
     *
     * Note: You could start multiple instances, all trying to use the same input. That would probably be a bad thing.
     *
     * @param input  the input used.
     * @param output the output used.
     *
     * If you are happy with System.in and System.out, use it {@link #startCLI() without parameters}.
     *
     * @see #startCLI() startCLI()
     */
    public void startCLI(InputStream input, PrintStream output) {
        Function<Scanner, Void> f = this::cmdList;
        CommandInput cmd = new CommandInput("list", f);
        cmd.input = input;
        cmd.output = output;
        f = this::cmdGet;
        cmd.add("get", f);
        f = this::cmdSet;
        cmd.add("set", f);
        f = this::cmdTest;
        cmd.add("test", f);
        f = this::cmdImport;
        cmd.add("import", f);
        cmd.start();
    }
    /**
     * Starts a new {@link CommandInput} instance, and registers the
     * "{@link #cmdList(Scanner) list}", "{@link #cmdGet(Scanner) get}", "{@link #cmdSet(Scanner) set}",
     * "{@link #cmdTest(Scanner) test}" and "{@link #cmdImport(Scanner) import}"
     * commands.
     *
     * The input stream to read from defaults to {@link System#in}, the output to {@link System#out}.
     * Use {@link #startCLI(InputStream, PrintStream)} if you need something else.
     *
     * Note: You could start multiple instances, all trying to use stdin. That would probably be a bad thing.
     *
     * @see #startCLI(InputStream, PrintStream) startCLI(InputStream, PrintStream)
     */
    public void startCLI() {
        this.startCLI(System.in, System.out);
    }

    public DataUpdate initialDump() {
        this.entriesLock.readLock().lock();
        DataUpdate update = new DataUpdate(new ArrayList<>(this.entries.values()));
        this.entriesLock.readLock().unlock();
        return update;
    }
    public int getNextFreeIndex() {
        this.entriesLock.readLock().lock();
        int size = this.entries.size();
        for (int i = 0; i < size+1; i++) {
            if (!this.entries.containsKey(i)) {
                this.entriesLock.readLock().unlock();
                return i;
            }
        }
        this.entriesLock.readLock().unlock();
        return size;
    }
    public DBEntry get(String path) {
        getLogger().finest("locking DB: read");
        getEntriesLock().readLock().lock();
        DBEntry node = this.get(0);
        String[] parts = path.split("\\.");
        if (parts.length == 0) {
            parts = new String[]{path};
        }
        for (String s : parts) {
            if (s.length() == 0) {
                continue;
            }
            switch (node.getType()) {
                case DICT:
                    node = ((DBDict)node).get(s);
                    if (node == null){
                        getEntriesLock().readLock().unlock();
                        getLogger().finest("unlocked DB: read");
                        throw new KeyDoesNotExistsException("There should be the key \"" + s + "\". It doesn't exists.");
                    }
                    break;
                case LIST:
                    try {
                        int index = new Integer(s);
                        node = ((DBList) node).get(index);
                    } catch (IndexOutOfBoundsException e) {
                        getEntriesLock().readLock().unlock();
                        getLogger().finest("unlocked DB: read");
                        throw e;
                    }
                    break;
                default: // (FLOAT, BOOLEAN, INT8, INT32, STRING)
                    getEntriesLock().readLock().unlock();
                    getLogger().finest("unlocked DB: read");
                    throw new KeyDoesNotExistsException("There should be the key \"" + s + "\". But have a "+ node.getType() + " object: " + node);
            }
        }
        getEntriesLock().readLock().unlock();
        getLogger().finest("unlocked DB: read");
        return node;
    }
    public DBEntry get(int id) {
        this.entriesLock.readLock().lock();
        DBEntry entry = this.entries.get(id);
        this.entriesLock.readLock().unlock();
        if (entry.getID() == -1 ||entry.getID() == null) {
            throw new AssertionError("Id of element in db is undefined (-1/null)!");
        } else if (entry.getID() != id) {
            throw new AssertionError("Id in db differs with id of element!");
        } else {
            return entry;
        }
    }
    public DBContainer getRootContainer() {
        DBEntry root = this.getRoot();
        if (root.isContainer()) {
            return (DBContainer) root;
        }
        else {
            throw new ClassCastException("Root is no Container, but " + root.getType().toString());
        }
    }
    public DBEntry getRoot() {
        if(!has(0)) {
            throw new ArrayIndexOutOfBoundsException("No root (id=0) found. Did you add a DBDict or DBList?");
        }
        return this.get(0);
    }

    /*public static void main(String[] args) {
        Database db = new Database();
        fillWithBasicDefault(db);
        db.print();
        boolean quit = false;
        while (!quit) {
            DataUpdate update = this.updates.poll();
            if (update == null) {
                quit = true;
                break;
            }
            byte[] bytes = update.toBytes();
            for (byte b : bytes) {
                System.out.printf("%02X ", b);
            }
            System.out.println("");
        }

        System.out.println("--");
    }*/

    public static Database newWithDefault() {
        Database db = new Database();
        return db.fillWithDefault();
    }

    public Database fillWithDefault() {
        return fillWithDefault(this);
    }

    /**
     * Creates a new database and adds some very basic values into the database.
     * @return The created database
     */
    public static Database newWithBasicDefault() {
        Database db = new Database();
        return db.fillWithBasicDefault();
    }

    /**
     * Inserts some very basic values into the database.
     * @return The created database
     */
    public Database fillWithBasicDefault() {
        return fillWithBasicDefault(this);
    }

    /**
     * Inserts some very basic values into the database.
     * @param db The database
     * @return The same database
     */
    public static Database fillWithBasicDefault(Database db) {
        try {
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

            DBList effectColorList = new DBList(null);  // EffectColor
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
        } catch (AlreadyTakenException | AlreadyInsertedException e) {
            e.printStackTrace();
        }
        return db;

    }

    /**
     * Inserts values into the database.
     *
     * @param db The database
     * @return The same database
     */
    public static Database fillWithDefault(Database db) {
        db.getLogger().info("Filling with default.");
        try {
            return fillWithBinFile(db, "OfflineData.bin");
        } catch (IOException e) {
            db.getLogger().warning("Could not load default OfflineData.bin from disk: " + e.toString());
        }
        // would have returned.
        try {
            return fillWithBinFile(db, new URL(DEFAULT_BIN_URL));
        } catch (IOException exc) {
            db.getLogger().warning("Could not load default OfflineData.bin from " + DEFAULT_BIN_URL + ": " + exc.toString());
        }
        db.getLogger().warning("Using the basic default instead.");
        return fillWithBasicDefault(db);
    }

    /**
     * Fills the database with a given .bin file.
     *
     * This file is created by the mobile apps, and contains the data the device got from the game.
     * It will be loaded by the app, when a user activates the offline mode. Android saves that file on the SD card.
     *
     * @param db The database to fill
     * @param path the path of the file
     * @return the same database instance
     * @throws IOException
     */
    public static Database fillWithBinFile(Database db, String path) throws IOException {
        return fillWithBinFile(db, new File(path));
    }

    /**
     * Fills the database with a given .bin file, which will be downloaded from the given url.
     *
     * This file is created by the mobile apps, and contains the data the device got from the game.
     * It will be loaded by the app, when a user activates the offline mode. Android saves that file on the SD card.
     *
     * @param db The database to fill
     * @param url the url of the file
     * @return the same database instance
     * @throws IOException
     */
    public static Database fillWithBinFile(Database db, URL url) throws IOException {
        return fillWithBinFile(db, url);
    }

    /**
     * Fills the database with a given .bin file.
     *
     * @see #fillWithBinFile(Database, BinFileReader)
     *
     *
     * @param db The database to fill
     * @param file the file to load
     * @return the same database instance
     * @throws IOException
     */
    public static Database fillWithBinFile(Database db, File file) throws IOException {
        BinFileReader binFileReader = new BinFileReader(file);
        binFileReader.readNextEntry(db);
        return db;
    }
    /**
     * Fills the database with a given .bin file.
     *
     * This file is created by the mobile apps, and contains the data the device got from the game.
     * It will be loaded by the app, when a user activates the offline mode. Android saves that file on the SD card.
     *
     * @param db The database to fill
     * @param binFileReader The BinFileReader instance.
     * @return the same database instance
     * @throws IOException
     */
    public static Database fillWithBinFile(Database db, BinFileReader binFileReader) throws IOException {
        binFileReader.readNextEntry(db);
        return db;
    }
    public void loadJsonRoot(ObjectNode node) {
        DBDict rootDict = new DBDict(null);
        this.add(rootDict);
        this.loadJsonNode(rootDict, node);
    }

    public void loadJsonNode(DBDict currentLevel, ObjectNode dictNode) {
        Iterator it = dictNode.fields();
        while (it.hasNext()) {
            Map.Entry<String, JsonNode> entry = (Map.Entry<String, JsonNode>) it.next();
            JsonNode subnode = entry.getValue();
            String key = entry.getKey();
            jsonNodeToDBEntry(currentLevel, dictNode, subnode, key);
        }
    }

    public void jsonNodeToDBEntry(DBEntry currentLevel, ContainerNode dictNode, JsonNode subnode, String key) {
        DBEntry dbEntry;
        switch (subnode.getNodeType()) {
            case ARRAY:
                dbEntry = new DBList(this);
                break;
            case BOOLEAN:
                dbEntry = new DBBoolean(subnode.asBoolean());
                break;
            case NUMBER:
                if (subnode.isFloatingPointNumber()) {
                    dbEntry = new DBFloat(this, subnode.floatValue());
                    break;
                }
                dbEntry = new DBInteger32(this, subnode.intValue());
                break;
            case OBJECT:
            case POJO:
                dbEntry = new DBDict(this);
                break;
            case STRING:
                dbEntry = new DBString(this, subnode.asText());
                break;
            case BINARY:
            case MISSING:
            case NULL:
            default:
                throw new IllegalStateException("What is that json? " + dictNode.toString());
        }
        this.add(dbEntry);
        if (currentLevel instanceof DBDict) {
            try {
                ((DBDict)currentLevel).add(key, dbEntry);
            } catch (AlreadyInsertedException | AlreadyTakenException e) {
                e.printStackTrace();
            }
        } else if (currentLevel instanceof DBList) {
            try {
                ((DBList)currentLevel).append(dbEntry);
            } catch (AlreadyInsertedException | AlreadyTakenException e) {
                e.printStackTrace();
            }
        }
        if (dbEntry instanceof DBList) {
            this.loadJsonNode((DBList) dbEntry, ((ArrayNode) subnode));
        } else if (dbEntry instanceof DBDict) {
            this.loadJsonNode((DBDict) dbEntry, ((ObjectNode) subnode));
        }
    }

    private void loadJsonNode(DBList currentLevel, ArrayNode listNode) {
        Iterator it = listNode.elements();
        while (it.hasNext()) {
            JsonNode subnode = (JsonNode) it.next();
            this.jsonNodeToDBEntry(currentLevel, listNode, subnode, null);
        }
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
        // check if we are recursively called from one of our siblings.
        final StackTraceElement[] trace = Thread.currentThread().getStackTrace();
        for(int i=0; i < trace.length-1; i++) {
            if( trace[i].equals(trace[trace.length-1]) ) {
                return "[DB detected recursion]";
            }
        }
        //

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
        StringBuilder sb =  new StringBuilder("Database(entries=[");
        int end = -1;
        this.entriesLock.readLock().lock();
        end = this.entries.size() - 1;
        if (end >= 0) {
            for (int i = 0; i < end; i++) {
                DBEntry entry = this.entries.get(i);
                if (entry == null) {
                    continue;
                }
                sb.append(i).append(": ").append(entry.toSimpleString()).append(", ");
            }
            sb.append(end).append(": ").append(this.entries.get(end).toString());
        }
        this.entriesLock.readLock().unlock();
        sb.append("], updateListener=[");
        this.updateListenerLock.readLock().lock();
        end = this.updateListener.size() - 1;
        if (end >= 0) {
            for (int i = 0; i < end; i++) {
                IDataUpdateListener entry = this.updateListener.get(i);
                sb.append(entry).append(", ");
            }
            sb.append(end).append(": ").append(this.updateListener.get(end).toString());
        }
        this.updateListenerLock.readLock().unlock();
        sb.append("])");
        return sb.toString();
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