package org.clip;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
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
    private final double Height = screenSize.getHeight() / 2 + 200;
    private final double Width = screenSize.getWidth() / 2 + 400;
    private static LocalDate date = null;
    private static Stage certainDayStage = null;
    private static CertainDay certainDay = null;

    // TODO:
    //  1. webview to show markdown.
    //  2. MenuBar for some setting configuration.
    //  3. light/dark theme switch.


    private CertainDay() {
        LocalDate currentDate = LocalDate.now();
        if (date.isBefore(currentDate)) {
            certainDayStage = PastPanel();
        } else if (date.isAfter(currentDate)) {
            certainDayStage = FuturePanel();
        } else {
            certainDayStage = CurrentPanel();
        }
    }

    public static Stage getCertainDayPanel(LocalDate dateSelected) {
        if (certainDay == null) {
            date = dateSelected;
            certainDay = new CertainDay();
        }
        return certainDayStage;
    }
    
    private String getFileContent(String type) {
        // 读取文件内容
        TxtFileManager txtFileManage = new TxtFileManager(date, type);
        return txtFileManage.readFileContent();
    }

    private void rewriteFileContent(String type, String content) {
        TxtFileManager txtFileManager = new TxtFileManager(date, type);
        txtFileManager.WriteToFile(content, false);
    }

    private void appendFileContent(String type, String content) {
        TxtFileManager txtFileManager = new TxtFileManager(date, type);
        txtFileManager.WriteToFile(content, true);
    }

    private void closePanel() {
        certainDayStage.hide();
        certainDay = null;
        MainPanel.showMainPanel();
    }

    private void hideCertainDayPanel() {
        certainDayStage.hide();
    }

    private void showCertainDayPanel() {
        certainDayStage.show();
    }


    private Stage QALearnPanel(TextArea textAreaContent,TextArea textAreaRemark) {
        Stage stage = new Stage();
        String title = "QALearn Panel";
        stage.setTitle(title);
        stage.setOnCloseRequest((WindowEvent we) -> {
            System.out.println("Hide QALearnPanel GUI.");
//            System.exit(0);
            we.consume(); // Prevent the default behavior (window closing)
            stage.hide();
            showCertainDayPanel();
        });

        // 设置子窗口的大小
        stage.setWidth(Width);  // 设置子窗口的宽度
        stage.setHeight(Height); // 设置子窗口的高度

        // 主标题 Label
        Label titleLabel = new Label("Question & Answer Learning based on the past content");
        titleLabel.getStyleClass().add("label-title");
        HBox hBoxTitleLabel = new HBox(titleLabel);
        hBoxTitleLabel.setAlignment(Pos.CENTER);

        Label labelDeepSeek = new Label("DeepSeek");
        labelDeepSeek.getStyleClass().add("label-text");
        TextArea textAreaDeepSeek = getTextArea("DeepSeek", true, true,Height * 0.8, Width / 2);
        Button DeepSeekButton = new Button("Generate questions!");
        DeepSeekButton.setOnAction(event -> {
            // 开启一个子线程来处理网络请求
            new Thread(() -> {
                System.out.println("调用子线程发送网络请求");

                Platform.runLater(() -> {
                    textAreaDeepSeek.setText("请耐心等待DeepSeek的回复。");
                });

                String res = "[DeepSeek] - " + TxtFileManager.getCurrentTimeString() + ":\n" + DeepSeek.concludeAndQuestion(textAreaContent.getText());

                // JavaFX线程处理GUI逻辑
                Platform.runLater(() -> {
                    textAreaDeepSeek.setText(res);
                    textAreaRemark.setText(textAreaRemark.getText() + "\n\n" + res);
                });

                // 将DeepSeek返回的内容 追加到左边的Content区域。
                appendFileContent("Remark", res);

                // 缓存
                appendFileContent("DeepSeek", res);
            }).start();
        });

        Label labelYou = new Label("You");
        labelYou.getStyleClass().add("label-text");
        TextArea textAreaYou = getTextArea("You", true, true, Height * 0.8, Width / 2);
        Button YouButton = new Button("Answer");
        YouButton.setOnAction(event -> {
            // 开启一个子线程来处理网络请求
            new Thread(() -> {
                // 发送答案
                String answer = "[You] - " + TxtFileManager.getCurrentTimeString() + ":\n" + textAreaYou.getText();

                appendFileContent("Remark",  answer);

                Platform.runLater(() -> {
                    textAreaRemark.setText(textAreaRemark.getText() + "\n\n" + answer);
                    textAreaYou.setText(textAreaYou.getText() + "\n\n请耐心等待回复。");
                });

                // 发送网络请求
                String res = "[DeepSeek] - " + TxtFileManager.getCurrentTimeString() + ":\n" + DeepSeek.answerQuestion(textAreaYou.getText());
                System.out.println("得到回复！");

                // JavaFX线程处理GUI逻辑
                Platform.runLater(() -> {
                    // 提醒用户进度
                    textAreaYou.setText(textAreaYou.getText() + "\n\n已回复，请查看！");
                    textAreaDeepSeek.setText(textAreaDeepSeek.getText() + "\n\n" + res);
                    textAreaRemark.setText(textAreaRemark.getText() + "\n\n" + res);
                });

                // 将DeepSeek返回的内容 追加到左边的Remark区域。
                appendFileContent("Remark", res);

                // 缓存
//                appendFileContent("You", answer);
                appendFileContent("DeepSeek", res);
            }).start();
        });

        // 使用 VBox 来分别布局每个 Label 和 TextArea
        VBox vBoxContent = new VBox(8, labelYou, textAreaYou, YouButton);
        vBoxContent.getStyleClass().add("hbox");
        vBoxContent.setAlignment(Pos.CENTER);


        VBox vBoxPlan = new VBox(8, labelDeepSeek, textAreaDeepSeek, DeepSeekButton);
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



    // RULEs: 1. You cant modify the past existing content, but can leave `remark`; (Content + Remark)
    //        2. You can  modify today's everything, please seize your day!; (Plan + Content)
    //        3. You can only set plan for future. (Plan)

    // TODO: 制作响应式布局

    private Stage PastPanel() {
        Stage stage = new Stage();
        String title = date.getDayOfMonth() + "-th, " + date.getMonth();
        stage.setTitle(title);
        stage.setOnCloseRequest((WindowEvent we) -> {
            System.out.println("Hide PastPanel GUI.");
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
        TextArea textAreaContent = getTextArea("Content",false, true,Height * 0.8, Width / 2);

        Label labelRemark = new Label("QALearn Start!");
        labelRemark.getStyleClass().add("label-text");

        TextArea textAreaRemark = getTextArea("Remark", true, true,Height * 0.8, Width / 2);
        labelRemark.setId("Remark");
        labelRemark.setOnMouseClicked(event -> {
            // 隐藏CertainDayPanel，打开QALearningPanel.
            hideCertainDayPanel();
            QALearnPanel(textAreaContent, textAreaRemark);
        });


        // 使用 VBox 来分别布局每个 Label 和 TextArea
        VBox vBoxContent = new VBox(10, labelContent, textAreaContent);
        vBoxContent.getStyleClass().add("hbox");
        vBoxContent.setAlignment(Pos.CENTER);

        VBox vBoxRemark = new VBox(10, labelRemark, textAreaRemark);
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
            System.out.println("Hide CurrentDayPanel GUI.");
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
        TextArea textAreaPlan = getTextArea("Plan", true, true,Height * 0.8, Width / 2);

        Label labelContent = new Label("Content");
        labelContent.getStyleClass().add("label-text");
        TextArea textAreaContent = getTextArea("Content", true, true,Height * 0.8, Width / 2);

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
            System.out.println("Hide FuturePanel GUI.");
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
        TextArea textAreaPlan = getTextArea("Plan", true, true,0.8 * Height, Width);

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

    private TextArea getTextArea(String type, Boolean editable, Boolean autoReservable, double height, double width) {
        TextArea textArea = new TextArea();
        textArea.setPromptText("Enter the first text...");
        textArea.setWrapText(true); // 启用换行
        if (autoReservable) textArea.setText(getFileContent(type));
        textArea.setEditable(editable);  // 禁止编辑
        textArea.setPrefHeight(height); // 设置 TextArea 的默认高度
        textArea.setMaxHeight(height);  // 强制设置最大高度
        textArea.setMinHeight(height);  // 强制设置最小高度
        textArea.setPrefWidth(width);

        textArea.setOnKeyReleased(event -> {
//                System.out.println(type + ' ' + date.toString());
            // 先写一个实时保存的功能，因为Ctrl+S保存的方案很多人不习惯。
            // 先采用全覆盖重写的方案，简单实现一下。
            if(autoReservable) {
                rewriteFileContent(type, textArea.getText());
            } else {
                System.out.println("Ctrl + S reserve...");
            }
        });
        return textArea;
    }

}
