package com.zjl.entity;

/**
 * Created by Administrator on 2016/3/3.
 */
public class BroadcastParams {
    private String singer;//参数歌手
    private String songName;
    private int seekBarProgress;
    private int action;
    private int currentTime;


    public String getSinger() {
        return singer;
    }

    public void setSinger(String singer) {
        this.singer = singer;
    }


    public String getSongName() {
        return songName;
    }

    public void setSongName(String songName) {
        this.songName = songName;
    }

    public int getSeekBarProgress() {
        return seekBarProgress;
    }

    public void setSeekBarProgress(int seekBarProgress) {
        this.seekBarProgress = seekBarProgress;
    }

    public int getAction() {
        return action;
    }

    public void setAction(int action) {
        this.action = action;
    }


    public int getCurrentTime() {
        return currentTime;
    }

    public void setCurrentTime(int currentTime) {
        this.currentTime = currentTime;
    }
}
