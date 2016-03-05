package com.zjl.musicplay;


import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
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
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.zjl.adapter.ListViewSortAdapater;
import com.zjl.component.LettersSideBarView;
import com.zjl.constant.CommonManage;
import com.zjl.constant.Constant;
import com.zjl.entity.Music;
import com.zjl.entity.SortEntity;
import com.zjl.fragmentchange.ISwitchFragment;
import com.zjl.service.PlayService;
import com.zjl.util.CharacterParser;
import com.zjl.util.PinYinComparator;
import com.zjl.util.Sort;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Administrator on 2016/2/24.
 */
public class FragmentMusic extends Fragment implements LettersSideBarView.OnTouchingLetterChangedListener {

    private ISwitchFragment switchFragment;
    private FragmentMain fragmentMain;
    private Sort mSort;
    private ArrayList<String> musicListViewTitle;//音乐列表的标题
    private List<Music> listMusicInfos;
    private ListView musicListView;
    private LettersSideBarView lettersSideBarView;
    private TextView mTagIcon;
    private Toolbar toolbar;

    private String[] oldData;

    private int listPosition;

    private int currentTime;
    private String url;
    private FragmentMusicReceiver fragmentMusicReceiver;

    private PinYinComparator pinyinComparator;
    private CharacterParser characterParser;
    private List<SortEntity> sortEntityList; // 数据
    private ListViewSortAdapater listViewSortAdapater;

    public static FragmentMusic newInstance() {
        FragmentMusic fragment = new FragmentMusic();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        try {
//            View view = inflater.inflate(R.layout.fragment_music, null);
            View view = null;
            if (view == null) {
                view = inflater.inflate(R.layout.fragment_music, container, false);
            }
            initComponent(view);
            initAdapter();

            initSetViewOnClick();

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
        if (listViewSortAdapater != null) {
            position = listViewSortAdapater.checkSection(s);
        }
        if (position != -1) {
            musicListView.setSelection(position);
        }
    }

    private void initComponent(View view) {
        characterParser = new CharacterParser();
        pinyinComparator = new PinYinComparator();
        musicListView = (ListView) view.findViewById(R.id.music_list);
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

        listMusicInfos = CommonManage.getCommoManage().musicList;
        fragmentMusicReceiver = new FragmentMusicReceiver();

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constant.BrocastConstant.MUSIC_CURRENT);
        getActivity().registerReceiver(fragmentMusicReceiver, filter);
    }

    private void initAdapter() {
        setOldData(CommonManage.getCommoManage().musicList);
        sortEntityList = filledData(oldData);// 填充数据
        Collections.sort(sortEntityList, pinyinComparator);
        listViewSortAdapater = new ListViewSortAdapater(getActivity(), sortEntityList);
        musicListView.setAdapter(listViewSortAdapater);
    }


    private void initSetViewOnClick() {
        musicListView.setOnTouchListener(new initEvents());
        musicListView.setOnItemClickListener(new initEvents());
    }


    private void setOldData(List<Music> musicList) {
        oldData = new String[musicList.size()];
        for (int i = 0; i < musicList.size(); i++) {
            oldData[i] = musicList.get(i).getArtist() + " - " + musicList.get(i).getTitle();
        }
    }


    /**
     * 填充数据
     *
     * @param date
     * @return
     */
    private List<SortEntity> filledData(String[] date) {
        List<SortEntity> mSortList = new ArrayList<SortEntity>();
        for (int i = 0; i < date.length; i++) {
            SortEntity sortEntity = new SortEntity();
            sortEntity.setName(date[i]);
            String pinyin = characterParser.getSelling(date[i]);
            String sortString = pinyin.substring(0, 1).toUpperCase();
            if (sortString.matches("[A-Z]")) {
                sortEntity.setSortLetters(sortString.toUpperCase());
            } else {
                sortEntity.setSortLetters("#");
            }

            mSortList.add(sortEntity);
        }
        return mSortList;

    }

    /**
     */
    private void fragmentMainView() {
        switchFragment = (ISwitchFragment) getActivity();
        switchFragment.switchFragment("tofragmentmain");
    }

    private class initEvents implements View.OnClickListener, View.OnTouchListener, Toolbar.OnMenuItemClickListener, ListView.OnItemClickListener {

        @Override
        public void onClick(View v) {

        }

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            boolean flag = false;
            switch (v.getId()) {
                case R.id.music_list:
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

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            int realPosition = 0;
            String tag = ((SortEntity) listViewSortAdapater.getItem(position)).getName();
            Toast.makeText(getActivity(), tag, Toast.LENGTH_SHORT).show();
            for (int i = 0; i < CommonManage.getCommoManage().musicList.size(); i++) {
                String strCompara = CommonManage.getCommoManage().musicList.get(i).getArtist() + " - " + CommonManage.getCommoManage().musicList.get(i).getTitle();
                if (tag.equals(strCompara)) {
                    realPosition = i;
                }
            }
            listPosition = realPosition;
            play();
        }
    }

    public void play() {
        Intent intent = new Intent(getActivity(), PlayService.class);
        setIntentExtra(intent, Constant.PlayConstant.PLAY, listPosition);
        getActivity().startService(intent);
    }


    private Intent setIntentExtra(Intent intent, int action, int listPosition) {
        intent.putExtra("action", action);
        intent.putExtra("url", listMusicInfos.get(listPosition).getUrl());
        intent.putExtra("listPosition", listPosition);
        intent.putExtra("singer", listMusicInfos.get(listPosition).getArtist());
        intent.putExtra("songName", listMusicInfos.get(listPosition).getTitle());
        intent.putExtra("duration", listMusicInfos.get(listPosition).getDuration());
        return intent;
    }

    @Override
    public void onDestroy() {
        getActivity().unregisterReceiver(fragmentMusicReceiver);
        super.onDestroy();
    }

    //用来接收Server发回来的广播
    public class FragmentMusicReceiver extends android.content.BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (Constant.BrocastConstant.MUSIC_CURRENT.equals(action)) {
                currentTime = intent.getIntExtra("currentTime", -1);
            }
        }
    }

}
