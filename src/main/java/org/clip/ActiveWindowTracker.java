package org.clip;

import com.sun.jna.Library;
import com.sun.jna.Native;
import com.sun.jna.Pointer;
import com.sun.jna.ptr.IntByReference;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class ActiveWindowTracker {
    public interface User32 extends Library {
        User32 INSTANCE = Native.load("user32", User32.class);

        // 获取当前活动窗口的句柄，返回 Pointer 类型
        Pointer GetForegroundWindow();

        // 获取窗口文本（Unicode版本）
        int GetWindowTextW(Pointer hWnd, char[] lpString, int nMaxCount);

        // 获取当前活动窗口的进程ID
        void GetWindowThreadProcessId(Pointer hWnd, IntByReference lpdwProcessId);

        // 根据进程ID获取该进程的名称
        static String getProcessName(int pid) {
            try {
                // 执行 tasklist 命令
                ProcessBuilder processBuilder = new ProcessBuilder("tasklist", "/FI", "PID eq " + pid);
                Process process = processBuilder.start();

                // 读取命令的输出
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.contains(String.valueOf(pid))) {
                        // 解析进程名称
                        String[] tokens = line.trim().split("\\s+");
                        return tokens[0];  // 进程名称通常是输出的第一列
                    }
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
                e.printStackTrace();
            }
            return null;  // 如果找不到
        }
    }

    // 用于存储进程ID和累计时间
    // 读取之前已经存储的记录：我们只是追踪用户在使用learningMode时，对于laptop的使用情况。
    private static Map<String, Long> appUsageTime = new HashMap<>();

    private static void initialAppUsageTime(TxtFileManager txtFileManager) {

        String content = txtFileManager.readFileContent();
        String[] contentArray = content.split("\n");
        // from i == 1 to filter the header.
        for (int i = 1; i < contentArray.length; i++) {
            String[] entryArray = contentArray[i].split("\t");
            long seconds = DurationManager.getSeconds(entryArray[1]);
            appUsageTime.put(entryArray[0], seconds);
        }
    }

    private static long startTime = System.currentTimeMillis();
    private static boolean running = true;
    private static boolean loseFocusAlready = false;

    private static String formattedEntry(String content) {
        return "-> [" + content + "] (" + TxtFileManager.getCurrentTimeString("HH:mm:ss") + ")";
    }

    public static void setupTracker() {
        setUp();
//        System.out.println("Setup successfully.");
        // reduce the times of new
        LocalDate localDate = LocalDate.now();
        TxtFileManager AppUsageTimeFileManager = new TxtFileManager(localDate, "AppUsageTime");
        initialAppUsageTime(AppUsageTimeFileManager);

        TxtFileManager AppFocusChainFileManager = new TxtFileManager(localDate, "AppFocusChain");
        String lastActiveWindowTitle = null;

        // 每秒钟检查一次当前活动窗口
        while (running) {
            // 获取当前活动窗口
            Pointer hwnd = User32.INSTANCE.GetForegroundWindow();
            if (hwnd == null) {
//                System.err.println("没有找到活动窗口");
                if (!loseFocusAlready) {
                    // 避免无意义的记录太多遍的没有找到活动窗口
                    loseFocusAlready = true;
                    AppFocusChainFileManager.WriteToFile(formattedEntry("没有找到活动窗口"), true);
                }
                continue;
            }
            loseFocusAlready = false;

            char[] windowText = new char[512]; // 用于存储窗口标题（Unicode版本）
            int length = User32.INSTANCE.GetWindowTextW(hwnd, windowText, windowText.length);

            if (length > 0) {
                String activeWindowTitle = new String(windowText, 0, length);

                if (!activeWindowTitle.equals(lastActiveWindowTitle)) {
                    lastActiveWindowTitle = activeWindowTitle;
                    AppFocusChainFileManager.WriteToFile(formattedEntry(lastActiveWindowTitle), true);
                }

                // 获取当前活动窗口的进程ID
                IntByReference pid = new IntByReference();
                User32.INSTANCE.GetWindowThreadProcessId(hwnd, pid);

                int processId = pid.getValue();

                String processName = User32.getProcessName(processId);

                // 更新进程的使用时间
                updateAppUsageTime(processName);
            } else {
                AppFocusChainFileManager.WriteToFile(formattedEntry("活动窗口无标题"), true);
            }

            try {
                // 每秒检查一次
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }
//        System.out.println("Shut down successfully.");
        // for storage.
        AppUsageTimeToFile(AppUsageTimeFileManager);
    }

    public static void shutDown() {
        running = false;
    }

    public static void setUp() {
        running = true;
    }

    // 更新进程的使用时间
    private static void updateAppUsageTime(String key) {
        long currentTime = System.currentTimeMillis();
        long elapsedTime = (currentTime - startTime) / 1000; // 秒数

        // 如果该进程已经存在，则累加时间
        if (appUsageTime.containsKey(key)) {
            long currentAppTime = appUsageTime.get(key);
            appUsageTime.put(key, currentAppTime + elapsedTime);
        } else {
            appUsageTime.put(key, elapsedTime);
        }

        // 更新起始时间
        startTime = currentTime;
    }

    /**
     * Storage the usageTime into txt file for storage.
     */
    private static void AppUsageTimeToFile(TxtFileManager txtFileManager) {
        txtFileManager.WriteToFile("AppName\tUsageTime", false);
        for (Map.Entry<String, Long> entry : appUsageTime.entrySet()) {
            String time = DurationManager.SecondsToString(entry.getValue());
            txtFileManager.WriteToFile(entry.getKey() + "\t" + time, true);
        }
    }


}
