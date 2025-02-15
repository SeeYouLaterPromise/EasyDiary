package org.clip;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

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
    public static void updateQuickNoteButtonState() {
        if (write) {
            write = false;
            quickNoteButton.setText("Write!");
        }
    }

    public static void updateListenerButtonState() {
        if (listened) {
            listened = false;
            listenedButton.setText("Listen!");
        }

    }

    private LearningMode () {
        LmPanel = panel();
    }

    public static Stage getLmPanel() {
        if (learningMode == null) {
            learningMode = new LearningMode();
        }
        return LmPanel;
    }

    // TODO: 将这些子窗口抽象成一个父类，让这里的closePanel...
    public static void closePanel() {
        MainPanel.updateState();
        if (LmPanel != null) LmPanel.hide();
        // 置空引用来让垃圾回收机制回收内存；且方便单例再次调用
        if (write) QuickNote.closePanel();
        if (listened) GlobalKeyListener.shutDownMode();
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

        // Create Buttons (Submit & Fix)
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        Button fixedButton = getFixedButton(stage);
        listenedButton = getListenerButton();
        quickNoteButton = getQuickNoteButton();

        buttonBox.getChildren().addAll(fixedButton, listenedButton, quickNoteButton);
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
        Button fixedButton = new Button("UnFix.");

        // Fix/Unfix button functionality
        fixedButton.setOnAction(e -> {
            // fixed indicates the status of `pin or not`
            if (fixed) {
                // if it has been fixed already, toggle it.
                stage.setAlwaysOnTop(false);
                // remind user the opposite action.
                fixedButton.setText("Fix!");
            } else {
                stage.setAlwaysOnTop(true);
                fixedButton.setText("UnFix.");
            }
            // toggle the status
            fixed = !fixed;
        });
        return fixedButton;
    }

    private static Button getListenerButton() {
        Button button = new Button("Listen!");

        button.setOnAction(e -> {
            if (listened) {
                button.setText("Listen!");
                GlobalKeyListener.shutDownMode();
            } else {
                button.setText("shut.");
                GlobalKeyListener.startUpMode();
            }
            listened = !listened;
        });

        return button;
    }

    private static Button getQuickNoteButton() {
        Button button = new Button("Write!");

        button.setOnAction(e -> {
            if (write) {
                button.setText("Write!");
                QuickNote.closePanel();
            } else {
                button.setText("close.");
                QuickNote.getQuickNote();
            }
            write = !write;
        });

        return button;
    }
}
