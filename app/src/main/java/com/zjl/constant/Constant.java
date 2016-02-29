package com.zjl.constant;

/**
 * Created by Administrator on 2016/2/26.
 */
public class Constant {

    public final static class PlayConstant {
        // 暂停
        public final static int PLAY_PAUSE = 0x00A40001;
        // 停止
        public final static int PLAY_STOP = 0x00A40002;
        // 继续
        public final static int PLAY_CONTINUE = 0x00A40003;
        // 上一首
        public final static int PLAY_PRIVIOUS = 0x00A40004;
        // 下一首
        public final static int PLAY_NEXT = 0x00A40005;
        // 正在播放
        public final static int PLAY_NOW = 0x00A40006;
        // 进度条
        public final static int PLAY_PROGRESS = 0x00A40007;
        // 播放
        public final static int PLAY = 0x00A40008;


    }


    public final static class BrocastConstant {
        public static final String MUSIC_CURRENT = "com.zjl.action.music.current";  //音乐当前时间改变动作
    }
}
