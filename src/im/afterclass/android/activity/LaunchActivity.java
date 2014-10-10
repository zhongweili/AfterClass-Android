package im.afterclass.android.activity;

import org.apache.http.Header;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import im.afterclass.android.R;
import im.afterclass.android.domain.MyActivity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnClickListener;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.NotificationCompat;
import android.support.v4.view.MenuItemCompat;
import android.support.v4.view.WindowCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

public class LaunchActivity extends ActionBarActivity implements View.OnClickListener{
	
	private MyCount mc;  
    private TextView show_time;  
    private TextView launch_theme;
    private TextView launch_name;
    private TextView launch_time;
    private TextView launch_address;
    private TextView launch_thought;
    private ImageView image_theme;
    private Button stickToNoticeBoard;
    private Button shareToOtherPlatform;
    private SharedPreferences sp;

	private static MyActivity myAct = new MyActivity();
    
    private static final String SHARED_FILE_NAME = "shared.png";
    private final static String BASE_URL = "http://ac-mobile.twtapps.net";
	private final static String ACTIVITY_CREATE_URL = "/act";
    
    @Override  
    protected void onCreate(Bundle savedInstanceState) {  
        // TODO Auto-generated method stub  
        super.onCreate(savedInstanceState);
//        copyPrivateRawResuorceToPubliclyAccessibleFile();
        supportRequestWindowFeature(WindowCompat.FEATURE_ACTION_BAR);
        setTheme(android.R.style.Theme_WithActionBar);
        setContentView(R.layout.activity_launch);  
       
        image_theme = (ImageView) findViewById(R.id.image_theme);
        launch_theme = (TextView) findViewById(R.id.launch_theme);
        launch_time = (TextView) findViewById(R.id.launch_time);
        launch_address = (TextView) findViewById(R.id.launch_address);
        launch_thought = (TextView) findViewById(R.id.launch_thought);
        show_time = (TextView) findViewById(R.id.show_time);  
        stickToNoticeBoard = (Button) findViewById(R.id.btn_noticeboard);
        shareToOtherPlatform = (Button) findViewById(R.id.btn_share);
        initView();
        stickToNoticeBoard.setOnClickListener(this);
        shareToOtherPlatform.setOnClickListener(this);
        
        ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
        
    }//end func  
  
