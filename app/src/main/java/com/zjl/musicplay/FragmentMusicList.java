package com.zjl.musicplay;


import android.content.Intent;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
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
import com.zjl.constant.CommonManage;
import com.zjl.entity.Music;
import com.zjl.util.Sort;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/24.
 */
public class FragmentMusicList extends Fragment implements LettersSideBarView.OnTouchingLetterChangedListener {

    private FragmentMain fragmentMain;
    private MusicLocalAdapter musicLocalAdapter;
    private Sort mSort;
    private ArrayList<String> musicArrayList;
    private ListView localMusicList;
    private LettersSideBarView lettersSideBarView;
    private TextView mTagIcon;
    private Toolbar toolbar;

    private String[] oldData;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
            View view = inflater.inflate(R.layout.music_local, null);
            initComponent(view);
            initAdapter();

            localMusicList.setOnTouchListener(new initEvents());
            lettersSideBarView.setTextView(mTagIcon);
            lettersSideBarView.setOnTouchingLetterChangedListener(this);


            toolbar.setNavigationIcon(R.drawable.back_main);//设置导航栏图标
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentMainView();
                }
            });

            toolbar.inflateMenu(R.menu.local_music_toolbar_menu);//设置右上角的填充菜单
            toolbar.setOnMenuItemClickListener(new initEvents());
            return view;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
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

    private void initComponent(View view) {
        localMusicList = (ListView) view.findViewById(R.id.local_music_list);
        lettersSideBarView = (LettersSideBarView) view.findViewById(R.id.letter_sidebar);
        mTagIcon = (TextView) this.getActivity().getLayoutInflater().inflate(R.layout.music_tag_icon, null);

        toolbar = (Toolbar) view.findViewById(R.id.toolbar);


        getActivity().getWindowManager()
                .addView(
                        mTagIcon,
                        new WindowManager.LayoutParams(
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT,
                                WindowManager.LayoutParams.TYPE_APPLICATION,
                                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                                        | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                                PixelFormat.TRANSLUCENT));
    }

    private void initAdapter() {
        mSort = new Sort();
        setOldData(CommonManage.getCommoManage().musicList);
        musicArrayList = mSort.addChar(mSort.autoSort(oldData));
        musicLocalAdapter = new MusicLocalAdapter(getActivity(), android.R.layout.simple_list_item_1, musicArrayList);
        localMusicList.setAdapter(musicLocalAdapter);
    }

    private void initSetViewOnClick() {

    }


    private void setOldData(List<Music> musicList) {
        oldData = new String[musicList.size()];
        for (int i = 0; i < musicList.size(); i++) {
            oldData[i] = musicList.get(i).getArtist() + " - " + musicList.get(i).getTitle();
        }
    }

    /**
     */
    private void fragmentMainView() {
        // 实例化Fragment页面
        fragmentMain = new FragmentMain();
        // 得到Fragment事务管理器
        FragmentTransaction fragmentTransaction = getActivity()
                .getSupportFragmentManager().beginTransaction();
        // 替换当前的页面
        fragmentTransaction.setCustomAnimations(
                R.anim.push_right_in,
                R.anim.push_right_out);

        fragmentTransaction.replace(R.id.frame_content, fragmentMain);
        fragmentTransaction.addToBackStack(null);

        // 事务管理提交
        fragmentTransaction.commit();

    }

    private class initEvents implements View.OnClickListener, View.OnTouchListener,Toolbar.OnMenuItemClickListener {

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean flag = false;
            switch (v.getId()) {
                case R.id.local_music_list:
                    flag = false;
                    break;
            }
            return flag;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            int menuItemId = item.getItemId();
            if (menuItemId == R.id.action_search) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.local_music_search, Toast.LENGTH_SHORT).show();

            } else if (menuItemId == R.id.action_item1) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.local_music_sort_singer, Toast.LENGTH_SHORT).show();

            } else if (menuItemId == R.id.action_item2) {
                Toast.makeText(getActivity().getApplicationContext(), R.string.local_music_sort_time, Toast.LENGTH_SHORT).show();
            }
            return true;
        }
    }
}
