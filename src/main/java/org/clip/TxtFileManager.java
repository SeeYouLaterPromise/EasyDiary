package org.clip;

import java.io.*;

public class TxtFileManager {
    private String StoreDir = "src/main/resources/";

    private String tempPath = "temp.txt";
    private String filePath = StoreDir + tempPath;

    TxtFileManager(String secondPath) {
        tempPath = secondPath;
        filePath = StoreDir + tempPath;
    }
    TxtFileManager(String directory, String secondPath) {
        StoreDir = directory;
        tempPath = secondPath;
        filePath = StoreDir + tempPath;
    }

    public void WriteToFile (String entry) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
            writer.write(entry);
            writer.newLine();
            System.out.println("Entry written to file successfully.");
        } catch (IOException e) {
            System.err.println(e.getMessage());
        }
    }

    public void WriteToFile (String[] records) {
        // 使用 BufferedWriter 写入文本到文件
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filePath))) {
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
        String[] lines = new TxtFileManager("nihao.txt").ReadFromFile();
    }
}
