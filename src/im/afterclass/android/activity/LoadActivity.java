package im.afterclass.android.activity;

import im.afterclass.android.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.os.Bundle;
import android.os.Handler;
import android.os.StrictMode;
import android.view.WindowManager;


public class LoadActivity extends Activity {
	
	  private SharedPreferences sp;
	
     
      private static final int LOAD_DISPLAY_TIME = 1500;
      
      /** Called when the activity is first created. */
      @Override
      public void onCreate(Bundle savedInstanceState) {
          super.onCreate(savedInstanceState);
                   getWindow().setFormat(PixelFormat.RGBA_8888);
         getWindow().addFlags(WindowManager.LayoutParams.FLAG_DITHER);
 
         if (android.os.Build.VERSION.SDK_INT > 9) {
        	    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        	    StrictMode.setThreadPolicy(policy);
        	}
         
         setContentView(R.layout.activity_load);
 
         new Handler().postDelayed(new Runnable() {
             public void run() {
            	 
     			/* Create an Intent that will start the Main WordPress Activity. */
                 Intent mainIntent = new Intent(LoadActivity.this, LoginActivity.class);
                 LoadActivity.this.startActivity(mainIntent);
                 LoadActivity.this.finish();
   
             }
         }, LOAD_DISPLAY_TIME); //1500 for release
 
     }}