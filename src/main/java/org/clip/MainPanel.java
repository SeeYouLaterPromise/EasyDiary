package org.clip;

import javafx.application.Application;
import javafx.beans.binding.Bindings;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.stage.Stage;


import java.awt.*;
import java.time.LocalDate;
import java.time.Month;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainPanel extends Application {

    // TODO: Login update or configuration.
    private String userName = "llyexin";
    private LocalDate currentDate = LocalDate.now();
    private int year = currentDate.getYear();

    private TableView<String> tableView;

    // get the size of device screen
    private Toolkit toolkit = Toolkit.getDefaultToolkit();
    private Dimension screenSize = toolkit.getScreenSize();

    // 主线程只用来管理UI，多线程来管理其他功能调用。
    private ExecutorService executorService;

    private static boolean ModeOn = false;
    private static Button ModeOnButton = new Button("Start up learning mode!");

    public static void updateState() {
        ModeOn = false;
        ModeOnButton.setText("Start up learning mode!");
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void stop() {
        // 应用退出时关闭线程池
        executorService.shutdown();
    }

    @Override
    public void start(Stage primaryStage) {
        // 创建一个固定大小的线程池
        executorService = Executors.newFixedThreadPool(3);

        // 设置根布局
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));

        // 创建年份标题
        Label yearLabel = new Label(userName + "'s " + currentDate.getYear());

        // TODO: how to make it locate center?
        yearLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold;");

        // 创建日期按钮（1-31）
        GridPane calendarGrid = createCalendarGrid();

        // 创建 TableView 用于显示选择日期的事件
        tableView = new TableView<>();
        TableColumn<String, String> eventColumn = new TableColumn<>("Events");
        eventColumn.setCellValueFactory(data -> Bindings.createObjectBinding(() -> data.getValue()));
        tableView.getColumns().add(eventColumn);
        tableView.setPrefHeight(150);

//         日期按钮点击事件
        for (int i = 1; i <= 12; i++) {
            for (int j = 1; j <= 31; j++) {
                Label label = (Label) calendarGrid.lookup("#" + i + "_" + j);
                // #i_j 确实可以索引到一个Label组件，然后进行event binding.
                if (label != null) {
                    // 先根据id算出月日，然后对比今天是`之前`还是`之后`。
                    LocalDate TargetDate = LocalDate.of(year, i, j);
                    if (TargetDate.isBefore(currentDate)) {
//                        label.setStyle("-fx-background-color: #fde75d;");
                    } else if (TargetDate.isEqual(currentDate)) {
//                        label.setStyle("-fx-background-color: #48f12e;");
                    } else {
//                        label.setStyle("-fx-background-color: #ff0b0b;");
                    }
                    int finalI = i; // 需要做一下 final 处理
                    int finalJ = j;
                    label.setOnMouseClicked(event -> {
                        tableView.getItems().clear();
                        tableView.getItems().add("Event for day (" + finalI + ", " + finalJ + ")"); // 模拟事件数据
                        openNewWindow(TargetDate);
                    });
                }
            }
        }

        // TODO: Learning start and stop management.

        ModeOnButton.setOnAction(event -> {
            if (ModeOn) {
                ModeOnButton.setText("Start up learning mode!");
                LearningMode.closePanel();
            } else {
                // remind user the status.
                ModeOnButton.setText("Shut down learning mode.");
                Stage LmStage = LearningMode.getLmPanel();
            }
            ModeOn = !ModeOn;
        });

        // 将元素添加到根布局
        root.getChildren().addAll(yearLabel, calendarGrid, tableView, ModeOnButton);

        // 创建并设置场景
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setWidth((double) screenSize.width / 2);  // 设置子窗口的宽度
        primaryStage.setHeight((double) screenSize.height / 2); // 设置子窗口的高度
        primaryStage.setTitle("Easy Diary");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // 创建子窗口的方法
    private void openNewWindow(LocalDate date) {
        // 创建子窗口
        Stage newStage = new Stage();
        newStage.setTitle("Child Window");

        // 设置子窗口的大小
        newStage.setWidth((double) screenSize.width / 2);  // 设置子窗口的宽度
        newStage.setHeight((double) screenSize.height / 2); // 设置子窗口的高度


        // 在子窗口中添加内容
        Label message = new Label("This is a new window.");
        StackPane secondaryLayout = new StackPane();
        secondaryLayout.getChildren().add(message);

        // 创建 TextArea 来显示文件内容
        TextArea textArea = new TextArea();
        textArea.setEditable(false);  // 禁止编辑
        textArea.setWrapText(true);   // 自动换行

        // 读取并显示文件内容
        TxtFileManager txtFileManage = new TxtFileManager(date);
        String fileContent = txtFileManage.readFileContent();  // 文件路径
        textArea.setText(fileContent);

        secondaryLayout.getChildren().add(textArea);

        // 创建子窗口的场景并显示
        Scene newScene = new Scene(secondaryLayout, 300, 200);
        newStage.setScene(newScene);
        newStage.show();
    }

    // 创建指定年份的日历视图
    private GridPane createCalendarGrid() {
        GridPane gridPane = new GridPane();
        gridPane.setVgap(5);
        gridPane.setHgap(5);

        // 显示年份
        Label yearLabel = new Label(String.valueOf(this.year));
        gridPane.add(yearLabel, 0, 0, 1, 1); // 标题在第一行占据31列

        // TODO:    col
        //      row ""  1  2  3  ...   31
        //          JAN
        //          FEB

        // 处理第一行
        for (int j = 1; j <= 31; j++) {
            Label label = new Label(String.valueOf(j));
            gridPane.add(label, j,0);
        }


        for (int i = 1; i <= 12; i++) {
            Label monLabel = new Label(Month.values()[i-1].toString().substring(0, 3));
            gridPane.add(monLabel, 0, i);
            int LengthOfMonth = LocalDate.of(this.year, i, 1).lengthOfMonth();
            // 根据每个月具体多少天来动态绑定
            for (int j = 1; j <= LengthOfMonth; j++) {
                Label label = new Label("0");
                label.setId(i + "_" + j);
                gridPane.add(label, j, i);
            }
        }


        return gridPane;
    }
}

