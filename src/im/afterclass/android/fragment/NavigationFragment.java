package im.afterclass.android.fragment; 

import im.afterclass.android.R;
import im.afterclass.android.activity.MyActivitiesActivity;
import im.afterclass.android.activity.PersonalActivity;
import im.afterclass.android.activity.PersonalInfoActivity;
import im.afterclass.android.activity.SettingsActivity;
import im.afterclass.android.adapter.NavigationItem;
import im.afterclass.android.adapter.NavigationListAdapter;

import java.util.ArrayList;

import java.util.List;

import android.content.Intent;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import android.widget.ListView;

import android.widget.TextView;

public class NavigationFragment extends  Fragment implements OnItemClickListener, OnClickListener{

	
	private View mView;
	private ListView navigationList;
	private List<NavigationItem> navigationArray = new  ArrayList<NavigationItem>();
	private NavigationListAdapter adapter;
	
	public NavigationFragment(){
		
	}
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);		
		
	}


	@Override
	public void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		
		mView = inflater.inflate(R.layout.navitation_channel_layout, null);
		initViews();
		return mView;	
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
	}
	
	private void initViews(){
		
		navigationList = (ListView)mView.findViewById(R.id.navitationlist);
		initArray();
		adapter = new NavigationListAdapter(getActivity(), navigationArray);
		navigationList.setAdapter(adapter);
		navigationList.setOnItemClickListener(this);
		
	}
	
	private void initArray(){
		navigationArray.add(new NavigationItem("我的活动", R.drawable.afterclass_0022));
		navigationArray.add(new NavigationItem("历史消息", R.drawable.afterclass_0021));
		navigationArray.add(new NavigationItem("个人资料", R.drawable.afterclass_0023));
		navigationArray.add(new NavigationItem("设置", R.drawable.afterclass_0020));
	}

	@Override
	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
		// TODO Auto-generated method stub
		Intent intent;
		switch(arg2){
		case 0:
			intent = new Intent(getActivity(), MyActivitiesActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.zoom_in, 0);
			break;
		case 1:
			break;
		case 2:
			intent = new Intent(getActivity(), PersonalInfoActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.zoom_in, 0);
			break;
		case 3:
			intent = new Intent(getActivity(),SettingsActivity.class);
			startActivity(intent);
			getActivity().overridePendingTransition(R.anim.zoom_in, 0);
			break;

		}
	}

	@Override
	public void onClick(View arg0) {
		// TODO Auto-generated method stub
		
	}
	
}
