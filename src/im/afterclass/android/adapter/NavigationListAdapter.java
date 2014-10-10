package im.afterclass.android.adapter;

import im.afterclass.android.R;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class NavigationListAdapter extends BaseAdapter {

	private Context context;
	private List<NavigationItem> navigationArray;
	
	public NavigationListAdapter(Context context, List<NavigationItem> navigationArray){
		this.context = context;
		this.navigationArray = navigationArray;
	}
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return 4;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return navigationArray.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		LayoutInflater inflater = LayoutInflater.from(context);
		NavigationItem item = navigationArray.get(position);
		if(convertView == null){
			convertView = inflater.inflate(R.layout.navigation_item, parent, false);
		}
		ImageView image = (ImageView)convertView.findViewById(R.id.navigationImage);
		TextView text = (TextView)convertView.findViewById(R.id.navigationText);
		image.setImageResource(item.mDrawableId);
		text.setText(item.mTitle);
		return convertView;
	}

}
