package de.luckydonald.pipboyserver;

import java.io.IOException;
import java.net.*;

import static de.luckydonald.pipboyserver.Constants.DISCOVER_STRING;
import static de.luckydonald.pipboyserver.Constants.DISCOVER_UDP_PORT;

/**
 * Created by luckydonald on 14.01.16.
 *
 *  The APP client
 */
public class PipBoyClient {

    public PipBoyClient() {
    }

    public static void main(String[] args) throws IOException {
        while (true) {

            try {
                DatagramSocket socket = new DatagramSocket();
                InetAddress address = InetAddress.getByName("255.255.255.255");
                DatagramPacket packet = new DatagramPacket(DISCOVER_STRING.getBytes(), DISCOVER_STRING.getBytes().length,
                        address, DISCOVER_UDP_PORT);
                socket.setBroadcast(true);
                socket.connect(address, DISCOVER_UDP_PORT);

                socket.send(packet);
            } catch (SocketException e) {
                e.printStackTrace();
            } catch (UnknownHostException e) {
                e.printStackTrace(); //send
            } catch (IOException e) {
                e.printStackTrace(); //send
                throw e;
            }
        }
    }
}
