package org.clip;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class StringClip {
    private static String[] records;
    private static int size = 0;
    private static Toolkit toolkit;
    private static Clipboard clipboard;
    StringClip() {
        toolkit = Toolkit.getDefaultToolkit();
        clipboard = toolkit.getSystemClipboard();
        records = new String[50];
    }

    public void excerpt() {
        toolkit.beep();
        // Now simulate the Ctrl + C press using Robot to trigger the system's copy action
        // The issue of this method lies in which the recycle revoke would be generated if your trigger way is the same.
//        try {
//            Robot robot = new Robot();
//            robot.keyPress(KeyEvent.VK_CONTROL);  // Press the CTRL key
//            robot.keyPress(KeyEvent.VK_C);       // Press the C key
//            robot.keyRelease(KeyEvent.VK_C);     // Release the C key
//            robot.keyRelease(KeyEvent.VK_CONTROL); // Release the CTRL key
//        } catch (AWTException ex) {
//            ex.printStackTrace();
//        }

        // excerpt函数线程貌似快于系统复制功能函数，所有我们需要小等一下。
        // Add a small delay to allow the system to copy the selected text to the clipboard
        try {
            // Add a small delay (30 ms) to ensure clipboard content is updated
            Thread.sleep(30);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }

        try {
            String text = (String) clipboard.getData(DataFlavor.stringFlavor);
            if (text == null) {
                System.out.println("Text is null.");
                return;
            }
            // Remove all types of newlines (\n and \r\n)
            String cleanedText = text.replaceAll("[\\r\\n]+", ""); // Removes both \n and \r\n


            records[size++] = cleanedText;
//            System.out.println(text);

        } catch (IOException | UnsupportedFlavorException e) {
            System.err.println(e.getMessage());
        }
    }

    public void showRecords() {

        for (int i = 0; i < size; i++) {
            System.out.println(records[i]);
        }
        System.out.println("Show done! Size: " + size);
    }

    public String[] getRecords() {
        return records;
    }
}
