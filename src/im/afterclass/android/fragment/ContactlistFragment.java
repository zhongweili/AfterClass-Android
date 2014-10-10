
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
package im.afterclass.android.fragment;

import im.afterclass.android.Constant;
import im.afterclass.android.DemoApplication;
import im.afterclass.android.activity.ChatActivity;
import im.afterclass.android.activity.GroupsActivity;
import im.afterclass.android.activity.NewFriendsMsgActivity;
import im.afterclass.android.adapter.ContactAdapter;
import im.afterclass.android.db.InviteMessgeDao;
import im.afterclass.android.db.UserDao;
import im.afterclass.android.domain.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.Toast;

import com.easemob.chat.EMContactManager;
import im.afterclass.android.R;

/**
 * 联系人列表页
 *
 */
public class ContactlistFragment extends Fragment{
	private ContactAdapter adapter;
	private List<User> contactList;
	private GridView gridView;
	//private ListView listView;
	private boolean hidden;
	//private Sidebar sidebar;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		return inflater.inflate(R.layout.fragment_contact_list, container, false);
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		gridView = (GridView) getView().findViewById(R.id.gridView);
		//listView = (ListView) getView().findViewById(R.id.list);
		//sidebar = (Sidebar) getView().findViewById(R.id.sidebar);
		//sidebar.setListView(listView);
		contactList = new ArrayList<User>();
		//获取设置contactlist
		getContactList();
		//设置adapter
		adapter = new ContactAdapter(getActivity(), R.layout.contact_gridview_item, contactList);
		gridView.setAdapter(adapter);
		gridView.setOnItemClickListener(new OnItemClickListener(){

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				// TODO Auto-generated method stub
				String username = adapter.getItem(position).getUsername();
				if(Constant.NEW_FRIENDS_USERNAME.equals(username)){
					//进入新的朋友页面
					User user = DemoApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME);
					user.setUnreadMsgCount(0);
					startActivity(new Intent(getActivity(), NewFriendsMsgActivity.class));
				}else if(Constant.GROUP_USERNAME.equals(username)){
					//进入群聊列表页面
					startActivity(new Intent(getActivity(), GroupsActivity.class));
				}else{
					
					showPopupWindow(view);
					//demo中直接进入聊天页面，实际一般是进入用户详情页
					startActivity(new Intent(getActivity(), ChatActivity.class).putExtra("userId", adapter.getItem(position).getUsername()));
				}
			}
		});
		registerForContextMenu(gridView);
		
	}
	
	public void showPopupWindow(View v) {
		
	}
	
	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
		getActivity().getMenuInflater().inflate(R.menu.delete_contact, menu);
		// }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_contact) {
			User tobeDeleteUser= adapter.getItem(((AdapterContextMenuInfo) item.getMenuInfo()).position);
			//删除此联系人
			deleteContact(tobeDeleteUser);
			//删除相关的邀请消息
			InviteMessgeDao dao = new InviteMessgeDao(getActivity());
			dao.deleteMessage(tobeDeleteUser.getUsername());
			return true;
		}
		return super.onContextItemSelected(item);
	}
	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if(!hidden){
			refresh();
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(!hidden){
			refresh();
		}
	}
	
	/**
	 * 删除联系人
	 * @param toDeleteUser
	 */
	public void deleteContact(final User tobeDeleteUser){
		final ProgressDialog pd = new ProgressDialog(getActivity());
		pd.setMessage("正在删除...");
		pd.setCanceledOnTouchOutside(false);
		pd.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						EMContactManager.getInstance().deleteContact(tobeDeleteUser.getUsername());
						//删除db和内存中此用户的数据
						UserDao dao = new UserDao(getActivity());
						dao.deleteContact(tobeDeleteUser.getUsername());
						DemoApplication.getInstance().getContactList().remove(tobeDeleteUser.getUsername());
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								pd.dismiss();
								adapter.remove(tobeDeleteUser);
								adapter.notifyDataSetChanged();
								
							}
						});
					} catch (final Exception e) {
						getActivity().runOnUiThread(new Runnable() {
							public void run() {
								pd.dismiss();
								Toast.makeText(getActivity(), "删除失败: " + e.getMessage(), Toast.LENGTH_SHORT).show();
							}
						});
						
					}
					
				}
			}).start();
			
	}
	
	//刷新ui
	public void refresh(){
		try {
			//可能会在子线程中调到这方法
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					getContactList();
					adapter.notifyDataSetChanged();
					
				}
			});
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void getContactList(){
		contactList.clear();
		Map<String, User> users = DemoApplication.getInstance().getContactList();
		Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, User> entry = iterator.next();
			if(!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(Constant.GROUP_USERNAME))
				contactList.add(entry.getValue());
		}
		//排序
		Collections.sort(contactList, new Comparator<User>() {

			@Override
			public int compare(User lhs, User rhs) {
				return lhs.getUsername().compareTo(rhs.getUsername());
			}
		});
		
		//加入"新的朋友"和"群聊"
		contactList.add(0,users.get(Constant.GROUP_USERNAME));
		//把"新的朋友"添加到首位
		contactList.add(0,users.get(Constant.NEW_FRIENDS_USERNAME));
	}
}

