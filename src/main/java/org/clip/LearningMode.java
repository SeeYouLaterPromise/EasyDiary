package org.clip;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.time.LocalDate;

public class LearningMode{
    private static final double height = 150;
    private static final double width = 300;

    // initial state of button.
    private static boolean fixed = true;
    private static boolean listened = false;
    private static boolean write = false;

    // lazy singleton mode.
    private static LearningMode learningMode = null;
    private static Stage LmPanel = null;

    private static Button quickNoteButton = null;

    private static Button listenedButton = null;

    /**
     * to accept the state of sub-layout.
     */

    public static void toggleWrite() {
        write = !write;
    }

    public static void toggleListened() {
        listened = !listened;
    }

    public static void updateQuickNoteButtonState() {
        if (write) {
            quickNoteButton.setText("Panel on!");
        } else {
            quickNoteButton.setText("Panel off.");
        }
    }

    public static void updateListenerButtonState() {
        if (listened) {
            listenedButton.setText("Listen on!");
        } else {
            listenedButton.setText("Listen off.");
        }

    }

    private LearningMode () {
        LmPanel = panel();
    }

    private static Label durationLabel = null;

    // to record the usage duration of learning mode.
    private static long startTime;

    public static Stage getLmPanel(Label label) {
        if (learningMode == null) {
            learningMode = new LearningMode();
            durationLabel = label;
            // 记下当前时间
            startTime = System.currentTimeMillis();
        }
        return LmPanel;
    }

    private static void setLearningDuration() {
        // 每次应该从txt文件中读最新的时间
        TxtFileManager txtFileManager = new TxtFileManager(LocalDate.now(), "LearningDuration");

        String existingDuration = txtFileManager.readFileContent().replace("\n", "");

        // accumulate duration function.
        // 提取已经存在的时间
        int seconds = ExtractExistingSeconds(existingDuration);


        long endTime = System.currentTimeMillis();
        seconds += (int) (( endTime - startTime ) / 1000);  // seconds

        // one day at most 24h.
        int hour = seconds / 3600;
        seconds %= 3600;
        String content = hour + "h";


        int minute = seconds / 60;
        seconds %= 60;

        // MainPanel display from the major part.
        if (hour != 0) durationLabel.setText(hour + "h");
        else if (minute != 0) durationLabel.setText(minute + "m");
        else durationLabel.setText(seconds + "s");

        // store in the .txt file.
        content = content + "_" + minute + "m_" + seconds + "s";
        txtFileManager.WriteToFile(content, false);
    }

    private static int getSeconds(char unit) {
        switch (unit) {
            case 'h': return 3600;
            case 'm': return 60;
            case 's': return 1;
        }
        return 0;
    }

    /**
     * format as "xxh_xxm_xxs"
     * @param existingDuration
     * @return
     */
    public static int ExtractExistingSeconds(String existingDuration) {
        int seconds = 0;
        String[] components = existingDuration.split("_");
        for (String component : components) {
            int end = component.length() - 1;
            char unit = component.charAt(end);
            int figure = Integer.parseInt(component.substring(0, end));
            seconds += figure * getSeconds(unit);
        }
        return seconds;
    }

    // TODO: 将这些子窗口抽象成一个父类，让这里的closePanel...
    public static void closePanel() {
        // 关闭开启着的其他功能
        if (write) QuickNote.closePanel();
        if (listened) GlobalKeyListener.shutDownMode();

        // 更新主面板的时长
        setLearningDuration();

        // 推出学习模式后，调出先前隐藏的学习面板
        MainPanel.showMainPanel();

        // 置空引用来让垃圾回收机制回收内存；且方便单例再次调用
        MainPanel.updateState();
        if (LmPanel != null) LmPanel.hide();
        learningMode = null;
    }

    private static Stage panel() {
        Stage stage = new Stage();
        stage.setTitle("Learning mode");

        // Handling the close event to ensure the app exits properly
        // TODO: how to send the close state to its outside => outside provide API for here to toggle.
        stage.setOnCloseRequest((WindowEvent we) -> {
            System.out.println("Hide Learning mode GUI.");
//            System.exit(0);
            we.consume(); // Prevent the default behavior (window closing)
            closePanel();
        });

        stage.setAlwaysOnTop(true);
        BorderPane root = new BorderPane();

        Label label = new Label("状态栏：");
        HBox labelHBox = new HBox(label);
        labelHBox.setAlignment(Pos.CENTER);

        // Create Buttons (Submit & Fix)


        Button fixedButton = getFixedButton(stage);
        listenedButton = getListenerButton();
        quickNoteButton = getQuickNoteButton();

        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        buttonBox.getChildren().addAll(fixedButton, listenedButton, quickNoteButton);

        root.setCenter(labelHBox);
        root.setBottom(buttonBox);

        // Set the stage and show
        Scene scene = new Scene(root, width, height);

        // Set the scene for the stage
        stage.setScene(scene);

        // if you don't do this, stage.getWidth() would be NaN.
        stage.setHeight(height);
        stage.setWidth(width);

        // 设置面板的初始位置为右下角
        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        // Set the stage position directly to the bottom-right corner
        stage.setX(screenBounds.getMaxX() - stage.getWidth());
        stage.setY(0.2 * stage.getWidth());
        // Show the stage after positioning
        stage.show();
        return stage;
    }

    private static Button getFixedButton(Stage stage) {
        Button fixedButton = new Button("Fixed!");

        // Fix/Unfix button functionality
        fixedButton.setOnAction(e -> {
            // fixed indicates the status of `pin or not`
            if (fixed) {
                // if it has been fixed already, toggle it.
                stage.setAlwaysOnTop(false);
                fixedButton.setText("UnFixed.");
            } else {
                stage.setAlwaysOnTop(true);
                fixedButton.setText("Fixed!");
            }
            // toggle the status
            fixed = !fixed;
        });
        return fixedButton;
    }

    private static Button getListenerButton() {
        Button button = new Button("Listen off.");

        button.setOnAction(e -> {

            if (listened) {
                GlobalKeyListener.shutDownMode();
            } else {
                GlobalKeyListener.startUpMode();
            }
        });

        return button;
    }

    private static Button getQuickNoteButton() {
        Button button = new Button("Panel off.");

        button.setOnAction(e -> {
            // 我现在觉得这样解释不会迷惑：write指示现在node的状态，如果是on，即true，那么button应该提供关闭功能，即close。
            if (write) {
                QuickNote.closePanel();
            } else {
                QuickNote.getQuickNote();
            }
        });

        return button;
    }
}
