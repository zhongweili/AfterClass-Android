package im.afterclass.android.activity;




import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.telephony.SmsManager;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import im.afterclass.android.R;

public class Register2Activity extends Activity {

	private Button register_btn;
	private Button confirm;
	private EditText phonenum;
	private EditText phoneyanzheng;
	
	SmsManager sManager;
	String code;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registert2);
		init();
//		initActionBar();
		
		register_btn.setOnClickListener(new ButtonClickListener());
		confirm.setOnClickListener(new ButtonClickListener());
		
	}
	
	private void init()
	{
		
		register_btn = (Button)this.findViewById(R.id.register_btn);
		confirm = (Button)this.findViewById(R.id.confirm);
		
		phonenum = (EditText)this.findViewById(R.id.phonenum);
		phoneyanzheng = (EditText)this.findViewById(R.id.phoneyanzheng);
		sManager = SmsManager.getDefault();
	}
	
	public class ButtonClickListener implements OnClickListener
	{
		@Override
		public void onClick(View view)
		{
			Intent intent;
			switch(view.getId()){
			
			case R.id.register_btn:
				String phnum = phonenum.getText().toString();
			
				if(!phnum.equals(""))
				{
					code = randomString();
					
					PendingIntent pi = PendingIntent.getActivity(Register2Activity.this, 0, new Intent(), 0);
					
					sManager.sendTextMessage(phnum, null, code, pi, null);
				
				}
				break;
			case R.id.confirm:
			//	if(validate())
				{
					
					if(phoneyanzheng.getText().toString().equals(code))
					{
						
						intent = new Intent(Register2Activity.this,RegisterActivity.class);
						startActivity(intent);
						finish();
					}
					
				}
				break;
			
			}
			
		}
	}
	
	
	
	private String randomString()
	{
		int num1 = (int)((Math.random())*10);
		String str1 = String.valueOf(num1);
		int num2 = (int)((Math.random())*10);
		String str2 = String.valueOf(num2);
		int num3 = (int)((Math.random())*10);
		String str3 = String.valueOf(num3);
		int num4 = (int)((Math.random())*10);
		String str4 = String.valueOf(num4);
		int num5 = (int)((Math.random())*10);
		String str5 = String.valueOf(num5);
		int num6 = (int)((Math.random())*10);
		String str6 = String.valueOf(num6);		
		
		return str1+str2+str3+str4+str5+str6;
	}
	
	private boolean changepass(){
		return true;
	}
	
}
	