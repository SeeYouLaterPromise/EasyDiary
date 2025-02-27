package org.clip;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Timer;
import java.util.TimerTask;

public class StringClip {
    private static final String CSDN_APPEND = "————————————————\n" +
            "\n" +
            "                            版权声明：本文为博主原创文章，遵循 CC 4.0 BY-SA 版权协议，转载请附上原文出处链接和本声明。";
    // 缓存功能，但是没什么必要；win11的win+V做的很好了。
    private static String[] records;
    private static int size = 0;
    private static Toolkit toolkit;
    private static Clipboard clipboard;
    private String lastClipboardContent;
    StringClip() {
        toolkit = Toolkit.getDefaultToolkit();
        clipboard = toolkit.getSystemClipboard();
        records = new String[50];
//        lastClipboardContent = getClipboardContent();
    }

    public static void playBeep() {
        toolkit.beep();
    }
    public String getClipboardContent() {
        String res;
        try {
            res = (String) clipboard.getData(DataFlavor.stringFlavor);
        } catch (IOException | UnsupportedFlavorException e) {
            res = e.getMessage();
        }
        return res;
    }

    // 启动一个定时任务监听剪切板内容变化

    /**
     * 轮询，更新则存储。考虑到了，先写一半放在这，需求不是很迫切。
     */
    public void startClipboardListener() {
        Timer timer = new Timer(true); // 使用守护线程来定时检查
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                String currentClipboardContent = getClipboardContent();
                if (!currentClipboardContent.equals(lastClipboardContent)) {
                    // 如果剪切板内容发生变化，存储新内容
                    String entry = "[Excerpt] - " + TxtFileManager.getCurrentTimeString() + ":\n" + currentClipboardContent + "\n";
                    TxtFileManager txtFileManager = new TxtFileManager();
                    txtFileManager.WriteToFile(entry, true);

                    lastClipboardContent = currentClipboardContent; // 更新上次的剪切板内容
                }
            }
        }, 0, 1000); // 每 1 秒检查一次剪切板

//         timer.cancel();  来取消定时任务
    }

    /**
     * 按键存储
     */
    public void excerpt() {
        playBeep();

        // excerpt函数线程貌似快于系统复制功能函数，所有我们需要小等一下。
        // Add a small delay to allow the system to copy the selected text to the clipboard
        try {
            // Add a small delay (30 ms) to ensure clipboard content is updated
            Thread.sleep(30);
        } catch (InterruptedException ex) {
            ex.printStackTrace();
        }


        String text = getClipboardContent();
        if (text == null) {
            System.out.println("Text is null.");
            return;
        }
        // Remove all types of newlines (\n and \r\n)
//            String cleanedText = text.replaceAll("[\\r\\n]+", ""); // Removes both \n and \r\n
        // clean the excerpt from CSDN
        text = text.replaceAll(CSDN_APPEND, "");
        String entry = "[Excerpt] - " + TxtFileManager.getCurrentTimeString() + ":\n" + text + "\n";
        TxtFileManager txtFileManager = new TxtFileManager();

        txtFileManager.WriteToFile(entry, true);

        records[size++] = text;

        // Avoid the index out of bound issue.
        size = size % 50;

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
