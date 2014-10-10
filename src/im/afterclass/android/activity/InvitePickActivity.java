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
import im.afterclass.android.adapter.ContactAdapter;
import im.afterclass.android.domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.http.Header;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.LinearLayout.LayoutParams;
import android.widget.Toast;

import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import im.afterclass.android.R;

public class InvitePickActivity extends ActionBarActivity {
	private GridView gridView;
	/** 是否为一个新建的群组 */
	protected boolean isCreatingNewGroup;
	/** 是否为单选 */
	private boolean isSignleChecked;
	private PickContactAdapter contactAdapter;
	/** group中一开始就有的成员 */
	private List<String> exitingMembers;
	private LinearLayout contactSelected;
	private LayoutParams params;
	
	private final static String BASE_URL = "http://ac-mobile.twtapps.net";
	private final static String ACTIVITY_JOIN_URL = "/act/d%/join/d%";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_invite_pick_contacts);

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		// String groupName = getIntent().getStringExtra("groupName");
		String groupId = getIntent().getStringExtra("groupId");
		if (groupId == null) {// 创建群组
			isCreatingNewGroup = true;
		} else {
			// 获取此群组的成员列表
			EMGroup group = EMGroupManager.getInstance().getGroup(groupId);
			exitingMembers = group.getMembers();
		}
		if(exitingMembers == null)
			exitingMembers = new ArrayList<String>();
		// 获取好友列表
		final List<User> alluserList = new ArrayList<User>();
		for (User user : DemoApplication.getInstance().getContactList().values()) {
			if (!user.getUsername().equals(Constant.NEW_FRIENDS_USERNAME) & !user.getUsername().equals(Constant.GROUP_USERNAME))
				alluserList.add(user);
		}
		// 对list进行排序
		Collections.sort(alluserList, new Comparator<User>() {
			@Override
			public int compare(User lhs, User rhs) {
				return (lhs.getUsername().compareTo(rhs.getUsername()));

			}
		});

		contactSelected = (LinearLayout) findViewById(R.id.contact_selected);
		params = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.MATCH_PARENT);
		params.leftMargin = 10;
		params.topMargin = 5;
		params.gravity = Gravity.CENTER;
		gridView = (GridView) findViewById(R.id.gridview);
		//listView = (ListView) findViewById(R.id.list);
		contactAdapter = new PickContactAdapter(this, R.layout.contact_gridview_item, alluserList);
		gridView.setAdapter(contactAdapter);
		
//		doPost();
	}

	/**
	 * 确认选择的members
	 * 
	 * @param v
	 */

	public boolean onCreateOptionsMenu(Menu menu) {
		 
        MenuItem saveItem = menu.add("saveButton");
        MenuItemCompat.setShowAsAction(saveItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        saveItem.setIcon(R.drawable.ic_action_save);
        saveItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				
				Toast.makeText(InvitePickActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
    			//Intent intent = new Intent(InvitePickActivity.this,LaunchActivity.class);
    			//startActivity(intent);
    			//overridePendingTransition(R.anim.head_in,0);
    			return true;
			}
        });
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
        //Intent intent;
        switch (item.getItemId()) {
    	case android.R.id.home:
    		//intent = new Intent(InvitePickActivity.this,CreateActivity.class);
			//startActivity(intent);
    		this.finish();
    		overridePendingTransition(0,R.anim.zoom_out);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
        }
	}
	
	/**
	 * 获取要被添加的成员
	 * 
	 * @return
	 */
	private List<String> getToBeAddMembers() {
		List<String> members = new ArrayList<String>();
		int length = contactAdapter.isCheckedArray.length;
		for (int i = 0; i < length; i++) {
			String username = contactAdapter.getItem(i + 1).getUsername();
			if (contactAdapter.isCheckedArray[i] && !exitingMembers.contains(username)) {
				members.add(username);
			}
		}

		return members;
	}
	
	private void doPost() {
		
		List<String> members = getToBeAddMembers();
		for (int i = 0; i < members.size(); i++) {
			String url = BASE_URL + String.format(ACTIVITY_JOIN_URL, 1, members.get(i));
			AsyncHttpClient client = new AsyncHttpClient();
			client.post(url, new AsyncHttpResponseHandler() {

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
	}

	/**
	 * adapter
	 */
	private class PickContactAdapter extends ContactAdapter {

		private boolean[] isCheckedArray;
		private int count = 0;
		private LayoutInflater inflater;

		public PickContactAdapter(Context context, int resource, List<User> users) {
			super(context, resource, users);
			isCheckedArray = new boolean[users.size()];
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			View view = super.getView(position, convertView, parent);
			if(position == 0){
				count++;
			}
				final String username = getItem(position).getUsername();
				final FrameLayout contactFrameLayout = (FrameLayout) view.findViewById(R.id.contact_framelayout);
				final ImageView cancel = (ImageView) view.findViewById(R.id.cancel);
				contactFrameLayout.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						if (isSignleChecked && isCheckedArray[position]) {
							for (int i = 0; i < isCheckedArray.length; i++) {
								if (i != position) {
									isCheckedArray[i] = false;
								}
							}
							contactAdapter.notifyDataSetChanged();
						}
						if(!isCheckedArray[position]){
							contactFrameLayout.setForeground(getResources().getDrawable(R.drawable.framelayout_translucent_fg));
							cancel.setVisibility(View.VISIBLE);
							isCheckedArray[position] = true;
							addViewToContactSelected(getContext(), position, username);
						}
					}
				});
				cancel.setOnClickListener(new OnClickListener() {
					
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						contactFrameLayout.setForeground(getResources().getDrawable(R.drawable.framelayout_transparent_fg));
						cancel.setVisibility(View.GONE);
						isCheckedArray[position] = false;
						refreshContactLayout();
					}
				});
				
					// 群组中原来的成员一直设为选中状态
					if (exitingMembers.contains(username)) {
							//checkBox.setChecked(true);
						contactFrameLayout.setForeground(getResources().getDrawable(R.drawable.framelayout_translucent_fg));	
						isCheckedArray[position] = true;
						if(count == 1||position != 0){
						addViewToContactSelected(getContext(), position, username);
						}
					} else {
						if(!isCheckedArray[position]){
							contactFrameLayout.setForeground(getResources().getDrawable(R.drawable.framelayout_transparent_fg));
					}
					}
			return view;
		}
		private void addViewToContactSelected(Context context, int position, String username){
			inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.contact_selected, null);
			//ImageView avatar = (ImageView) v.findViewById(R.id.avatar);
			TextView name = (TextView) v.findViewById(R.id.name);
			name.setText(username);
			contactSelected.addView(v, params);
		}
		
		private void refreshContactLayout(){
			contactSelected.removeAllViews();
			for(int i = 0; i < isCheckedArray.length; i++){
				if(isCheckedArray[i]){
					addViewToContactSelected(getContext(), i, getItem(i).getUsername());
				}
			}
		}
	}

	public void back(View view){
		finish();
	}
	
}

