package com.wanshe.view;

import android.media.AudioManager;
import android.media.MediaPlayer;

public class MediaManager {
	//private static MediaManager mInstance;
	//private String filePath;
	//private  MediaPlayer.OnCompletionListener mListener;
	private static MediaPlayer mMediaPlayer;
	private static boolean isPause;   //暂停标志位

//	private MediaManager(String filePath,MediaPlayer.OnCompletionListener onCompletionListener) {
//		this.filePath = filePath;
//		this.mListener = onCompletionListener;
//	}
	
//	public static MediaManager getInstance(String filePath,MediaPlayer.OnCompletionListener onCompletionListener){
//		if(mInstance==null){
//			mInstance = new MediaManager(filePath, onCompletionListener);
//		}
//		return mInstance;
//	}
	
	/**
	 * 播放音乐
	 */
	public static void playSound(String filePath,MediaPlayer.OnCompletionListener onCompletionListener){
		if(mMediaPlayer == null) {
            mMediaPlayer = new MediaPlayer();

            //设置一个error监听器
            mMediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {

                @Override
                public boolean onError(MediaPlayer mp, int what, int extra) {
                	//重置
                    mMediaPlayer.reset();
                    return false;
                }
            });
        }else {
            mMediaPlayer.reset();
        }
		
		 try {
			 mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			 mMediaPlayer.setOnCompletionListener(onCompletionListener);
			 mMediaPlayer.setDataSource(filePath);
			 mMediaPlayer.prepare();
			 mMediaPlayer.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
     * 暂停播放
     */
    public static void pause(){
        if(mMediaPlayer != null && mMediaPlayer.isPlaying()) { //正在播放的时候
            mMediaPlayer.pause();
            isPause = true;
        }
    }

    /**
     * 当前是isPause状态
     */
    public static void resume(){
        if(mMediaPlayer != null && isPause) {
            mMediaPlayer.start();
            isPause = false;
        }
    }

    /**
     * 释放资源
     */
    public static void release(){
        if(mMediaPlayer != null) {
            mMediaPlayer.release();
            mMediaPlayer = null;
        }
    }
}
