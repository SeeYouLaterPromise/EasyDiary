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
    private static final double height = 130;
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


    private static long learnSeconds = 0;

    private static long pauseSeconds = 0;

    // 使用守护线程来进行计时器任务
    private static Timer scheduler = null;
    private static volatile boolean isPaused = false;

    private static synchronized void togglePause() {
        isPaused = !isPaused;
    }

    private static void realTimeLearningDurationDisplay() {
        scheduler = new Timer(true);
        scheduler.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                if (!isPaused) {
                    learnSeconds++;
                    // pass the `seconds` to get the formatted time string.
                    Platform.runLater(() -> LearningDurationLabel.setText(DurationManager.getTimerString(learnSeconds)));
                } else {
                    pauseSeconds++;
                    Platform.runLater(() -> LearningDurationLabel.setText(DurationManager.getTimerString(pauseSeconds)));
                }
            }
        }, 0, 1000); // 每 1 秒刷新一次
    }

    /**
     * 点击屏幕中间的计时，暂停计时；再次点击，继续计时。
     */
    private static void bindEventToLearningDurationLabel(BorderPane root, HBox emptyBox, HBox reminderBox) {
        // hide the learningModePanel
        LearningDurationLabel.setOnMouseClicked(event -> {

            if (root.getStyleClass().contains("root-running")) {
                root.getStyleClass().remove("root-running");
                root.getStyleClass().add("root-pause");

                emptyBox.getStyleClass().remove("hbox-running");
                emptyBox.getStyleClass().add("hbox-pause");

                reminderBox.getStyleClass().remove("hbox-running");
                reminderBox.getStyleClass().add("hbox-pause");

            } else if (root.getStyleClass().contains("root-pause")) {
                root.getStyleClass().remove("root-pause");
                root.getStyleClass().add("root-running");

                emptyBox.getStyleClass().remove("hbox-pause");
                emptyBox.getStyleClass().add("hbox-running");

                reminderBox.getStyleClass().remove("hbox-pause");
                reminderBox.getStyleClass().add("hbox-running");
            }

            isPaused = !isPaused;  // 目前没有并发困扰
//            togglePause();
        });
    }

    // 创建状态显示
    private static final String ListenerOnString = "* Listener is running...";
    private static final String ListenerOffString = "Listener is off";
    private static final Label listenerReminder = new Label(ListenerOffString);

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
        long seconds = DurationManager.getSeconds(existingDuration) + learnSeconds;

        // 因为我发现，我关闭LearningMode重开之后，static变量好像没有重新初始化？
        learnSeconds = 0;

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

    /**
     * 用来作为引线，当需要隐藏状态栏后再次打开的场景使用。
     */
    private static void fusePanel() {
        Stage stage = new Stage();
        stage.setTitle("fuse");

        stage.setAlwaysOnTop(true);
        BorderPane root = new BorderPane();

        HBox emptyHBox = new HBox();
        emptyHBox.getStyleClass().add("hbox-running");


        emptyHBox.setOnMouseClicked(event -> {
            // 打开状态栏面部
            show();
            // 关闭本fuse面部
            stage.hide();
        });

        root.setCenter(emptyHBox);


        double h = 30;
        double w = 30;
        // 创建 Scene 并加载 CSS 样式
        Scene scene = new Scene(root, w, h); // 设置窗口大小

        // 导入CSS文件
        scene.getStylesheets().add(Objects.requireNonNull(
                TrayMenu.class.getClassLoader().getResource("learningMode.css")
        ).toExternalForm());

        // 设定 Scene
        stage.setScene(scene);

        // 设置窗口大小
        stage.setHeight(h);
        stage.setWidth(w);

        // 让窗口在右上角显示
        javafx.geometry.Rectangle2D screenBounds = Screen.getPrimary().getVisualBounds();
        stage.setX(screenBounds.getMaxX() - stage.getWidth() - 10);
        stage.setY(50);

        // 设置窗口样式
        stage.initStyle(StageStyle.TRANSPARENT);
        scene.setFill(Color.TRANSPARENT);  // 确保 Scene 透明

        // 创建一个真正裁剪窗口的圆角矩形
        Rectangle clip = new Rectangle(w, h);
        clip.setArcWidth(40);
        clip.setArcHeight(40);
        root.setClip(clip);

        // 启用拖拽窗口
        enableDragging(stage, root);

        // 加一个icon美化
        stage.getIcons().add(new Image(Objects.requireNonNull(LearningMode.class.getResourceAsStream("/puzzle0.png"))));

        stage.show();

    }

    private static Stage panel() {
        Stage stage = new Stage();
        stage.setTitle("Learning mode");

        BorderPane root = new BorderPane();
        root.getStyleClass().add("root-running");

        // 通常没有任何FX窗口后，FX线程就会自动执行退出。
        Platform.setImplicitExit(false); // 关键：禁止隐式退出

        stage.setAlwaysOnTop(true);

        HBox emptyHBox = new HBox();
        emptyHBox.getStyleClass().add("hbox-running");

        // 绑定事件，暂时隐藏状态栏
        emptyHBox.setOnMouseClicked(event -> {
            hide();
            fusePanel();
        });

        LearningDurationLabel.getStyleClass().add("label-time"); // 设置样式
        HBox DurationLabelHBox = new HBox(LearningDurationLabel);
        DurationLabelHBox.setAlignment(Pos.CENTER);


        listenerReminder.getStyleClass().add("label-status");
        HBox reminderBox = new HBox(listenerReminder);
        reminderBox.setAlignment(Pos.CENTER);
        reminderBox.getStyleClass().add("hbox-running");

        // listener on and off control
        reminderBox.setOnMouseClicked(event -> {
            StateManager.switchGlobalListener();
        });

        // 布局
        root.setTop(emptyHBox);
        root.setCenter(DurationLabelHBox);
        root.setBottom(reminderBox);

        // label事件绑定
        bindEventToLearningDurationLabel(root, emptyHBox, reminderBox);

        // 创建 Scene 并加载 CSS 样式
        Scene scene = new Scene(root, width, height); // 设置窗口大小

        // 导入CSS文件
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
