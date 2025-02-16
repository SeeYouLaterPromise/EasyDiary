package org.clip;
import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.application.Platform;


/**
 * 快捷键 全局监听 方便用户释放鼠标，使用键盘快速调用
 */
public class GlobalKeyListener implements NativeKeyListener {
    private boolean CTRL = false;
    private final StringClip stringClip = new StringClip();

    // TODO: Could you find more efficient way to do combination?

    private boolean QuickPanelOn = false;

    private static GlobalKeyListener globalKeyListener = null;
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int key = e.getKeyCode();

        switch (key) {
            case NativeKeyEvent.VC_CONTROL:
                CTRL = true;
//                System.out.println("Ctrl Here!");
                break;
            case NativeKeyEvent.VC_C:
                // CTRL equals to one, indicating that last pressing is CTRL.
                if (CTRL) {
                    CTRL = false;
                    System.out.println("Excerpt");
                    // DoneTODO: Excerpt function to extract String from clipboard.
                    stringClip.excerpt();
                }
                break;
            case NativeKeyEvent.VC_K:
                if (CTRL) {
                    CTRL = false;
                    // 当按下Ctrl + K时，QuickPanel存在时，即关闭这个Panel.
                    if (QuickPanelOn) {
                        System.out.println("Shut down the existing panel");
                        Platform.runLater(() -> {
                            QuickPanelOn = false;
                            QuickNote.closePanel();
                        });
                    } else {
                        System.out.println("Call on a panel for writing.");
                        // 调用JavaFX线程来更新GUI
                        Platform.runLater(() -> {
                            QuickPanelOn = true;
                            QuickNote.getQuickNote();
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
                if (CTRL) {
                    CTRL = false;
                    StringClip.playBeep();
                    QuickNote.submit();
                }
            default:
                CTRL = false;
        }
    }

    public void nativeKeyReleased(NativeKeyEvent e) {}

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