package org.demo;

import org.clip.TxtFileManager;

import java.io.*;
import java.net.*;
import javax.sound.sampled.*;

public class JavaToPythonTTS {
    public static void main(String[] args) {
        try {
//            String text = "Hello, canro. nice to meet you! Can we make a friend?";
            String text = "你好，卢志强。今天过的还好吗？祝你拥有幸福的晚年！";

            TxtFileManager txtFileManager = new TxtFileManager("novel.txt");
            text = txtFileManager.readFileContent();

            // URL 编码
            String encodedText = URLEncoder.encode(text, "UTF-8");

            // 构建 URL
            URL url = new URL("http://localhost:8000/synthesize?text=" + encodedText);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            // 使用 BufferedInputStream 包装输入流，避免 mark/reset 错误
            try (InputStream inputStream = connection.getInputStream();
                 BufferedInputStream bufferedInputStream = new BufferedInputStream(inputStream)) {

                // 获取并播放音频
                try (AudioInputStream audioStream = AudioSystem.getAudioInputStream(bufferedInputStream)) {
                    Clip clip = AudioSystem.getClip();
                    clip.open(audioStream);
                    clip.start();
                    while (!clip.isRunning()) {
                        Thread.sleep(10);
                    }
                    while (clip.isRunning()) {
                        Thread.sleep(10);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
