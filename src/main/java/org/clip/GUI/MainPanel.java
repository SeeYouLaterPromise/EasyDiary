package org.clip.GUI;

import javafx.animation.FadeTransition;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import org.clip.TxtFileManager;

import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class MainPanel extends Application {

    // TODO: Login update or configuration.
    private String userName = "llyexin";
    private LocalDate currentDate = LocalDate.now();

    // 为处理跨年学习的情况，不设置为final
    private int year = currentDate.getYear();

    private final Label yearLabel = new Label(userName + "'s " + year);

    // 为解决跨越0：00问题而创建的定时任务
    private void updateCurrentDate() {
        // 获取本地时区
        ZoneId zoneId = ZoneId.systemDefault();

        // 获取当前时间
        ZonedDateTime now = ZonedDateTime.now(zoneId);

        // 设置目标时间为今天的 23:59:59
        ZonedDateTime targetTime = now.withHour(23).withMinute(59).withSecond(59).withNano(0);

        // 如果目标时间已经过去，设置目标时间为明天的 23:59:59
        if (now.isAfter(targetTime)) {
            targetTime = targetTime.plusDays(1);
        }

        // 计算从当前时间到目标时间的延迟（毫秒）
        long delay = java.time.Duration.between(now, targetTime).toMillis();

        // 创建调度服务
        ScheduledExecutorService scheduler = Executors.newSingleThreadScheduledExecutor();

        // 创建定时任务
        Runnable task = () -> {
            // Add a delay (1s) to ensure LocateDate updated already.
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {
                System.err.println(ex.getMessage());
            }

            currentDate = LocalDate.now();
            year = currentDate.getYear();
            yearLabel.setText(userName + "'s " + year);
        };

        // 安排任务在指定时间执行
        scheduler.schedule(task, delay, TimeUnit.MILLISECONDS);
    }


    // TODO: used for display your plan today
    private TableView<String> tableView;

    // get the size of device screen
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final double Height = screenSize.getHeight() / 2 + 200;
    private final double Width = screenSize.getWidth() / 2 + 400;

    // learning mode function
    private static boolean ModeOn = false;
    private static final Button LearningModeOnButton = new Button("Start up learning mode!");

    private static Stage stage = null;
    private GridPane calendarGrid = null;
    private final String todayLabelId = "#" + currentDate.getMonthValue() + "_" + currentDate.getDayOfMonth();
    private static Label todayLabel = null;
    public static Label getTodayLabel() {
        return todayLabel;
    }

    /**
     * for learning mode shutdown, update the status in the main panel to remind user.
     */
    public static void updateState() {
        ModeOn = false;
        LearningModeOnButton.setText("Start up learning mode!");
    }

    public static void show() {
        stage.show();
    }

    public static void hide() {
        stage.hide();
    }

    public static void main(String[] args) {
        launch(args);
    }

    private void closePanel () {
        // ModeOn, then shut down mode.
        if (ModeOn) LearningMode.closePanel();

    }

    // 显示信息窗口
    private void showMessage(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    @Override
    public void stop() throws Exception {}

    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        // 通常没有任何FX窗口后，FX线程就会自动执行退出。
        Platform.setImplicitExit(false); // 关键：禁止隐式退出

        // 系统栏托盘
        TrayMenu.getTrayIcon();

        // 定时任务来处理跨越0：00问题
        new Thread(this::updateCurrentDate).start();

        // 点击后上角关闭按钮的行为
        primaryStage.setOnCloseRequest((WindowEvent we) -> {
            we.consume(); // Prevent the default behavior (window closing)

            Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
            alert.setTitle("退出程序");
            alert.setHeaderText(null);
            alert.setContentText("是否退出程序？");

            // 获取 Alert 的 Stage
            Stage alertStage = (Stage) alert.getDialogPane().getScene().getWindow();

            // 设置 Icon
            alertStage.getIcons().add(new Image(Objects.requireNonNull(TrayMenu.class.getClassLoader().getResourceAsStream("puzzle0.png"))));

            Optional<ButtonType> result = alert.showAndWait();
            if(result.get().equals(ButtonType.OK)){
                // 确认后退出
                closePanel();
//                Platform.exit();
                System.exit(0);
            } else if (result.get().equals(ButtonType.CANCEL)) {
                // 点击取消为最小化: 隐藏面板
//                Platform.exit(); 这个会退出FX线程
                MainPanel.hide();
            }

        });

        // 设置根布局
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        // 创建年份Label的ID
        yearLabel.setId("year-title");

        // 创建日历组件
        calendarGrid = createCalendarGrid();
        calendarGrid.setId("calendar-grid");

        // 创建 TableView 用于显示选择日期的事件
        tableView = new TableView<>();
        TableColumn<String, String> eventColumn = new TableColumn<>("Events");
        eventColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue()));
        tableView.getColumns().add(eventColumn);
        tableView.setPrefHeight(150);
        tableView.setOpacity(0);

        // 表格内容刷新时加入过渡效果
        FadeTransition fadeIn = new FadeTransition(Duration.seconds(2), tableView);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();


