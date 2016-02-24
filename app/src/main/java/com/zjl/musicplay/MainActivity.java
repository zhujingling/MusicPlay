package com.zjl.musicplay;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;

import com.zjl.constant.CommonManage;
import com.zjl.entity.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/24.
 */
public class MainActivity extends FragmentActivity {
    private FragmentMain fragmentMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getMusicInfos(this);
        fragmentView();
    }


    /**
     * 点击了“聊天”按钮
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
