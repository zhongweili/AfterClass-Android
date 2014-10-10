package im.afterclass.android.activity;

import im.afterclass.android.R;

import android.app.Activity;
import android.os.Bundle;

public class AddEventActivity extends Activity {

	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_activity);
		initViews();
	}
	
	private void initViews(){
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
}
