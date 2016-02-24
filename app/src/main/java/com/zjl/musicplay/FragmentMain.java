package com.zjl.musicplay;


import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Created by Administrator on 2016/2/24.
 */
public class FragmentMain extends Fragment {

    private FragmentMusicList fragmentMusicList;
    private TextView tv_love;
    private RelativeLayout jump_local_music;


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
        tv_love = (TextView) view.findViewById(R.id.tv_love);
        jump_local_music = (RelativeLayout) view.findViewById(R.id.jump_local_music);
    }

    private void initSetViewOnClick() {
        tv_love.setOnClickListener(new ViewOnClick());
        jump_local_music.setOnClickListener(new ViewOnClick());
    }

    private class ViewOnClick implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.jump_local_music:
                    fragmentMusicListView();
                    break;
            }
        }
    }


    /**
     */
    private void fragmentMusicListView() {
        // 实例化Fragment页面
        fragmentMusicList = new FragmentMusicList();
        // 得到Fragment事务管理器
        android.support.v4.app.FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        // 替换当前的页面
        fragmentTransaction.setCustomAnimations(
                R.anim.push_left_in,
                R.anim.push_left_out);
        fragmentTransaction.replace(R.id.frame_content, fragmentMusicList);
        fragmentTransaction.addToBackStack(null);
        // 事务管理提交
        fragmentTransaction.commit();

    }

}

