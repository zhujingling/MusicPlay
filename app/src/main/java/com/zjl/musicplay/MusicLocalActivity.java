package com.zjl.musicplay;

import android.app.Activity;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ListView;
import android.widget.TextView;

import com.zjl.adapter.MusicLocalAdapter;
import com.zjl.component.LettersSideBarView;
import com.zjl.util.Sort;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/19.
 */
public class MusicLocalActivity extends Activity implements LettersSideBarView.OnTouchingLetterChangedListener {

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
}
