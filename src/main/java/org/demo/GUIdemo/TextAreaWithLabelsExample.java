package org.demo.GUIdemo;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class TextAreaWithLabelsExample extends Application {

    @Override
    public void start(Stage primaryStage) {
        // 主标题 Label
        Label titleLabel = new Label("Form Title");
        titleLabel.getStyleClass().add("label-title");

        // 第一个 TextArea 及其 Label
        Label label1 = new Label("First TextArea");
        label1.getStyleClass().add("label-text");
        TextArea textArea1 = new TextArea();
        textArea1.setPromptText("Enter the first text...");
        textArea1.setWrapText(true); // 启用换行
        textArea1.setPrefHeight(100); // 设置 TextArea 的默认高度
        ScrollPane scrollPane1 = new ScrollPane(textArea1); // 为 TextArea 添加 ScrollPane

        // 第二个 TextArea 及其 Label
        Label label2 = new Label("Second TextArea");
        label2.getStyleClass().add("label-text");
        TextArea textArea2 = new TextArea();
        textArea2.setPromptText("Enter the second text...");
        textArea2.setWrapText(true); // 启用换行
        textArea2.setPrefHeight(100); // 设置 TextArea 的默认高度
        ScrollPane scrollPane2 = new ScrollPane(textArea2); // 为 TextArea 添加 ScrollPane

        // 使用 HBox 来分别布局每个 Label 和 TextArea
        HBox hBox1 = new HBox(15, label1, scrollPane1);
        hBox1.getStyleClass().add("hbox");
        HBox hBox2 = new HBox(15, label2, scrollPane2);
        hBox2.getStyleClass().add("hbox");

        // 使用 VBox 布局将整个界面垂直排列
        VBox vBox = new VBox(20);
        vBox.getChildren().addAll(titleLabel, hBox1, hBox2);

        // 设置根容器的样式
        StackPane root = new StackPane();
        root.getChildren().add(vBox);
        root.getStyleClass().add("root");

        // 创建场景并设置 CSS 样式
        Scene scene = new Scene(root, 400, 300);
        scene.getStylesheets().add("file:style.css");

        // 设置窗口
        primaryStage.setTitle("TextArea with Labels");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

