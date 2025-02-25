package org.demo.GUIdemo;

import javax.swing.*;
import javax.swing.plaf.basic.BasicPopupMenuUI;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Objects;


public class DemoTrayIcon {

    public static void main(String[] args) throws Exception {



        //初始化窗体
        JFrame frame=new JFrame("My QQ");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 500);
        frame.setLocationRelativeTo(null);
        frame.setType(Window.Type.UTILITY);

        ImageIcon img=new ImageIcon(Objects.requireNonNull(DemoTrayIcon.class.getClassLoader().getResource("puzzle.png")));
        frame.setIconImage(img.getImage());

        //定义弹出菜单
        JPopupMenu Jmenu=new JPopupMenu();

        //为JPopupMenu设置UI
        Jmenu.setUI(new BasicPopupMenuUI(){
            @Override
            public void paint(Graphics g, JComponent c){
                super.paint(g, c);

                //画弹出菜单左侧的灰色背景
                g.setColor(new Color(236,237,238));
                g.fillRect(0, 0, 25, c.getHeight());

                //画弹出菜单右侧的白色背景
                g.setColor(new Color(255,255,255));
                g.fillRect(25, 0, c.getWidth()-25, c.getHeight());
            }
        });

        //定义弹出菜单项
        JMenuItem online = new JMenuItem("我在线上",new ImageIcon(
                Objects.requireNonNull(DemoTrayIcon.class.getClassLoader().getResource("puzzle.png"))));
        JMenuItem busy = new JMenuItem("忙碌",new ImageIcon(
                Objects.requireNonNull(DemoTrayIcon.class.getClassLoader().getResource("puzzle.png"))));
        JMenuItem invisible= new JMenuItem("隐身");
        JMenuItem openmenu = new JMenuItem("打开主面板");
        JMenuItem closemenu = new JMenuItem("退出MyQQ");

        //添加弹出菜单项到弹出菜单
        Jmenu.add(online);
        Jmenu.add(busy);
        Jmenu.add(invisible);
        Jmenu.addSeparator();//添加分割线
        Jmenu.add(openmenu);
        Jmenu.add(closemenu);

        //得到当前系统托盘
        SystemTray systemtray = SystemTray.getSystemTray();

        //创建带指定图像、工具提示和弹出菜单的 MyTrayIcon
        MyTrayIcon trayicon = new MyTrayIcon(img.getImage(),"MyQQ",Jmenu);

        //将TrayIcon添加到系统托盘
        try {
            systemtray.add(trayicon);
        } catch (AWTException e1) {
            e1.printStackTrace();
        }

        //设置单击击系统托盘图标显示主窗口
        trayicon.addMouseListener(new MouseAdapter(){
            @Override
            public void mouseClicked(MouseEvent e) {

                //鼠标左键点击,设置窗体状态，正常显示
                if(e.getButton()==MouseEvent.BUTTON1){
                    frame.setExtendedState(JFrame.NORMAL);
                    frame.setVisible(true);
                }
            }
        });

        //定义ActionListener监听器
        ActionListener MenuListen = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {

                if (e.getActionCommand().equals("退出MyQQ")){

                    systemtray.remove(trayicon);
                    System.exit(0);
                }
                else if(e.getActionCommand().equals("打开主面板")){
                    frame.setExtendedState(JFrame.NORMAL);
                    frame.setVisible(true);
                }

            }};

        //为弹出菜单项添加监听器
        openmenu.addActionListener(MenuListen);
        closemenu.addActionListener(MenuListen);

        frame.setVisible(true);

    }

}
