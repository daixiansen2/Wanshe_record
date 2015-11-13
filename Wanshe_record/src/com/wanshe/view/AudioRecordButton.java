package com.wanshe.view;
import com.wanshe.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;

public class AudioRecordButton extends Button {
	private static final int DISTANCE_Y = 50;
	private static final int STATE_NOEMAL = 1;
	private static final int STATE_RECORDING = 2;
	private static final int STATE_WANT_CANCEL = 3;

	private int mCurState = STATE_NOEMAL; // 默认状态标志
	private boolean isCording = false; // 录音状态
	private DialogManager mDialogManager;

	public AudioRecordButton(Context context) {
		this(context, null);
	}

	public AudioRecordButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		mDialogManager = new DialogManager(getContext());
		
		setOnLongClickListener(new OnLongClickListener() {
			
			@Override
			public boolean onLongClick(View v) {
				isCording = true;
				mDialogManager.showRecordingDialog();
				return true;
			}
		});
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
			if (isCording) {
				// 根据x,y的坐标，判断是否想要取消
				if (wantToCancel(x, y)) {
					changeState(STATE_WANT_CANCEL);
				} else {
					changeState(STATE_RECORDING);
				}
			}

			break;
		case MotionEvent.ACTION_UP:
			if (mCurState == STATE_RECORDING) {
				// 结束并保存录音
				mDialogManager.dimissDialog();
			} else if (mCurState == STATE_WANT_CANCEL) {
				mDialogManager.dimissDialog();
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
		isCording = false;
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
		if(x < 0 || x > getWidth()){
			return true;
		}
		if(y < -DISTANCE_Y || y > getHeight() + DISTANCE_Y){
			return true;
		}
		return false;
	}

	/**
	 * button状态
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
				if(isCording){
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
