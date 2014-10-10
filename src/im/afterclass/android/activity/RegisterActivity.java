/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package im.afterclass.android.activity;

import java.util.Random;

import org.apache.http.Header;

import im.afterclass.android.DemoApplication;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.chat.EMChatConfig;
import com.easemob.chat.EMChatManager;
import im.afterclass.android.R;
import im.afterclass.android.utils.CipherUtil;

import com.easemob.exceptions.EMNetworkUnconnectedException;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 注册页
 *
 */
public class RegisterActivity extends Activity{
	private EditText userNameEditText;
	private EditText passwordEditText;
	private EditText confirmPwdEditText;
	
	private final static String BASE_URL = "http://ac-mobile.twtapps.net";
	private final static String USER_REGISTER = "/user";
	
	private static Random randGen = null;
	private static char[] numbersAndLetters = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		userNameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		confirmPwdEditText=(EditText) findViewById(R.id.confirm_password);
	}
	
	
	/**
	 * 注册
	 * @param view
	 */
	public void register(View view){
		final String username = userNameEditText.getText().toString().trim();
		final String pwd = passwordEditText.getText().toString().trim();
		String confirm_pwd=confirmPwdEditText.getText().toString().trim();
		if(TextUtils.isEmpty(username))
		{
			Toast.makeText(this, "用户名不能为空！", Toast.LENGTH_SHORT).show();
			userNameEditText.requestFocus();
			return;
		}else if(TextUtils.isEmpty(pwd))
		{
			Toast.makeText(this, "密码不能为空！", Toast.LENGTH_SHORT).show();
			passwordEditText.requestFocus();
			return;
		}else if(TextUtils.isEmpty(confirm_pwd))
		{
			Toast.makeText(this, "确认密码不能为空！", Toast.LENGTH_SHORT).show();
			confirmPwdEditText.requestFocus();
			return;
		}else if(!pwd.equals(confirm_pwd))
		{
			Toast.makeText(this, "两次输入的密码不一致，请重新输入！", Toast.LENGTH_SHORT).show();
			return;
		}
		
		if(!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)){
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("正在注册...");
			pd.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						//调用sdk注册方法
						EMChatManager.getInstance().createAccountOnServer(username, pwd);
						runOnUiThread(new Runnable() {
							public void run() {
								if(!RegisterActivity.this.isFinishing())
								pd.dismiss();
								//保存用户名
								DemoApplication.getInstance().setUserName(username);
								Toast.makeText(getApplicationContext(), "注册成功", 0).show();
								newRegister();
								finish();
							}
						});
					} catch (final Exception e) {
						runOnUiThread(new Runnable() {
							public void run() {
								if(!RegisterActivity.this.isFinishing())
								pd.dismiss();
								if(e!=null&&e.getMessage()!=null)
								{
									String errorMsg=e.getMessage();
									if(errorMsg.indexOf("EMNetworkUnconnectedException")!=-1)
									{
										Toast.makeText(getApplicationContext(), "网络异常，请检查网络！", 0).show();
									}else if(errorMsg.indexOf("conflict")!=-1)
									{
										Toast.makeText(getApplicationContext(), "用户已存在！", 0).show();
									}else{
										Toast.makeText(getApplicationContext(), "注册失败: " + e.getMessage(), 1).show();
									}
									
								}else{
									Toast.makeText(getApplicationContext(), "注册失败: 未知异常", 1).show();
									
								}
							}
						});
					}
				}
			}).start();
			
			
		}
	}
	
	public void back(View view){
		finish();
	}
	
	public void newRegister(){
		String mobile = getIntent().getStringExtra("mobile");
		String url = BASE_URL + USER_REGISTER;
		
		String passwd = randomString(6);
		CipherUtil cipher = new CipherUtil();
		String enPasswd = cipher.generatePassword(passwd);
		
		RequestParams params = new RequestParams();
		params.add("sid", "2");
		params.add("mobile", mobile);
		params.add("key", enPasswd);
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, params, new AsyncHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] header, byte[] data,
					Throwable throwable) {
				super.onFailure(statusCode, header, data, throwable);
				// TODO handle error
			}

			@Override
			public void onSuccess(int statusCode, Header[] header, byte[] data) {
				super.onSuccess(statusCode, header, data);
			
			}

		});
	}
	

	public static final String randomString(int length) {
        if (length < 1) {
            return null;
        }
        if (randGen == null) {
               randGen = new Random();
               numbersAndLetters = ("0123456789abcdefghijklmnopqrstuvwxyz" +
                  "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
                 //numbersAndLetters = ("0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZ").toCharArray();
                }
        char [] randBuffer = new char[length];
        for (int i=0; i<randBuffer.length; i++) {
            randBuffer[i] = numbersAndLetters[randGen.nextInt(71)];
         //randBuffer[i] = numbersAndLetters[randGen.nextInt(35)];
        }
        return new String(randBuffer);
}
}
