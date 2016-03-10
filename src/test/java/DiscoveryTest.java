import de.luckydonald.pipboyserver.PipBoyServer.Discovery;
import org.junit.Test;

import java.io.IOException;
import java.net.*;

import static de.luckydonald.pipboyserver.Constants.DISCOVER_UDP_PORT;
import static junit.framework.TestCase.assertEquals;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 01.03.2016
 **/
public class DiscoveryTest{
    /**
     * Tests if it fails executing at all.
     */
    @Test(timeout = 10000)
    public void testDiscovery() throws IOException, InterruptedException {
        Discovery d = new Discovery();
        Thread t = new Thread(d);
        t.start();

        DatagramSocket datagramSocket = new DatagramSocket();

        byte[] sendBuffer = "{\"cmd\":\"autodiscover\"}".getBytes();
        InetAddress receiverAddress = InetAddress.getLocalHost();

        DatagramPacket packet = new DatagramPacket(
                sendBuffer, sendBuffer.length, receiverAddress, DISCOVER_UDP_PORT);
        String receicedText = "{\"IsBusy\": false, \"MachineType\": \"PC\", \"name\": \"Test\"}";
        byte[] buffer = receicedText.getBytes();
        DatagramPacket receivedPackage = new DatagramPacket(buffer, buffer.length);
        Thread.sleep(1000);
        datagramSocket.send(packet);
        datagramSocket.receive(receivedPackage);
        assertEquals("Discovery", new String(receicedText.getBytes()), new String(receivedPackage.getData()));
        d.shouldStop();
    }

}