package org.clip;

import java.io.*;
import java.time.LocalDate;

public class TxtFileManager {
    private String StoreDir = "src/main/resources/";
    private String tempPath = "temp.txt";
    private String filePath;

    private String paddingZeroAhead(int num) {
        return num > 10 ? String.valueOf(num) : "0" + num;
    }

    // TODO: 考虑定时任务解决跨越0：00的问题，但是初期我们先记下来，暂先不实现。
    private void getDate() {
        LocalDate currentDate = LocalDate.now();
        int year = currentDate.getYear();
        int month = currentDate.getMonthValue();
        int day = currentDate.getDayOfMonth();
//        System.out.println("Year: " + year);
//        System.out.println("Month: " + month);
//        System.out.println("Day: " + day);
        // create the aimed directory hierarchy
        StoreDir = StoreDir + year + '/' + month + '/';
        tempPath = paddingZeroAhead(month) + paddingZeroAhead(day) + ".txt";
    }

    private void ensureDirectory() {
        File directory = new File(StoreDir);
        if (!directory.exists()) {
            boolean res = directory.mkdirs();
            if (!res) {
                System.err.println("Cant ensure directory: " + StoreDir);
            }
        }
    }

    private void ensureFile() {
        File file = new File(filePath);
        try {
            if (!file.exists()) {
                boolean res = file.createNewFile();
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    private void ensure() {
        ensureDirectory();
        ensureFile();
    }

    TxtFileManager(String secondPath) {
        tempPath = secondPath;
        filePath = StoreDir + tempPath;
        ensure();
    }
    TxtFileManager(String directory, String secondPath) {
        StoreDir = directory;
        tempPath = secondPath;
        filePath = StoreDir + tempPath;
        ensure();
    }
    TxtFileManager () {
        getDate();
        filePath = StoreDir + tempPath;
        ensure();
    }

    public void WriteToFile (String entry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            writer.write(entry);
            writer.newLine();
            System.out.println("Entry written to file successfully.");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void WriteToFile (String[] records) {
        // 使用 BufferedWriter 写入文本到文件
        // 覆盖模式：new FileWriter(filePath) 默认会覆盖文件内容。
        // 追加模式：new FileWriter(filePath, true) 通过传递 true 启用追加模式，写入的新内容将追加到文件的末尾。
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath, true))) {
            for (String entry : records) {
                writer.write(entry); // 写入内容
                writer.newLine(); // 添加换行
            }
            System.out.println("Records written to file successfully.");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public String[] ReadFromFile () {
        StringBuilder content = new StringBuilder();
        // 使用 BufferedReader 读取文件内容
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath))) {
            String line;
            while ((line = reader.readLine()) != null) {  // 按行读取
                System.out.println(line);  // 输出每一行的内容
                content.append(line).append('#');
            }
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
        System.out.println("Text read from file successfully.");
        return content.toString().split("#");
    }

    public void setStoreDir(String Dir) {
        StoreDir = Dir;
        filePath = StoreDir + tempPath;
    }

    public String getStoreDir() {
        return StoreDir;
    }

    public String getTempPath() {
        return tempPath;
    }

    public void setTempPath(String tempPath) {
        tempPath = tempPath;
        filePath = StoreDir + tempPath;
    }

    public static void main(String[] args) {
        new TxtFileManager().getDate();
//        String[] lines = new TxtFileManager("nihao.txt").ReadFromFile();
    }
}