    @Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.btn_noticeboard:
			showIfInviteFriendDialog();
			break;
		case R.id.btn_share:
			showShare();
			break;
		}
	}
    
    public void showIfInviteFriendDialog(){
    	AlertDialog.Builder builder = new Builder(this);
    	builder.setTitle("是否邀请好友？");
    	//builder.setMessage("");
    	builder.setPositiveButton("是", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LaunchActivity.this, InvitePickActivity.class);
				dialog.dismiss();
				startActivity(intent);
			}
		});
    	builder.setNegativeButton("否", new OnClickListener() {
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				Toast.makeText(LaunchActivity.this, "发布成功！", Toast.LENGTH_SHORT).show();
				
				String url = BASE_URL + ACTIVITY_CREATE_URL;
				
				RequestParams params = new RequestParams();
	/*			params.add("actname", "sendFromAndroidTestDemo");
				params.add("tag", "test tag");
				params.add("address", "TJU");
				params.add("invite", "120");
				params.add("content", "alotofcontent");
				params.add("time", "2014-09-27 18:00:00");
				params.add("sid", "1");
				params.add("uid", "1");
				
				sp = getSharedPreferences("afterclass", MODE_PRIVATE);
				params.add("actname", sp.getString("theme", ""));
				params.add("tag", "1");
				params.add("address", "a");
				params.add("invite", "120");
				params.add("content", sp.getString("thought", ""));
				params.add("time", "2014-09-28 17:00:00");
				params.add("sid", "1");
				params.add("uid", "1");
		*/		
				params.add("actname", myAct.gettheme());
				params.add("tag", "1");
				params.add("address", myAct.getlocation());
				params.add("invite", myAct.getshowtime());
				params.add("content", myAct.getthought());
				params.add("time", myAct.gettime());
				params.add("sid", "1");
				params.add("uid", myAct.getname());
				
				
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
		//				String strData = new String(data);
		//				mTextView.setText(strData);
					}

				});
				
				dialog.dismiss();
			}
		});
    	builder.create().show();
    	
    }
    
    public void initView(){
    	sp = getSharedPreferences("afterclass", MODE_PRIVATE);
  //  	int type = sp.getInt("theme_type", 0);
    	String type = sp.getString("theme_type","");
	
		if(type.equals("")){
			image_theme.setImageResource(R.drawable.defaultpic0);
		}
		else if(type.equals("桌游")){
			image_theme.setImageResource(R.drawable.defaultpic);
		}
		else if(type.equals("聚会")){
			image_theme.setImageResource(R.drawable.defaultpic2);
		}
		else if(type.equals("运动")){
			image_theme.setImageResource(R.drawable.defaultpic3);
		}
		else{
			image_theme.setImageResource(R.drawable.defaultpic4);
		}
    	
    	String theme = sp.getString("theme", "");
    	String thought = sp.getString("thought", "");
    	int left_time = sp.getInt("left_time", 0);
    	launch_theme.setText(theme);
    	launch_time.setText("2014-09-09");
    	launch_address.setText("阿瓦山寨");
    	launch_thought.setText(thought);
    	
    	myAct = new MyActivity();
    	myAct.settheme(theme);
    	myAct.setthought(thought);
    	myAct.settime("2014-09-28 17:00:00");
    	myAct.setlocation("a");
    	myAct.setname("1");
    	myAct.setshowtime("180");
    	
    	mc = new MyCount(left_time*3600*1000, 1000);  
        mc.start();  
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Menu items default to never show in the action bar. On most devices this means
        // they will show in the standard options menu panel when the menu button is pressed.
        // On xlarge-screen devices a "More" button will appear in the far right of the
        // Action Bar that will display remaining items in a cascading menu.
   //     menu.add("Normal item");

        MenuItem shareItem = menu.add("Action Button");

        // Items that show as actions should favor the "if room" setting, which will
        // prevent too many buttons from crowding the bar. Extra items will show in the
        // overflow area.
        MenuItemCompat.setShowAsAction(shareItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);

        // Items that show as actions are strongly encouraged to use an icon.
        // These icons are shown without a text description, and therefore should
        // be sufficiently descriptive on their own.
        shareItem.setIcon(android.R.drawable.ic_menu_share);
        shareItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				// TODO Auto-generated method stub
				Intent intent = new Intent(LaunchActivity.this, InvitePickActivity.class);
				startActivity(intent);
				return false;
			}
		});
        
        
        // Notification test
        MenuItem notifyItem = menu.add("showNotification");
        notifyItem.setIcon(android.R.drawable.ic_dialog_info);
        notifyItem.setShowAsAction(MenuItemCompat.SHOW_AS_ACTION_ALWAYS);
        notifyItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
			@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				
				Intent intent = new Intent(LaunchActivity.this, LaunchActivity.class);
				PendingIntent pIntent = PendingIntent.getActivity(LaunchActivity.this, 0, intent, 0);
				
				NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(LaunchActivity.this)
					.setAutoCancel(true)
					.setSmallIcon(android.R.drawable.ic_dialog_info)
					.setContentTitle("刘晓虎想和你吃")
					.setContentText("时间：2014-08-30 12：30\n地点：阿瓦山寨")
					.addAction(android.R.drawable.ic_media_play, "OK", pIntent)
					.addAction(android.R.drawable.ic_btn_speak_now, "聊聊", pIntent)
					.addAction(android.R.drawable.ic_input_delete, "忽略", pIntent);			
				
				int notificationId = 99;
				NotificationManager mManager = (NotificationManager) LaunchActivity.this.getSystemService(Context.NOTIFICATION_SERVICE);
				mManager.notify(notificationId, mBuilder.build());
				
				return false;
			}	
        });
        
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
  //      Toast.makeText(this, "Selected Item: " + item.getTitle(), Toast.LENGTH_SHORT).show();
        Intent intent;
        switch (item.getItemId()) {
        	case android.R.id.home:
        		//intent = new Intent(LaunchActivity.this,MainActivity.class);
    			//startActivity(intent);
        		this.finish();
        		overridePendingTransition(0,R.anim.zoom_out);
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
        //return true;
    }
    
    /*定义一个倒计时的内部类*/  
    class MyCount extends CountDownTimer {     
        public MyCount(long millisInFuture, long countDownInterval) {     
            super(millisInFuture, countDownInterval);     
        }     
        @Override     
        public void onFinish() {     
            show_time.setText("finish");        
        } 
        @Override     
        public void onTick(long millisUntilFinished) {   
        	String ntime = timeTrans(millisUntilFinished);
            show_time.setText(ntime);     
    //        Toast.makeText(LaunchActivity.this, millisUntilFinished / 1000 + "", Toast.LENGTH_LONG).show();//toast有显示时间延迟       
        }    
    }
    
    public String timeTrans(long ptime) {
    	long hour = ptime/1000/3600;
    	long minu = ptime/1000/60 - hour*60;
    	long seco = ptime/1000 - hour*3600 - minu*60;
    	String shour, sminu, sseco;
    	if(hour > 9)
    		shour = Long.toString(hour);
    	else 
    		shour = "0" + Long.toString(hour);
    	if(minu > 9)
    		sminu = Long.toString(minu);
    	else 
    		sminu = "0" + Long.toString(minu);
    	if(seco > 9)
    		sseco = Long.toString(seco);
    	else 
    		sseco = "0" + Long.toString(seco);
    	return (shour + ":" + sminu + ":" + sseco);
    }
    
    private void showShare() {
        ShareSDK.initSDK(this);
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        
        // 分享时Notification的图标和文字
        oks.setNotification(R.drawable.ic_launcher, getString(R.string.app_name));
        // title标题，印象笔记、邮箱、信息、微信、人人网和QQ空间使用
        oks.setTitle(getString(R.string.share));
        // titleUrl是标题的网络链接，仅在人人网和QQ空间使用
        oks.setTitleUrl("http://sharesdk.cn");
        // text是分享文本，所有平台都需要这个字段
        oks.setText("我是分享文本");
        // imagePath是图片的本地路径，Linked-In以外的平台都支持此参数
        oks.setImagePath("/sdcard/test.jpg");
        // url仅在微信（包括好友和朋友圈）中使用
        oks.setUrl("http://sharesdk.cn");
        // comment是我对这条分享的评论，仅在人人网和QQ空间使用
        oks.setComment("我是测试评论文本");
        // site是分享此内容的网站名称，仅在QQ空间使用
        oks.setSite(getString(R.string.app_name));
        // siteUrl是分享此内容的网站地址，仅在QQ空间使用
        oks.setSiteUrl("http://sharesdk.cn");

        // 启动分享GUI
        oks.show(this);
   }

} 



