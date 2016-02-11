/**
 * Created by luckydonald on 11.02.16.
 */
import de.luckydonald.pipboyserver.PipBoyServer.*;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyInsertedException;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyTakenException;
import de.luckydonald.pipboyserver.PipBoyServer.types.*;
import junit.framework.TestCase;
import org.junit.Before;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.function.Function;
import java.util.logging.*;

public class DatabaseTests {
    private static Logger jlog = Logger.getLogger("tests");

    static {
        Logger.getGlobal().setLevel(Level.ALL);
        jlog.log(Level.INFO, "Loaded.");
    }

    Database db;
    DBDict root;

    @Before
    public void setUp() {
        db = null;
        root = null;
        db = new Database();
        root = new DBDict(null);
        db.add(root);
        assertEquals("db[0] == root", root, db.get(0));
        // {}

    }

    /**
     * Checks that Ids are set automatically and correctly when adding to {@link DBList}s and {@link DBDict}s.
     * Also checks inserting all the different types of {@link DBEntry}s.
     *
     * @throws AlreadyTakenException
     * @throws AlreadyInsertedException
     */
    public void testCreation(boolean printDebug) throws AlreadyTakenException, AlreadyInsertedException {
        /*
            BOOLEAN (1),    // java: bool
            INT8    (2),    // java: byte
            INT32   (3),    // java: int
            FLOAT   (5),    // java: float
            STRING  (6),    // java: String
            LIST    (7),
            DICT    (8);
         */
        if (printDebug) System.out.println("");
        if (printDebug) System.out.println("a1");
        DBBoolean a1 = new DBBoolean(db, true);
        root.add("a1", a1);
        // {"a1": True}
        if (printDebug) db.print();
        assertEquals("db[1] == a1", a1, db.get(1));

        if (printDebug) System.out.println("");
        if (printDebug) System.out.println("a2");
        DBInteger8 a2 = new DBInteger8(db, 8);
        root.add("a2", a2);
        // {"a1": True, "a2": 0x08}
        if (printDebug) db.print();
        assertEquals("db[2] == a2", a2, db.get(2));

        if (printDebug) System.out.println("");
        if (printDebug) System.out.println("a3");
        DBInteger32 a3 = new DBInteger32(db, 32);
        root.add("a3", a3);
        // {"a1": True, "a2": 0x08, "a3": 32}
        if (printDebug) db.print();
        assertEquals("db[3] = a3", a3, db.get(3));

        if (printDebug) System.out.println("");
        if (printDebug) System.out.println("a4");
        DBFloat a4 = new DBFloat(db, 3.4);
        root.add("a4", a4);
        // {"a1": True, "a2": 0x08, "a3": 32, "a4": 3.4}
        if (printDebug) db.print();
        assertEquals("db[4] = a4", a4, db.get(4));

        if (printDebug) System.out.println("");
        if (printDebug) System.out.println("a5");
        DBString a5 = new DBString(db, "foo");
        root.add("a5", a5);
        // { "a1": True, "a2": 0x08, "a3": 32, "a4": 3.4,
        //   "a5: "foo" }
        if (printDebug) db.print();
        assertEquals("db[5] = a5", a5, db.get(5));

        if (printDebug) System.out.println("");
        if (printDebug) System.out.println("a6 +  b1");
        DBString b1 = new DBString(db, "bar");
        DBList a6 = new DBList(db, b1);
        // ["bar"]
        root.add("a6", a6);
        // { "a1": True, "a2": 0x08, "a3": 32, "a4": 3.4,
        //   "a5: "foo", "a6": ["bar"] }
        if (printDebug) db.print();
        assertEquals("db[6] = b1", b1, db.get(6));
        assertEquals("db[7] = a6", a6, db.get(7));

        if (printDebug) System.out.println("");
        if (printDebug) System.out.println("a6 -> b2");
        DBInteger32 b2 = new DBInteger32(db, 34);
        a6.append(b2);
        // { "a1": True, "a2": 0x08, "a3": 32, "a4": 3.4,
        //   "a5: "foo", "a6": ["bar", 34] }
        if (printDebug) db.print();
        assertEquals("db[8] = b2", b2, db.get(8));
        assertEquals("db[7] = a6", a6, db.get(7));

        if (printDebug) System.out.println("");
        if (printDebug) System.out.println("a6 -> b3");
        DBDict b3 = new DBDict(db);
        // {}
        a6.append(b3);
        // { "a1": True, "a2": 0x08, "a3": 32, "a4": 3.4,
        //   "a5: "foo", "a6": ["bar", 34, {}] }
        if (printDebug) db.print();
        assertEquals("db[9] = b3", b3, db.get(9));

        if (printDebug) System.out.println("");
        if (printDebug) System.out.println("a6 -> b3 -> c1");
        DBString c1 = new DBString(db, "baz");
        DBInteger32 c2 = new DBInteger32(db, 35);
        b3.add("c1", c1).add("c2", c2);
        // {
        //  "a1": True,
        //  "a2": 0x08,
        //  "a3": 32,
        //  "a4": 3.4,
        //  "a5: "foo",
        //  "a6": [
        //          "bar",
        //          34,
        //          {"c1": "baz", "c2": 35}
        //  ]
        // }
        if (printDebug) db.print();
        assertEquals("db[10] = c1", c1, db.get(10));
        assertEquals("db[11] = c2", c2, db.get(11));
        if (printDebug) System.out.println("");
    }

