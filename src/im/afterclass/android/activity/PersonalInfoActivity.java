package im.afterclass.android.activity;


import im.afterclass.android.R;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class PersonalInfoActivity extends ActionBarActivity implements OnClickListener{

	private LinearLayout nickNameLayout;
	private LinearLayout signLayout;
	private LinearLayout QRCodeLayout;
	private LinearLayout currentStateLayout;
	private TextView nickName;
	private TextView sign;
	private ImageView QRCode;
	private TextView currentState;
	private String nickName1;
	private String sign1;
	private String currentState1;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.personal_info);
		initViews();
	}
	
	private void initViews(){
		getPersonalInfo();
		nickNameLayout = (LinearLayout) findViewById(R.id.nickname_layout);
		signLayout = (LinearLayout) findViewById(R.id.sign_layout);
		QRCodeLayout = (LinearLayout) findViewById(R.id.QRCode_layout);
		currentStateLayout = (LinearLayout) findViewById(R.id.currentstate_layout);
		nickName = (TextView) findViewById(R.id.nickname);
		nickName.setText(nickName1);
		sign = (TextView) findViewById(R.id.sign);
		sign.setText(sign1);
		QRCode = (ImageView) findViewById(R.id.QRCode);
		currentState = (TextView) findViewById(R.id.current_state);
		currentState.setText(currentState1);
		nickNameLayout.setOnClickListener(this);
		signLayout.setOnClickListener(this);
		QRCodeLayout.setOnClickListener(this);
		currentStateLayout.setOnClickListener(this);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void getPersonalInfo(){
		SharedPreferences preferences = getSharedPreferences("personalInfo", 0);
		nickName1 = preferences.getString("nickName", "暂无");
		sign1 = preferences.getString("sign", "暂无");
		currentState1 = preferences.getString("currentState", "暂无");
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}

	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
    	case android.R.id.home:
			this.finish();
			overridePendingTransition(0,R.anim.zoom_out);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
        }
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		Intent intent;
		String defaultValue;
		switch(v.getId()){
		case R.id.nickname_layout:
			defaultValue = nickName.getText().toString();
			intent = new Intent(PersonalInfoActivity.this,ChangeNicknameActivity.class);
			intent.putExtra("nickName", defaultValue);
			startActivityForResult(intent, 0);			
			overridePendingTransition(R.anim.zoom_in, 0);
			break;
		case R.id.sign_layout:
			defaultValue = sign.getText().toString();
			intent = new Intent(PersonalInfoActivity.this,ChangeSignActivity.class);
			intent.putExtra("sign", defaultValue);
			startActivityForResult(intent, 0);
			overridePendingTransition(R.anim.zoom_in, 0);
			break;
		case R.id.QRCode_layout:
			break;
		case R.id.currentstate_layout:
			defaultValue = currentState.getText().toString();
			intent = new Intent(PersonalInfoActivity.this,ChangeCurrentStateActivity.class);
			intent.putExtra("currentState", defaultValue);
			startActivityForResult(intent, 0);
			overridePendingTransition(R.anim.zoom_in, 0);
			break;
		}
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data){
		if(1 == resultCode){
			refresh();
		}
	}
	
	private void refresh(){
		getPersonalInfo();
		nickName.setText(nickName1);
		sign.setText(sign1);
		currentState.setText(currentState1);
	}
	
}
