package org.demo.GUIdemo;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class SystemTrayExample extends Application {

    private SystemTray systemTray;
    private TrayIcon trayIcon;
    private Stage primaryStage;  // Store reference to the main stage

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        Platform.setImplicitExit(false); // 关键：禁止隐式退出
        this.primaryStage = primaryStage;  // Save the stage reference

        // 设置 JavaFX 窗口
        primaryStage.setTitle("JavaFX System Tray Example");
        Button btn = new Button("Show Tray Icon");
        btn.setOnAction(event -> {
            primaryStage.hide();
            showMessage("Hello", "This is a system tray example!");

        });

        StackPane root = new StackPane();
        root.getChildren().add(btn);

        Scene scene = new Scene(root, 300, 250);
        primaryStage.setScene(scene);
        primaryStage.show();

        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            primaryStage.hide();

            // 创建托盘图标
            if (SystemTray.isSupported()) {
                systemTray = SystemTray.getSystemTray();
                trayIcon = createTrayIcon();
                addTrayIcon();
            } else {
                showMessage("Error", "System Tray is not supported on this system.");
            }
        });
    }

    // 创建托盘图标
    private TrayIcon createTrayIcon() {
        // 设置托盘图标的图片，使用完整路径加载图标
        Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/puzzle.png")); // 确保路径正确
        TrayIcon trayIcon = new TrayIcon(image, "JavaFX Tray Example", createPopupMenu());
        trayIcon.setImageAutoSize(true);

        // 为托盘图标添加点击事件
        trayIcon.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                javafx.application.Platform.runLater(()-> {
                    System.out.println("here is FX thread.");
                    primaryStage.show();

                });



            }
        });

        return trayIcon;
    }

    // 创建托盘图标的右键菜单
    private PopupMenu createPopupMenu() {
        PopupMenu popupMenu = new PopupMenu();

        // 创建退出菜单项
        MenuItem exitItem = new MenuItem("Exit");
        exitItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);  // 退出应用
            }
        });

        popupMenu.add(exitItem);
        return popupMenu;
    }

    // 添加托盘图标
    private void addTrayIcon() {
        try {
            systemTray.add(trayIcon);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    // 显示信息窗口
    private void showMessage(String title, String message) {
        Alert alert = new Alert(AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {
//         在应用关闭时移除托盘图标
        if (systemTray != null && trayIcon != null) {
            systemTray.remove(trayIcon);
        }
    }
}
