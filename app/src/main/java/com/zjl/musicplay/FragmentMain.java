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
    private TextView song_count;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.fragment_main, null);
            initComponent(view);
            initSetViewOnClick();
            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private void initComponent(View view) {
       try{
           play_music = (ImageView) view.findViewById(R.id.play_music);
           tv_love = (TextView) view.findViewById(R.id.tv_love);
           jump_local_music = (RelativeLayout) view.findViewById(R.id.fragment_music);
           song_count = (TextView) view.findViewById(R.id.song_count);
           song_count.setText(Integer.toString(CommonManage.getCommoManage().musicList.size())+"首");
       }catch (Exception e){
           e.printStackTrace();
       }
    }

    private void initSetViewOnClick() {
        tv_love.setOnClickListener(new initEvents());
        jump_local_music.setOnClickListener(new initEvents());
        play_music.setOnClickListener(new initEvents());
    }

    private class initEvents implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.fragment_music:
                    fragmentMusicListView();
                    break;
                case R.id.play_music:
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
        intent.putExtra("url", CommonManage.getCommoManage().musicList.get(listPosition).getUrl());
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("action", Constant.PlayConstant.PLAY);
        intent.putExtra("singer", CommonManage.getCommoManage().musicList.get(listPosition).getArtist());
        intent.putExtra("song", CommonManage.getCommoManage().musicList.get(listPosition).getTitle());
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

