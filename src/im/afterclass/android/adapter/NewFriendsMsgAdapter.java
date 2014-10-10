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
package im.afterclass.android.adapter;

import im.afterclass.android.R;
import im.afterclass.android.db.InviteMessgeDao;
import im.afterclass.android.domain.InviteMessage;
import im.afterclass.android.domain.InviteMessage.InviteMesageStatus;

import java.util.List;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.chat.EMChatManager;

public class NewFriendsMsgAdapter extends ArrayAdapter<InviteMessage> {

	private Context context;
	private InviteMessgeDao messgeDao;

	public NewFriendsMsgAdapter(Context context, int textViewResourceId, List<InviteMessage> objects) {
		super(context, textViewResourceId, objects);
		this.context = context;
		messgeDao = new InviteMessgeDao(context);
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		final ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = View.inflate(context, R.layout.row_invite_msg, null);
			holder.avator = (ImageView) convertView.findViewById(R.id.avatar);
			holder.reason = (TextView) convertView.findViewById(R.id.message);
			holder.name = (TextView) convertView.findViewById(R.id.name);
			holder.status = (Button) convertView.findViewById(R.id.user_state);
			// holder.time = (TextView) convertView.findViewById(R.id.time);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		final InviteMessage msg = getItem(position);
		if (msg != null) {
			holder.reason.setText(msg.getReason());
			holder.name.setText(msg.getFrom());
			// holder.time.setText(DateUtils.getTimestampString(new
			// Date(msg.getTime())));
			if (msg.getStatus() == InviteMesageStatus.BEAGREED) {
				holder.status.setVisibility(View.INVISIBLE);
				holder.reason.setText("已同意你的好友请求");
			} else if (msg.getStatus() == InviteMesageStatus.BEINVITEED) {
				holder.status.setVisibility(View.VISIBLE);
				holder.status.setText("同意");
				if (msg.getReason() == null) {
					// 如果没写理由
					holder.reason.setText("请求加你为好友");
				}
				// 设置点击事件
				holder.status.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// 同意别人发的好友请求
						acceptInvitation(holder.status, msg);
					}
				});
			} else if (msg.getStatus() == InviteMesageStatus.AGREED) {
				holder.status.setText("已同意");
				holder.status.setBackgroundDrawable(null);
				holder.status.setEnabled(false);
			} else if(msg.getStatus() == InviteMesageStatus.REFUSED){
				holder.status.setText("已拒绝");
				holder.status.setBackgroundDrawable(null);
				holder.status.setEnabled(false);
			}

			// 设置用户头像
		}

		return convertView;
	}

	/**
	 * 同意好友请求
	 * 
	 * @param button
	 * @param username
	 */
	private void acceptInvitation(final Button button, final InviteMessage msg) {
		final ProgressDialog pd = new ProgressDialog(context);
		pd.setMessage("正在添加...");
		pd.setCanceledOnTouchOutside(false);
		pd.show();

		new Thread(new Runnable() {
			public void run() {
				// 调用sdk的同意方法
				try {
					EMChatManager.getInstance().acceptInvitation(msg.getFrom());
					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							button.setText("已同意");
							msg.setStatus(InviteMesageStatus.AGREED);
							// 更新db
							ContentValues values = new ContentValues();
							values.put(InviteMessgeDao.COLUMN_NAME_STATUS, msg.getStatus().ordinal());
							messgeDao.updateMessage(msg.getId(), values);
							button.setBackgroundDrawable(null);
							button.setEnabled(false);

						}
					});
				} catch (final Exception e) {
					((Activity) context).runOnUiThread(new Runnable() {

						@Override
						public void run() {
							pd.dismiss();
							Toast.makeText(context, "添加失败: " + e.getMessage(), 1).show();
						}
					});

				}
			}
		}).start();
	}

	private static class ViewHolder {
		ImageView avator;
		TextView name;
		TextView reason;
		Button status;
		// TextView time;
	}

}
