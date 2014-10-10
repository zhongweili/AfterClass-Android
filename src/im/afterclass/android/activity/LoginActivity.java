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

import im.afterclass.android.Constant;
import im.afterclass.android.DemoApplication;
import im.afterclass.android.db.UserDao;
import im.afterclass.android.domain.User;
import im.afterclass.android.utils.CommonUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import im.afterclass.android.R;
import com.easemob.util.HanziToPinyin;

/**
 * 登陆页面
 * 
 */
public class LoginActivity extends Activity {
	private EditText usernameEditText;
	private EditText passwordEditText;
	
	private boolean progressShow;
	

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_login);

		usernameEditText = (EditText) findViewById(R.id.username);
		passwordEditText = (EditText) findViewById(R.id.password);
		// 如果用户名密码都有，直接进入主页面
		if (DemoApplication.getInstance().getUserName() != null && DemoApplication.getInstance().getPassword() != null) {
			usernameEditText.setText(DemoApplication.getInstance().getUserName());
			passwordEditText.setText(DemoApplication.getInstance().getPassword());
			startActivity(new Intent(this, MainActivity.class));
			finish();
		}
		// 如果用户名改变，清空密码
		usernameEditText.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				passwordEditText.setText(null);
			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		});

	}

	/**
	 * 登陆
	 * 
	 * @param view
	 */
	public void login(View view) {
		if (!CommonUtils.isNetWorkConnected(this)) {
			Toast.makeText(this, R.string.network_isnot_available, Toast.LENGTH_SHORT).show();
			return;
		}
		final String username = usernameEditText.getText().toString();
		final String password = passwordEditText.getText().toString();

		if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(password)) {
			progressShow=true;
			final ProgressDialog pd = new ProgressDialog(LoginActivity.this);
			pd.setCancelable(true);
			pd.setOnCancelListener(new OnCancelListener() {
				
				@Override
				public void onCancel(DialogInterface dialog) {
					progressShow=false;
				}
			});
			pd.setMessage("正在登陆...");
			pd.show();
			// 调用sdk登陆方法登陆聊天服务器
			EMChatManager.getInstance().login(username, password, new EMCallBack() {

				@Override
				public void onSuccess() {
					if(!progressShow)
					{
						return;
					}
					// 登陆成功，保存用户名密码
					DemoApplication.getInstance().setUserName(username);
					DemoApplication.getInstance().setPassword(password);
					runOnUiThread(new Runnable() {
						public void run() {
							pd.setMessage("正在获取好友和群聊列表...");
						}
					});
					try {
						// demo中简单的处理成每次登陆都去获取好友username，开发者自己根据情况而定
						List<String> usernames = EMChatManager.getInstance().getContactUserNames();
						Map<String, User> userlist = new HashMap<String, User>();
						for (String username : usernames) {
							User user = new User();
							user.setUsername(username);
							setUserHearder(username, user);
							userlist.put(username, user);
						}
						// 添加user"新的朋友"
						User newFriends = new User();
						newFriends.setUsername(Constant.NEW_FRIENDS_USERNAME);
						newFriends.setNick("新的朋友");
						newFriends.setHeader("");
						userlist.put(Constant.NEW_FRIENDS_USERNAME,newFriends);
						//添加"群聊"
						User groupUser = new User();
						groupUser.setUsername(Constant.GROUP_USERNAME);
						groupUser.setNick("群聊");
						groupUser.setHeader("");
						userlist.put(Constant.GROUP_USERNAME, groupUser);
						
						//存入内存
						DemoApplication.getInstance().setContactList(userlist);
						// 存入db
						UserDao dao = new UserDao(LoginActivity.this);
						List<User> users = new ArrayList<User>(userlist.values());
						dao.saveContactList(users);

						// 获取群聊列表,sdk会把群组存入到EMGroupManager和db中
						EMGroupManager.getInstance().getGroupsFromServer();
					} catch (Exception e) {
					}
					
					
					if(!LoginActivity.this.isFinishing())
					pd.dismiss();
					// 进入主页面
					startActivity(new Intent(LoginActivity.this, MainActivity.class));
					finish();
				}

				@Override
				public void onProgress(int progress, String status) {

				}

				@Override
				public void onError(int code, final String message) {
					if(!progressShow)
					{
						return;
					}
					runOnUiThread(new Runnable() {
						public void run() {
							pd.dismiss();
							Toast.makeText(getApplicationContext(), "登录失败: " + message, 0).show();

						}
					});
				}
			});
		}
	}

	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
		startActivityForResult(new Intent(this, Register1Activity.class), 0);
	}

	@Override
	protected void onResume() {
		super.onResume();
		if (DemoApplication.getInstance().getUserName() != null) {
			usernameEditText.setText(DemoApplication.getInstance().getUserName());
		}
	}


	/**
	 * 设置hearder属性，方便通讯中对联系人按header分类显示，以及通过右侧ABCD...字母栏快速定位联系人
	 * 
	 * @param username
	 * @param user
	 */
	protected void setUserHearder(String username, User user) {
		String headerName = null;
		if (!TextUtils.isEmpty(user.getNick())) {
			headerName = user.getNick();
		} else {
			headerName = user.getUsername();
		}
		if (username.equals(Constant.NEW_FRIENDS_USERNAME)) {
			user.setHeader("");
		} else if (Character.isDigit(headerName.charAt(0))) {
			user.setHeader("#");
		} else {
			user.setHeader(HanziToPinyin.getInstance().get(headerName.substring(0, 1)).get(0).target.substring(0, 1).toUpperCase());
			char header = user.getHeader().toLowerCase().charAt(0);
			if (header < 'a' || header > 'z') {
				user.setHeader("#");
			}
		}
	}
}
