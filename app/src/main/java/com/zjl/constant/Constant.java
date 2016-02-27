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
        public static final String UPDATE_ACTION = "com.zjl.action.UPDATE_ACTION";  //更新动作
        public static final String CTL_ACTION = "com.zjl.action.CTL_ACTION";        //控制动作
        public static final String MUSIC_CURRENT = "com.zjl.action.MUSIC_CURRENT";  //音乐当前时间改变动作
        public static final String MUSIC_DURATION = "com.zjl.action.MUSIC_DURATION";//音乐播放长度改变动作
        public static final String MUSIC_PLAYING = "com.zjl.action.MUSIC_PLAYING";  //音乐正在播放动作
        public static final String REPEAT_ACTION = "com.zjl.action.REPEAT_ACTION";  //音乐重复播放动作
        public static final String SHUFFLE_ACTION = "com.zjl.action.SHUFFLE_ACTION";//音乐随机播放动作

        public static final String MUSIC_SERVICE = "com.zjl.action.MUSIC_SERVICE";//音乐随机播放动作
    }
}
