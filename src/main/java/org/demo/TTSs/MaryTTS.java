package org.demo.TTSs;

import marytts.LocalMaryInterface;
import marytts.MaryInterface;
import marytts.exceptions.MaryConfigurationException;
import marytts.exceptions.SynthesisException;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioFileFormat;
import java.io.File;
import java.io.IOException;
import java.util.Locale;

import marytts.modules.synthesis.Voice;
import marytts.util.data.audio.MaryAudioUtils;
public class MaryTTS {
    public static void main(String[] args) throws MaryConfigurationException, SynthesisException, IOException {
        MaryInterface marytts = new LocalMaryInterface();
        System.out.println(marytts.getAvailableVoices());

        // FIXME: marytts-lang-zh has existed before, but now was deleted.
        marytts.setVoice("cmu-slt-hsmm");

//        String text = "你好，欢迎使用";
        String text = "Welcome to use MarryTTS!";
        AudioInputStream audio = marytts.generateAudio(text);
        AudioSystem.write(audio, AudioFileFormat.Type.WAVE, new File("output.wav"));
//        MaryAudioUtils.playWavFile("output.wav", 5);
    }
}
