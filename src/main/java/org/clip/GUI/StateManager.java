package org.clip.GUI;

import javafx.application.Platform;
import org.clip.GlobalKeyListener;

public class StateManager {
    public static boolean quickNoteOpen = false;

    public static boolean learningModeOn = false;

    public static boolean listenerOn = false;

    /**
     * toggle the two states
     */
    public static void switchLearningMode() {
        // 现在状态是：On，switch做的事情就是off
        if (learningModeOn) {
            Platform.runLater(LearningMode::closePanel);
        } else {
            Platform.runLater(() -> LearningMode.setupLearningMode(MainPanel.getTodayLabel()));
        }
        learningModeOn = !learningModeOn;

        TrayMenu.setLearningModeDisplayState(learningModeOn);

        LearningMode.setListenerReminderText(listenerOn);
    }

    public static void switchQuickPanelState() {
        if (quickNoteOpen) {
            Platform.runLater(QuickNote::closePanel);
        } else {
            Platform.runLater(QuickNote::getQuickNote);
        }
        quickNoteOpen = !quickNoteOpen;

        TrayMenu.setGetQuickPanelDisplayState(quickNoteOpen);

    }

    public static void moveQuickPanel(int keycode) {
        if (quickNoteOpen) {
            Platform.runLater(() -> QuickNote.move(keycode));
        }
    }

    public static void submitQuickPanel() {
        if (quickNoteOpen) QuickNote.submit();
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
