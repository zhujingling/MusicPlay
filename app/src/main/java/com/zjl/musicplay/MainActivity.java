package com.zjl.musicplay;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.zjl.constant.CommonManage;
import com.zjl.entity.Music;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private TextView tv_love;
    private RelativeLayout jump_local_music;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tv_love = (TextView) findViewById(R.id.tv_love);
        tv_love.setOnClickListener(this);

        jump_local_music = (RelativeLayout) findViewById(R.id.jump_local_music);
        jump_local_music.setOnClickListener(this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_love:
                Toast.makeText(getApplicationContext(), "我喜欢", Toast.LENGTH_LONG).show();
                break;
            case R.id.jump_local_music:
                Toast.makeText(getApplicationContext(), "跳到本地音乐列表", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(this, MusicLocalActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.scale_in, R.anim.scale_out);
                break;

        }
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
        CommonManage.getCommoManage().musicList = musicsInfos;
        return musicsInfos;
    }
}
