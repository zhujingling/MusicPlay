package com.zjl.service;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zjl.entity.Music;

import java.util.List;

/**
 * Created by Administrator on 2016/2/22.
 */
public class PlayService extends Service {
    private MediaPlayer mp;
    private List<Music> musicList;
    //当前播放歌曲的索引
    private int currentListItem = 0;

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    //播放音乐
    void playMusic(String path) {
        try {
            mp.reset();
            mp.setDataSource(path);
            mp.prepare();
            mp.start();
            mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    nextMusic();
                }
            });
        } catch (Exception e) {
            // TODO: handle exception
            e.printStackTrace();
        }
    }

    //下一首
    void nextMusic() {
        if (++currentListItem >= musicList.size()) {
            currentListItem = 0;
        } else {
            playMusic(musicList.get(currentListItem).getUrl());
        }
    }
}
