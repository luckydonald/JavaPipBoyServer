package de.luckydonald.pipboyserver.PipBoyServer;

/**
 * Created by luckydonald on 14.01.16.
 */
public class Main {
    public Main() {
    }
    public static void main(String[] args) {
        Discovery d = new Discovery();
        Thread t = new Thread(d);
        t.start();
        Server s = new Server();
        s.run();
    }
}

