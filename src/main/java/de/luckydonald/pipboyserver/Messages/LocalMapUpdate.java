package de.luckydonald.pipboyserver.Messages;

import de.luckydonald.pipboyserver.MESSAGE_CHANNEL;

public class LocalMapUpdate extends Message {
    public LocalMapUpdate() {
        super(MESSAGE_CHANNEL.LocalMapUpdate);
        //TODO
    }
}

/*
    struct Extend {
      float32_t x,
      float32_t y
    }

    struct Map {
          uint32_t width,
          uint32_t height,
          Extend nw,
          Extend ne,
          Extend sw,
          uint8_t pixel[ width * height ]
    }
*/

