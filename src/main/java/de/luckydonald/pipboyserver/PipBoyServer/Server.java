package de.luckydonald.pipboyserver.PipBoyServer;

import de.luckydonald.pipboyserver.Constants;
import de.luckydonald.pipboyserver.Messages.ConnectionAccepted;
import de.luckydonald.pipboyserver.Messages.DataUpdate;
import de.luckydonald.pipboyserver.Messages.IDataUpdateListener;
import de.luckydonald.pipboyserver.Messages.KeepAlive;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by luckydonald on 14.01.16.
 */
public class Server {
    public static final int MAX_CLIENTS = 10;
    ServerSocket socket;
    Database db;
    boolean quit = false;
    public Server() {

    }
    ExecutorService threadPool = Executors.newFixedThreadPool(MAX_CLIENTS);
    public void run() {
        db = new Database();
        db.fillWithDefault();
        while (!this.quit) {
            try {
                ServerSocket socket = new ServerSocket(Constants.CONNECT_TCP_PORT);
                while (!this.quit) {
                    System.out.println(db.toSimpleString());
                    System.out.println("Waiting for game to connect.");
                    Socket acceptedSocket = socket.accept();
                    Runnable session = new Session(acceptedSocket, db);
                    threadPool.submit(session);
                }
            } catch (IOException e) {
                socket = null;
                e.printStackTrace();
            }
        }
    }
}
class Session implements Runnable, IDataUpdateListener {
    private final Database db;
    private final Socket socket;
    private final ConcurrentLinkedQueue<DataUpdate> updates;
    boolean quit = false;

    public Session(Socket socket, Database db) {
        this.socket = socket;
        this.db = db;
        this.updates = new ConcurrentLinkedQueue<DataUpdate>();
        this.db.registerDataUpdateListener(this);
    }

    @Override
    public void run() {
        KeepAliveThread heartbeat = null;
        Thread heartbeatThread = null;
        try {
            OutputStream stream = socket.getOutputStream();
            heartbeat = new KeepAliveThread(stream);
            stream.write(new ConnectionAccepted().toBytes());
            stream.flush();
            heartbeatThread = new Thread(heartbeat);
            heartbeatThread.start();
            while (!quit) {
                System.out.println("Checking for updates.");
                DataUpdate update = this.updates.poll();
                if (update != null) {
                    System.out.println("Sending update: " + update.toString());
                    stream.write(update.toBytes());
                    stream.flush();
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
    }

    @Override
    public void onDataUpdate(DataUpdate update) {
        this.updates.add(update);
    }

    @Override
    public String toString() {
        return "Session{" +
                "db=" + db +
                ", socket=" + socket +
                ", updates=" + updates +
                ", quit=" + quit +
                '}';
    }
}
class KeepAliveThread implements Runnable{
    OutputStream stream;
    boolean quit = false;
    public KeepAliveThread(OutputStream stream) {
        this.stream = stream;
    }

    @Override
    public void run(){
        try {
            while (!quit) {
                System.out.println("ping");
                stream.write(new KeepAlive().toBytes());
                stream.flush();
                try {
                    Thread.sleep(999);
                } catch (InterruptedException ignore) {
                    quit = true;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}