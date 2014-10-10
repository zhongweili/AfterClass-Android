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

import im.afterclass.android.DemoApplication;
import im.afterclass.android.activity.ChatActivity;
import im.afterclass.android.activity.CreateActivity;
import im.afterclass.android.activity.MainActivity;

import im.afterclass.android.db.InviteMessgeDao;
import im.afterclass.android.domain.User;
import im.afterclass.android.utils.SmileUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.EditText;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView.BufferType;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContact;
import com.easemob.chat.EMConversation;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.ImageMessageBody;
import com.easemob.chat.TextMessageBody;
import com.easemob.util.DateUtils;
import com.easemob.util.EasyUtils;

import im.afterclass.android.R;

/**
 * 聊天记录Fragment
 * 
 */
public class ChatHistoryFragment extends Fragment implements OnClickListener,
		OnLongClickListener, AnimationListener {

	// private InputMethodManager inputMethodManager;
	// private ListView listView;
	private Map<String, User> contactList;
	// private ChatHistoryAdapter adapter;

	public RelativeLayout errorItem;
	public TextView errorText;
	private boolean hidden;
	private List<EMContact> historyList;
	private List<Integer> position;
	private LinearLayout leftLayout;
	private LinearLayout rightLayout;
	private View commonChat;
	private View groupChat;
	private LayoutInflater inflater;
	private int leftHeight;
	private int rightHeight;
	private int screenWidth;
	private LayoutParams commonParams;
	private LayoutParams groupParams;
	private int id;

	private PopupWindow mPopupWindow;
	private Animation animSlideLeftin;
	private Animation animSlideRightin;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		Log.i("chatHistoryFragment", "OnCreateView");
		return inflater.inflate(R.layout.fragment_conversation_history,
				container, false);

	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		Log.i("chatHistoryFragment", "OnActivityCreated");
		super.onActivityCreated(savedInstanceState);
		// inputMethodManager = (InputMethodManager)
		// getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
		errorItem = (RelativeLayout) getView().findViewById(R.id.rl_error_item);
		errorText = (TextView) errorItem.findViewById(R.id.tv_connect_errormsg);
		// contact list
		contactList = DemoApplication.getInstance().getContactList();

		initViews();

	}

	private void initViews() {
		inflater = LayoutInflater.from(getActivity());
		historyList = loadUsersWithRecentChat();
		position = new ArrayList<Integer>(historyList.size());
		leftLayout = (LinearLayout) getView().findViewById(R.id.left_layout);
		rightLayout = (LinearLayout) getView().findViewById(R.id.right_layout);
		leftHeight = rightHeight = 0;

		screenWidth = getScreenWidth();
		commonParams = new LayoutParams((screenWidth - 32) / 2,
				(screenWidth - 30) / 4);
		commonParams.topMargin = 10;
		groupParams = new LayoutParams((screenWidth - 32) / 2,
				(screenWidth - 30) / 2);
		groupParams.topMargin = 10;
		addViewToLayout();

		animSlideLeftin = AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), R.anim.slide_in_from_left);
		animSlideLeftin.setAnimationListener(this);
		animSlideRightin = AnimationUtils.loadAnimation(getActivity()
				.getApplicationContext(), R.anim.slide_in_from_right);
		animSlideRightin.setAnimationListener(this);

	}

	private int getScreenWidth() {
		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		return dm.widthPixels;
	}

	private void addViewToLayout() {
		for (int i = 0; i < historyList.size(); i++) {
			if (leftHeight <= rightHeight) {
				if (historyList.get(i) instanceof EMGroup) {
					leftHeight += 2;
					initGroupChat();
					setGroupChat(historyList.get(i), i);
					leftLayout.addView(groupChat, groupParams);
				} else {
					leftHeight += 1;
					initCommonChat();
					setCommonChat(historyList.get(i), i);
					leftLayout.addView(commonChat, commonParams);

				}
				position.add(i, 0);
			} else {
				if (historyList.get(i) instanceof EMGroup) {
					rightHeight += 2;
					initGroupChat();
					setGroupChat(historyList.get(i), i);
					rightLayout.addView(groupChat, groupParams);
				} else {
					rightHeight += 1;
					initCommonChat();
					setCommonChat(historyList.get(i), i);
					rightLayout.addView(commonChat, commonParams);

				}
				position.add(i, 1);
			}
			System.out.println(position.get(i));
		}
	}

	/** 和谁的聊天记录 */
	TextView name;
	/** 消息未读数 */
	TextView unreadLabel;
	/** 最后一条消息的内容 */
	TextView message;
	/** 最后一条消息的时间 */
	TextView time;
	/** 用户头像 */
	ImageView avatar;
	TextView userName;
	TextView groupName;
	TextView groupUnreadLabel;
	TextView groupMessage;
	TextView groupTime;
	ImageView groupAvatar;
	TextView userName2;
	TextView groupMessage2;
	TextView userName3;
	TextView groupMessage3;
	TextView userName4;
	TextView groupMessage4;

	private void initCommonChat() {
		commonChat = inflater.inflate(R.layout.common_chat, null, false);
		name = (TextView) commonChat.findViewById(R.id.name);
		unreadLabel = (TextView) commonChat
				.findViewById(R.id.unread_msg_number);
		message = (TextView) commonChat.findViewById(R.id.message);
		time = (TextView) commonChat.findViewById(R.id.time);
		avatar = (ImageView) commonChat.findViewById(R.id.avatar);
		commonChat.setOnClickListener(this);
		commonChat.setOnLongClickListener(this);
	}

	private void setCommonChat(EMContact user, final int id) {
		commonChat.setId(id);
		// 群聊消息，显示群聊头像
		avatar.setImageResource(R.drawable.default_avatar);
		avatar.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				EMContact emContact = historyList.get(id);
				Intent intent = new Intent(getActivity(), ChatActivity.class);
				intent.putExtra("userId", emContact.getUsername());
				startActivity(intent);

			}
		});

		String username = user.getUsername();
		// 获取与此用户/群组的会话
		EMConversation conversation = EMChatManager.getInstance()
				.getConversation(username);
		name.setText(user.getNick() != null ? user.getNick() : username);
		if (conversation.getUnreadMsgCount() > 0) {
			// 显示与此用户的消息未读数
			unreadLabel
					.setText(String.valueOf(conversation.getUnreadMsgCount()));
			unreadLabel.setVisibility(View.VISIBLE);
		} else {
			unreadLabel.setVisibility(View.INVISIBLE);
		}

		if (conversation.getMsgCount() != 0) {
			// 把最后一条消息的内容作为item的message内容
			EMMessage lastMessage = conversation.getLastMessage();
			message.setText(SmileUtils.getSmiledText(getActivity(),
					getMessageDigest(lastMessage, (this.getActivity()))),
					BufferType.SPANNABLE);

			time.setText(DateUtils.getTimestampString(new Date(lastMessage
					.getMsgTime())));
		}
	}

	private void initGroupChat() {
		groupChat = inflater.inflate(R.layout.group_chat, null, false);
		groupName = (TextView) groupChat.findViewById(R.id.name);
		groupUnreadLabel = (TextView) groupChat
				.findViewById(R.id.unread_msg_number);
		groupMessage = (TextView) groupChat.findViewById(R.id.message);
		groupTime = (TextView) groupChat.findViewById(R.id.time);
		groupAvatar = (ImageView) groupChat.findViewById(R.id.avatar);
		userName = (TextView) groupChat.findViewById(R.id.username);
		userName2 = (TextView) groupChat.findViewById(R.id.username2);
		groupMessage2 = (TextView) groupChat.findViewById(R.id.message2);
		userName3 = (TextView) groupChat.findViewById(R.id.username3);
		groupMessage3 = (TextView) groupChat.findViewById(R.id.message3);
		userName4 = (TextView) groupChat.findViewById(R.id.username4);
		groupMessage4 = (TextView) groupChat.findViewById(R.id.message4);
		groupChat.setOnClickListener(this);
		groupChat.setOnLongClickListener(this);
	}

	private void setGroupChat(EMContact user, int id) {
		groupChat.setId(id);
		// 群聊消息，显示群聊头像
		groupAvatar.setImageResource(R.drawable.group_icon);

		String username = user.getUsername();
		// 获取与此用户/群组的会话
		EMConversation conversation = EMChatManager.getInstance()
				.getConversation(username);
		groupName.setText(user.getNick() != null ? user.getNick() : username);
		if (conversation.getUnreadMsgCount() > 0) {
			// 显示与此用户的消息未读数
			groupUnreadLabel.setText(String.valueOf(conversation
					.getUnreadMsgCount()));
			groupUnreadLabel.setVisibility(View.VISIBLE);
		} else {
			groupUnreadLabel.setVisibility(View.INVISIBLE);
		}

		int count = conversation.getMsgCount();
		if (count != 0) {
			// 把最后一条消息的内容作为item的message内容
			EMMessage lastMessage = conversation.getLastMessage();
			userName.setText(lastMessage.getFrom() + ":");
			groupMessage.setText(SmileUtils.getSmiledText(getActivity(),
					getMessageDigest(lastMessage, (this.getActivity()))),
					BufferType.SPANNABLE);

			groupTime.setText(DateUtils.getTimestampString(new Date(lastMessage
					.getMsgTime())));
		}
		if (count >= 2) {
			EMMessage message = conversation.getMessage(count - 2);
			userName2.setText(message.getFrom() + ":");
			groupMessage2.setText(
					SmileUtils.getSmiledText(getActivity(),
							getMessageDigest(message, (this.getActivity()))),
					BufferType.SPANNABLE);
		}
		if (count >= 3) {
			EMMessage message = conversation.getMessage(count - 3);
			userName3.setText(message.getFrom() + ":");
			groupMessage3.setText(
					SmileUtils.getSmiledText(getActivity(),
							getMessageDigest(message, (this.getActivity()))),
					BufferType.SPANNABLE);
		}
		if (count >= 4) {
			EMMessage message = conversation.getMessage(count - 4);
			userName4.setText(message.getFrom() + ":");
			groupMessage4.setText(
					SmileUtils.getSmiledText(getActivity(),
							getMessageDigest(message, (this.getActivity()))),
					BufferType.SPANNABLE);
		}
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		EMContact emContact = historyList.get(v.getId());
		if (emContact.getUsername().equals(
				DemoApplication.getInstance().getUserName()))
			Toast.makeText(getActivity(), "不能和自己聊天", 0).show();
		else {
			// 进入聊天页面
			Intent intent = new Intent(getActivity(), ChatActivity.class);
			if (emContact instanceof EMGroup) {
				// it is group chat
				intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
				intent.putExtra("groupId", ((EMGroup) emContact).getGroupId());
				startActivity(intent);
			} else {
				// it is single chat
				// intent.putExtra("userId", emContact.getUsername());
				// startActivity(intent);
				if (position.get(v.getId()) == 0)
					showRightPopupWindow(v);
				if (position.get(v.getId()) == 1)
					showLeftPopupWindow(v);
			}

		}
	}

	private void setListView(View v) {
//		LayoutInflater inflater = LayoutInflater.from(getActivity());
//		View view = inflater.inflate(R.layout.half_popupwindow, null);

		String[] themes = new String[] { "运动", "聚餐", "自定义" };
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < themes.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("theme", themes[i]);
			listItems.add(listItem);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),
				listItems, R.layout.theme_simple_item,
				new String[] { "theme" }, new int[] { R.id.theme });
		ListView themelist = (ListView) inflater.inflate(R.layout.half_popupwindow, null).findViewById(R.id.themeListView);
		themelist.setAdapter(simpleAdapter);
		themelist.setOnItemClickListener(new ItemClickListener());
	}
	
	private void showRightPopupWindow(View v) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View parent = getActivity().findViewById(R.id.main_button);
		View view = inflater.inflate(R.layout.half_popupwindow, null);

		String[] themes = new String[] { "运动", "聚餐", "自定义" };
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < themes.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("theme", themes[i]);
			listItems.add(listItem);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),
				listItems, R.layout.theme_simple_item,
				new String[] { "theme" }, new int[] { R.id.theme });
		ListView themelist = (ListView) view.findViewById(R.id.themeListView);
		themelist.setAdapter(simpleAdapter);
		themelist.setOnItemClickListener(new ItemClickListener());
		
		mPopupWindow = new PopupWindow(view);
		view.startAnimation(animSlideRightin);

		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenHeight = dm.heightPixels;
		int[] location = new int[2];
		parent.getLocationInWindow(location);
		Rect frame = new Rect();
		getActivity().getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int width = frame.width();
		mPopupWindow.setHeight(location[1]
				- getActivity().getActionBar().getHeight() - statusBarHeight);
		mPopupWindow.setWidth(width / 2);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.showAtLocation(getActivity()
				.findViewById(R.id.main_bottom),
				Gravity.BOTTOM | Gravity.RIGHT, 0, screenHeight - location[1]);

	}

	private void showLeftPopupWindow(View v) {
		LayoutInflater inflater = LayoutInflater.from(getActivity());
		View parent = getActivity().findViewById(R.id.main_button);
		View view = inflater.inflate(R.layout.half_popupwindow, null);

		String[] themes = new String[] { "运动", "聚餐", "自定义" };
		List<Map<String, Object>> listItems = new ArrayList<Map<String, Object>>();
		for (int i = 0; i < themes.length; i++) {
			Map<String, Object> listItem = new HashMap<String, Object>();
			listItem.put("theme", themes[i]);
			listItems.add(listItem);
		}
		SimpleAdapter simpleAdapter = new SimpleAdapter(getActivity(),
				listItems, R.layout.theme_simple_item,
				new String[] { "theme" }, new int[] { R.id.theme });
		ListView themelist = (ListView) view.findViewById(R.id.themeListView);
		themelist.setAdapter(simpleAdapter);
		themelist.setOnItemClickListener(new ItemClickListener());

		mPopupWindow = new PopupWindow(view);
		view.startAnimation(animSlideLeftin);

		DisplayMetrics dm = new DisplayMetrics();
		getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenHeight = dm.heightPixels;
		int[] location = new int[2];
		parent.getLocationInWindow(location);
		Rect frame = new Rect();
		getActivity().getWindow().getDecorView()
				.getWindowVisibleDisplayFrame(frame);
		int statusBarHeight = frame.top;
		int width = frame.width();
		mPopupWindow.setHeight(location[1]
				- getActivity().getActionBar().getHeight() - statusBarHeight);
		mPopupWindow.setWidth(width / 2);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new BitmapDrawable());
		mPopupWindow.showAtLocation(getActivity()
				.findViewById(R.id.main_bottom), Gravity.BOTTOM | Gravity.LEFT,
				0, screenHeight - location[1]);

	}
	

	class  ItemClickListener implements OnItemClickListener  {  
		public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened   
	                                  View arg1,//The view within the AdapterView that was clicked  
	                                  int arg2,//The position of the view in the adapter  
	                                  long arg3//The row id of the item that was clicked  
	                                  ) {  
			mPopupWindow.dismiss();
			LayoutInflater inflater = LayoutInflater.from(getActivity());
			final View view = inflater.inflate(R.layout.p2pdialog, null); 
			new AlertDialog.Builder(getActivity()).setTitle("").setView(view).setPositiveButton("发送", new DialogInterface.OnClickListener() {  
			    @Override  
			    public void onClick(DialogInterface dialog, int which) {  
			        // TODO Auto-generated method stub  
			        EditText editText = (EditText) view.findViewById(R.id.edit_text);  
			        Toast.makeText(getActivity(), "发送成功", Toast.LENGTH_SHORT).show();  
			    }  
			}).show();
		
		}
	}

	@Override
	public boolean onLongClick(View v) {
		// TODO Auto-generated method stub
		id = v.getId();
		registerForContextMenu(v);
		return false;
	}

	/**
	 * 根据消息内容和消息类型获取消息内容提示
	 * 
	 * @param message
	 * @param context
	 * @return
	 */
	private String getMessageDigest(EMMessage message, Context context) {
		String digest = "";
		switch (message.getType()) {
		case LOCATION: // 位置消息
			if (message.direct == EMMessage.Direct.RECEIVE) {
				digest = EasyUtils.getAppResourceString(context,
						"location_recv");
				digest = String.format(digest, message.getFrom());
				return digest;
			} else {
				digest = EasyUtils.getAppResourceString(context,
						"location_prefix");
			}
			break;
		case IMAGE: // 图片消息
			ImageMessageBody imageBody = (ImageMessageBody) message.getBody();
			digest = EasyUtils.getAppResourceString(context, "picture")
					+ imageBody.getFileName();
			break;
		case VOICE:// 语音消息
			digest = EasyUtils.getAppResourceString(context, "voice");
			break;
		case VIDEO: // 视频消息
			digest = EasyUtils.getAppResourceString(context, "video");
			break;
		case TXT: // 文本消息
			TextMessageBody txtBody = (TextMessageBody) message.getBody();
			digest = txtBody.getMessage();
			break;
		default:
			System.err.println("error, unknow type");
			return "";
		}

		return digest;
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v,
			ContextMenuInfo menuInfo) {
		super.onCreateContextMenu(menu, v, menuInfo);
		// if(((AdapterContextMenuInfo)menuInfo).position > 0){ m,
		getActivity().getMenuInflater().inflate(R.menu.delete_message, menu);
		// }
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		if (item.getItemId() == R.id.delete_message) {
			EMContact tobeDeleteUser = historyList.get(id);
			// 删除此会话
			EMChatManager.getInstance().deleteConversation(
					tobeDeleteUser.getUsername());
			InviteMessgeDao inviteMessgeDao = new InviteMessgeDao(getActivity());
			inviteMessgeDao.deleteMessage(tobeDeleteUser.getUsername());
			historyList.remove(tobeDeleteUser);
			removeViewFromLayout();
			addViewToLayout();

			// 更新消息未读数
			// ((MainActivity) getActivity()).updateUnreadLabel();

			return true;
		}
		return super.onContextItemSelected(item);
	}

	/**
	 * 刷新页面
	 */
	public void refresh() {
		// adapter = new ChatHistoryAdapter(getActivity(),
		// R.layout.row_chat_history, loadUsersWithRecentChat());
		// listView.setAdapter(adapter);
		// adapter.notifyDataSetChanged();
		historyList = loadUsersWithRecentChat();
		removeViewFromLayout();
		addViewToLayout();
	}

	private void removeViewFromLayout() {
		leftHeight = rightHeight = 0;
		leftLayout.removeAllViewsInLayout();
		rightLayout.removeAllViewsInLayout();
	}

	/**
	 * 获取有聊天记录的users和groups
	 * 
	 * @param context
	 * @return
	 */
	private List<EMContact> loadUsersWithRecentChat() {
		List<EMContact> resultList = new ArrayList<EMContact>();
		for (User user : contactList.values()) {
			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(user.getUsername());
			if (conversation.getMsgCount() > 0) {
				resultList.add(user);
			}
		}
		for (EMGroup group : EMGroupManager.getInstance().getAllGroups()) {
			EMConversation conversation = EMChatManager.getInstance()
					.getConversation(group.getGroupId());
			if (conversation.getMsgCount() > 0) {
				resultList.add(group);
			}

		}
		// 排序
		sortUserByLastChatTime(resultList);
		return resultList;
	}

	/**
	 * 根据最后一条消息的时间排序
	 * 
	 * @param usernames
	 */
	private void sortUserByLastChatTime(List<EMContact> contactList) {
		Collections.sort(contactList, new Comparator<EMContact>() {
			@Override
			public int compare(final EMContact user1, final EMContact user2) {
				EMConversation conversation1 = EMChatManager.getInstance()
						.getConversation(user1.getUsername());
				EMConversation conversation2 = EMChatManager.getInstance()
						.getConversation(user2.getUsername());

				EMMessage user2LastMessage = conversation2.getLastMessage();
				EMMessage user1LastMessage = conversation1.getLastMessage();
				if (user2LastMessage.getMsgTime() == user1LastMessage
						.getMsgTime()) {
					return 0;
				} else if (user2LastMessage.getMsgTime() > user1LastMessage
						.getMsgTime()) {
					return 1;
				} else {
					return -1;
				}
			}

		});
	}

	@Override
	public void onHiddenChanged(boolean hidden) {
		super.onHiddenChanged(hidden);
		this.hidden = hidden;
		if (!hidden) {
			refresh();
		}
	}

	@Override
	public void onResume() {
		Log.i("chatHistoryFragment", "OnResume");
		super.onResume();
		if (!hidden) {
			EMChatManager.getInstance().activityResumed();
			refresh();
		}
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub

	}

}
