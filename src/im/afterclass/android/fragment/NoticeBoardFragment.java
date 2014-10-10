package im.afterclass.android.fragment;

import java.util.ArrayList;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import im.afterclass.android.activity.NoticeboardDetailActivity;
import im.afterclass.android.adapter.NoticeBoardAdapter;
import im.afterclass.android.domain.ActivityItem;

import im.afterclass.android.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

public class NoticeBoardFragment extends Fragment {

	private ListView noticeList;
	private NoticeBoardAdapter adapter;
	private List<ActivityItem> list;
	private final static String BASE_URL = "http://ac-mobile.twtapps.net";
	private final static String ACTIVITY_LIST_URL = "/act?page=%d";
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.noticeboard, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState){
		super.onActivityCreated(savedInstanceState);
		initViews();
	}
	
	private void initViews(){
		list = new ArrayList<ActivityItem>();
		initList();
		noticeList = (ListView) getView().findViewById(R.id.notice_list);
		adapter = new NoticeBoardAdapter(getActivity(), 1, list);
		noticeList.setAdapter(adapter);
		noticeList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(getActivity(),NoticeboardDetailActivity.class);
				intent.putExtra("actid", position+1);
				startActivity(intent);
				
			}
		});
	}
	
	private void initList(){
		String url = BASE_URL + String.format(ACTIVITY_LIST_URL, 0);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new JsonHttpResponseHandler(){
			@Override
			public void onFailure(int statusCode, Header[] header, byte[] data,
					Throwable throwable) {
				super.onFailure(statusCode, header, data, throwable);
				// TODO handle error
			}

			@Override
			public void onSuccess(int statusCode, Header[] header, JSONArray response) {
				super.onSuccess(statusCode, header, response);
				if(statusCode == 200){
					for(int i = 0; i<response.length(); i++){
						try{
							JSONObject object = response.getJSONObject(i);
							ActivityItem item = new ActivityItem();
							item.setTheme(object.getString("actname"));
							item.setLocation(object.getString("address"));
							item.setTime(object.getString("recommend_time"));
							item.setContent(object.getString("content"));
							list.add(item);
						}catch(JSONException exception){
							exception.printStackTrace();
						}
					}
				}
				// TODO to handle json 
				// you can use gson lib
			}
		});
	}
	
	@Override
	public void onDestroy(){
		super.onDestroy();
	}
}
