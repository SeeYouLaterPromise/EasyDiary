package org.demo;

import java.io.IOException;
import java.nio.file.*;
import java.nio.file.attribute.BasicFileAttributes;

public class BatchRenameFolders {

    public static void main(String[] args) {
        Path targetDirectory = Paths.get("F:\\yexin\\java\\DemoTool\\src\\main\\resources\\txtData\\2025\\2"); // 指定目标目录路径
        System.out.println("Hello!");
        try {
            Files.walkFileTree(targetDirectory, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
//                    System.out.println("visitFile Function.");
                    // 只修改文件
                    if (Files.isRegularFile(file)) {
                        String oldFolderName = file.getFileName().toString();
                        String newFolderName = "Content_" + oldFolderName; // 示例：在文件夹名称前加上 "New_"
                        Path newFolderPath = file.getParent().resolve(newFolderName);

                        // 重命名文件
                        Files.move(file, newFolderPath);

                        System.out.println("Renamed folder: " + oldFolderName + " -> " + newFolderName);
                    }
                    return FileVisitResult.CONTINUE;
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

