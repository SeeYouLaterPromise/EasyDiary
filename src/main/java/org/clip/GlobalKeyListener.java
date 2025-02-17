package org.clip;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.application.Platform;
import javafx.stage.Stage;


/**
 * 快捷键 全局监听 方便用户释放鼠标，使用键盘快速调用
 */
public class GlobalKeyListener implements NativeKeyListener {
    private boolean CTRL = false;
    private final StringClip stringClip = new StringClip();

    // TODO: Could you find more efficient way to do combination?

    private boolean QuickPanelOn = false;

    private static GlobalKeyListener globalKeyListener = null;

    private Stage QuickNoteStage = null;
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int key = e.getKeyCode();
//        System.out.println(e.getKeyChar() + " : " + key);

        switch (key) {
            case NativeKeyEvent.VC_CONTROL:
                CTRL = true;
//                System.out.println("Ctrl Here!");
                break;
            case NativeKeyEvent.VC_C:
                // CTRL equals to one, indicating that last pressing is CTRL.
                if (CTRL) {
                    System.out.println("Excerpt");
                    // DoneTODO: Excerpt function to extract String from clipboard.
                    stringClip.excerpt();
                }
                break;
            case NativeKeyEvent.VC_K:
                if (CTRL) {
                    // 当按下Ctrl + K时，QuickPanel存在时，即关闭这个Panel.
                    if (QuickPanelOn) {
                        System.out.println("Shut down the existing panel");
                        Platform.runLater(() -> {
                            QuickPanelOn = false;
                            QuickNote.closePanel();
                            QuickNoteStage = null;
                        });
                    } else {
                        System.out.println("Call on a panel for writing.");
                        // 调用JavaFX线程来更新GUI
                        Platform.runLater(() -> {
                            QuickPanelOn = true;
                            QuickNoteStage = QuickNote.getQuickNote();
                        });
                    }
                }
                // TODO: Call on a panel for user to write down.
                break;
            case NativeKeyEvent.VC_ESCAPE:
                // To exit the learning mode.
                if (CTRL) {
                    StringClip.playBeep();
                    shutDownMode();
                }
                break;
            case NativeKeyEvent.VC_ENTER:
                if (CTRL && QuickNoteStage != null) {
                    StringClip.playBeep();
                    QuickNote.submit();
                }
                break;
            case NativeKeyEvent.VC_UP:
                if (CTRL && QuickNoteStage != null) {
                    Platform.runLater(() -> {
                        // we should pay attention that, the downside is the positive direction.
                        double y = QuickNoteStage.getY() - 10;
                        QuickNoteStage.setY(y);
                    });
                }
                break;
            case NativeKeyEvent.VC_DOWN:
                if (CTRL && QuickNoteStage != null) {
                    Platform.runLater(() -> {
                        double y = QuickNoteStage.getY() + 10;
                        QuickNoteStage.setY(y);
                    });
                }
                break;
            case NativeKeyEvent.VC_LEFT:
                if (CTRL && QuickNoteStage != null) {
                    Platform.runLater(() -> {
                        double x = QuickNoteStage.getX() - 10;
                        QuickNoteStage.setX(x);
                    });
                }
                break;
            case NativeKeyEvent.VC_RIGHT:
                if (CTRL) {
                    Platform.runLater(() -> {
                        double x = QuickNoteStage.getX() + 10;
                        QuickNoteStage.setX(x);
                    });
                }
                break;
            default:
                CTRL = false;
        }
    }

    // 这样就可以解决Ctrl释放后，依然等待的问题。
    public void nativeKeyReleased(NativeKeyEvent e) {
        if (e.getKeyCode() == NativeKeyEvent.VC_CONTROL) {
//            System.out.println("release CTRL");
            CTRL = false;
        }
    }

    public void nativeKeyTyped(NativeKeyEvent e) {}

    public static void startUpMode() {
        try {
            // Check if the thread is on or not.
            if (!GlobalScreen.isNativeHookRegistered()) {
                // 这一步是开启JNativeHook线程
                System.out.println("Set up the JNativeHook Thread.");
                GlobalScreen.registerNativeHook();
            }

            // 添加全局键盘监听器
            System.out.println("Add listener!");
            globalKeyListener = new GlobalKeyListener();
            GlobalScreen.addNativeKeyListener(globalKeyListener);

            // 更新LearningMode浮窗上面的状态
            LearningMode.toggleListened();
            LearningMode.updateListenerButtonState();
        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }


    public static void shutDownMode() {
        if (GlobalScreen.isNativeHookRegistered()) {
            System.out.println("Remove the listener.");
            GlobalScreen.removeNativeKeyListener(globalKeyListener);
            // 更新LearningMode浮窗上面的状态
            // 如果使用快捷键调用shutDownMode()的话，会错误使用JNativeHook线程来对GUI操作，引起异常
            Platform.runLater(() -> {
                LearningMode.toggleListened();
                LearningMode.updateListenerButtonState();
            });
        }

    }
}