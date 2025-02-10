package org.demo;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileReaderExample {

    public static void main(String[] args) {
        // 获取 resources 文件夹下的 txt 文件
        InputStream inputStream = FileReaderExample.class.getResourceAsStream("/api_key.txt");

        if (inputStream != null) {
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);  // 输出文件内容
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            System.out.println("File not found in resources!");
        }
    }
}
