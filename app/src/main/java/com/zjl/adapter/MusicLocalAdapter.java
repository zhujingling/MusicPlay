package com.zjl.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SectionIndexer;
import android.widget.TextView;

import com.zjl.musicplay.R;

import java.util.ArrayList;

/**
 * Created by Administrator on 2016/2/19.
 */
public class MusicLocalAdapter extends ArrayAdapter implements SectionIndexer {

    public ArrayList<String> mObject;


    private View view = null;

    private TextView tViewSinger = null;//歌手
    private TextView tViewSong = null;//歌名

    private ArrayList<String> arrayList = new ArrayList<String>();

    private String[] english = {"A", "B", "C", "D", "E", "F", "G", "H", "I",
            "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V",
            "W", "X", "Y", "Z","#"};

    public MusicLocalAdapter(Context context, int textViewResourceId,
                             ArrayList<String> objects) {
        super(context, textViewResourceId, objects);
        // TODO Auto-generated constructor stub
        initArrayList();
        mObject = objects;
    }

    @Override
    /**
     * 添加item时判断，如果读取到的数据可以在arrayList中找到（即大写的单独字母），则添加为标题，否则是内容
     *
     */
    public View getView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub

        if (arrayList.contains(getItem(position))) {
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.music_list_item_tag, null);
        } else {
            view = LayoutInflater.from(getContext()).inflate(
                    R.layout.music_list_item, null);
        }

        tViewSinger = (TextView) view
                .findViewById(R.id.music_list_singer);
        tViewSinger.setText(getItem(position).toString());


//        tViewSong = (TextView) view
//                .findViewById(R.id.music_list_song);
//        tViewSong.setText(getItem(position).toString());

        return view;
    }

    /**
     * 根据字母导航所点击的字母，锁定名单中对应标题的位置
     *
     * @param str 所点击的字母
     * @return 返回字母在数据源中的位置。
     */
    public int checkSection(String str) {

        for (int i = 0; i < mObject.size(); i++) {
            if (mObject.get(i).equals(str))
                return i;
        }
        return -1;
    }

    /**
     * 讲26字母添加进list中，方便getView判断
     */
    public void initArrayList() {
        for (int i = 0; i < english.length; i++)
            arrayList.add(english[i]);
    }

    @Override
    public Object[] getSections() {
        return null;
    }

    @Override
    public int getPositionForSection(int sectionIndex) {
        for (int i = 0; i < getCount(); i++) {
            String sortStr = arrayList.get(i);
            char firstChar = sortStr.toUpperCase().charAt(0);
            if (firstChar == sectionIndex) {
                return i;
            }
        }
        return -1;
    }

    @Override
    public int getSectionForPosition(int position) {
        return arrayList.get(position).charAt(0);
    }

}
