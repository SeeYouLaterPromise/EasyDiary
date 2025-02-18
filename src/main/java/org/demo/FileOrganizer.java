package org.demo;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class FileOrganizer {

    public static void organize(String directoryPath) {
        File directory = new File(directoryPath);

        // 获取目录中的所有文件
        File[] files = directory.listFiles();

        if (files != null) {
            for (File file : files) {
                if (file.isFile()) {
                    // 提取文件名中的日期部分（假设文件名格式为 "type_day.txt"）
                    String fileName = file.getName();
                    String[] parts = fileName.split("_");
                    if (parts.length > 1) {
                        String day = parts[1].split("\\.")[0]; // 获取 day 部分（不包含文件后缀）
                        String type = parts[0];  // 获取 type 部分

                        // 创建新的目录
                        File newDirectory = new File(directoryPath + File.separator + day);
                        if (!newDirectory.exists()) {
                            newDirectory.mkdirs();  // 如果目录不存在，则创建
                        }

                        // 创建新的文件路径
                        File newFile = new File(newDirectory, type + ".txt");

                        // 移动并重命名文件
                        try {
                            Files.move(file.toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
                            System.out.println("Moved: " + fileName + " -> " + newFile.getPath());
                        } catch (IOException e) {
                            System.err.println("Failed to move file: " + fileName);
                            e.printStackTrace();
                        }
                    }
                }
            }
        }
    }
    public static void main(String[] args) {
        String base_dir = "src/main/resources/txtData/2025/";  // 请修改为你的文件夹路径
        for (int i = 4; i <= 11; i++) {
            String directoryPath = base_dir + String.valueOf(i);
            organize(directoryPath);
        }
    }
}

