package org.clip;

import com.github.kwhat.jnativehook.GlobalScreen;
import com.github.kwhat.jnativehook.NativeHookException;
import com.github.kwhat.jnativehook.keyboard.NativeKeyEvent;
import com.github.kwhat.jnativehook.keyboard.NativeKeyListener;
import com.github.kwhat.jnativehook.mouse.NativeMouseEvent;
import com.github.kwhat.jnativehook.mouse.NativeMouseListener;
import org.clip.GUI.StateManager;


/**
 * 快捷键 全局监听 方便用户释放鼠标，使用键盘快速调用
 */
public class GlobalKeyListener implements NativeKeyListener, NativeMouseListener {
    private boolean CTRL = false;
    private final StringClip stringClip = new StringClip();
    private static GlobalKeyListener globalKeyListener = null;

    // 记录上一次用户交互的时间
    private volatile long lastUserInteractionTime = 0;

    @Override
    public void nativeMousePressed(NativeMouseEvent e) {
//        lastUserInteractionTime = System.currentTimeMillis();  // 鼠标按下时间
    }

    @Override
    public void nativeMouseReleased(NativeMouseEvent e) {
//        lastUserInteractionTime = System.currentTimeMillis();  // 鼠标释放时间
    }

    @Override
    public void nativeKeyPressed(NativeKeyEvent e) {
        int key = e.getKeyCode();
//        System.out.println(e.getKeyChar() + " : " + key);

        switch (key) {
            case NativeKeyEvent.VC_CONTROL:
                CTRL = true;
                lastUserInteractionTime = System.currentTimeMillis();
//                System.out.println("Ctrl Here!");
                break;
            case NativeKeyEvent.VC_C:
                // CTRL equals to one, indicating that last pressing is CTRL.
                if (CTRL) {
                    long now = System.currentTimeMillis();
                    long timeDiff = now - lastUserInteractionTime;
                    System.out.println("CTRL+C: " + timeDiff);
                    // 判断是否是“真实用户按下”或“划词触发”
                    // 词典的CTRL+C时间间距在1ms左右，而人类，比如我，至少需要36ms
                    if (timeDiff > 8) {
                        System.out.println("✅ 真实用户按下 Ctrl + C");
                        stringClip.excerpt();
                        // 执行你的复制逻辑
                    } else {
                        System.out.println("❌ 检测到划词翻译触发，忽略");
                    }
                }
                break;
            case NativeKeyEvent.VC_K:
                if (CTRL) {
                    // 当按下Ctrl + K时，QuickPanel存在时，即关闭这个Panel.
                    StateManager.switchQuickPanelState();
                }
                // 1TODO: Call on a panel for user to write down.
                break;
            case NativeKeyEvent.VC_M:
                if (CTRL) {
                    // 当按下Ctrl + M时，打开alert框供用户填写完成的事件内容
                    StateManager.switchEventMarker();
                }
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
            GlobalScreen.addNativeMouseListener(globalKeyListener);

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
            GlobalScreen.removeNativeMouseListener(globalKeyListener);
        }

    }
}