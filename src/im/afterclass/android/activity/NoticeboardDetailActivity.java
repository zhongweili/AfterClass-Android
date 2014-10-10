package im.afterclass.android.activity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;

import im.afterclass.android.R;
import im.afterclass.android.adapter.ReplyAdapter;
import im.afterclass.android.domain.ActivityItem;
import im.afterclass.android.domain.ReplyContent;
import android.content.Intent;
import im.afterclass.android.widget.InnerListView;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class NoticeboardDetailActivity extends ActionBarActivity implements OnClickListener{

	private TextView theme;
	private ImageView picture;
	private TextView remainTime;
	private ImageView avatar;
	private TextView name;
	private TextView address;
	private TextView time;
	private TextView content;
	private TextView participant;
	private int actid;
	
	private EditText replyEdt;
	private Button replyBtn;
	private InnerListView contentList;
	private ReplyAdapter adapter;
	private List<ReplyContent> replyList;
	private ReplyContent reply;
	private String userName;
	private final static String BASE_URL = "http://ac-mobile.twtapps.net";
	private final static String ACTIVITY_DETAIL_URL = "/act/%d";
	private final static String ACTIVITY_COMMENTS_URL = "/act/%d/comments";
	private final static String ACTIVITY_COMMENTS_TO_URL = "/act/%d/comments/%s/to/%s?content=%s";
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.noticeboard_detail);
		initViews();
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	private void initViews(){
		actid = getIntent().getIntExtra("actid", 1);
		actid = 1;
		theme = (TextView) findViewById(R.id.theme);
		picture = (ImageView) findViewById(R.id.picture);
		remainTime = (TextView) findViewById(R.id.remain_time);
		avatar = (ImageView) findViewById(R.id.avatar);
		name = (TextView) findViewById(R.id.name);
		address = (TextView) findViewById(R.id.address);
		time = (TextView) findViewById(R.id.time);
		content = (TextView) findViewById(R.id.content);
		participant = (TextView) findViewById(R.id.participant);
		initContent();
		replyEdt = (EditText) findViewById(R.id.reply_edit);
		replyBtn = (Button) findViewById(R.id.reply_btn);
		replyBtn.setOnClickListener(this);
		contentList = (InnerListView) findViewById(R.id.content_list);
		replyList = new ArrayList<ReplyContent>();
		initReply();
		adapter = new ReplyAdapter(this, R.layout.reply_list_item, replyList);
		contentList.setAdapter(adapter);
		if(contentList.getCount()>0){
			contentList.setSelection(replyList.size()-1);
		}
	
	}
	
	private void initContent(){
		String url = BASE_URL + String.format(ACTIVITY_DETAIL_URL, actid);
		AsyncHttpClient client = new AsyncHttpClient();
		client.get(url, new JsonHttpResponseHandler(){
			@Override
			public void onFailure(int statusCode, Header[] header, byte[] data,
					Throwable throwable) {
				super.onFailure(statusCode, header, data, throwable);
				// TODO handle error
			}

			@Override
			public void onSuccess(int statusCode, Header[] header, JSONObject response) {
				super.onSuccess(statusCode, header, response);
				if(statusCode == 200){
						try{
							theme.setText(response.getString("actname"));
							address.setText(response.getString("address"));
							time.setText(response.getString("recommend_time"));
							content.setText(response.getString("content"));
						}catch(JSONException exception){
							exception.printStackTrace();
						}
				}
				// TODO to handle json 
				// you can use gson lib
			}
		});
	}
	
	private void initReply(){
		String url = BASE_URL + String.format(ACTIVITY_COMMENTS_URL, actid);
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
							JSONObject object = response.getJSONObject(i);
							ReplyContent item = new ReplyContent();
							item.setName(object.getString("uid"));
							item.setTo_name(object.getString("to_id"));
							item.setContent(object.getString("comment"));
							item.setTime(object.getString("created_at"));
							replyList.add(item);
							
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
	
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.reply_btn:
			addReplyContentToList();
			break;
		}
	}
	
	 @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
    	case android.R.id.home:
    		intent = new Intent(NoticeboardDetailActivity.this,MainActivity.class);
			startActivity(intent);
    		this.finish();
    		overridePendingTransition(0,R.anim.zoom_out);
    		return true;
  
    	default:
    		return super.onOptionsItemSelected(item);
        }
    }
	
	@SuppressLint("SimpleDateFormat")
	private void addReplyContentToList(){
		String replyText = replyEdt.getText().toString();
		if(replyText == null || replyText.length()<=0){
			Toast.makeText(this, "回复内容不能为空", Toast.LENGTH_SHORT).show();
		}else{
			reply = new ReplyContent();
			reply.setName(getUserName());
			reply.setContent(replyText);
			reply.setTo_name("0");
			SimpleDateFormat df = new SimpleDateFormat("MM-dd HH:mm");
			String date = df.format(new Date());
			reply.setTime(date);
			postReply(reply);
			replyList.add(reply);
			adapter.notifyDataSetChanged();
			contentList.setSelection(replyList.size()-1);
		}
		replyEdt.getText().clear();
	}
	
	private void postReply(ReplyContent reply) {
		String url = BASE_URL + String.format(ACTIVITY_COMMENTS_TO_URL, actid, reply.getName(), reply.getTo_name(), reply.getContent());
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
	
	//获取当前登录用户名
	private String getUserName() {
		if (userName == null) {
			SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
			userName = preferences.getString("username", null);
		}
		return userName;
	}
	
}
