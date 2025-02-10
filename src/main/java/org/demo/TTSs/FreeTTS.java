package org.demo.TTSs;

import com.sun.speech.freetts.*;

public class FreeTTS {

    public static void main(String[] args) {
//        StackOverFlow: https://stackoverflow.com/questions/44514247/text-to-speech-conversion-for-web-application
        System.setProperty("freetts.voices", "com.sun.speech.freetts.en.us.cmu_us_kal.KevinVoiceDirectory");
        String voiceName = "kevin16";
        // 创建一个语音合成器（Voice）
        VoiceManager voiceManager = VoiceManager.getInstance();
        Voice voice = voiceManager.getVoice("kevin16");  // 选择一个声音，kevin16 是一个默认的声音

        Voice[] voices = voiceManager.getVoices();

        System.out.println("Available voices:");
        for (Voice v : voices) {
            System.out.println(v.getName());
        }


        if (voice != null) {
            voice.allocate();  // 初始化声音

//            String text = "你好";
            // 文本内容
            String text = "Hello, welcome to Java Text to Speech conversion! My name is kevin16, Nice to meet you!";

            try {
                // 将文本转化为语音并播放
                voice.speak(text);  // 合成并播放文本
            } catch (Exception e) {
                e.printStackTrace();
            }

            voice.deallocate();  // 释放资源
        } else {
            System.out.println("Voice not available.");
        }
    }
}

