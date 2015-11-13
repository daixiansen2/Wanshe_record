package com.wanshe.view;
import com.wanshe.R;

import android.R.raw;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

public class DialogManager {

	private Context mContext;
	private Dialog mDialog;
	private ImageView iv_icon;
	private ImageView iv_vol;
	private TextView tv_finger;

	public DialogManager(Context context) {
		mContext = context;
	}
	
	public void showRecordingDialog(){
		mDialog = new Dialog(mContext,R.style.Mydialog);
		//mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		View view = LayoutInflater.from(mContext).inflate(R.layout.dialog_recorder,null);
		mDialog.setContentView(view);
		
		iv_icon = (ImageView) view.findViewById(R.id.iv_recording_icon);
		iv_vol = (ImageView) view.findViewById(R.id.iv_recording_vol);
		tv_finger = (TextView) view.findViewById(R.id.tv_finger);
		mDialog.show();
	}
	
	public void recording(){
		if(mDialog != null && mDialog.isShowing()){
			iv_icon.setVisibility(View.VISIBLE);
			iv_vol.setVisibility(View.VISIBLE);
			tv_finger.setVisibility(View.VISIBLE);
			
			iv_icon.setImageResource(R.drawable.recorder);
			tv_finger.setText(R.string.str_recorder_want_cancel_text);
		}
		
	}
	
	public void wantToCancel(){
		if(mDialog != null && mDialog.isShowing()){
			iv_icon.setVisibility(View.VISIBLE);
			iv_vol.setVisibility(View.GONE);
			tv_finger.setVisibility(View.VISIBLE);
			
			iv_icon.setImageResource(R.drawable.cancel);
			tv_finger.setText(R.string.str_recorder_recording_text);
		}
	}
	
	public void tooShort(){
		if(mDialog != null && mDialog.isShowing()){
			iv_icon.setVisibility(View.VISIBLE);
			iv_vol.setVisibility(View.GONE);
			tv_finger.setVisibility(View.VISIBLE);
			
			iv_icon.setImageResource(R.drawable.voice_to_short);
			tv_finger.setText(R.string.str_recorder_normal_text);
		}
	}
	
	public void dimissDialog(){
		if(mDialog != null && mDialog.isShowing()){
			mDialog.dismiss();
			mDialog = null;
		}
	}
	
	/**
	 * 通过level去更新iv_vol图片的显示
	 * 更新音量
	 * @param level
	 */
	public void updateVoiceLevel(int level){
		if(mDialog != null && mDialog.isShowing()){
			iv_icon.setVisibility(View.VISIBLE);
			iv_vol.setVisibility(View.VISIBLE);
			tv_finger.setVisibility(View.VISIBLE);
			
			int resId = mContext.getResources().getIdentifier("v"+level, "drawable", mContext.getPackageName());
			iv_vol.setImageResource(resId);
		}
	}
}
