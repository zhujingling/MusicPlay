package com.zjl.musicplay;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.ImageButton;
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
    private static ImageButton playBtn;//播放、暂停
    private int listPosition = 0;   //标识列表位置
    private List<Music> musicList = null;
    private BroadcastReceiver broadcastReceiver;
    private String url; // 歌曲路径
    private int currentTime;//当前时间
    private FragmentMain fragmentMain;

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
        playBtn = (ImageButton) findViewById(R.id.play_pause);
        broadcastReceiver = new BroadcastReceiver();
        musicList = CommonManage.getCommoManage().musicList;
        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BrocastConstant.UPDATE_ACTION);
        filter.addAction(Constant.BrocastConstant.MUSIC_CURRENT);
        filter.addAction(Constant.BrocastConstant.MUSIC_DURATION);
        registerReceiver(broadcastReceiver, filter);
    }

    //注册一些事件
    private void initEvents(){
        playBtn.setOnClickListener(new onClick());


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

    private class onClick implements View.OnClickListener {
        Intent intent;
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play_pause:
                    intent=new Intent(MainActivity.this, PlayService.class);
                    playOrPause(intent);//暂停播放
                    break;
                case R.id.paly_music:
                    playMusic(getRandomMusicPosition());
                    break;
            }
        }

        private void playOrPause(Intent intent) {
            if (CommonManage.getCommoManage().isPlaying) {
                playBtn.setBackgroundResource(R.drawable.play_pause);
                intent.setAction(Constant.BrocastConstant.MUSIC_SERVICE);//在服务里面注册这个监听器，然后就发到服务里面去了
                intent.putExtra("action", Constant.PlayConstant.PLAY_PAUSE);
                startService(intent);
                CommonManage.getCommoManage().isPlaying = false;
                CommonManage.getCommoManage().isPause = true;
            } else if (CommonManage.getCommoManage().isPause) {
                playBtn.setBackgroundResource(R.drawable.playing);
                intent.setAction(Constant.BrocastConstant.MUSIC_SERVICE);
                intent.putExtra("action", Constant.PlayConstant.PLAY_CONTINUE);
                startService(intent);
                CommonManage.getCommoManage().isPause = false;
                CommonManage.getCommoManage().isPlaying = true;
            }
        }
    }


    public void playMusic(int listPosition) {
        Intent intent = new Intent();
        intent.setAction(Constant.BrocastConstant.MUSIC_SERVICE);
        intent.putExtra("url", musicList.get(listPosition).getUrl());
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("action", Constant.PlayConstant.PLAY);
        startService(intent);
        CommonManage.getCommoManage().isPlaying=true;
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
     *
     * @author wwj
     */
    public class BroadcastReceiver extends android.content.BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.BrocastConstant.MUSIC_CURRENT.equals(action)) {
                currentTime = intent.getIntExtra("currentTime", -1);
                playBtn.setBackgroundResource(R.drawable.playing);

            } else if (Constant.BrocastConstant.MUSIC_DURATION.equals(action)) {
                int duration = intent.getIntExtra("duration", -1);

            } else if (Constant.BrocastConstant.UPDATE_ACTION.equals(action)) {
                //获取Intent中的current消息，current代表当前正在播放的歌曲
                listPosition = intent.getIntExtra("current", -1);
                url = musicList.get(listPosition).getUrl();
                if (listPosition >= 0) {

                }
                if (listPosition == 0) {
                    playBtn.setBackgroundResource(R.drawable.play_pause);
                    CommonManage.getCommoManage().isPause = true;
                }
            }
        }

    }

    //生成一个随机数
    private int getRandomMusicPosition(){
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
