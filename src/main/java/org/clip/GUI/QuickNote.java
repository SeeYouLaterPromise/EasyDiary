package org.clip.GUI;

import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.WindowEvent;
import org.clip.TxtFileManager;

public class QuickNote {
    private static final double height = 300;
    private static final double width = 300;

    private static final TextArea textArea = new TextArea();
    private static boolean submitted = false;
    private static boolean fixed = true;

    private static final String PLACEHOLDER_TEXT = "Enter your thoughts here...";

    // 单例 懒汉模式
    private static QuickNote quickNote = null;
    private static Stage noteStage = null;

    public static void show() {
        if (noteStage != null) {
            noteStage.show();
        }
    }

    public static void hide() {
        if (noteStage != null) {
            noteStage.hide();
        }
    }
    private QuickNote() {
        noteStage = NewPanel();
    }
    public static Stage getQuickNote() {
        if (quickNote == null) {
            quickNote = new QuickNote();
        }
        return noteStage;
    }

    public static void moveUp() {
        noteStage.setY(noteStage.getY() - 10);
    }

    public static void moveDown() {
        noteStage.setY(noteStage.getY() + 10);
    }

    public static void moveLeft() {
        noteStage.setX(noteStage.getX() - 10);
    }

    public static void moveRight() {
        noteStage.setX(noteStage.getX() + 10);
    }

    public static void move(int direction) {
        switch (direction) {
            case NativeKeyEvent.VC_UP:
                moveUp();
                break;
            case NativeKeyEvent.VC_DOWN:
                moveDown();
                break;
            case NativeKeyEvent.VC_LEFT:
                moveLeft();
                break;
            case NativeKeyEvent.VC_RIGHT:
                moveRight();
                break;
            default:
                break;
        }
    }


    // Submit the content of the text area to a file
    public static void submit() {
        String text = textArea.getText();
        String entry = "[Thought] - " + TxtFileManager.getCurrentTimeString() + ":\n" + text + "\n";
        TxtFileManager txtFileManager = new TxtFileManager();
        txtFileManager.WriteToFile(entry, true);

        // Optionally, you can add a Date to the entry before writing
        // Add Date to mark the submission
        submitted = true;
    }

    public static void closePanel() {
        QuickNote.hide();
        // 置空引用来让垃圾回收机制回收内存；且方便单例再次调用
        quickNote = null;
    }

    private static Stage NewPanel() {
        Stage stage = new Stage();
        stage.setTitle("Quick Note");

        // Handling the close event to ensure the app exits properly
        stage.setOnCloseRequest((WindowEvent we) -> {
            we.consume(); // Prevent the default behavior (window closing)

            // 注意这个不要写在closePanel()里面，会互相二次调用。
            StateManager.switchQuickPanelState();

            // 清空引用
            closePanel();
        });

        stage.setAlwaysOnTop(true);
        BorderPane root = new BorderPane();

        // Create TextArea (for input area)
        textArea.setText(PLACEHOLDER_TEXT);
        textArea.setStyle("-fx-text-fill: gray;");
        textArea.setWrapText(true);

        // Add a listener to handle placeholder text removal and restoration
        textArea.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue) { // Focus gained
                if (textArea.getText().equals(PLACEHOLDER_TEXT)) {
                    textArea.clear();
                    textArea.setStyle("-fx-text-fill: black;");
                } else if (submitted) {
                    textArea.clear();
                    submitted = false;
                }
            } else { // Focus lost
                if (textArea.getText().isEmpty()) {
                    textArea.setText(PLACEHOLDER_TEXT);
                    textArea.setStyle("-fx-text-fill: gray;");
                }
            }
        });

        root.setCenter(textArea);

        // Create Buttons (Submit & Fix)
        HBox buttonBox = new HBox(10);
        buttonBox.setAlignment(Pos.CENTER);
        Button fixedButton = new Button("Not Fixed anymore.");
        Button submitButton = new Button("Finish!");

        // Fix/Unfix button functionality
        fixedButton.setOnAction(e -> {
            // fixed indicates the status of `pin or not`
            if (fixed) {
                // if it has been fixed already, toggle it.
                stage.setAlwaysOnTop(false);
                // remind user the opposite action.
                fixedButton.setText("Fix your panel!");
            } else {
                stage.setAlwaysOnTop(true);
                fixedButton.setText("Not Fixed anymore.");
            }
            // toggle the status
            fixed = !fixed;
        });

        // Submit button functionality
        submitButton.setOnAction(e -> {
            submit();
        });

        buttonBox.getChildren().addAll(fixedButton, submitButton);
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
        stage.setX(screenBounds.getMaxX() - stage.getWidth() - 20);
        stage.setY(screenBounds.getMaxY() - stage.getHeight() - 30);
        // Show the stage after positioning
        stage.initStyle(StageStyle.UTILITY);
        stage.show();
        return stage;
    }
}
