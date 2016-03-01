package de.luckydonald.pipboyserver.PipBoyServer.input;

import at.HexLib.library.HexLib;
import javax.swing.*;
import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;

/**
 * Created by  on
 *
 * @author luckydonald
 * @since 27.02.2016
 **/
public class BinFileReaderGui extends BinFileReader {
    JFrame frame = null;
    HexLib hex = new HexLib();

    public BinFileReaderGui(BufferedInputStream buff) {
        super(buff);
        hex.setByteContent(buffStore.toByteArray());
    }

    public BinFileReaderGui(URL url) throws IOException {
        super(url);
        hex.setByteContent(buffStore.toByteArray());
    }

    public BinFileReaderGui(File file) throws IOException {
        super(file);
        hex.setByteContent(buffStore.toByteArray());
    }

    public void updateHex() {
        if (this.frame == null) {
            SwingUtilities.invokeLater(() -> {
                frame = new JFrame( "HAX" );
                frame.setVisible(true);
                frame.setSize(200, 200);
                frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                frame.add(hex);
            });
        }
        hex.setByteContent(buffStore.toByteArray());
        hex.setCursorPostion((int) pos);
        hex.updateUI();
    }
}