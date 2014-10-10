package im.afterclass.android.adapter;

import java.util.List;

import im.afterclass.android.R;
import im.afterclass.android.domain.ActivityItem;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NoticeBoardAdapter extends ArrayAdapter<ActivityItem> {

	private Context context;
	private LayoutInflater inflater;
	
	public  NoticeBoardAdapter(Context context, int resId, List<ActivityItem> list){
		super(context, resId, list);
		this.context = context;
		inflater = LayoutInflater.from(context);
	}
	

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		if(convertView ==null){
			convertView = inflater.inflate(R.layout.noticeboard_item, parent, false);
		}
		ViewHolder holder = (ViewHolder) convertView.getTag();
		if(holder == null){
			holder = new ViewHolder();
			holder.theme = (TextView) convertView.findViewById(R.id.theme);
			holder.remainTime = (TextView) convertView.findViewById(R.id.remain_time);
			holder.pic = (ImageView) convertView.findViewById(R.id.picture);
			holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.location = (TextView) convertView.findViewById(R.id.location);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.participant = (TextView) convertView.findViewById(R.id.participant);
			convertView.setTag(holder);
		}
		ActivityItem item = getItem(position);
		holder.theme.setText(item.getTheme());
		//holder.remainTime.setText(item.getRemainTime());
		//holder.name.setText(item.getName());
		holder.location.setText(item.getLocation());
		holder.time.setText(item.getTime());
		holder.content.setText(item.getContent());
		//holder.participant.setText(item.getParticipant());
		return convertView;
	}
	
	private static class ViewHolder{
		TextView theme;
		TextView remainTime;
		ImageView pic;
		ImageView avatar;
		TextView name;
		TextView location;
		TextView time;
		TextView content;
		TextView participant;
	}

}
