package im.afterclass.android.adapter;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import im.afterclass.android.R;
import im.afterclass.android.adapter.DragGridBaseAdapter;
import android.content.SharedPreferences;
import android.content.Context;
import android.content.SharedPreferences.Editor;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class DragAdapter extends BaseAdapter implements DragGridBaseAdapter{
	private List<HashMap<String, Object>> list;
	private LayoutInflater mInflater;
	private int mHidePosition = -1;
	private SharedPreferences sp;

	public DragAdapter(Context context, List<HashMap<String, Object>> list){
		this.list = list;
		mInflater = LayoutInflater.from(context);
		sp = context.getSharedPreferences("afterclass", context.MODE_PRIVATE);
		
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	/**
	 * 由于复用convertView导致某些item消失了，所以这里不复用item，
	 */
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = mInflater.inflate(R.layout.create_item, null);
		ImageView mImageView = (ImageView) convertView.findViewById(R.id.ItemImage);
		TextView mTextView = (TextView) convertView.findViewById(R.id.ItemText);
		
		mImageView.setImageResource((Integer) list.get(position).get("ItemImage"));
		mTextView.setText((CharSequence) list.get(position).get("ItemText"));
		
		if(position == mHidePosition){
			convertView.setVisibility(View.INVISIBLE);
		}
		
		return convertView;
	}
	

	@Override
	public void reorderItems(int oldPosition, int newPosition) {
		String themelist = sp.getString("themelist", "自定义*吃*桌游*聚会*运动*游泳*排球*滑冰*自习*网吧*咖啡*酒吧*电影*KTV*逛街*会议*公园*演唱会*话剧");
		String[] themearray = themelist.split(Pattern.quote("*"));
		StringBuffer themenew = new StringBuffer();
		String tempString = "";
		if(oldPosition < newPosition){
			for(int i=oldPosition; i<newPosition; i++){
				tempString = themearray[i];
				themearray[i] = themearray[i+1];
				themearray[i+1] = tempString;
			}
		}else if(oldPosition > newPosition){
			for(int i=oldPosition; i>newPosition; i--){
				tempString = themearray[i];
				themearray[i] = themearray[i-1];
				themearray[i-1] = tempString;
			}
		}
		for(int k=0; k<themearray.length-1; k++){
			themenew.append(themearray[k]);
			themenew.append("*");
		}
		themenew.append(themearray[themearray.length-1]);
		Editor editor = sp.edit();
		editor.putString("themelist", themenew.toString());
		editor.commit();
		
		HashMap<String, Object> temp = list.get(oldPosition);
		if(oldPosition < newPosition){
			for(int i=oldPosition; i<newPosition; i++){
				Collections.swap(list, i, i+1);
			}
		}else if(oldPosition > newPosition){
			for(int i=oldPosition; i>newPosition; i--){
				Collections.swap(list, i, i-1);
			}
		}

	}

	@Override
	public void setHideItem(int hidePosition) {
		this.mHidePosition = hidePosition; 
		notifyDataSetChanged();
	}


}
