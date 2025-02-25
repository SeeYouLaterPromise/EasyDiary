package org.clip.GUI;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.clip.ActiveWindowTracker;
import org.clip.DurationManager;
import org.clip.TxtFileManager;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Timer;
import java.util.TimerTask;

public class LearningMode{
    private static final double height = 120;
    private static final double width = 250;

    // lazy singleton mode.
    private static LearningMode learningMode = null;
    private static Stage LmPanel = null;

    public static void show() {
        if (LmPanel != null) {
            LmPanel.show();
        }
    }

    public static void hide() {
        if (LmPanel != null) {
            LmPanel.hide();
        }
    }

    private LearningMode () {
        LmPanel = panel();
    }

    private static Label todayLabel = null;

    // 创建时间显示
    private static final Label LearningDurationLabel = new Label("00:00:00");

    private static void bindEventToLearningDurationLabel() {
        // hide the learningModePanel
        LearningDurationLabel.setOnMouseClicked(event -> {
            hide();
        });
    }
    private static long currentSeconds = 0;

    // 使用守护线程来进行计时器任务
    private static Timer scheduler = null;

    private static void realTimeLearningDurationDisplay() {
        scheduler = new Timer(true);
        scheduler.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
               currentSeconds++;
                Platform.runLater(() -> LearningDurationLabel.setText(DurationManager.getTimerString(currentSeconds)));
            }
        }, 0, 1000); // 每 1 秒检查一次剪切板
    }

    // 创建状态显示
    private static final String ListenerOnString = "* Listener is running...";
    private static final String ListenerOffString = "Listener is off";
    private static final Label listenerReminder = new Label(ListenerOffString);

    private static void bindEventToListenerReminder() {
        listenerReminder.setOnMouseClicked(event -> {
            StateManager.switchGlobalListener();
        });
    }

    public static void setListenerReminderText(boolean on) {
        if (on) {
            listenerReminder.setText(ListenerOnString);
        } else {
            listenerReminder.setText(ListenerOffString);
        }
    }

    public static void setupLearningMode(Label label) {
        if (learningMode == null) {
            learningMode = new LearningMode();

            // todayLabel
            todayLabel = label;

            // Timer setup
            realTimeLearningDurationDisplay();

            // FX only for GUI stuff.
            new Thread(ActiveWindowTracker::setupTracker).start();
        }
    }

    private static void setLearningDuration() {
        // 每次应该从txt文件中读最新的时间
        TxtFileManager txtFileManager = new TxtFileManager(LocalDate.now(), "LearningDuration");

        String existingDuration = txtFileManager.readFileContent().replace("\n", "");

        // accumulate duration function.
        // 提取已经存在的时间
        long seconds = DurationManager.getSeconds(existingDuration) + currentSeconds;

        // 因为我发现，我关闭LearningMode重开之后，static变量好像没有重新初始化？
        currentSeconds = 0;

        // one day at most 24h.
        int hour = (int) seconds / 3600;
        seconds %= 3600;

        int minute = (int) seconds / 60;
        seconds %= 60;

        // MainPanel display from the major part.
        if (hour != 0) todayLabel.setText(hour + "h");
        else if (minute != 0) todayLabel.setText(minute + "m");
        else todayLabel.setText(seconds + "s");

        // store in the .txt file.
        String content = hour + "h_" + minute + "m_" + seconds + "s";
        txtFileManager.WriteToFile(content, false);
    }

    public static void closePanel() {
        // 关闭开启着的其他功能
//        if (StateManager.quickNoteOpen) QuickNote.closePanel();
//        if (StateManager.listenerOn) GlobalKeyListener.shutDownMode();

        // 关闭Timer计时任务
        scheduler.cancel();

        // 更新主面板的时长
        setLearningDuration();

        // 关闭Tracker
        ActiveWindowTracker.shutDown();

        // 置空引用来让垃圾回收机制回收内存；且方便单例再次调用
        MainPanel.updateState();
        LearningMode.hide();
        learningMode = null;
    }

    private static Stage panel() {
        Stage stage = new Stage();
        stage.setTitle("Learning mode");

        bindEventToLearningDurationLabel();
        bindEventToListenerReminder();

        stage.setAlwaysOnTop(true);
        BorderPane root = new BorderPane();


        LearningDurationLabel.getStyleClass().add("label-time"); // 设置样式
        HBox labelHBox = new HBox(LearningDurationLabel);
        labelHBox.setAlignment(Pos.CENTER);


        listenerReminder.getStyleClass().add("label-status");

        HBox hBox = new HBox(listenerReminder);
        hBox.setAlignment(Pos.CENTER);
        hBox.getStyleClass().add("hbox-buttons");


        root.setCenter(labelHBox);
        root.setBottom(hBox);

        // 创建 Scene 并加载 CSS 样式
        Scene scene = new Scene(root, width, height); // 设置窗口大小
        scene.getStylesheets().add(Objects.requireNonNull(
                TrayMenu.class.getClassLoader().getResource("learningMode.css")
        ).toExternalForm());

        // 设定 Scene
        stage.setScene(scene);

        // 设置窗口大小
        stage.setHeight(height);
        stage.setWidth(width);

        // 让窗口在右上角显示
        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMaxX() - stage.getWidth() - 10);
        stage.setY(50);

        // 设置窗口样式
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);  // 确保 Scene 透明

        // 创建一个真正裁剪窗口的圆角矩形
        Rectangle clip = new Rectangle(width, height);
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        root.setClip(clip);

        // 启用拖拽窗口
        enableDragging(stage, root);

        // 加一个icon美化
        stage.getIcons().add(new Image(Objects.requireNonNull(LearningMode.class.getResourceAsStream("/puzzle0.png"))));

        stage.show();
        return stage;
    }

    private static double xOffset = 0;
    private static double yOffset = 0;

    private static void enableDragging(Stage stage, Node root) {
        root.setOnMousePressed(event -> {
            xOffset = event.getSceneX();
            yOffset = event.getSceneY();
        });

        root.setOnMouseDragged(event -> {
            stage.setX(event.getScreenX() - xOffset);
            stage.setY(event.getScreenY() - yOffset);
        });
    }

}
