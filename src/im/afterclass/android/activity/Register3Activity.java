package im.afterclass.android.activity;

import java.util.Random;

import org.apache.http.Header;
import org.json.JSONException;
import org.json.JSONObject;

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
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 注册页
 *
 */
public class Register3Activity extends Activity{
	private EditText nicknameEditText;
	private String mobile;
	
	private final static String BASE_URL = "http://ac-mobile.twtapps.net";
	private final static String USER_REGISTER = "/user";
	
	private static Random randGen = null;
	private static char[] numbersAndLetters = null;
	private String uuid;
	private String enPasswd;
	private boolean check;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register1);
		nicknameEditText = (EditText) findViewById(R.id.edit_nickname);
		mobile = getIntent().getStringExtra("mobile");
	}
	
	
	/**
	 * 注册
	 * @param view
	 */
	public void register(View view){

		if(newRegister()){
			final ProgressDialog pd = new ProgressDialog(this);
			pd.setMessage("正在注册...");
			pd.show();
			new Thread(new Runnable() {
				public void run() {
					try {
						//调用sdk注册方法
			//			EMChatManager.getInstance().createAccountOnServer(uuid, enPasswd);
						runOnUiThread(new Runnable() {
							public void run() {
								if(!Register3Activity.this.isFinishing())
								pd.dismiss();
								//保存用户名
								DemoApplication.getInstance().setUserName(uuid);
								DemoApplication.getInstance().setPassword(enPasswd);
								Toast.makeText(getApplicationContext(), "注册成功", 0).show();
								finish();
							}
						});
					} catch (final Exception e) {
						runOnUiThread(new Runnable() {
							public void run() {
								if(!Register3Activity.this.isFinishing())
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
	
	public boolean newRegister(){
		
		String url = BASE_URL + USER_REGISTER;
		
		String passwd = randomString(6);
		CipherUtil cipher = new CipherUtil();
		enPasswd = cipher.generatePassword(passwd);
		
		RequestParams params = new RequestParams();
		params.add("sid", "2");
		params.add("mobile", mobile);
		params.add("key", enPasswd);
		
		AsyncHttpClient client = new AsyncHttpClient();
		client.post(url, params, new JsonHttpResponseHandler() {

			@Override
			public void onFailure(int statusCode, Header[] header, byte[] data,
					Throwable throwable) {
				super.onFailure(statusCode, header, data, throwable);
				// TODO handle error
				check = false;
			}

			@Override
			public void onSuccess(int statusCode, Header[] header, JSONObject object) {
				super.onSuccess(statusCode, header, object);
				try{
					uuid = object.getString("uuid");
					check = true;
				}catch(JSONException exception){
					exception.printStackTrace();
				}
				
			}

		});
		return check;
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
