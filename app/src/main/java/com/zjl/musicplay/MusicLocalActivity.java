package com.zjl.musicplay;

import android.app.Activity;
import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zjl.adapter.MusicLocalAdapter;
import com.zjl.component.LettersSideBarView;
import com.zjl.util.Sort;

import java.util.ArrayList;

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
}
