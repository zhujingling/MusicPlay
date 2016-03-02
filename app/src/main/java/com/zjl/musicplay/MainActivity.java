package com.zjl.musicplay;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zjl.constant.CommonManage;
import com.zjl.constant.Constant;
import com.zjl.entity.Music;
import com.zjl.service.PlayService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/2/24.
 */
public class MainActivity extends FragmentActivity {
    private ImageButton playOrPause;//播放、暂停
    private ImageButton playNext;//下一首
    private int listPosition = 0;   //标识列表位置
    private List<Music> musicList = null;
    private MainActivityReceiver mainActivityReceiver;
    private FragmentMain fragmentMain;

    private TextView tv_singer;
    private TextView tv_song;
    private SeekBar seekBar;
    private int seekBarProgress;//当前时间

    private String strSinger;
    private String strSong;
    private long duration;
    private int currentTime;

    private Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getMusicInfos(this);
        initComponent();
        initEvents();
        fragmentView();
    }


    private void initComponent() {
        playOrPause = (ImageButton) findViewById(R.id.play_pause);
        playNext = (ImageButton) findViewById(R.id.play_next);
        tv_singer = (TextView) findViewById(R.id.bottom_singer);
        tv_song = (TextView) findViewById(R.id.bottom_song_name);

        seekBar = (SeekBar) findViewById(R.id.seek_bar);
        mainActivityReceiver = new MainActivityReceiver();
        musicList = CommonManage.getCommoManage().musicList;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BrocastConstant.MUSIC_CURRENT);
        registerReceiver(mainActivityReceiver, filter);
    }

    //注册一些事件
    private void initEvents() {
        playOrPause.setOnClickListener(new initEvents());
        playNext.setOnClickListener(new initEvents());
    }

    /**
     */
    private void fragmentView() {
        // 实例化Fragment页面
        fragmentMain = new FragmentMain();
        // 得到Fragment事务管理器
        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();
        // 替换当前的页面
        fragmentTransaction.replace(R.id.frame_content, fragmentMain);
        // 事务管理提交
        fragmentTransaction.commit();
    }

    private class initEvents implements View.OnClickListener {
        Intent intent;

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play_pause:
                    intent = new Intent(MainActivity.this, PlayService.class);
                    playOrPause(intent);//暂停播放
                    break;
                case R.id.play_music:
                    playMusic(getRandomMusicPosition());
                    break;
                case R.id.play_next:
                    nextMusic();
                    break;
            }
        }

        private void playOrPause(Intent intent) {
            if (CommonManage.getCommoManage().isPlaying) {
                playOrPause.setBackgroundResource(R.drawable.play_pause);
                intent.putExtra("action", Constant.PlayConstant.PLAY_PAUSE);
                intent.putExtra("url", musicList.get(listPosition).getUrl());
                intent.putExtra("listPosition", listPosition);
                intent.putExtra("singer", strSinger);
                intent.putExtra("song", strSong);
                intent.putExtra("duration", musicList.get(listPosition).getDuration());
                startService(intent);
                CommonManage.getCommoManage().isPlaying = false;
                CommonManage.getCommoManage().isPause = true;
            } else if (CommonManage.getCommoManage().isPause) {
                playOrPause.setBackgroundResource(R.drawable.playing);
                intent.putExtra("action", Constant.PlayConstant.PLAY_CONTINUE);
                intent.putExtra("url", musicList.get(listPosition).getUrl());
                intent.putExtra("listPosition", listPosition);
                intent.putExtra("singer", strSinger);
                intent.putExtra("song", strSong);
                intent.putExtra("duration", musicList.get(listPosition).getDuration());
                startService(intent);
                CommonManage.getCommoManage().isPause = false;
                CommonManage.getCommoManage().isPlaying = true;
            }
        }
    }


    public void playMusic(int listPosition) {
        Intent intent = new Intent(MainActivity.this, PlayService.class);
        intent.putExtra("url", musicList.get(listPosition).getUrl());
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("action", Constant.PlayConstant.PLAY);
        intent.putExtra("singer", musicList.get(listPosition).getArtist());
        intent.putExtra("song", musicList.get(listPosition).getTitle());
        startService(intent);
        CommonManage.getCommoManage().isPlaying = true;
    }

    /**
     * 下一首歌曲
     */
    public void nextMusic() {
        listPosition = listPosition + 1;
        if (listPosition <= musicList.size() - 1) {
            Music music = musicList.get(listPosition);
            Intent intent = new Intent(MainActivity.this, PlayService.class);
            intent.putExtra("action", Constant.PlayConstant.PLAY_NEXT);
            intent.putExtra("listPosition", listPosition);
            intent.putExtra("url", music.getUrl());
            intent.putExtra("singer", musicList.get(listPosition).getArtist());
            intent.putExtra("song", musicList.get(listPosition).getTitle());
            intent.putExtra("duration", musicList.get(listPosition).getDuration());
            startService(intent);
        } else {
            Toast.makeText(this, "没有下一首了", Toast.LENGTH_SHORT).show();
        }
    }


    /**
     * 用来接收从service传回来的广播的内部类
     *
     * @author wwj
     */
    public class MainActivityReceiver extends android.content.BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.BrocastConstant.MUSIC_CURRENT.equals(action)) {
                try {
                    currentTime = intent.getIntExtra("currentTime", -1);
                    seekBarProgress = intent.getIntExtra("seekBarProgress", 0);
                    if (intent.getBooleanExtra("isPause", false)) {
                        playOrPause.setBackgroundResource(R.drawable.play_pause);
                    } else {
                        playOrPause.setBackgroundResource(R.drawable.playing);
                    }
                    strSong = intent.getStringExtra("song");
                    strSinger = intent.getStringExtra("singer");
                    listPosition = intent.getIntExtra("listPosition", 0);
                    tv_song.setText(strSong);
                    tv_singer.setText(strSinger);

                    seekBar.setProgress(seekBarProgress);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

    //生成一个随机数
    private int getRandomMusicPosition() {
        return new Random().nextInt(musicList.size());
    }

    private void getMusicInfos(Context context) {
        Cursor cursor = context.getContentResolver().query(

                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI, null, null, null,

                MediaStore.Audio.Media.DEFAULT_SORT_ORDER);

        List<Music> musicsInfos = new ArrayList<Music>();

        for (int i = 0; i < cursor.getCount(); i++) {

            Music musicInfo = new Music();

            cursor.moveToNext();

            long id = cursor.getLong(cursor

                    .getColumnIndex(MediaStore.Audio.Media._ID)); // 音乐id

            String title = cursor.getString((cursor

                    .getColumnIndex(MediaStore.Audio.Media.TITLE)));// 音乐标题

            String artist = cursor.getString(cursor

                    .getColumnIndex(MediaStore.Audio.Media.ARTIST));// 艺术家

            long duration = cursor.getLong(cursor

                    .getColumnIndex(MediaStore.Audio.Media.DURATION));// 时长

            long size = cursor.getLong(cursor

                    .getColumnIndex(MediaStore.Audio.Media.SIZE)); // 文件大小

            String url = cursor.getString(cursor

                    .getColumnIndex(MediaStore.Audio.Media.DATA)); // 文件路径

            int isMusic = cursor.getInt(cursor

                    .getColumnIndex(MediaStore.Audio.Media.IS_MUSIC));// 是否为音乐

            if (isMusic != 0) { // 只把音乐添加到集合当中

                musicInfo.setId(id);

                musicInfo.setTitle(title);

                musicInfo.setArtist(artist);

                musicInfo.setDuration(duration);

                musicInfo.setSize(size);

                musicInfo.setUrl(url);

                musicsInfos.add(musicInfo);

            }

        }
        CommonManage.getCommoManage().musicList = musicsInfos;
    }
}
