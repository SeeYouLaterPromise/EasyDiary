package org.clip.GUI;

import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;
import javafx.stage.Stage;
import org.clip.TxtFileManager;

import java.util.Objects;
import java.util.Optional;

public class ManualEventMarker {
    // 单例实例
    private static volatile ManualEventMarker eventMarker = null;
    private Alert alert;
    private static final TextArea inputArea =  new TextArea();

    // 构造函数 - 初始化 Alert 及其内容
    private ManualEventMarker() {

    }

    // 单例获取实例方法（双重检查锁保证线程安全）
    public static ManualEventMarker getInstance() {
        if (eventMarker == null) {
            synchronized (ManualEventMarker.class) {
                if (eventMarker == null) {
                    eventMarker = new ManualEventMarker();
                }
            }
        }
        return eventMarker;
    }

    public Alert getAlertBox() {
        if (alert == null) {
            inputArea.setPromptText("请输入事件内容...");
            keyboardEvent(inputArea);
            // attention the order.
            this.alert = createAlertBox();
        }
        return alert;
    }

    // 创建 Alert 对象
    private Alert createAlertBox() {
        alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("事件标记");
        alert.setHeaderText("请在下面的文本框中输入您刚刚完成的事件吧！");

        // 创建 TextArea
        GridPane content = new GridPane();
        content.setPadding(new Insets(10));
        content.setHgap(10);
        content.setVgap(10);
        content.add(inputArea, 0, 0);

        alert.getDialogPane().setContent(content);

        // 自定义按钮
        ButtonType saveButton = new ButtonType("保存", ButtonBar.ButtonData.OK_DONE);
        ButtonType cancelButton = new ButtonType("取消", ButtonBar.ButtonData.CANCEL_CLOSE);
        alert.getButtonTypes().setAll(saveButton, cancelButton);

        // 获取 Alert 的 Stage
        Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();
        alertStage.getIcons().add(new Image(Objects.requireNonNull(TrayMenu.class.getClassLoader().getResourceAsStream("puzzle0.png"))));

        // 替换右上角 “?” 图标
        ImageView customIcon = new ImageView(new Image(Objects.requireNonNull(TrayMenu.class.getClassLoader().getResourceAsStream("puzzle0.png"))));
        customIcon.setFitHeight(32);
        customIcon.setFitWidth(32);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setGraphic(customIcon);

        // 处理按钮点击事件
        alert.setOnCloseRequest(event -> {
            alert = null;
        });

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == saveButton) {
            emptyCheckSubmit(inputArea);
        }

        return alert;
    }

    public void getFocus() {
        inputArea.requestFocus();
    }

    public void close() {
        if (alert != null) {
            alert.close();
            alert = null;  // 防止 `alert` 复用
        }
    }

    // 提交事件
    private void submit(String text) {
        String entry = "[Event] - " + TxtFileManager.getCurrentTimeString() + ":\n" + text + "\n";
        new TxtFileManager().WriteToFile(entry, true);
    }

    // 检查并提交
    private void emptyCheckSubmit(TextArea inputArea) {
        String note = inputArea.getText().trim();
        if (!note.isEmpty()) {
            submit(note);
            showSuccessAlert("成功保存！");
        } else {
            showErrorAlert("不能为空，请重新输入。");
        }

        close();
    }

    // 键盘事件
    private static void keyboardEvent(TextArea textArea) {
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isControlDown()) {
                textArea.appendText("\n");
                event.consume();
            } else if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                ManualEventMarker.getInstance().emptyCheckSubmit(textArea);
            }
        });
    }

    // 成功提示
    public static void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("成功");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 错误提示
    public static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
