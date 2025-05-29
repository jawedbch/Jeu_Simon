package com.example.jeudusimon;

import android.content.Context;
import android.media.AudioManager;
import android.media.SoundPool;

import java.util.HashMap;

public class SoundManager {
    private SoundPool soundPool;
    private HashMap<Integer, Integer> soundPoolMap;
    private AudioManager audioManager;
    private Context context;

    // Initialisation du gestionnaire de sons
    public void initSounds(Context context) {
        this.context = context;
        soundPool = new SoundPool(4, AudioManager.STREAM_MUSIC, 0);
        soundPoolMap = new HashMap<>();
        audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
    }

    // Ajout d'un son à la playlist
    public void ajouterSon(int index, int soundId) {
        soundPoolMap.put(index, soundPool.load(context, soundId, 1));
    }

    // Jouer un son une seule fois
    public void jouerSon(int index) {
        float streamVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        streamVolume = streamVolume / audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        soundPool.play(soundPoolMap.get(index), streamVolume, streamVolume, 1, 0, 1f);
    }
}
