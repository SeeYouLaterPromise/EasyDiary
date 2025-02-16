package org.clip;

import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import java.awt.*;
import java.time.LocalDate;
import java.util.Objects;

public class CertainDay {
    private final Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
    private final double Height = screenSize.getHeight() / 2 + 120;
    private final double Width = screenSize.getWidth() / 2 + 220;
    private static LocalDate date = null;
    private static Stage cerainDayStage = null;
    private static CertainDay certainDay = null;


    private CertainDay() {
        LocalDate currentDate = LocalDate.now();
        if (date.isBefore(currentDate)) {
            cerainDayStage = PastPanel();
        } else if (date.isAfter(currentDate)) {
            cerainDayStage = FuturePanel();
        } else {
            cerainDayStage = CurrentPanel();
        }
    }

    public static Stage getCertainDayPanel(LocalDate dateSelected) {
        if (certainDay == null) {
            date = dateSelected;
            certainDay = new CertainDay();
        }
        return cerainDayStage;
    }
    
    private String getFileContent(String type) {
        // 读取文件内容
        TxtFileManager txtFileManage = new TxtFileManager(date, type);
        return txtFileManage.readFileContent();
    }

    private void reserveContent(String type, String content) {
        TxtFileManager txtFileManager = new TxtFileManager(date, type);
        txtFileManager.WriteToFile(content, false);
    }

    private void closePanel() {
        cerainDayStage.hide();
        certainDay = null;
        MainPanel.showMainPanel();
    }



    // RULEs: 1. You cant modify the past existing content, but can leave `remark`; (Content + Remark)
    //        2. You can  modify today's everything, please seize your day!; (Plan + Content)
    //        3. You can only set plan for future. (Plan)

    // TODO: 制作响应式布局

    private Stage PastPanel() {
        Stage stage = new Stage();
        String title = date.getDayOfMonth() + "-th, " + date.getMonth();
        stage.setTitle(title);
        stage.setOnCloseRequest((WindowEvent we) -> {
            System.out.println("Hide Certain Day GUI.");
//            System.exit(0);
            we.consume(); // Prevent the default behavior (window closing)
            closePanel();
        });

        // 设置子窗口的大小
        stage.setWidth(Width);  // 设置子窗口的宽度
        stage.setHeight(Height); // 设置子窗口的高度

        // 主标题 Label
        Label titleLabel = new Label(title + ". Learn from your past.");
        titleLabel.getStyleClass().add("label-title");
        HBox hBoxTitleLabel = new HBox(titleLabel);
        hBoxTitleLabel.setAlignment(Pos.CENTER);

        Label labelContent = new Label("Content");
        labelContent.getStyleClass().add("label-text");
        TextArea textAreaContent = getTextArea("Content",false, Height * 0.8, Width / 2);

        Label labelRemark = new Label("Remark");
        labelRemark.getStyleClass().add("label-text");

        // TODO: `Remark` with bold style, binding the event for revoking AI to help us conclude the content left, and prepare 3-4 questions.
        labelRemark.setId("Remark");
        labelRemark.setOnMouseClicked(event -> {
            System.out.println("AI comments Revoking.");
            // function()
        });

        TextArea textAreaRemark = getTextArea("Remark", true, Height * 0.4, Width / 2);

        // 使用 VBox 来分别布局每个 Label 和 TextArea
        VBox vBoxContent = new VBox(15, labelContent, textAreaContent);
        vBoxContent.getStyleClass().add("hbox");
        vBoxContent.setAlignment(Pos.CENTER);

        VBox vBoxRemark = new VBox(15, labelRemark, textAreaRemark);
        vBoxRemark.getStyleClass().add("hbox");
        vBoxRemark.setAlignment(Pos.CENTER);

        BorderPane bPane = new BorderPane();
        bPane.setLeft(vBoxContent);
        bPane.setRight(vBoxRemark);

        // 使用 VBox 布局将整个界面垂直排列
        VBox vBox = new VBox(20);
        titleLabel.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(hBoxTitleLabel, bPane);

        // 设置根容器的样式
        StackPane root = new StackPane();
        root.getChildren().add(vBox);
        root.getStyleClass().add("root");

        // 创建场景并设置 CSS 样式
        Scene scene = new Scene(root, Width, Height);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/CertainDay.css")).toExternalForm());

        stage.setScene(scene);
//        stage.initStyle(StageStyle.UTILITY); // 改为无边框的现代窗口样式

        stage.show();
        return stage;
    }

