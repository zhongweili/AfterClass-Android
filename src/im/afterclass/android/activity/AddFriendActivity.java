package im.afterclass.android.activity;

import im.afterclass.android.DemoApplication;

import com.easemob.chat.EMContactManager;
import im.afterclass.android.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;

public class AddFriendActivity extends ActionBarActivity {

	private EditText editText;
	private LinearLayout searchedUserLayout;
	private TextView nameText;
	private Button searchBtn;
	//private ImageView avatar;
	//private InputMethodManager inputMethodManager;
	private String toAddUsername;
	private ProgressDialog progressDialog;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.add_friends);
		initViews();
	}
	
	private void initViews(){
		editText = (EditText) findViewById(R.id.add_friend_by_search_keyword);
		searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
		nameText = (TextView) findViewById(R.id.name);
		searchBtn = (Button) findViewById(R.id.add_friend_by_search_submit);
		//avatar = (ImageView) findViewById(R.id.avatar);
		//inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		 
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	
	public boolean onOptionsItemSelected(MenuItem item) {
  
        Intent intent;
        switch (item.getItemId()) {
        	case android.R.id.home:
        		intent = new Intent(AddFriendActivity.this,MainActivity.class);
    			startActivity(intent);
        		this.finish();
        		overridePendingTransition(0,R.anim.slide_out_to_right);
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
        //return true;
    }
	
	/**
	 * 查找contact
	 * @param v
	 */
	public void searchContact(View v) {
		final String name = editText.getText().toString();
		String saveText = searchBtn.getText().toString();
		
		if (getString(R.string.button_search).equals(saveText)) {
			toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				startActivity(new Intent(this, AlertDialogActivity.class).putExtra("msg", "请输入用户名"));
				return;
			}
			
			// TODO 从服务器获取此contact,如果不存在提示不存在此用户
			
			//服务器存在此用户，显示此用户和添加按钮
			searchedUserLayout.setVisibility(View.VISIBLE);
			nameText.setText(toAddUsername);
			
		} 
	}	
	
	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(View view){
		if(DemoApplication.getInstance().getUserName().equals(nameText.getText().toString())){
			startActivity(new Intent(this, AlertDialogActivity.class).putExtra("msg", "不能添加自己"));
			return;
		}
		
		if(DemoApplication.getInstance().getContactList().containsKey(nameText.getText().toString())){
			startActivity(new Intent(this, AlertDialogActivity.class).putExtra("msg", "此用户已是你的好友"));
			return;
		}
		
		progressDialog = new ProgressDialog(this);
		progressDialog.setMessage("正在发送请求...");
		progressDialog.setCanceledOnTouchOutside(false);
		progressDialog.show();
		
		new Thread(new Runnable() {
			public void run() {
				
				try {
					//demo写死了个reason，实际应该让用户手动填入
					EMContactManager.getInstance().addContact(toAddUsername, "加个好友呗");
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "发送请求成功,等待对方验证", Toast.LENGTH_SHORT).show();
						}
					});
				} catch (final Exception e) {
					runOnUiThread(new Runnable() {
						public void run() {
							progressDialog.dismiss();
							Toast.makeText(getApplicationContext(), "请求添加好友失败:" + e.getMessage(), Toast.LENGTH_SHORT).show();
						}
					});
				}
			}
		}).start();
	}
	
	public void back(View v) {
		finish();
	}
}
