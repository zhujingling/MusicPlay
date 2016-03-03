package com.zjl.musicplay;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjl.constant.CommonManage;
import com.zjl.constant.Constant;
import com.zjl.fragmentchange.ISwitchFragment;
import com.zjl.service.PlayService;

import java.util.Random;

/**
 * Created by Administrator on 2016/2/24.
 */
public class FragmentMain extends Fragment {

    ISwitchFragment switchFragment;
    private FragmentMusic fragmentMusic;
    private TextView tv_love;
    private RelativeLayout tomusiclist;
    private ImageView play_music;
    private TextView song_count;

    public static FragmentMain newInstance() {
        FragmentMain fragment = new FragmentMain();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
//            View view = inflater.inflate(R.layout.fragment_main, null);
            View view = null;
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_main, container, false);
            }
            ViewGroup parent = (ViewGroup) view.getParent();
            if (parent != null) {
                parent.removeView(view);
            }
            initComponent(view);
            initSetViewOnClick();
            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initComponent(View view) {
        try {
            play_music = (ImageView) view.findViewById(R.id.play_music);
            tv_love = (TextView) view.findViewById(R.id.mine_love);
            tomusiclist = (RelativeLayout) view.findViewById(R.id.to_fragment_music);
            song_count = (TextView) view.findViewById(R.id.song_count);
            song_count.setText(Integer.toString(CommonManage.getCommoManage().musicList.size()) + "首");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initSetViewOnClick() {
        tv_love.setOnClickListener(new initEvents());
        tomusiclist.setOnClickListener(new initEvents());
        play_music.setOnClickListener(new initEvents());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

    }

    private class initEvents implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.play_music:
                    playMusic(getRandomMusicPosition());
                    break;
                case R.id.to_fragment_music:
                    fragmentMusic();
                    break;
            }
        }
    }


    //生成一个随机数
    private int getRandomMusicPosition() {
        return new Random().nextInt(CommonManage.getCommoManage().musicList.size());
    }


    public void playMusic(int listPosition) {
        Intent intent = new Intent(getActivity(), PlayService.class);
        setIntentExtra(intent, Constant.PlayConstant.PLAY, listPosition);
        getActivity().startService(intent);
        CommonManage.getCommoManage().isPlaying = true;
    }


    private Intent setIntentExtra(Intent intent, int action, int listPosition) {
        intent.putExtra("action", action);
        intent.putExtra("url", CommonManage.getCommoManage().musicList.get(listPosition).getUrl());
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("singer", CommonManage.getCommoManage().musicList.get(listPosition).getArtist());
        intent.putExtra("songName", CommonManage.getCommoManage().musicList.get(listPosition).getTitle());
        intent.putExtra("duration", CommonManage.getCommoManage().musicList.get(listPosition).getDuration());
        return intent;
    }

    /**
     */

    private void fragmentMusic() {
        switchFragment=(ISwitchFragment)getActivity();
        switchFragment.switchFragment("tofragmentmusic");
    }


    public static boolean onKeyDown(int keyCode, KeyEvent event) {
        // TODO Auto-generated method stub
        return true;
    }
}

