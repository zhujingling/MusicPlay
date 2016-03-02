package com.zjl.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.support.annotation.Nullable;

import com.zjl.constant.CommonManage;
import com.zjl.constant.Constant;
import com.zjl.entity.Music;

import java.util.List;

/**
 * Created by Administrator on 2016/2/22.
 */
public class PlayService extends Service {
    private MediaPlayer mp;
    private List<Music> musicList;

    private String path;            // 音乐文件路径
    private boolean isPause;        // 暂停状态
    private int listMusicPosition = 0;        // 记录当前正在播放的音乐
    private int currentTime;        //当前播放进度
    private PlayServiceReceiver playServiceReceiver;  //自定义广播接收器
    private String singer;//歌手
    private String song;//歌名
    private int duration;           //音乐的长度
    private int status = 3;         //播放状态，默认为顺序播放
    private int action;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {
                currentTime = mp.getCurrentPosition();
                Intent intent = new Intent();
                intent.setAction(Constant.BrocastConstant.MUSIC_CURRENT);
                intent.putExtra("currentTime", currentTime);
                intent.putExtra("listPosition",listMusicPosition);
                intent.putExtra("isPause", isPause);
                intent.putExtra("seekBarProgress", currentTime * 100 / duration);
                intent.putExtra("singer", singer);
                intent.putExtra("song", song);
                sendBroadcast(intent);
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
                    listMusicPosition++;  //下一首位置
                    if (listMusicPosition <= musicList.size() - 1) {
                        Intent sendIntent = new Intent();
                        sendIntent.putExtra("current", currentTime);
                        sendBroadcast(sendIntent);
                        path = musicList.get(listMusicPosition).getUrl();
                        duration = (int) musicList.get(listMusicPosition).getDuration();
                        play(0);
                    } else {
                        mp.seekTo(0);
                        currentTime = 0;
                        Intent sendIntent = new Intent();
                        sendIntent.putExtra("current", currentTime);
                        // 发送广播，将被Activity组件中的BroadcastReceiver接收到
                        sendBroadcast(sendIntent);
                    }
                }
            }
        });


        playServiceReceiver = new PlayServiceReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BrocastConstant.MUSIC_CURRENT);
        registerReceiver(playServiceReceiver, filter);

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public boolean bindService(Intent service, ServiceConnection conn, int flags) {
        return super.bindService(service, conn, flags);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        path = intent.getStringExtra("url");        //歌曲路径
        listMusicPosition = intent.getIntExtra("listPosition", -1);   //当前播放歌曲的在MusicList的位置
        action = intent.getIntExtra("action", 0);         //播放信息
        singer = intent.getStringExtra("singer");
        song = intent.getStringExtra("song");
        duration = (int) intent.getLongExtra("duration", -1);
        if (action == Constant.PlayConstant.PLAY) {    //直接播放音乐
            play(0);
        } else if (action == Constant.PlayConstant.PLAY_PAUSE) {    //暂停
            pause();
        } else if (action == Constant.PlayConstant.PLAY_STOP) {     //停止
            stop();
        } else if (action == Constant.PlayConstant.PLAY_CONTINUE) { //继续播放
            resume();
        } else if (action == Constant.PlayConstant.PLAY_NEXT) {     //下一首
            nextMusic(path, 0, singer, song);
        } else if (action == Constant.PlayConstant.PLAY_PROGRESS) {  //进度更新
            currentTime = intent.getIntExtra("progress", -1);
            play(currentTime);
        } else if (action == Constant.PlayConstant.PLAY_NOW) {
            handler.sendEmptyMessage(1);
        }
        super.onStart(intent, startId);
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
    private void nextMusic(String path, int currentTime, String singer, String song) {
        Intent sendIntent = new Intent();
        sendIntent.putExtra("current", currentTime);
        sendIntent.putExtra("path", path);
        sendIntent.putExtra("singer", singer);
        sendIntent.putExtra("song", song);
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
//            Intent intent = new Intent();
//            duration = mediaPlayer.getDuration();
//            intent.putExtra("duration", duration);  //通过Intent来传递歌曲的总长度
//            sendBroadcast(intent);
        }
    }

    public class PlayServiceReceiver extends android.content.BroadcastReceiver {

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
