package im.afterclass.android.activity;

import java.util.HashMap;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import im.afterclass.android.R;
import im.afterclass.android.domain.ReplyContent;

public class Register1Activity extends Activity{

	private EditText SchoolEditText;
	private EditText GradeEditText;
	private EditText MajorEditText;
	private Button btn1;
	private Button btn2;

	private String[] mProvinceDatas;
	private Map<String, String[]> mSchoolDatasMap = new HashMap<String, String[]>();
	
	private final static String BASE_URL = "http://ac-mobile.twtapps.net";
	private final static String INFO_SCHOOLS = "/info/schools";
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.register1);
		
		SMSSDK.initSDK(this, "2cea174f66e7", "4495d06411242c83ed850b84d38050c2");
		
		SchoolEditText = (EditText) findViewById(R.id.widget1);
		GradeEditText = (EditText) findViewById(R.id.widget3);
		MajorEditText=(EditText) findViewById(R.id.widget2);
		btn1 = (Button) findViewById(R.id.btn1);
		btn2 = (Button) findViewById(R.id.btn2);
		
		btn1.setOnClickListener(new ButtonClickListener());
		btn2.setOnClickListener(new ButtonClickListener());
	}
	public class ButtonClickListener implements OnClickListener
	{
		@Override
		public void onClick(View view)
		{
			switch(view.getId()){
				case R.id.btn1:
					final String school = SchoolEditText.getText().toString().trim();
					final String grade = GradeEditText.getText().toString().trim();
					final String major = MajorEditText.getText().toString().trim();
					if(TextUtils.isEmpty(school))
					{
						Toast.makeText(Register1Activity.this, "学校不能为空！", Toast.LENGTH_SHORT).show();
						SchoolEditText.requestFocus();
						return;
					}else if(TextUtils.isEmpty(grade))
					{
						Toast.makeText(Register1Activity.this, "年级不能为空！", Toast.LENGTH_SHORT).show();
						GradeEditText.requestFocus();
						return;
					}else if(TextUtils.isEmpty(major))
					{
						Toast.makeText(Register1Activity.this, "专业不能为空！", Toast.LENGTH_SHORT).show();
						MajorEditText.requestFocus();
						return;
					}
					if(!TextUtils.isEmpty(school) && !TextUtils.isEmpty(grade) && !TextUtils.isEmpty(major)){
						//注册存入信息
						
						RegisterPage registerPage = new RegisterPage();
						registerPage.setRegisterCallback(new EventHandler() {
						        public void afterEvent(int event, int result, Object data) {
						                // 解析注册结果
						                if (result == SMSSDK.RESULT_COMPLETE) {
						            //            @SuppressWarnings("unchecked")
						                        HashMap<String,Object> phoneMap = (HashMap<String, Object>) data;
						                        String country = (String) phoneMap.get("country");
						                        String phone = (String) phoneMap.get("phone");
						                        
						                        Intent intent = new Intent(Register1Activity.this,Register3Activity.class);
						                        intent.putExtra("mobile", phone);
						                        // 提交用户信息
						           //             registerUser(country, phone);
						                        startActivity(intent);
						            			finish();
						                }
						        }
						});
						registerPage.show(Register1Activity.this);
					}
					
					break;
				case R.id.btn2:
					startActivity(new Intent(Register1Activity.this,LoginActivity.class));
					finish();
					break;
			}
		}
	}
	
	private void initDatas() {
		String url = BASE_URL + String.format(INFO_SCHOOLS);
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
							String[] mSchoolDatas = new String[response.length()];
							JSONObject object = response.getJSONObject(i);
							String school = object.getString("sname");
							mSchoolDatas[i] = school;
							String sid = object.getString("sid");
							String pid = object.getString("pid");
							mSchoolDatasMap.put(sid, mSchoolDatas);
							
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
	
}
