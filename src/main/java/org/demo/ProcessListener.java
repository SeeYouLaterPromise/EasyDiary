package org.demo;

import java.io.BufferedReader;
import java.io.InputStreamReader;

public class ProcessListener {
    public static void main(String[] args) {
        try {
            // 执行命令
            ProcessBuilder processBuilder = new ProcessBuilder("tasklist");
            Process process = processBuilder.start();

            // 读取输出
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null) {
                System.out.println(line);  // 打印进程列表
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
