package im.afterclass.android.activity;

import im.afterclass.android.R;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;

public class FirstActivity extends Activity{
	
	private SharedPreferences preferences;
	
	public void onCreate(Bundle savedInstanceState) {   
        super.onCreate(savedInstanceState);   
        setContentView(R.layout.firstblank);   
           
        preferences = getSharedPreferences("afterclass",MODE_PRIVATE);   
        int count = preferences.getInt("count", 0);    
        Intent intent;
        if (count == 0) {    
        intent = new Intent(FirstActivity.this,LoadfirstActivity.class);    
        startActivity(intent);    
        this.finish();    
        }    
        else{
            
            intent = new Intent(FirstActivity.this,LoadActivity.class);     
            startActivity(intent);    
            this.finish();
        }
        
        Editor editor = preferences.edit();    
        editor.putInt("count", ++count);    
        editor.commit();
	}
}
