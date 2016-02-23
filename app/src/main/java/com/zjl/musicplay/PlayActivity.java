package com.zjl.musicplay;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.zjl.constant.CommonManage;
import com.zjl.entity.Music;
import com.zjl.entity.MusicState;

import java.util.List;

/**
 * Created by Administrator on 2016/2/22.
 */
public class PlayActivity extends Activity {
    private static ImageButton playBtn;//播放、暂停
    private ListView musicListView;
    private int listPosition = 0;   //标识列表位置
    private List<Music> musicList = null;
    private boolean isPlaying;//正在播放
    private boolean isPause;//暂停
    private PlayReceiver playReceiver;


    private int currentTime;//当前时间

    private long id; // 歌曲ID

    private String title; // 歌曲名称

    private String album; // 专辑

    private long albumId;// 专辑ID

    private String displayName; // 显示名称

    private String artist; // 歌手名称

    private long duration; // 歌曲时长

    private long size; // 歌曲大小

    private String url; // 歌曲路径

    private String lrcTitle; // 歌词名称

    private String lrcSize; // 歌词大小

    public static final String UPDATE_ACTION = "com.wwj.action.UPDATE_ACTION";  //更新动作
    public static final String CTL_ACTION = "com.wwj.action.CTL_ACTION";        //控制动作
    public static final String MUSIC_CURRENT = "com.wwj.action.MUSIC_CURRENT";  //音乐当前时间改变动作
    public static final String MUSIC_DURATION = "com.wwj.action.MUSIC_DURATION";//音乐播放长度改变动作
    public static final String MUSIC_PLAYING = "com.wwj.action.MUSIC_PLAYING";  //音乐正在播放动作
    public static final String REPEAT_ACTION = "com.wwj.action.REPEAT_ACTION";  //音乐重复播放动作
    public static final String SHUFFLE_ACTION = "com.wwj.action.SHUFFLE_ACTION";//音乐随机播放动作


    public static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case 1:
                    int c = msg.arg2;
                    int s = msg.arg1;
                    Music music = MusicState.list.get(c);
                    switch (s) {
                        case 0:
                            playBtn.setBackgroundResource(R.drawable.play_start);
                            break;
                        case 1:
                            playBtn.setBackgroundResource(R.drawable.play_pause);
                            break;
                    }
                    break;
            }
        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState, PersistableBundle persistentState) {
        super.onCreate(savedInstanceState, persistentState);
        setContentView(R.layout.foot_bottom);

        initFindViewById();
        intSetViewOnclickListener();


    }

    private void initFindViewById() {
        playBtn = (ImageButton) findViewById(R.id.play_pause);
        musicListView = (ListView) findViewById(R.id.local_music_list);
        playReceiver=new PlayReceiver();

        musicList= CommonManage.getCommoManage().musicList;

        IntentFilter filter = new IntentFilter();
        filter.addAction(UPDATE_ACTION);
        filter.addAction(MUSIC_CURRENT);
        filter.addAction(MUSIC_DURATION);
        registerReceiver(playReceiver, filter);
    }

    private void intSetViewOnclickListener() {
        playBtn.setOnClickListener(new onClick());
        musicListView.setOnItemClickListener(new MusicListItemClickListener());
    }

    private class onClick implements View.OnClickListener{
        Intent intent = new Intent();
        @Override
        public void onClick(View v) {
            switch (v.getId()){
                case R.id.play_pause:
                    playPause(intent);//暂停播放
                    break;
            }
        }

        private void playPause(Intent intent){
            if (isPlaying) {
                playBtn.setBackgroundResource(R.drawable.play_pause);
                intent.setAction("com.wwj.media.MUSIC_SERVICE");
                startService(intent);
                isPlaying = false;
                isPause = true;

            } else if (isPause) {
                playBtn.setBackgroundResource(R.drawable.play);
                intent.setAction("com.wwj.media.MUSIC_SERVICE");
                startService(intent);
                isPause = false;
                isPlaying = true;
            }
        }
    }

    private class MusicListItemClickListener implements AdapterView.OnItemClickListener {
        /**
         * 点击列表播放音乐
         */
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            listPosition = position;
            playMusic(listPosition);
        }
    }

    public void playMusic(int listPosition) {
        Intent intent = new Intent();
        intent.setAction("com.wwj.media.MUSIC_SERVICE");
        intent.putExtra("url", url);
        intent.putExtra("listPosition", listPosition);
        startService(intent);
    }

    /**
     * 下一首歌曲
     */
    public void nextMusic() {
        listPosition = listPosition + 1;
        if (listPosition <= musicList.size() - 1) {
            Music music = musicList.get(listPosition);

            Intent intent = new Intent();
            intent.setAction("com.wwj.media.MUSIC_SERVICE");
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", music.getUrl());
            startService(intent);
        } else {
            Toast.makeText(this, "没有下一首了", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 用来接收从service传回来的广播的内部类
     * @author wwj
     *
     */
    public class PlayReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(action.equals(MUSIC_CURRENT)) {
                currentTime = intent.getIntExtra("currentTime", -1);

            } else if(action.equals(MUSIC_DURATION)) {
                int duration = intent.getIntExtra("duration", -1);

            } else if(action.equals(UPDATE_ACTION)) {
                //获取Intent中的current消息，current代表当前正在播放的歌曲
                listPosition = intent.getIntExtra("current", -1);
                url = musicList.get(listPosition).getUrl();
                if(listPosition >= 0) {

                }
                if(listPosition == 0) {
                    playBtn.setBackgroundResource(R.drawable.play_pause);
                    isPause = true;
                }
            }
        }

    }


}
