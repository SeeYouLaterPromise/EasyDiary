package org.clip;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class QuickNoteApp {
    // 变量来存储拖动过程中的鼠标位置
    private static int xMouse, yMouse;
    // indicates the status of the panel (fix or not)
    private static boolean fixed = true;
    private static final JTextArea textArea = new JTextArea();
    private static final String storeDir = "src/main/resources/";
    private static final String storeFileName = "0206.txt";
    private static TxtFileManager txtFileManager = new TxtFileManager(storeDir, storeFileName);
    private static boolean submitted = false;

    // TODO: Design memory function to prevent interrupt?

    public static void main(String[] args) {

        showQuickNotePanel();

        // FIXME: How to fix the panel on the screen, whatever I click another somewhere. But User can select fix or not.
        // FIXME: when I click the exit button, the programme haven't exited.
        // FIXME: Click the page or write sth down to eliminate the placeholder ("Enter your thoughts here...").
        // TODO: How to access and manage the string content of TextArea?
    }

    // 快速记录面板
    public static void showQuickNotePanel() {
        // 创建一个新的 JFrame 用作快速记录面板
        JFrame noteFrame = new JFrame("Quick Note");
        noteFrame.setSize(300, 300);
        noteFrame.setAlwaysOnTop(true); // Make sure the window stays on top of other windows
//        noteFrame.setFocusableWindowState(false); // Prevent it from losing focus when clicking elsewhere
        noteFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE); // Exit the application when the main window is closed
        // 设置面板大小可调整
        noteFrame.setResizable(true);

        // 设置面板的初始位置为右下角
        Toolkit toolkit = Toolkit.getDefaultToolkit();
        Dimension screenSize = toolkit.getScreenSize();
        noteFrame.setLocation(screenSize.width - noteFrame.getWidth() - 20, screenSize.height - noteFrame.getHeight() - 100);

        // 创建一个文本区域来记录感想
        JScrollPane scrollPane = getPane();
        noteFrame.add(scrollPane, BorderLayout.CENTER);

        // 创建fix or not按钮
        JButton fixedButton = getFixedButton(noteFrame);
        noteFrame.add(fixedButton, BorderLayout.NORTH);

        // create `submit` button
        JButton submitButton = getSubmitButton(noteFrame);
        noteFrame.add(submitButton, BorderLayout.SOUTH);

//        使面板可拖动
        {
            noteFrame.addMouseListener(new java.awt.event.MouseAdapter() {
                public void mousePressed(java.awt.event.MouseEvent evt) {
                    // 获取当前鼠标按下的初始位置
                    xMouse = evt.getX();
                    yMouse = evt.getY();
                }
            });
            noteFrame.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
                public void mouseDragged(java.awt.event.MouseEvent evt) {
                    // 获取拖动过程中的鼠标位置
                    int x = evt.getXOnScreen();
                    int y = evt.getYOnScreen();

                    // 设置面板位置
                    noteFrame.setLocation(x - xMouse, y - yMouse);
                }
            });
        }

        // doesn't work.
////        Listen for to quickly close the note window
//        noteFrame.addKeyListener(new KeyAdapter() {
//            @Override
//            public void keyPressed(KeyEvent e) {
//                // Check if Ctrl + Enter is pressed
//                if (e.isControlDown()) {
//                    System.out.println("Heard something!");
//                    noteFrame.dispose();  // Close the panel immediately
//                }
//            }
//        });

        // 显示面板
        noteFrame.setVisible(true);
    }

    private static JButton getSubmitButton(JFrame noteFrame) {
        JButton button = new JButton("Submit");

        button.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String text = textArea.getText();
                System.out.println(textArea.getText());
                String entry = "[Thought]: " + text;
                txtFileManager.WriteToFile(entry);
                // after submitting, clear the textarea.
                textArea.setText("");
            }
        });
        return button;
    }

    private static JButton getFixedButton(JFrame noteFrame) {
        JButton fixedButton = new JButton("Not Fixed any more");

        fixedButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                fixed = !fixed;
                noteFrame.setAlwaysOnTop(fixed);
                if (fixed) {
                    fixedButton.setText("Not Fixed any more");
                } else {
                    fixedButton.setText("Fix your panel");
                }
            }
        });
        return fixedButton;
    }

    private static JScrollPane getPane() {
        textArea.setText("Enter your thoughts here...");
        textArea.setLineWrap(true);
        textArea.setWrapStyleWord(true);
        textArea.setForeground(Color.GRAY);  // Set the placeholder text color

        // Add focus listener to handle placeholder removal and restoration
        textArea.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                if (textArea.getText().equals("Enter your thoughts here...")) {
                    textArea.setText(""); // Clear the placeholder text
                    textArea.setForeground(Color.BLACK); // Set text color to black when the user types
                }
                System.out.println("Here is foucs Gained.");
            }

            public void focusLost(java.awt.event.FocusEvent evt) {
                if (textArea.getText().isEmpty()) {
                    textArea.setText("Enter your thoughts here..."); // Restore the placeholder if nothing is typed
                    textArea.setForeground(Color.GRAY); // Placeholder color
                }
            }
        });


        // 设置文本区域的滚动条
        return new JScrollPane(textArea);
    }
}

