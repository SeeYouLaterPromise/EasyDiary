package org.clip.GUI;

import javafx.application.Platform;
import org.demo.GUIdemo.MyTrayIcon;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;

public class TrayMenu {
    private static MyTrayIcon myTrayIcon;
    private static final SystemTray systemTray = SystemTray.getSystemTray();

    // Online or offline icon
    private static final ImageIcon onIcon = new ImageIcon(Objects.requireNonNull(TrayMenu.class.getClassLoader().getResource("on.png")));
    private static final ImageIcon offIcon = new ImageIcon(Objects.requireNonNull(TrayMenu.class.getClassLoader().getResource("off.png")));

    private static final String LearningModeStateOnString = "学习模式已开启";
    private static final String LearningModeStateOffString = "学习模式未开启";
    private static final String getLearningModePanelWithoutPrecondition = "请先开启学习模式";
    private static final String getLearningModePanelReady = "查看学习模式状态栏";
    private static final JMenuItem getLearningModePanel = new JMenuItem(getLearningModePanelWithoutPrecondition);
    private static final JMenuItem LearningModeState = new JMenuItem(LearningModeStateOffString, offIcon);
    public static void setLearningModeDisplayState(boolean on) {
        if (on) {
            LearningModeState.setIcon(onIcon);
            LearningModeState.setText(LearningModeStateOnString);
            getLearningModePanel.setText(getLearningModePanelReady);
        } else {
            LearningModeState.setIcon(offIcon);
            LearningModeState.setText(LearningModeStateOffString);
            getLearningModePanel.setText(getLearningModePanelWithoutPrecondition);
        }
    }

    private static final String QuickPanelOpening = "快写面板已打开";
    private static final String QuickPanelClosed = "快写面板未打开";
    private static final JMenuItem getQuickNotePanel = new JMenuItem(QuickPanelClosed);
    public static void setGetQuickPanelDisplayState(boolean on) {
        if (on) {
            getQuickNotePanel.setText(QuickPanelOpening);
        } else {
            getQuickNotePanel.setText(QuickPanelClosed);
        }
    }

    private static final String GlobalListenerStateOffString = "快捷键监听未开启";
    private static final String GlobalListenerStateOnString = "快捷键监听已开启";
    private static final JMenuItem GlobalListenerState = new JMenuItem(GlobalListenerStateOffString, offIcon);
    public static void setGlobalListenerDisplayState(boolean on) {
        if (on) {
            GlobalListenerState.setText(GlobalListenerStateOnString);
            GlobalListenerState.setIcon(onIcon);
        } else {
            GlobalListenerState.setText(GlobalListenerStateOffString);
            GlobalListenerState.setIcon(offIcon);
        }
    }


    private static void addTrayIcon() {
        // 将TrayIcon添加到系统托盘
        try {
            systemTray.add(myTrayIcon);
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
    }

    private static JPopupMenu getPopupMenu() {
        JPopupMenu Jmenu = new JPopupMenu();

        // 设置首选尺寸
        Jmenu.setPreferredSize(new Dimension(300, 300)); // 根据需要设置宽高

        // 为JPopupMenu设置UI
        Jmenu.setUI(new BasicPopupMenuUI() {
            @Override
            public void paint(Graphics g, JComponent c) {
                super.paint(g, c);

                // 画弹出菜单左侧的灰色背景
                g.setColor(new Color(236, 237, 238));
                g.fillRect(0, 0, 25, c.getHeight());

                // 画弹出菜单右侧的白色背景
                g.setColor(new Color(255, 255, 255));
                g.fillRect(25, 0, c.getWidth() - 25, c.getHeight());

                // 调整分割线样式：颜色、粗细
                g.setColor(new Color(200, 200, 200)); // 设置分割线颜色
                g.fillRect(0, 1, 1, 1);  // 最后一条分割线
            }
        });

        return Jmenu;
    }

    public static void getTrayIcon() {
        //将本机系统外观设置为窗体当前外观
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }catch (Exception e) {
            e.printStackTrace();
        }

        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(TrayMenu.class.getClassLoader().getResource("puzzle0.png")));

        // 定义弹出菜单
        JPopupMenu Jmenu = getPopupMenu();



        // 定义弹出菜单项



        JMenuItem Exit = new JMenuItem("关闭所有程序");


        // 将菜单项添加到菜单栏中
        Jmenu.add(LearningModeState);
        Jmenu.add(GlobalListenerState);
        Jmenu.addSeparator(); // 添加分割线
        Jmenu.add(getQuickNotePanel);
        Jmenu.add(getLearningModePanel);
        Jmenu.add(Exit);

        myTrayIcon = new MyTrayIcon(imageIcon.getImage(), "EasyDiary", Jmenu);

        // 将TrayIcon添加到系统托盘
        addTrayIcon();

        // 设置单击击系统托盘图标显示主窗口
        myTrayIcon.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON1) {
                    // show mainPanel if exists
                    Platform.runLater(MainPanel::show);
                }
            }
        });

        // 定义ActionListener监听器
        ActionListener MenuListen = new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                String actionName = e.getActionCommand();

                if (actionName.equals(LearningModeState.getText())) {
                    StateManager.switchLearningMode();
                } else if (actionName.equals(GlobalListenerState.getText())) {
                    StateManager.switchGlobalListener();
                } else if (actionName.equals(getQuickNotePanel.getText())) {
                    StateManager.switchQuickPanelState();
                } else if (actionName.equals(getLearningModePanelReady)) {
                    // 学习模式打开后，才允许调出学习模式状态栏
                    Platform.runLater(LearningMode::show);
                } else if (actionName.equals(Exit.getText())) {
                    systemTray.remove(myTrayIcon);
                    System.exit(0);
                }
            }
        };

        // 设置菜单项的字体、颜色等
        for (Component comp : Jmenu.getComponents()) {
            if (comp instanceof JMenuItem) {
                JMenuItem item = (JMenuItem) comp;
                item.setFont(new Font("微软雅黑", Font.PLAIN, 22)); // 使用微软雅黑字体，适合中文
                item.setForeground(Color.BLACK); // 设置字体颜色
                // 为弹出菜单项添加监听器
                item.addActionListener(MenuListen);
            }
        }
    }



}
