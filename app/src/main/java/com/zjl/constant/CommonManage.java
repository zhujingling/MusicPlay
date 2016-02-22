package com.zjl.constant;

import com.zjl.entity.Music;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/2/22.
 */
public class CommonManage {
    private static CommonManage commonManage;
    public static CommonManage getCommoManage(){
        if (commonManage==null){
            synchronized(CommonManage.class){
                if (commonManage==null){
                    commonManage=new CommonManage();
                }
            }
        }
        return commonManage;
    }

    public List<Music> musicList=new ArrayList<Music>();
}
