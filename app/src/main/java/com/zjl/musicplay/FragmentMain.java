package com.zjl.musicplay;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.zjl.constant.CommonManage;
import com.zjl.constant.Constant;
import com.zjl.service.PlayService;

import java.util.Random;

/**
 * Created by Administrator on 2016/2/24.
 */
public class FragmentMain extends Fragment {

    private FragmentMusic fragmentMusic;
    private TextView tv_love;
    private RelativeLayout jump_local_music;
    private ImageView play_music;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.music_main, null);
            initComponent(view);
            initSetViewOnClick();
            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initComponent(View view) {
        play_music = (ImageView) view.findViewById(R.id.paly_music);
        tv_love = (TextView) view.findViewById(R.id.tv_love);
        jump_local_music = (RelativeLayout) view.findViewById(R.id.jump_local_music);
    }

    private void initSetViewOnClick() {
        tv_love.setOnClickListener(new ViewOnClick());
        jump_local_music.setOnClickListener(new ViewOnClick());
        play_music.setOnClickListener(new ViewOnClick());
    }

    private class ViewOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.jump_local_music:
                    fragmentMusicListView();
                    break;
                case R.id.paly_music:
                    playMusic(getRandomMusicPosition());
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
        intent.setAction(Constant.BrocastConstant.MUSIC_SERVICE);
        intent.putExtra("url", CommonManage.getCommoManage().musicList.get(listPosition).getUrl());
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("action", Constant.PlayConstant.PLAY);
        getActivity().startService(intent);
        CommonManage.getCommoManage().isPlaying = true;
    }

    /**
     */
    private void fragmentMusicListView() {
        // 实例化Fragment页面
        fragmentMusic = new FragmentMusic();
        // 得到Fragment事务管理器
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        // 替换当前的页面
        fragmentTransaction.setCustomAnimations(
                R.anim.push_left_in,
                R.anim.push_left_out);
        fragmentTransaction.replace(R.id.frame_content, fragmentMusic);
        fragmentTransaction.addToBackStack(null);
        // 事务管理提交
        fragmentTransaction.commit();

    }

}

