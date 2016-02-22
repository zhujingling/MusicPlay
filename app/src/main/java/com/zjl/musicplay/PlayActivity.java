package com.zjl.musicplay;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PersistableBundle;
import android.widget.ImageButton;

import com.zjl.entity.Music;
import com.zjl.entity.MusicState;

/**
 * Created by Administrator on 2016/2/22.
 */
public class PlayActivity extends Activity {
    private static ImageButton play;
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
                            play.setBackgroundResource(R.drawable.play_start);
                            break;
                        case 1:
                            play.setBackgroundResource(R.drawable.play_pause);
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
        play = (ImageButton) findViewById(R.id.play_pause);
    }



}
