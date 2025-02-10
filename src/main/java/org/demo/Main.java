package org.demo;

import javax.sound.sampled.Clip;
import java.awt.*;
import java.awt.datatransfer.*;
import java.io.IOException;

public class Main {

    public static void clip(){
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();

//        String str = "Test insertion";
//        StringSelection selection = new StringSelection(str);
//        clipboard.setContents(selection, selection);

        // 获取剪贴板内容（假设剪贴板中有文本）
        try {
//            String text = (String) clipboard.getData(DataFlavor.stringFlavor);
//            System.out.println("剪贴板中的内容是: " + text);
            // 从系统剪切板中获取数据
            Transferable content = clipboard.getContents(null);
            // 判断是否为文本类型
            if (content.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                // 从数据中获取文本值
                String text = (String) content.getTransferData(DataFlavor.stringFlavor);

                if (text == null) {
                    System.out.println("Text is null.");
                    return;
                }

                System.out.println(text);

            } else if (content.isDataFlavorSupported(DataFlavor.imageFlavor)) {
                System.out.println("Image Flavor.");

                // 从数据中获取文本值
                Image image = (Image) content.getTransferData(DataFlavor.imageFlavor);


                System.out.println(image);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            System.out.println(e.getMessage());
        }

    }


    public static void main(String[] args) {
        clip();
    }
}
