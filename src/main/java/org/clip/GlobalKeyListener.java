package org.clip;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import javafx.application.Platform;
import javafx.stage.Stage;
import org.clip.GUI.ManualEventMarker;
import org.clip.GUI.StateManager;


/**
 * 快捷键 全局监听 方便用户释放鼠标，使用键盘快速调用
 */
public class GlobalKeyListener implements NativeKeyListener {
    private boolean CTRL = false;
    private final StringClip stringClip = new StringClip();

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
                    StateManager.switchQuickPanelState();
                }
                // TODO: Call on a panel for user to write down.
                break;
            case NativeKeyEvent.VC_M:
                if (CTRL) {
                    // 当按下Ctrl + M时，打开alert框供用户填写完成的事件内容
                    Platform.runLater(()-> {
                        ManualEventMarker.getManualEventMarker();
                    });
                }
                // TODO: Call on a panel for user to write down.
                break;
            case NativeKeyEvent.VC_ESCAPE:
                // To exit the listener
                if (CTRL) {
                    StringClip.playBeep();
                    StateManager.switchGlobalListener();
                }
                break;
            case NativeKeyEvent.VC_I:
                if (CTRL) {
                    // Listener no longer is junior to learningMode.
                    StringClip.playBeep();
                    StateManager.switchLearningMode();
                }
            default:
                if (CTRL) {
                    // temporarily set default action to move the QuickPanel.
                    StateManager.moveQuickPanel(key);
                }
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
//                System.out.println("Set up the JNativeHook Thread.");
                GlobalScreen.registerNativeHook();
            }

            // 添加全局键盘监听器
//            System.out.println("Add listener!");
            globalKeyListener = new GlobalKeyListener();
            GlobalScreen.addNativeKeyListener(globalKeyListener);

        } catch (NativeHookException ex) {
            System.err.println("There was a problem registering the native hook.");
            System.err.println(ex.getMessage());
            System.exit(1);
        }
    }


    public static void shutDownMode() {
        if (GlobalScreen.isNativeHookRegistered()) {
//            System.out.println("Remove the listener.");
            GlobalScreen.removeNativeKeyListener(globalKeyListener);
        }

    }
}