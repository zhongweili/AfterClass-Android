package im.afterclass.android.activity;

import im.afterclass.android.R;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.view.View.OnClickListener;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class PersonalActivity extends Activity implements OnClickListener {

	private Button myactivities;
	private Button personalInfo;
	private Button setting;
	private Button message;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);

		setContentView(R.layout.activity_personal);
		personalInfo = (Button) findViewById(R.id.personalinfo);
		myactivities = (Button) findViewById(R.id.myactivities);
		setting = (Button) findViewById(R.id.setting);
		message = (Button) findViewById(R.id.message);

		personalInfo.setOnClickListener(this);
		myactivities.setOnClickListener(this);
		setting.setOnClickListener(this);
		message.setOnClickListener(this);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

	}

	public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
    	case android.R.id.home:
    		intent = new Intent(PersonalActivity.this,MainActivity.class);
			startActivity(intent);
    		this.finish();
    		overridePendingTransition(0, R.anim.zoom_out);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
        }
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		switch (v.getId()) {
		case R.id.personalinfo:
			intent = new Intent(PersonalActivity.this, PersonalInfoActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.zoom_in, 0);
			break;
		case R.id.myactivities:
			intent = new Intent(PersonalActivity.this, MyActivitiesActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.zoom_in, 0);
			break;
		case R.id.setting:
			intent = new Intent(PersonalActivity.this,SettingsActivity.class);
			startActivity(intent);
			overridePendingTransition(R.anim.zoom_in, 0);
			break;
		case R.id.message:

			break;
		}
	}
}
