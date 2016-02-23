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
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;

import com.zjl.constant.CommonManage;
import com.zjl.entity.Music;
import com.zjl.musicplay.PlayActivity;

import java.util.List;

/**
 * Created by Administrator on 2016/2/22.
 */
public class PlayService extends Service {
    private MediaPlayer mp;
    private List<Music> musicList;
    //当前播放歌曲的索引
    private int currentListItem = 0;

    private String path;            // 音乐文件路径
    private boolean isPause;        // 暂停状态
    private int current = 0;        // 记录当前正在播放的音乐
    private MyReceiver myReceiver;  //自定义广播接收器
    private int currentTime;        //当前播放进度
    private int duration;           //播放长度
    private int status = 3;         //播放状态，默认为顺序播放

    //服务要发送的一些Action
    public static final String UPDATE_ACTION = "com.wwj.action.UPDATE_ACTION";  //更新动作
    public static final String CTL_ACTION = "com.wwj.action.CTL_ACTION";        //控制动作
    public static final String MUSIC_CURRENT = "com.wwj.action.MUSIC_CURRENT";  //当前音乐播放时间更新动作
    public static final String MUSIC_DURATION = "com.wwj.action.MUSIC_DURATION";//新音乐长度更新动作

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                currentTime = mp.getCurrentPosition();
                Intent intent = new Intent();
                intent.setAction(MUSIC_CURRENT);
                intent.putExtra("currentTime", currentTime);
                sendBroadcast(intent); // 给PlayerActivity发送广播
                handler.sendEmptyMessageDelayed(1, 1000);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        mp = new MediaPlayer();
        musicList = CommonManage.getCommoManage().musicList;
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                if (status == 3) { // 顺序播放
                    current++;  //下一首位置
                    if (current <= musicList.size() - 1) {
                        Intent sendIntent = new Intent(UPDATE_ACTION);
                        sendIntent.putExtra("current", current);
                        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                        sendBroadcast(sendIntent);
                        path = musicList.get(current).getUrl();
                        play(0);
                    } else {
                        mp.seekTo(0);
                        current = 0;
                        Intent sendIntent = new Intent(UPDATE_ACTION);
                        sendIntent.putExtra("current", current);
                        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                        sendBroadcast(sendIntent);
                    }
                }
            }
        });


        myReceiver = new MyReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(PlayActivity.CTL_ACTION);
        registerReceiver(myReceiver, filter);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

// 播放音乐

    private void play(int currentTime) {
        try {
            mp.reset();// 把各项参数恢复到初始状态
            mp.setDataSource(path);
            mp.prepare(); // 进行缓冲
            mp.setOnPreparedListener(new PreparedListener(currentTime));// 注册一个监听器
            handler.sendEmptyMessage(1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /**
     * 暂停音乐
     */
    private void pause() {
        if (mp != null && mp.isPlaying()) {
            mp.pause();
            isPause = true;
        }
    }

    private void resume() {
        if (isPause) {
            mp.start();
            isPause = false;
        }
    }

    /**
     * 下一首
     */
    private void nextMusic() {
        Intent sendIntent = new Intent(UPDATE_ACTION);
        sendIntent.putExtra("current", current);
        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
        sendBroadcast(sendIntent);
        play(0);
    }


    /**
     * 停止音乐
     */
    private void stop() {
        if (mp != null) {
            mp.stop();
            try {
                mp.prepare(); // 在调用stop后如果需要再次通过start进行播放,需要之前调用prepare函数
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mp != null) {
            mp.stop();
            mp.release();
            mp = null;
        }
    }

    /**
     * 实现一个OnPrepareLister接口,当音乐准备好的时候开始播放
     */
    private final class PreparedListener implements MediaPlayer.OnPreparedListener {
        private int currentTime;

        public PreparedListener(int currentTime) {
            this.currentTime = currentTime;
        }

        @Override
        public void onPrepared(MediaPlayer mediaPlayer) {
            mp.start(); // 开始播放
            if (currentTime > 0) { // 如果音乐不是从头播放
                mp.seekTo(currentTime);
            }
            Intent intent = new Intent();
            intent.setAction(MUSIC_DURATION);
            duration = mediaPlayer.getDuration();
            intent.putExtra("duration", duration);  //通过Intent来传递歌曲的总长度
            sendBroadcast(intent);
        }
    }

    public class MyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            int control = intent.getIntExtra("control", -1);
            switch (control) {
                case 1:
                    status = 1; // 将播放状态置为1表示：单曲循环
                    break;
                case 2:
                    status = 2; //将播放状态置为2表示：全部循环
                    break;
                case 3:
                    status = 3; //将播放状态置为3表示：顺序播放
                    break;
                case 4:
                    status = 4; //将播放状态置为4表示：随机播放
                    break;
            }
        }
    }

}
