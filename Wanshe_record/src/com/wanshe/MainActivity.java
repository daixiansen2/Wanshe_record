package com.wanshe;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.wanshe.adapter.RecorderAdapter;
import com.wanshe.bean.Recorder;
import com.wanshe.view.AudioRecordButton;
import com.wanshe.view.AudioRecordButton.AudioFinishRecorderListener;
import com.wanshe.view.MediaManager;

public class MainActivity extends Activity implements OnItemClickListener, OnCompletionListener {

	private AudioRecordButton recorde_button;
	private ListView mListview;
	private RecorderAdapter mAdapter;
	private List<Recorder> mDatas = new ArrayList<Recorder>();
	private View animView;
	private AnimationDrawable anim;
//	private MediaManager mMediaManager;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		init();
	}

	private void init() {
		mListview = (ListView) findViewById(R.id.lv_listview);
		recorde_button = (AudioRecordButton) findViewById(R.id.id_recorde_button);
		recorde_button
				.setOnAudioFinishRecorderListener(new AudioFinishRecorderListener() {

					@Override
					public void onFinish(float seconds, String filePath) {
						Recorder recorder = new Recorder(seconds, filePath);
						//添加数据
						//jjjiji
						mDatas.add(recorder);
						// 更新显示
						mAdapter.notifyDataSetChanged();
						// 将lisview设置为最后一个
						Log.e("test", "音源: "+ mDatas.size());
						Log.e("test", "所有数据: "+ mDatas.toString());
						
						mListview.setSelection(mDatas.size() - 1);
					}
				});

		mAdapter = new RecorderAdapter(MainActivity.this, mDatas);
		mListview.setAdapter(mAdapter);
		mListview.setOnItemClickListener(this);
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if (animView != null) {
			animView.setBackgroundResource(R.drawable.adj);
			animView = null;
		}
		// 播放动画
		animView = view.findViewById(R.id.recorder_anim);
		animView.setBackgroundResource(R.drawable.play_anim);
		anim = (AnimationDrawable) animView.getBackground();
		anim.start();
		//mMediaManager = MediaManager.getInstance(mDatas.get(position).getFilePath(),this);
		String filePath = mDatas.get(position).getFilePath();
		
		MediaManager.playSound(filePath,this);
	}

	/**
	 * 音乐播放完毕的回调
	 */
	@Override
	public void onCompletion(MediaPlayer mp) {
		animView.setBackgroundResource(R.drawable.adj);
	}
	
	@Override
	protected void onResume() {
		super.onResume();
//		if(mMediaManager != null){
//			mMediaManager.resume();
//		}
		MediaManager.release();
	}
	
	
	@Override
	protected void onPause() {
		super.onPause();
//		if(mMediaManager != null){
//			mMediaManager.pause();
//		}
		anim.stop();
		if (animView != null) {
			animView.setBackgroundResource(R.drawable.adj);
			animView = null;
		}
		MediaManager.pause();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
//		if(mMediaManager != null){
//			mMediaManager.release();
//		}
		MediaManager.release();
	}

}