//         日历标签点击事件绑定
        for (int i = 1; i <= 12; i++) {
            for (int j = 1; j <= 31; j++) {
                Label label = (Label) calendarGrid.lookup("#" +  i + "_" + j);
                // #i_j 确实可以索引到一个Label组件，然后进行event binding.
                if (label != null) {
                    // 先根据id算出月日，然后对比今天是`之前`还是`之后`。
                    LocalDate TargetDate = LocalDate.of(year, i, j);

                    int finalI = i; // 需要做一下 final 处理
                    int finalJ = j;
                    label.setOnMouseClicked(event -> {
                        // 表格操作
                        tableView.getItems().clear();
                        tableView.getItems().add("Event for day (" + finalI + ", " + finalJ + ")"); // 模拟事件数据

                        getCertainDayPanel(TargetDate);
                        // 打开学习模式后，隐藏主面板
                        hide();
                    });
                }
            }
        }

        // bind the todayLabel
        todayLabel = (Label) calendarGrid.lookup(todayLabelId);


        // 将年份标签放入一个HBox中，并设置HBox的对齐方式
        VBox vBox = new VBox(yearLabel, calendarGrid);
        vBox.setAlignment(Pos.CENTER);  // 设置HBox的内容居中
        vBox.setPadding(new Insets(10));  // 可选：给HBox添加一些内边距，使其不至于紧贴边缘

        // 将元素添加到根布局
        root.getChildren().addAll(vBox, tableView);

        // 创建并设置场景
        Scene scene = new Scene(root, Width, Height);

        // 添加CSS sheet
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/mainPanel.css")).toExternalForm());

        primaryStage.setWidth(Width);  // 设置子窗口的宽度
        primaryStage.setHeight(Height); // 设置子窗口的高度
        primaryStage.setTitle("Easy Diary");
        primaryStage.setScene(scene);

//        primaryStage.initStyle(StageStyle.UNDECORATED);

        primaryStage.getIcons().add(new Image(Objects.requireNonNull(getClass().getResourceAsStream("/puzzle0.png"))));
        primaryStage.show();
    }

    // 创建子窗口的方法
    private void getCertainDayPanel(LocalDate date) {
        CertainDay.getCertainDayPanel(date);
    }

    // 创建指定年份的日历视图
    private GridPane createCalendarGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(10);
        gridPane.setHgap(12);

        // 显示年份
        Label yearLabel = new Label(String.valueOf(this.year));
        yearLabel.setId("Label-head");

        gridPane.add(yearLabel, 0, 0, 1, 1); // 标题在第一行占据31列

        // TABl:    col
        //      row ""  1  2  3  ...   31
        //          JAN
        //          FEB

        // 处理第一行
        for (int j = 1; j <= 31; j++) {
            Label label = new Label(String.valueOf(j));
            label.setId("Label-head");
            gridPane.add(label, j,0);
        }


        for (int i = 1; i <= 12; i++) {
            Label monLabel = new Label(Month.values()[i-1].toString().substring(0, 3));
            monLabel.setId("Label-head");
            gridPane.add(monLabel, 0, i);
            int LengthOfMonth = LocalDate.of(this.year, i, 1).lengthOfMonth();
            // 根据每个月具体多少天来动态绑定
            for (int j = 1; j <= LengthOfMonth; j++) {
                TxtFileManager txtFileManager = new TxtFileManager(LocalDate.of(year, i, j), "LearningDuration");
                String content = txtFileManager.readFileContent().replace("\n", "");

                content = content.isEmpty() ? "0h" : extractMajorDuration(content.split("_"));
                Label label = new Label(content);
                label.setId(i + "_" + j);
                gridPane.add(label, j, i);
            }
        }


        return gridPane;
    }

    private String extractMajorDuration(String[] content) {
        // 兼容之前的方案
        if (content.length == 1) {
            return content[0];
        }

        String labelContent;
        if (!content[0].equals("0h")) {
            labelContent = content[0];
        } else if(!content[1].equals("0m")) {
            labelContent = content[1];
        } else {
            labelContent = content[2];
        }
        return labelContent;
    }
}

