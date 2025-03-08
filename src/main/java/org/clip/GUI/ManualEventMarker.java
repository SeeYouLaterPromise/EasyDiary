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
    private static ManualEventMarker eventMarker = null;

    private ManualEventMarker() {
        alertBox();
    }

    public static void getManualEventMarker() {
        if (eventMarker == null) {
            eventMarker = new ManualEventMarker();
        }
    }
    public static void alertBox() {
        // 创建 Alert 对象
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("事件标记");
        alert.setHeaderText("请在下面的文本框中输入您刚刚完成的事件吧！");

        // 创建 TextArea（支持多行）
        TextArea inputArea = new TextArea();
        inputArea.setPromptText("请输入事件内容...");

        // 监听 `Enter` 键事件，确保 `Enter` 只换行
        keyboardEvent(inputArea);

        // GridPane 将 TextArea 添加到 Alert
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

        // 设置 Icon
        alertStage.getIcons().add(new Image(Objects.requireNonNull(TrayMenu.class.getClassLoader().getResourceAsStream("puzzle0.png"))));

        // 替换右上角 “?” 图标
        ImageView customIcon = new ImageView(new Image(Objects.requireNonNull(TrayMenu.class.getClassLoader().getResourceAsStream("puzzle0.png")))); // 图标路径
        customIcon.setFitHeight(32);
        customIcon.setFitWidth(32);

        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setGraphic(customIcon);  // 替换默认 “?” 图标

        // 处理按钮点击事件
        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == saveButton) {
            emptyCheckSubmit(inputArea);
        }
    }

    private static void submit(String text) {
        String entry;
        TxtFileManager txtFileManager = new TxtFileManager();
        entry = "[Event] - " + TxtFileManager.getCurrentTimeString() + ":\n" + text + "\n";
        txtFileManager.WriteToFile(entry, true);
    }

    private static void emptyCheckSubmit(TextArea inputArea) {
        String note = inputArea.getText().trim();
        if (!note.isEmpty()) {
            submit(note);
            showSuccessAlert("成功保存！");
        } else {
            showErrorAlert("不能为空，请重新输入。");
        }
    }

    private static void keyboardEvent(TextArea textArea) {
        textArea.addEventFilter(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode() == KeyCode.ENTER && !event.isControlDown()) {
                // Enter 键默认换行
                textArea.appendText("\n");
                event.consume();  // 阻止冒泡，避免触发确认按钮
            } else if (event.getCode() == KeyCode.ENTER && event.isControlDown()) {
                emptyCheckSubmit(textArea);
            }
        });
    }

    // 成功提示
    private static void showSuccessAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("成功");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // 错误提示
    private static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("错误");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
