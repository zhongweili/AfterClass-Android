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
import im.afterclass.android.activity.GroupsActivity;
import im.afterclass.android.activity.LoginActivity;
import im.afterclass.android.db.InviteMessgeDao;
import im.afterclass.android.db.UserDao;
import im.afterclass.android.domain.InviteMessage;
import im.afterclass.android.domain.User;
import im.afterclass.android.domain.InviteMessage.InviteMesageStatus;
import im.afterclass.android.fragment.ChatHistoryFragment;
import im.afterclass.android.fragment.ContactlistFragment;
import im.afterclass.android.utils.CommonUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.TextView;

import com.easemob.chat.ConnectionListener;
import com.easemob.chat.EMChat;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactListener;
import com.easemob.chat.EMContactManager;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chat.EMMessage.Type;
import com.easemob.chat.EMNotifier;
import com.easemob.chat.GroupChangeListener;
import com.easemob.chat.TextMessageBody;
import im.afterclass.android.R;
import com.easemob.util.HanziToPinyin;

public class CommunicationFragment extends Fragment implements OnClickListener, AnimationListener{

	protected static final String TAG = "MainActivity";
	public static Activity main;
	// 未读消息textview
	private TextView unreadLabel;
	// 未读通讯录textview
	private TextView unreadAddressLable;

	private Button[] mTabs;
	private ViewPager pager;
	private FragmentPagerAdapter adapter;
	private SimpleOnPageChangeListener changeListener;
	private ContactlistFragment contactListFragment;
	private ChatHistoryFragment chatHistoryFragment;
	private Fragment[] fragments;
	private int index;
	// 当前fragment的index
	private int currentTabIndex;
	private NewMessageBroadcastReceiver msgReceiver;
	// 账号在别处登录
	private boolean isConflict = false;

	private TextView mSearchText;
    private int mSortMode = -1;
    private int width;
    private int height;
    
