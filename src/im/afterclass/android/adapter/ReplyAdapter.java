package im.afterclass.android.adapter;

import im.afterclass.android.R;
import im.afterclass.android.domain.ReplyContent;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReplyAdapter extends ArrayAdapter<ReplyContent> {

	private LayoutInflater inflater;
	private int res;
	private List<ReplyContent> objects;
	
	public ReplyAdapter(Context context, int resource,
			List<ReplyContent> objects) {
		super(context, resource, objects);
		this.res = resource;
		this.objects = objects;
		inflater = LayoutInflater.from(context);
		
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent){
		if(convertView == null){
			convertView = inflater.inflate(res, parent, false);
		}
		ViewHolder  holder = (ViewHolder) convertView.getTag();
		if(holder == null){
			holder = new ViewHolder();
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.content = (TextView) convertView.findViewById(R.id.content);
			holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		}
		ReplyContent reply = getItem(position);
		holder.name.setText(reply.getName());
		holder.content.setText(reply.getContent());
		holder.time.setText(reply.getTime());
		return convertView;
	}
	
	@Override
	public ReplyContent getItem(int position){
		return objects.get(position);
	}
	
	@Override
	public int getCount(){
		return objects.size();
	}

	private static class ViewHolder{
		
		TextView name;
		TextView content;
		TextView time;
		
	}

}