    @Test
    public void testCreation() throws AlreadyTakenException, AlreadyInsertedException {
        this.testCreation(false);
    }

    @Test
    public void testInsertBoolean() throws AlreadyTakenException, AlreadyInsertedException {
        DBBoolean b1 = new DBBoolean(db, true);
        root.add("boolean1", b1);
        assertEquals("boolean without DB added", b1, db.get(1));

        DBBoolean b2 = new DBBoolean(true);
        root.add("boolean2", b2);
        assertEquals("boolean with DB added", b2, db.get(2));
    }

    @Test
    public void testInsertInt8() throws AlreadyTakenException, AlreadyInsertedException {
        DBInteger8 i1 = new DBInteger8(db, 16);
        root.add("Integer8_1a", i1);
        assertEquals("boolean without DB added", i1, db.get(1));

        DBInteger8 i2 = new DBInteger8(16);
        root.add("Integer8_1b", i2);
        assertEquals("boolean with DB added", i2, db.get(2));

        DBInteger8 i3 = new DBInteger8(db, (byte)17);
        root.add("Integer8_2a", i3);
        assertEquals("boolean without DB added", i3, db.get(3));

        DBInteger8 i4 = new DBInteger8((byte)17);
        root.add("Integer8_2b", i4);
        assertEquals("boolean with DB added", i4, db.get(4));
    }

    @Test
    public void testInsertInt32() throws AlreadyTakenException, AlreadyInsertedException {
        DBInteger32 i1 = new DBInteger32(db, 16);
        root.add("Integer32_1a", i1);
        assertEquals("boolean without DB added", i1, db.get(1));

        DBInteger32 i2 = new DBInteger32(16);
        root.add("Integer32_1b", i2);
        assertEquals("boolean with DB added", i2, db.get(2));
    }

    @Test
    public void testTraversal() throws AlreadyTakenException, AlreadyInsertedException {
        testCreation();
        // {
        //  "a1": True,
        //  "a2": 0x08,
        //  "a3": 32,
        //  "a4": 3.4,
        //  "a5: "foo",
        //  "a6": [
        //          "bar",
        //          34,
        //          {"c1": "baz", "c2": 35}
        //  ]
        // }
        assertEquals(
                "root[\"a6\"][2] traversal",
                db.getRoot().dictValue().get("a6").listValue().get(2).dictValue().get("c1").textValue(),
                "baz"
        );
    }
}