    Animation animPushbottomin;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){
		return inflater.inflate(R.layout.fragment_communication, container, false);
	}
    
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		
		//mSearchText = new TextView(this);
		//setContentView(mSearchText);
		//getActivity().setTheme(android.R.style.Theme_WithActionBar);
		//setContentView(R.layout.activity_main);
	
		main = getActivity();
	
		chatHistoryFragment = new ChatHistoryFragment();
		contactListFragment = new ContactlistFragment();
		fragments = new Fragment[]{chatHistoryFragment,contactListFragment};
		
		initView();		
        
		inviteMessgeDao = new InviteMessgeDao(getActivity());
		userDao = new UserDao(getActivity());
		// 添加显示第一个fragment
		/*getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, chatHistoryFragment)
				.add(R.id.fragment_container, contactListFragment).hide(contactListFragment).show(chatHistoryFragment).commit();*/

		// 注册一个接收消息的BroadcastReceiver
		msgReceiver = new NewMessageBroadcastReceiver();
		IntentFilter intentFilter = new IntentFilter(EMChatManager.getInstance().getNewMessageBroadcastAction());
		intentFilter.setPriority(3);
		getActivity().registerReceiver(msgReceiver, intentFilter);

		// 注册一个ack回执消息的BroadcastReceiver
		IntentFilter ackMessageIntentFilter = new IntentFilter(EMChatManager.getInstance().getAckMessageBroadcastAction());
		ackMessageIntentFilter.setPriority(3);
		getActivity().registerReceiver(ackMessageReceiver, ackMessageIntentFilter);


		// setContactListener监听联系人的变化等
		EMContactManager.getInstance().setContactListener(new MyContactListener());
		// 注册一个监听连接状态的listener
		EMChatManager.getInstance().addConnectionListener(new MyConnectionListener());
		// 注册群聊相关的listener
		EMGroupManager.getInstance().addGroupChangeListener(new MyGroupChangeListener());
		// 通知sdk，UI 已经初始化完毕，注册了相应的receiver和listener, 可以接受broadcast了
		EMChat.getInstance().setAppInited();
	}

	/**
	 * 初始化组件
	 */
	private void initView() {
		unreadLabel = (TextView) getView().findViewById(R.id.unread_msg_number);
		unreadAddressLable = (TextView) getView().findViewById(R.id.unread_address_number);
		pager = (ViewPager) getView().findViewById(R.id.viewPager);
		adapter = new FragmentPagerAdapter(
				getActivity().getSupportFragmentManager()) {

			public int getCount() {
				return fragments.length;
			}

			public Fragment getItem(int position) {
				return fragments[position];
			}
		};
		changeListener=new SimpleOnPageChangeListener(){
			public void onPageSelected(int position) {
				mTabs[currentTabIndex].setSelected(false);
				mTabs[position].setSelected(true);
				currentTabIndex = position;
			}
		};
		pager.setAdapter(adapter);
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(changeListener);
		mTabs = new Button[2];
		mTabs[0] = (Button) getView().findViewById(R.id.btn_conversation);
		mTabs[1] = (Button) getView().findViewById(R.id.btn_address_list);
		mTabs[0].setOnClickListener(this);
		mTabs[1].setOnClickListener(this);
		// 把第一个tab设为选中状态
		mTabs[0].setSelected(true);

		 // load the animation
        animPushbottomin = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),
                R.anim.push_bottom_in);
         
        // set animation listener
        animPushbottomin.setAnimationListener(this);
	}
	

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
 
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }

    public void onSort(MenuItem item) {
        mSortMode = item.getItemId();
        Intent intent;
        switch (mSortMode) {
        	case R.id.action_add_person:
        		intent = new Intent(getActivity(),AddFriendActivity.class);
    			startActivity(intent);
    			getActivity().overridePendingTransition(R.anim.slide_in_from_left,0);
    			break;
        	case R.id.action_add_group:
        		intent = new Intent(getActivity(),AddFriendActivity.class);
    			startActivity(intent);
    			break;
        	case R.id.action_sort_scan:
        		intent = new Intent(getActivity(),AddFriendActivity.class);
    			startActivity(intent);
    			break;
        	default:
        		// Request a call to onPrepareOptionsMenu so we can change the sort icon
        		getActivity().invalidateOptionsMenu();
        }
        
        
    }

    public boolean onQueryTextChange(String newText) {
        newText = newText.isEmpty() ? "" : "Query so far: " + newText;
        mSearchText.setText(newText);
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        Toast.makeText(getActivity(), "Searching for: " + query + "...", Toast.LENGTH_SHORT).show();
        return true;
    }*/
	
	/*private void showTopPopupWindow(View v){
		if(!isOpen){
		LayoutInflater inflater = LayoutInflater.from(this);
		View view = inflater.inflate(R.layout.vertical_popupwindow, null);
		ListView list = (ListView) view.findViewById(R.id.morelist);
		list.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_expandable_list_item_1, getData()));
		mPopupWindow = new PopupWindow(view);
		list.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
					long arg3) {
				// TODO Auto-generated method stub
				switch(arg2){
				case 0:
					break;
				case 1:
					break;
				case 2:
					Intent intent = new Intent(MainActivity.this,SettingsActivity.class);
					startActivity(intent);
					break;
				}
				mPopupWindow.dismiss();
			}
		});
		list.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
		mPopupWindow.setWidth(list.getMeasuredWidth());
		mPopupWindow.setHeight((list.getMeasuredHeight()+20)*3);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.showAsDropDown(v);
		}else{
			mPopupWindow.dismiss();
		}
	}*/
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.btn_conversation:
			index = 0;
			pager.setCurrentItem(0);
			break;
		case R.id.btn_address_list:
			index = 1;
			pager.setCurrentItem(1);
			break;
		}
		/*if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}*/
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	/**
	 * button点击事件
	 * 
	 * @param view
	 */
	public void onTabClicked(View view) {
		
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		// 注销广播接收者
		try {
			getActivity().unregisterReceiver(msgReceiver);
		} catch (Exception e) {
		}
		try {
			getActivity().unregisterReceiver(ackMessageReceiver);
		} catch (Exception e) {
		}

		if (conflictBuilder != null) {
			conflictBuilder.create().dismiss();
			conflictBuilder = null;
		}

	}

	/**
	 * 刷新未读消息数
	 */
	public void updateUnreadLabel() {
		int count = getUnreadMsgCountTotal();
		if (count > 0) {
			unreadLabel.setText(String.valueOf(count));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}
	}

	/**
	 * 刷新新的朋友消息数
	 */
	public void updateUnreadAddressLable() {
		getActivity().runOnUiThread(new Runnable() {
			public void run() {
				int count = getUnreadAddressCountTotal();
				if (count > 0) {
					unreadAddressLable.setText(String.valueOf(count));
					unreadAddressLable.setVisibility(View.VISIBLE);
				} else {
					unreadAddressLable.setVisibility(View.INVISIBLE);
				}
			}
		});
		

	}

	/**
	 * 获取未读新的朋友消息
	 * 
	 * @return
	 */
	public int getUnreadAddressCountTotal() {
		int unreadAddressCountTotal = 0;
		if (DemoApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME) != null)
			unreadAddressCountTotal = DemoApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME).getUnreadMsgCount();
		return unreadAddressCountTotal;
	}

	/**
	 * 获取未读消息数
	 * 
	 * @return
	 */
	public int getUnreadMsgCountTotal() {
		int unreadMsgCountTotal = 0;
		unreadMsgCountTotal = EMChatManager.getInstance().getUnreadMsgsCount();
		return unreadMsgCountTotal;
	}

	/**
	 * 新消息广播接收者
	 * 
	 * 
	 */
	private class NewMessageBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			// 消息id
			String msgId = intent.getStringExtra("msgid");
			// 收到这个广播的时候，message已经在db和内存里了，可以通过id获取mesage对象
			// EMMessage message =
			// EMChatManager.getInstance().getMessage(msgId);

			// 刷新bottom bar消息未读数
			updateUnreadLabel();
			if (currentTabIndex == 0) {
				// 当前页面如果为聊天历史页面，刷新此页面
				if (chatHistoryFragment != null) {
					chatHistoryFragment.refresh();
				}
			}
			// 注销广播，否则在ChatActivity中会收到这个广播
			abortBroadcast();
		}
	}

	/**
	 * 消息回执BroadcastReceiver
	 */
	private BroadcastReceiver ackMessageReceiver = new BroadcastReceiver() {

		@Override
		public void onReceive(Context context, Intent intent) {
			String msgid = intent.getStringExtra("msgid");
			String from = intent.getStringExtra("from");
			EMConversation conversation = EMChatManager.getInstance().getConversation(from);
			if (conversation != null) {
				// 把message设为已读
				EMMessage msg = conversation.getMessage(msgid);
				if (msg != null) {
					msg.isAcked = true;
				}
			}
			abortBroadcast();
		}
	};

	private InviteMessgeDao inviteMessgeDao;
	private UserDao userDao;

	/***
	 * 联系人变化listener
	 * 
	 */
	private class MyContactListener implements EMContactListener {

		@Override
		public void onContactAdded(List<String> usernameList) {
			// 保存增加的联系人
			Map<String, User> localUsers = DemoApplication.getInstance().getContactList();
			Map<String, User> toAddUsers = new HashMap<String, User>();
			for (String username : usernameList) {
				User user = new User();
				user.setUsername(username);
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
				// 暂时有个bug，添加好友时可能会回调added方法两次
				if (!localUsers.containsKey(username)) {
					userDao.saveContact(user);
				}
				toAddUsers.put(username, user);
			}
			localUsers.putAll(toAddUsers);
			// 刷新ui
			if (currentTabIndex == 1)
				contactListFragment.refresh();

		}

		@Override
		public void onContactDeleted(List<String> usernameList) {
			// 被删除
			Map<String, User> localUsers = DemoApplication.getInstance().getContactList();
			for (String username : usernameList) {
				localUsers.remove(username);
				userDao.deleteContact(username);
				inviteMessgeDao.deleteMessage(username);
			}
			// 刷新ui
			if (currentTabIndex == 1)
				contactListFragment.refresh();
			updateUnreadLabel();

		}

		@Override
		public void onContactInvited(String username, String reason) {
			// 接到邀请的消息，如果不处理(同意或拒绝)，掉线后，服务器会自动再发过来，所以客户端不要重复提醒
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			msg.setReason(reason);
			Log.d(TAG, username + "请求加你为好友,reason: " + reason);
			// 设置相应status
			msg.setStatus(InviteMesageStatus.BEINVITEED);
			notifyNewIviteMessage(msg);

		}

		

		@Override
		public void onContactAgreed(String username) {
			List<InviteMessage> msgs = inviteMessgeDao.getMessagesList();
			for (InviteMessage inviteMessage : msgs) {
				if (inviteMessage.getFrom().equals(username)) {
					return;
				}
			}
			// 自己封装的javabean
			InviteMessage msg = new InviteMessage();
			msg.setFrom(username);
			msg.setTime(System.currentTimeMillis());
			Log.d(TAG, username + "同意了你的好友请求" );
			msg.setStatus(InviteMesageStatus.BEAGREED);
			notifyNewIviteMessage(msg);

		}

		@Override
		public void onContactRefused(String username) {
			//参考同意，被邀请实现此功能,demo未实现

		}

	}
	
	protected void notifyNewIviteMessage(InviteMessage msg) {
		// 保存msg
		inviteMessgeDao.saveMessage(msg);
		// 未读数加1
		User user = DemoApplication.getInstance().getContactList().get(Constant.NEW_FRIENDS_USERNAME);
		user.setUnreadMsgCount(user.getUnreadMsgCount() + 1);
		// 提示有新消息
		EMNotifier.getInstance(getActivity().getApplicationContext()).notifyOnNewMsg();

		// 刷新bottom bar消息未读数
		updateUnreadAddressLable();
		// 刷新好友页面ui
		if (currentTabIndex == 1)
			contactListFragment.refresh();
	}

	/**
	 * 连接监听listener
	 *
	 */
	private class MyConnectionListener implements ConnectionListener {

		@Override
		public void onConnected() {
			Log.i("Connect!!!", "Connect Successfully!!!");
			//chatHistoryFragment.errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onDisConnected(String errorString) {
			if (errorString != null && errorString.contains("conflict")) {
				// 显示帐号在其他设备登陆dialog
				showConflictDialog();
			} else {
				chatHistoryFragment.errorItem.setVisibility(View.VISIBLE);
				chatHistoryFragment.errorText.setText("连接不到聊天服务器");
			}
		}

		@Override
		public void onReConnected() {
			chatHistoryFragment.errorItem.setVisibility(View.GONE);
		}

		@Override
		public void onReConnecting() {
		}

		@Override
		public void onConnecting(String progress) {
		}

	}

	/**
	 * MyGroupChangeListener
	 */
	private class MyGroupChangeListener implements GroupChangeListener {

		@Override
		public void onInvitationReceived(String groupId, String groupName, String inviter, String reason) {
			// 被邀请
			EMMessage msg = EMMessage.createReceiveMessage(Type.TXT);
			msg.setChatType(ChatType.GroupChat);
			msg.setFrom(inviter);
			msg.setTo(groupId);
			msg.setMsgId(UUID.randomUUID().toString());
			msg.addBody(new TextMessageBody(inviter + "邀请你加入了群聊"));
			// 保存邀请消息
			EMChatManager.getInstance().saveMessage(msg);
			// 提醒新消息
			EMNotifier.getInstance(getActivity().getApplicationContext()).notifyOnNewMsg();

			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					// 刷新ui
					if (currentTabIndex == 0)
						chatHistoryFragment.refresh();
					if (CommonUtils.getTopActivity(getActivity()).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});

		}

		@Override
		public void onInvitationAccpted(String groupId, String inviter, String reason) {

		}

		@Override
		public void onInvitationDeclined(String groupId, String invitee, String reason) {

		}

		@Override
		public void onUserRemoved(String groupId, String groupName) {
			// 提示用户被T了，demo省略此步骤
			// 刷新ui
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					try {
						updateUnreadLabel();
						if (currentTabIndex == 0)
							chatHistoryFragment.refresh();
						if (CommonUtils.getTopActivity(getActivity()).equals(GroupsActivity.class.getName())) {
							GroupsActivity.instance.onResume();
						}
					} catch (Exception e) {
						Log.e("###", "refresh exception " + e.getMessage());
					}

				}
			});
		}

		@Override
		public void onGroupDestroy(String groupId, String groupName) {
			// 群被解散
			// 提示用户群被解散,demo省略
			// 刷新ui
			getActivity().runOnUiThread(new Runnable() {
				public void run() {
					updateUnreadLabel();
					if (currentTabIndex == 0)
						chatHistoryFragment.refresh();
					if (CommonUtils.getTopActivity(getActivity()).equals(GroupsActivity.class.getName())) {
						GroupsActivity.instance.onResume();
					}
				}
			});

		}

	}

	@Override
	public void onResume() {
		super.onResume();
		if (!isConflict) {
			updateUnreadLabel();
			updateUnreadAddressLable();
		}

	}
	
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		 if (keyCode == KeyEvent.KEYCODE_BACK) {  
			 moveTaskToBack(false);  
	         return true;  
	     }  
	     return super.onKeyDown(keyCode, event);  
	}*/

	private android.app.AlertDialog.Builder conflictBuilder;

	/**
	 * 显示帐号在别处登录dialog
	 */
	private void showConflictDialog() {

		DemoApplication.getInstance().logout();
		
		if (!getActivity().isFinishing()) {
			// clear up global variables
			try {
				if (conflictBuilder == null)
					conflictBuilder = new android.app.AlertDialog.Builder(getActivity());
				conflictBuilder.setTitle("下线通知");
				conflictBuilder.setMessage(R.string.connect_conflict);
				conflictBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						conflictBuilder = null;
						getActivity().finish();
						startActivity(new Intent(getActivity(), LoginActivity.class));
					}
				});
				conflictBuilder.setCancelable(false);
				conflictBuilder.create().show();
				isConflict = true;
			} catch (Exception e) {
				Log.e("###", "---------color conflictBuilder error" + e.getMessage());
			}

		}

	}
	

	@Override
    public void onAnimationEnd(Animation animation) {
        // Take any action after completing the animation
 
        // check for fade in animation
        if (animation == animPushbottomin) {
   //         Toast.makeText(getApplicationContext(), "Animation Stopped",
    //                Toast.LENGTH_SHORT).show();
        }
 
    }
 
    @Override
    public void onAnimationRepeat(Animation animation) {
        // TODO Auto-generated method stub
 
    }
 
    @Override
    public void onAnimationStart(Animation animation) {
        // TODO Auto-generated method stub
 
    }

	
	/*class  ItemClickListener implements OnItemClickListener  {  
		public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened   
	                                  View arg1,//The view within the AdapterView that was clicked  
	                                  int arg2,//The position of the view in the adapter  
	                                  long arg3//The row id of the item that was clicked  
	                                  ) {  
	    //arg2=arg3  
	    HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);  
	    //显示所选Item的ItemText  
	    
	    SharedPreferences.Editor editor = sp.edit();
	    String themelist = sp.getString("themelist", "自定义*吃*桌游*聚会*运动*游泳*排球*滑冰*自习*网吧*咖啡*酒吧*电影*KTV*逛街*会议*公园*演唱会*话剧");
		themearray = themelist.split(Pattern.quote("*"));
	//    editor.putInt("theme_type", arg2);
	    editor.putString("theme_type", themearray[arg2]);
	    editor.commit();
	    getActivity().setTitle((String)item.get("ItemText"));  
	    Intent intent = new Intent(getActivity(),CreateActivity.class);
		mPopupWindow.dismiss();
		startActivity(intent);
		getActivity().finish();
		getActivity().overridePendingTransition(R.anim.head_in,R.anim.head_out);
		}
	}*/
	
}
