package com.zjl.musicplay;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.zjl.constant.CommonManage;
import com.zjl.constant.Constant;
import com.zjl.entity.Music;
import com.zjl.fragmentchange.ISwitchFragment;
import com.zjl.service.PlayService;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Created by Administrator on 2016/2/24.
 */
public class MainActivity extends FragmentActivity implements ISwitchFragment {
    private Fragment fragment;
    private FragmentMain fragmentMain;
    private FragmentMusic fragmentMusic;
    private ImageButton playOrPause;//播放、暂停
    private ImageButton playNext;//下一首
    private int listPosition = 0;   //标识列表位置
    private List<Music> musicList = null;
    private MainActivityReceiver mainActivityReceiver;

    private TextView singer;
    private TextView songName;
    private SeekBar seekBar;


    private String strSinger;//参数歌手
    private String strSongName;
    private int seekBarProgress;
    private int action;
    private long duration;


    private Handler m_handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1) {

            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_main);
            getMusicInfos(this);
            initComponent();
            initEvents();
            fragmentView();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void initComponent() {
        playOrPause = (ImageButton) findViewById(R.id.play_pause);
        playNext = (ImageButton) findViewById(R.id.play_next);
        singer = (TextView) findViewById(R.id.singer);
        songName = (TextView) findViewById(R.id.song_name);

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
        if (fragmentMain == null) {
            fragmentMain = FragmentMain.newInstance();
        }
        // 得到Fragment事务管理器
        FragmentTransaction fragmentTransaction = this
                .getSupportFragmentManager().beginTransaction();
        // 替换当前的页面
        fragmentTransaction.add(R.id.frame_content, fragmentMain);
        // 事务管理提交
        fragmentTransaction.commit();
    }

    @Override
    public void switchFragment(String tag) {
        try {
            if (tag.equals("tofragmentmusic")) {
                fragmentMusic = FragmentMusic.newInstance();
                // 得到Fragment事务管理器
                android.support.v4.app.FragmentTransaction fragmentTransaction = this
                        .getSupportFragmentManager().beginTransaction();
                // 替换当前的页面
                fragmentTransaction.setCustomAnimations(
                        R.anim.push_left_in,
                        R.anim.push_left_out);
                fragmentTransaction.add(R.id.frame_content, fragmentMusic);
                fragmentTransaction.addToBackStack(null);
                // 事务管理提交
                fragmentTransaction.commit();
            } else if (tag.equals("tofragmentmain")) {

                fragmentMain = FragmentMain.newInstance();
                // 得到Fragment事务管理器
                android.support.v4.app.FragmentTransaction fragmentTransaction = this
                        .getSupportFragmentManager().beginTransaction();
                // 替换当前的页面
                fragmentTransaction.setCustomAnimations(
                        R.anim.push_right_in,
                        R.anim.push_right_out);
                fragmentTransaction.add(R.id.frame_content, fragmentMain);
                // 事务管理提交
                fragmentTransaction.commit();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
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
                setIntentExtra(intent, Constant.PlayConstant.PLAY_PAUSE, listPosition);
                startService(intent);
                CommonManage.getCommoManage().isPlaying = false;
                CommonManage.getCommoManage().isPause = true;
            } else if (CommonManage.getCommoManage().isPause) {
                playOrPause.setBackgroundResource(R.drawable.playing);
                setIntentExtra(intent, Constant.PlayConstant.PLAY_CONTINUE, listPosition);
                startService(intent);
                CommonManage.getCommoManage().isPause = false;
                CommonManage.getCommoManage().isPlaying = true;
            }
        }
    }


    public void playMusic(int listPosition) {
        Intent intent = new Intent(MainActivity.this, PlayService.class);
        setIntentExtra(intent, Constant.PlayConstant.PLAY, listPosition);
        startService(intent);
        CommonManage.getCommoManage().isPlaying = true;
    }

    /**
     * 下一首歌曲
     */
    public void nextMusic() {
        listPosition = listPosition + 1;
        if (listPosition <= musicList.size() - 1) {
            Intent intent = new Intent(MainActivity.this, PlayService.class);
            setIntentExtra(intent, Constant.PlayConstant.PLAY_NEXT, listPosition);
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
                    setPlayOrPauseBackgroundResource(intent.getBooleanExtra("isPause", false));
                    getIntentExtra(intent);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }


    private Intent setIntentExtra(Intent intent, int action, int listPosition) {
        intent.putExtra("action", action);
        intent.putExtra("url", musicList.get(listPosition).getUrl());
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("singer", musicList.get(listPosition).getArtist());
        intent.putExtra("songName", musicList.get(listPosition).getTitle());
        intent.putExtra("duration", musicList.get(listPosition).getDuration());
        return intent;
    }


    private Intent getIntentExtra(Intent intent) {
        listPosition = intent.getIntExtra("listPosition", -1);   //当前播放歌曲的在MusicList的位置
        action = intent.getIntExtra("action", 0);         //播放信息
        strSinger = intent.getStringExtra("singer");
        strSongName = intent.getStringExtra("songName");
        seekBarProgress = intent.getIntExtra("seekBarProgress", 0);
        duration = intent.getLongExtra("duration", -1);

        singer.setText(strSinger);
        songName.setText(strSongName);
        seekBar.setProgress(seekBarProgress);

        return intent;
    }

    private void setPlayOrPauseBackgroundResource(boolean isPause) {
        if (isPause) {
            playOrPause.setBackgroundResource(R.drawable.play_pause);
        } else {
            playOrPause.setBackgroundResource(R.drawable.playing);
        }
    }

    //生成一个随机数
    private int getRandomMusicPosition() {
        return new Random().nextInt(musicList.size());
    }


    @Override
    protected void onPause() {
        super.onPause();
        overridePendingTransition(R.anim.push_right_in,
                R.anim.push_right_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (fragment instanceof FragmentMain) {
            FragmentMain.onKeyDown(keyCode, event);
        }
        return super.onKeyDown(keyCode, event);
    }

    private void existApplication() {

        AlertDialog alertDialog = new AlertDialog.Builder(this).setTitle("提醒")
                .setMessage("是否退出程序")
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.addCategory(Intent.CATEGORY_HOME);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        android.os.Process.killProcess(android.os.Process.myPid());
                    }

                }).setNegativeButton("取消",

                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                return;
                            }
                        }).create(); // 创建对话框
        alertDialog.show(); // 显示对话框
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
