package org.clip;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public class ImageClip extends JPanel {
    private Image image;

    public ImageClip() {
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Clipboard clipboard = toolkit.getSystemClipboard();
        try {
            // 获取剪贴板中的图像数据
            if (clipboard.isDataFlavorAvailable(DataFlavor.imageFlavor)) {
                image = (Image) clipboard.getData(DataFlavor.imageFlavor);
                System.out.println(image);
                image = image.getScaledInstance(108, 144, Image.SCALE_SMOOTH);
            }
        } catch (UnsupportedFlavorException | IOException e) {
            e.printStackTrace();
        }
    }

    public int getImageWidth() {
        return image.getWidth(null);
    }

    public int getImageHeight() {
        return image.getHeight(null);
    }

    // 重写 paintComponent 方法绘制图像
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            // 绘制图像
            g.drawImage(image, 0, 0, this);
        }
    }

    public static void main(String[] args) {
        // 创建窗口
        JFrame frame = new JFrame("Clipboard Image Display");

        // 添加剪贴板图像面板
        ImageClip panel = new ImageClip();

        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(panel.getImageWidth(), panel.getImageHeight());


        frame.add(panel);

        frame.setVisible(true);
    }


}
