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
    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
//        System.out.println("Key Pressed: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
//        System.out.println(e.getKeyCode());
//        System.out.println("CTRL: " + CTRL);
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

    public void nativeKeyReleased(NativeKeyEvent e) {
//        System.out.println("Key Released: " + NativeKeyEvent.getKeyText(e.getKeyCode()));
    }

    public void nativeKeyTyped(NativeKeyEvent e) {
//        System.out.println("Key Typed: " + e.getKeyText(e.getKeyCode()));
//        System.out.println("Key Typed: " + e.paramString());
    }

    public static void startUpMode() {
        System.out.println("Enter Listening Mode!");
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
    }

    // FIXME: IF you shutdown immediately after startUp, there is something would be caught, which means that register have not been finished.
    public static void shutDownMode() {
        try {
            System.out.println("Exit the Listening mode.");
            GlobalScreen.unregisterNativeHook();
            LearningMode.updateListenerButtonState();
        } catch (NativeHookException nativeHookException) {
            System.err.println(nativeHookException.getMessage());
        }
    }

    public static void main(String[] args) {
        try {
            GlobalScreen.registerNativeHook();
        }
        catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());

            System.exit(1);
        }
        GlobalScreen.addNativeKeyListener(new GlobalKeyListener());
    }
}