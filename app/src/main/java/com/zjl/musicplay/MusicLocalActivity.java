package com.zjl.musicplay;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zjl.adapter.MusicLocalAdapter;
import com.zjl.component.LettersSideBarView;
import com.zjl.entity.Music;
import com.zjl.util.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/19.
 */
public class MusicLocalActivity extends Activity implements LettersSideBarView.OnTouchingLetterChangedListener, View.OnClickListener {

    private MusicLocalAdapter musicLocalAdapter;
    private Sort mSort;
    private ArrayList<String> musicArrayList;
    private ListView localMusicList;
    private LettersSideBarView lettersSideBarView;
    private TextView mTagIcon;

    private String[] oldData = {"阿小毛驴", "逼小毛驴", "擦小毛驴", "傻吊", "高富帅", "百富美",
            "屌丝", "王老气", "鬼脚七", "马云", "李彦宏", "习近平", "朱镕基", "李克强",
            "孙悟空", "如来", "玉皇大帝", "唐僧", "猪八戒", "及时雨", "李白", "白居易",
            "王宝强", "周星驰", "刘备", "曹操", "孙权", "袁术", "取尔首级", "探囊取物",
            "降龙十八掌", "六脉神剑", "小相公", "秦始皇", "汉武帝", "汉高祖", "唐太宗", "don't",
            "say", "anything", "just", "kiss", "me", "you", "me",
            "hello", "java", "C#", "python", "ObjectC", "谷歌", "亚马逊",
            "NBA", "James", "kobe", "韦德", "#123", "123"};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.music_local);


            localMusicList = (ListView) findViewById(R.id.local_music_list);
            lettersSideBarView = (LettersSideBarView) findViewById(R.id.letter_sidebar);
            mTagIcon = (TextView) this.getLayoutInflater().inflate(R.layout.music_tag_icon, null);

            mSort = new Sort();
            setOldData(getMusicInfos(this));
            musicArrayList = mSort.addChar(mSort.autoSort(oldData));
            musicLocalAdapter = new MusicLocalAdapter(this, android.R.layout.simple_list_item_1, musicArrayList);
            localMusicList.setAdapter(musicLocalAdapter);


            this.getWindowManager()
                    .addView(
                            mTagIcon,
                            new WindowManager.LayoutParams(
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT,
                                    WindowManager.LayoutParams.TYPE_APPLICATION,
                                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                            | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                    PixelFormat.TRANSLUCENT));

            localMusicList.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {

                    return false;
                }
            });

            lettersSideBarView.setTextView(mTagIcon);
            lettersSideBarView.setOnTouchingLetterChangedListener(this);


            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);

            toolbar.setNavigationIcon(R.drawable.back_main);//设置导航栏图标
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                }
            });
//            toolbar.setLogo(R.mipmap.ic_launcher);//设置app logo
//            toolbar.setTitle(R.string.local_music_title);//设置主标题
//            toolbar.setSubtitle(R.string.local_music_title);//设置子标题

            toolbar.inflateMenu(R.menu.local_music_toolbar_menu);//设置右上角的填充菜单
            toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {

                    int menuItemId = item.getItemId();
                    if (menuItemId == R.id.action_search) {
                        Toast.makeText(getApplicationContext(), R.string.local_music_search, Toast.LENGTH_SHORT).show();

                    } else if (menuItemId == R.id.action_item1) {
                        Toast.makeText(getApplicationContext(), R.string.local_music_sort_singer, Toast.LENGTH_SHORT).show();

                    } else if (menuItemId == R.id.action_item2) {
                        Toast.makeText(getApplicationContext(), R.string.local_music_sort_time, Toast.LENGTH_SHORT).show();

                    }
                    return true;
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onTouchingLetterChanged(String s) {
        int position = 0;
        // 该字母首次出现的位置
        if (musicLocalAdapter != null) {
            position = musicLocalAdapter.checkSection(s);
        }
        if (position != -1) {
            localMusicList.setSelection(position);
        }
    }

    @Override
    public void onClick(View v) {

    }


    private List<Music> getMusicInfos(Context context) {
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

        return musicsInfos;
    }

    private void setOldData(List<Music> musicList) {
        oldData = new String[musicList.size()];
        for (int i = 0; i < musicList.size(); i++) {
            oldData[i] = musicList.get(i).getArtist() + " - " + musicList.get(i).getTitle();
        }
    }
}
