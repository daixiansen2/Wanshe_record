package com.wanshe.view;

import com.wanshe.R;
import com.wanshe.view.AudioManager.AudioStateListener;

import android.R.integer;
import android.content.Context;
import android.content.res.Resources.Theme;
import android.os.Environment;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class AudioRecordButton extends Button implements AudioStateListener {
	private static final int DISTANCE_Y = 50;
	private static final int STATE_NOEMAL = 1;
	private static final int STATE_RECORDING = 2;
	private static final int STATE_WANT_CANCEL = 3;
	
	private float mTime;   //录音计时
	//是否触发longclick
	private boolean mReady = false;

	private int mCurState = STATE_NOEMAL; // 默认状态标志
	private boolean isReCording = false; // 录音状态
	private  DialogManager mDialogManager;
	private AudioManager mAudioManager;

	public AudioRecordButton(Context context) {
		this(context, null); 
	}

	public AudioRecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		String dir = Environment.getExternalStorageDirectory() + "/wx_wanshe";
		mAudioManager = AudioManager.getInstance(dir);
		mAudioManager.setOnAudioStateListener(this);

		mDialogManager = new DialogManager(getContext());

		setOnLongClickListener(new OnLongClickListener() {

			@Override
			public boolean onLongClick(View v) {
//				isCording = true;
//				mDialogManager.showRecordingDialog();
				//准备录音
				mReady = true;
				mAudioManager.prepareAudio();
				return true;
			}
		});
	}
	
	/**
	 * 录音完成后的回调
	 * @author daixiansen
	 *
	 */
	public interface AudioFinishRecorderListener{
		void onFinish(float seconds,String filePath);
	}
	
	private AudioFinishRecorderListener mListener;
	
	public void setOnAudioFinishRecorderListener(AudioFinishRecorderListener listener) {
		this.mListener = listener;
	}

	/**
	 *  获取录音大小和时长
	 */
	private Runnable mGetVoiceLevelRunnable = new Runnable() {
		
		@Override
		public void run() {
			while(isReCording){
				try {
					Thread.sleep(100);
					mTime += 0.1f;
					mHandler.sendEmptyMessage(MSG_VOICE_CHANGED);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	};

	private static final int MSG_AUDIO_PREPARED = 4;
	private static final int MSG_VOICE_CHANGED = 5;
	private static final int MSG_DIALOG_DIMISS = 6;

	private Handler mHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
			switch (msg.what) {
			case MSG_AUDIO_PREPARED:
				//显示应该在audio_perapred之后
				isReCording = true;
				mDialogManager.showRecordingDialog();
				new Thread(mGetVoiceLevelRunnable).start();
				break;
			case MSG_VOICE_CHANGED:
				//更新音量大小
				mDialogManager.updateVoiceLevel(mAudioManager.getVoiceLevel(7));

				break;
			case MSG_DIALOG_DIMISS:
				mDialogManager.dimissDialog();
				break;

			default:
				break;
			}
		};
	};

	/**
	 * 录音的回调函数
	 */
	@Override
	public void wellPrepared() {
		mHandler.sendEmptyMessage(MSG_AUDIO_PREPARED);

	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			// 开始录音
			changeState(STATE_RECORDING);
			break;
		case MotionEvent.ACTION_MOVE:
			//先判断开始录音没
			if (isReCording) {
				// 根据x,y的坐标，判断是否想要取消
				if (wantToCancel(x, y)) {
					changeState(STATE_WANT_CANCEL);
				} else {
					changeState(STATE_RECORDING);
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			Log.e("test", "isReCording: "+isReCording+","+"time: " +mTime);
			//没有触发longClick就抬起来了
			if(!mReady){
				Log.i("test", "按下时间过短");
				reset();
				return super.onTouchEvent(event);
			}
			//Prepared 还没有完成,isReCording就还为false
			//或者时间过短
			if(!isReCording || mTime < 0.6f){
				Log.i("test", "录音时间过短");
				mDialogManager.tooShort();
				mAudioManager.cancel();
				//加延迟, 让dialog显示1.3S再消失
				mHandler.sendEmptyMessageDelayed(MSG_DIALOG_DIMISS, 1300);
			}else if (mCurState == STATE_RECORDING) {  //正常录制结束
				Log.i("test", "正常录音结束");
				// 结束并保存录音
				mDialogManager.dimissDialog();
				mAudioManager.release();
				if(mListener != null){
					mListener.onFinish(mTime, mAudioManager.getCurrentFilePath());
				}
			} else if (mCurState == STATE_WANT_CANCEL) {
				mDialogManager.dimissDialog();
				mAudioManager.cancel();
			}

			// 恢复状态
			reset();

			break;

		default:
			break;
		}
		return super.onTouchEvent(event);
	}

	/**
	 * 恢复状态及标志位
	 */
	private void reset() {
		mReady = false;
		isReCording = false;
		mTime = 0;
		changeState(STATE_NOEMAL);
	}

	/**
	 * 判断Button的move状态
	 * 
	 * @param x
	 * @param y
	 * @return
	 */
	private boolean wantToCancel(int x, int y) {
		if (x < 0 || x > getWidth()) {
			return true;
		}
		if (y < -DISTANCE_Y || y > getHeight() + DISTANCE_Y) {
			return true;
		}
		return false;
	}

	/**
	 * 更新button状态的状态
	 * 
	 * @param stateRecording
	 */
	private void changeState(int state) {
		if (mCurState != state) {
			mCurState = state;
			switch (state) {
			case STATE_NOEMAL:
				setBackgroundResource(R.drawable.btn_recorder_normal);
				setText(R.string.str_recorder_normal);
				break;
			case STATE_RECORDING:
				setBackgroundResource(R.drawable.btn_recorder);
				setText(R.string.str_recorder_recording);
				if (isReCording) {
					mDialogManager.recording();
				}
				break;
			case STATE_WANT_CANCEL:
				setBackgroundResource(R.drawable.btn_recorder);
				setText(R.string.str_recorder_want_cancel);
				mDialogManager.wantToCancel();
				break;
			default:
				break;
			}
		}
	}
}
