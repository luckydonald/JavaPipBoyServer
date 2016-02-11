package de.luckydonald.pipboyserver.Messages;

public interface IDataUpdateListener {
    void onDataUpdate(DataUpdate update);

    /**
     * To have a to string, which omits the DB, so you wont get stuck in a recursive loop when called from db.toString.
     *
     * @return String: A textual representation.
     */
    String toStringWithDB();
}
