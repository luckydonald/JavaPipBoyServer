package de.luckydonald.pipboyserver;

/**
 * Created by luckydonald on 14.01.16.
 */
public class Constants {

    public static final int DISCOVER_UDP_PORT = 28000;
    public static final String DISCOVER_STRING = "{\"cmd\":\"autodiscover\"}";
    public static final int CONNECT_TCP_PORT = 27000;

    public static  String discover_response (String type, boolean busy) {
        return "{\"IsBusy\": " + (busy ? "true" : "false") + ", \"MachineType\": \""+ type + "\", \"name\": \"Test\"}";
    }
    public static  String discover_response (String type) {
        return discover_response(type, false);
    }

    public static String discover_response() {
        return discover_response("PC");
    }
}

