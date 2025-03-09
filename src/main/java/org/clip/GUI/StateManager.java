package org.clip.GUI;

import javafx.application.Platform;
import org.clip.GlobalKeyListener;

public class StateManager {
    public static boolean quickNoteOpen = false;

    public static boolean learningModeOn = false;

    public static boolean listenerOn = false;

    public static ManualEventMarker eventMarker = ManualEventMarker.getInstance();
    public static boolean alertOn = false;

    public static void switchEventMarker() {
        if (!alertOn) {
            Platform.runLater(()->{
                eventMarker.getAlertBox();
                // for immediate writing
                eventMarker.getFocus();
            });
        }else {
            Platform.runLater(()->eventMarker.close());
        }
        alertOn = !alertOn;
    }

    /**
     * toggle the two states
     */
    public static void switchLearningMode() {
        // 现在状态是：On，switch做的事情就是off
        if (learningModeOn) {
            Platform.runLater(() -> {
                LearningMode.closePanel();
                // 退出学习模式后，调出先前隐藏的学习面板
                MainPanel.show();
            });
        } else {
            Platform.runLater(() -> {
                // 打开学习模式后，隐藏MainPanel
                MainPanel.hide();
                LearningMode.setupLearningMode(MainPanel.getTodayLabel());
            });
        }
        learningModeOn = !learningModeOn;

        TrayMenu.setLearningModeDisplayState(learningModeOn);

        LearningMode.setListenerReminderText(listenerOn);
    }

    public static void switchQuickPanelState() {
        if (quickNoteOpen) {
            Platform.runLater(QuickNote::closePanel);
        } else {
            Platform.runLater(() -> {
                QuickNote.getQuickNote();
                // Once opening the quickNotePanel, users can write immediately.
                QuickNote.getFocus();
            });
        }
        quickNoteOpen = !quickNoteOpen;

        TrayMenu.setGetQuickPanelDisplayState(quickNoteOpen);

    }

    public static void moveQuickPanel(int keycode) {
        if (quickNoteOpen) {
            Platform.runLater(() -> QuickNote.move(keycode));
        }
    }

    public static void switchGlobalListener() {
        if (listenerOn) {
            GlobalKeyListener.shutDownMode();
        } else {
            GlobalKeyListener.startUpMode();
        }

        listenerOn = !listenerOn;

        // SystemTrayMenu Display
        TrayMenu.setGlobalListenerDisplayState(listenerOn);
        // LearningMode Display
        // you should set a precondition, if LearningMode on, you switch the display; if not, hold!
        if (learningModeOn) {
            LearningMode.setListenerReminderText(listenerOn);
        }
    }

}
