package im.afterclass.android.activity;

import im.afterclass.android.adapter.NoticeBoardAdapter;

import im.afterclass.android.R;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ListView;

public class ArrangeActivity extends Activity {

	private ListView arrangeList;
	private NoticeBoardAdapter adapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noticeboard);
		initViews();
	}
	
	private void initViews(){
		arrangeList = (ListView) findViewById(R.id.notice_list);
		//adapter = new NoticeBoardAdapter(this);
		arrangeList.setAdapter(adapter);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
}
