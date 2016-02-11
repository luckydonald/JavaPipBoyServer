package de.luckydonald.pipboyserver.PipBoyServer.types;

import de.luckydonald.pipboyserver.PipBoyServer.Database;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyInsertedException;
import de.luckydonald.pipboyserver.PipBoyServer.exceptions.AlreadyTakenException;

public abstract class DBContainer extends DBEntry {
    public DBContainer(Database db) {
        super(db);
    }

    public DBContainer() {
        super();
    }

    public void addNewEntryToDB(DBEntry entry) throws AlreadyInsertedException, AlreadyTakenException {
        if (!entry.hasID()) {
            // Entry doesn't have a ID
            if (this.hasDatabase() && this.hasID() ) {
                //Entry doesn't have a ID, and this DBDict has db & id.
                entry.addToDB(this.getDatabase());
            } else {
                // Entry doesn't have a ID, and this DBDict has no id either, or a DB for that mater.
                getLogger().warning("Added unregistered DBEntry to unregistered DBDict.");
                //Specifically: entry.id is null and either this db or this id is null too.
            }
        } else if (getDatabase().has(entry.getID())) {
            if(getDatabase().get(entry.getID()) != entry) {
                // Entry has a ID, but the ID is already taken in the DB by something else.
                throw new AlreadyTakenException("Entry has a ID, but the ID is already taken in the DB by something else.");
            }
        } else {
            // Entry has a ID, but the ID isn't in the DB
            throw new AlreadyInsertedException("Entry has a ID, but the ID isn't in the DB");
        }
    }
    /**
     * Adds each of our children to the {@link Database}.
     * (Via {@link DBEntry#addToDB(Database)})
     *
     * @param db The Database where we add to.
     * @return Itself
     * @throws AlreadyInsertedException See {@link DBEntry#addToDB(Database)}
     */
    public abstract DBContainer addChildrenToDB(Database db) throws AlreadyInsertedException;
}
