package com.wanshe.adapter;

import java.util.List;

import android.content.Context;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.BaseAdapter;
import android.widget.FrameLayout;
import android.widget.TextView;
import com.wanshe.R;
import com.wanshe.bean.Recorder;

public class RecorderAdapter extends BaseAdapter {

	private List<Recorder> mDatas;
	private Context mContext;
	//
	private int mMinItemWidth;
	private int mMaxItemWidth;

	public RecorderAdapter(Context context, List<Recorder> datas) {
		this.mContext = context;
		this.mDatas = datas;
		
		//获取屏幕的宽度
		WindowManager wm  = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
		
		DisplayMetrics outMetrics = new DisplayMetrics();
		wm.getDefaultDisplay().getMetrics(outMetrics);
		
		mMinItemWidth = (int) (outMetrics.widthPixels * 0.15f);
		mMaxItemWidth = (int) (outMetrics.widthPixels * 0.7f); 
	}

	@Override
	public int getCount() {
		return mDatas == null ? 0 : mDatas.size();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder = null;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.item_recorder, null);
//			holder = new ViewHolder();
//			holder.recorder_length = (FrameLayout) convertView
//					.findViewById(R.id.recorder_length);
//			holder.recorder_time = (TextView) convertView
//					.findViewById(R.id.recorder_time);
//			convertView.setTag(holder);
			
		}
		holder = (ViewHolder) convertView.getTag();
		holder = ViewHolder.getHolder(convertView);
		holder.recorder_time.setText((int)mDatas.get(position).getTime() + "\"");
		ViewGroup.LayoutParams lp = holder.recorder_length.getLayoutParams();
		lp.width = (int) (mMinItemWidth + (mMaxItemWidth / 60f)
				* mDatas.get(position).getTime());
		return convertView;
	}

	@Override
	public Object getItem(int position) {
		return null;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}

	private static class ViewHolder {
		FrameLayout recorder_length;
		TextView recorder_time;

		public static ViewHolder getHolder(View convertView) {
			Object tag = convertView.getTag();
			if (tag != null) {
				return (ViewHolder) tag;
			} else {
				ViewHolder holder = new ViewHolder();
				holder.recorder_length = (FrameLayout) convertView
						.findViewById(R.id.recorder_length);
				holder.recorder_time = (TextView) convertView
						.findViewById(R.id.recorder_time);
				convertView.setTag(holder);
				return holder;
			}
		}
	}
	
//	private static class ViewHolder{
//		FrameLayout recorder_length;
//		TextView recorder_time;
//	}
}
