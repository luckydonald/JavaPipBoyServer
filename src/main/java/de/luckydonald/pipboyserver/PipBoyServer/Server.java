package de.luckydonald.pipboyserver.PipBoyServer;

import de.luckydonald.pipboyserver.Constants;
import de.luckydonald.pipboyserver.Messages.ConnectionAccepted;
import de.luckydonald.pipboyserver.Messages.DataUpdate;
import de.luckydonald.pipboyserver.Messages.IDataUpdateListener;
import de.luckydonald.pipboyserver.Messages.KeepAlive;
import de.luckydonald.utils.ObjectWithLogger;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * The Pip Boy Server. Contains a Database.
 *
 * Created by luckydonald on 14.01.16.
 */
public class Server extends ObjectWithLogger {
    public static final int MAX_CLIENTS = 10;
    private boolean quit = false;
    private ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
    public void run() {
        Database db = new Database();
        db.addLogConsoleHandler(Level.FINER).setFilter(
                record -> true //record.getSourceMethodName().startsWith("fill")
        );
        db.fillWithDefault();
        db.startCLI();
        while (!this.quit) {
            ServerSocket socket;
            try {
                socket = new ServerSocket(Constants.CONNECT_TCP_PORT);
                while (!this.quit) {
                    getLogger().info("Waiting for game to connect.");
                    Socket acceptedSocket = socket.accept();
                    Runnable session = new Session(acceptedSocket, db);
                    threadPool.submit(session);
                    getLogger().info("Started Game session.");
                }
            } catch (IOException e) {
                e.printStackTrace();
                try {
                    Thread.sleep(999);
                } catch (InterruptedException ignored) {}
            }
        }
    }
    public boolean shouldQuit() {
        return quit;
    }
    public void setQuit(boolean quit) {
        this.quit = quit;
    }
}
class Session extends ObjectWithLogger implements Runnable, IDataUpdateListener {
    private final Database db;
    private final Socket socket;
    private final ConcurrentLinkedQueue<DataUpdate> updates;
    boolean quit = false;

    public Session(Socket socket, Database db) {
        this.socket = socket;
        this.db = db;
        this.updates = new ConcurrentLinkedQueue<>();
        this.db.registerDataUpdateListener(this);
    }

    @Override
    public void run() {
        KeepAliveThread heartbeat = null;
        Thread heartbeatThread = null;
        System.out.println("Started session.");
        try {
            OutputStream stream = socket.getOutputStream();
            //InputStream inStream = socket.getInputStream();  //TODO: Read what app says.
            heartbeat = new KeepAliveThread(socket);
            stream.write(new ConnectionAccepted().toBytes());
            stream.flush();
            heartbeatThread = new Thread(heartbeat, "Heartbeat");
            heartbeatThread.start();
            while (!quit) {
                getLogger().finer("Checking for updates.");
                DataUpdate update = this.updates.poll();
                if (update != null) {
                    getLogger().fine("Sending update.");
                    //stream.write(update.toBytes());
                    heartbeat.sendMe.add(update.toBytes());
                    //stream.flush();
                    getLogger().fine("Send update: " + update.toString().substring(0, Math.min(1000, update.toString().length())));
                }
                if (!heartbeatThread.isAlive()) {
                    getLogger().warning("Heartbeat thread is dead.");
                    break;
                }
                try {
                    Thread.sleep(999);
                } catch (InterruptedException ignore) {
                    quit = true;
                }
            }
        } catch (IOException e) {
            if (heartbeat != null) {
                heartbeat.quit = true;
            }
            if (heartbeatThread != null) {
                try {
                    heartbeatThread.join(1000);
                } catch (InterruptedException ignore) {
                }
            }
            e.printStackTrace();
        }
        getLogger().warning("Shuting down update thread.");
    }

    @Override
    public void onDataUpdate(DataUpdate update) {
        this.updates.add(update);
    }

    @Override
    public String toString() {
        return "Session{" +
                "socket=" + socket +
                ", updates=" + updates +
                ", quit=" + quit +
                '}';
    }
    public String toStringWithDB() {
        return "Session{" +
                "db=" + db +
                ", socket=" + socket +
                ", updates=" + updates +
                ", quit=" + quit +
                '}';
    }
}
class KeepAliveThread extends ObjectWithLogger implements Runnable{
    Socket socket;
    boolean quit = false;
    public KeepAliveThread(Socket socket) {
        this.socket = socket;
    }
    public ConcurrentLinkedQueue<byte[]> sendMe = new ConcurrentLinkedQueue<>();
    @Override
    public void run(){
        try {
            OutputStream stream = this.socket.getOutputStream();
            //InputStream in = this.socket.getInputStream();
            while (!quit) {
                byte[] bytesToSend = new KeepAlive().toBytes();
                //byte[] bytesToRead = new byte[bytesToSend.length];
                byte[] bytesToSendInstead = sendMe.poll();
                bytesToSendInstead = (bytesToSendInstead == null ? bytesToSend : bytesToSendInstead);
                stream.write(bytesToSendInstead);
                stream.flush();
                // TODO: read answer.
                /*
                int left = bytesToSend.length;
                int read = 0;
                while (read < left) {

                    int newlyRead = in.read(bytesToRead, 0, bytesToRead.length);
                    getLogger().finest(new String(bytesToRead,"UTF-8"));
                    if (newlyRead == -1) {
                        getLogger().log(Level.SEVERE, "Got " + read + " of " + left + " bytes.");
                        throw new IOException("Möööp! Left: " + left);
                    }
                    read += newlyRead;
                }
                */
                try {
                    Thread.sleep(999);
                } catch (InterruptedException ignore) {
                    quit = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        getLogger().warning("Heartbeat thread done.");
    }
}