    private Stage CurrentPanel() {
        Stage stage = new Stage();
        String title = date.getDayOfMonth() + "-th, " + date.getMonth();
        stage.setTitle(title);
        stage.setOnCloseRequest((WindowEvent we) -> {
            System.out.println("Hide Learning mode GUI.");
//            System.exit(0);
            we.consume(); // Prevent the default behavior (window closing)
            closePanel();
        });

        // 设置子窗口的大小
        stage.setWidth(Width);  // 设置子窗口的宽度
        stage.setHeight(Height); // 设置子窗口的高度

        // 主标题 Label
        Label titleLabel = new Label(title + ". Seize your day!");
        titleLabel.getStyleClass().add("label-title");
        HBox hBoxTitleLabel = new HBox(titleLabel);
        hBoxTitleLabel.setAlignment(Pos.CENTER);

        Label labelPlan = new Label("Plan");
        labelPlan.getStyleClass().add("label-text");
        TextArea textAreaPlan = getTextArea("Plan", true, Height * 0.8, Width / 2);

        Label labelContent = new Label("Content");
        labelContent.getStyleClass().add("label-text");
        TextArea textAreaContent = getTextArea("Content", true, Height * 0.8, Width / 2);

        // 使用 VBox 来分别布局每个 Label 和 TextArea
        VBox vBoxContent = new VBox(15, labelContent, textAreaContent);
        vBoxContent.getStyleClass().add("hbox");
        vBoxContent.setAlignment(Pos.CENTER);


        VBox vBoxPlan = new VBox(15, labelPlan, textAreaPlan);
        vBoxPlan.getStyleClass().add("hbox");
        vBoxPlan.setAlignment(Pos.CENTER);

        BorderPane bPane = new BorderPane();
        bPane.setRight(vBoxContent);
        bPane.setLeft(vBoxPlan);

        // 使用 VBox 布局将整个界面垂直排列
        VBox vBox = new VBox(20);
        titleLabel.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(hBoxTitleLabel, bPane);

        // 设置根容器的样式
        StackPane root = new StackPane();
        root.getChildren().add(vBox);
        root.getStyleClass().add("root");

        // 创建场景并设置 CSS 样式
        Scene scene = new Scene(root, Width, Height);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/CertainDay.css")).toExternalForm());

        stage.setScene(scene);
//        stage.initStyle(StageStyle.UTILITY); // 改为无边框的现代窗口样式

        stage.show();
        return stage;
    }

    private Stage FuturePanel() {
        Stage stage = new Stage();
        String title = date.getDayOfMonth() + "-th, " + date.getMonth() + ". Plan your future.";
        stage.setTitle(title);
        stage.setOnCloseRequest((WindowEvent we) -> {
            System.out.println("Hide Learning mode GUI.");
//            System.exit(0);
            we.consume(); // Prevent the default behavior (window closing)
            closePanel();
        });

        // 设置子窗口的大小
        stage.setWidth(Width);  // 设置子窗口的宽度
        stage.setHeight(Height); // 设置子窗口的高度

        // 主标题 Label
        Label titleLabel = new Label(title);
        titleLabel.getStyleClass().add("label-title");
        HBox hBoxTitleLabel = new HBox(titleLabel);
        hBoxTitleLabel.setAlignment(Pos.CENTER);

        Label labelPlan = new Label("Plan");
        labelPlan.getStyleClass().add("label-text");
        TextArea textAreaPlan = getTextArea("Plan", true, 0.8 * Height, Width);

        // 使用 VBox 来分别布局每个 Label 和 TextArea
        VBox vBoxPlan = new VBox(15, labelPlan, textAreaPlan);
        vBoxPlan.getStyleClass().add("hbox");
        vBoxPlan.setAlignment(Pos.CENTER);

        BorderPane bPane = new BorderPane();
        bPane.setLeft(vBoxPlan);

        // 使用 VBox 布局将整个界面垂直排列
        VBox vBox = new VBox(20);
        titleLabel.setAlignment(Pos.CENTER);
        vBox.getChildren().addAll(hBoxTitleLabel, bPane);

        // 设置根容器的样式
        StackPane root = new StackPane();
        root.getChildren().add(vBox);
        root.getStyleClass().add("root");

        // 创建场景并设置 CSS 样式
        Scene scene = new Scene(root, Width, Height);
        scene.getStylesheets().add(Objects.requireNonNull(getClass().getResource("/CertainDay.css")).toExternalForm());

        stage.setScene(scene);
//        stage.initStyle(StageStyle.UTILITY); // 改为无边框的现代窗口样式

        stage.show();
        return stage;
    }

    private TextArea getTextArea(String type, Boolean editable, double height, double width) {
        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter the first text...");
        textArea.setWrapText(true); // 启用换行
        textArea.setText(getFileContent(type));
        textArea.setEditable(editable);  // 禁止编辑
        textArea.setPrefHeight(height); // 设置 TextArea 的默认高度
        textArea.setMaxHeight(height);  // 强制设置最大高度
        textArea.setMinHeight(height);  // 强制设置最小高度
        textArea.setPrefWidth(width);
        if (editable) {
            textArea.setOnKeyReleased(event -> {
//                System.out.println(type + ' ' + date.toString());
                // 先写一个实时保存的功能，因为Ctrl+S保存的方案很多人不习惯。
                // 先采用全覆盖重写的方案，简单实现一下。
                reserveContent(type, textArea.getText());
            });
        }
        return textArea;
    }

}
