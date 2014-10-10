package im.afterclass.android.activity;


import im.afterclass.android.widget.SegmentedGroup;

import im.afterclass.android.R;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;

import android.graphics.Color;
import android.graphics.drawable.Drawable;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.MenuItem.OnMenuItemClickListener;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import android.widget.RadioGroup;
import android.widget.SearchView;
import android.widget.Toast;

public class CreateActivity extends ActionBarActivity implements OnClickListener, RadioGroup.OnCheckedChangeListener{

	//private Button invite_btn;
	private Button select_btn;
	private EditText theme_edit;
	private EditText thought_edit;
	private ImageView image_theme;
	private SharedPreferences sp;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setTheme(android.R.style.Theme_WithActionBar);
		
		setContentView(R.layout.activity_create);
	
		image_theme = (ImageView) findViewById(R.id.image_theme);
		theme_edit = (EditText) findViewById(R.id.edit_theme);
		thought_edit = (EditText) findViewById(R.id.edit_thought);
	//	invite_btn = (Button) findViewById(R.id.next_invite); 
		select_btn = (Button) findViewById(R.id.select_btn);
//		invite_btn.setOnClickListener(this);
		select_btn.setOnClickListener(this);
		
		sp = getSharedPreferences("afterclass", MODE_PRIVATE);
		
		String type = sp.getString("theme_type","");

		if(type.equals("吃")){

			theme_edit.setText("吃");

			thought_edit.setText("饿了。。");
			image_theme.setImageResource(R.drawable.defaultpic0);
		}
		else if(type.equals("桌游")){

			theme_edit.setText("桌游");

			thought_edit.setText("狼人杀");
			image_theme.setImageResource(R.drawable.defaultpic);
		}
		else if(type.equals("聚会")){

			theme_edit.setText("聚会");
			thought_edit.setText("有多久没聚了 ");
			image_theme.setImageResource(R.drawable.defaultpic2);
		}
		else if(type.equals("运动")){

			theme_edit.setText("羽毛球");
			thought_edit.setText("一起打球吧");
			image_theme.setImageResource(R.drawable.defaultpic3);
		}
		else{

			theme_edit.setText("");
			thought_edit.setText("");
			image_theme.setImageResource(R.drawable.defaultpic4);
		}
		
		int left_time = sp.getInt("left_time", 0);
		select_btn.setText(String.valueOf(left_time));
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
		
		SegmentedGroup segmented_place = (SegmentedGroup) findViewById(R.id.segmented_place);
    	segmented_place.setTintColor(Color.parseColor("#FFD0FF3C"), Color.parseColor("#FF7B07B2"));
    	segmented_place.setOnCheckedChangeListener(this);
    	SegmentedGroup segmented_time = (SegmentedGroup) findViewById(R.id.segmented_time);
    	segmented_time.setTintColor(getResources().getColor(R.color.radio_button_selected_color));
    	segmented_time.setOnCheckedChangeListener(this);
	
	}
	
	@Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (checkedId) {
           
            case R.id.button1_place:
        //        Toast.makeText(CreateActivity.this, "One", Toast.LENGTH_SHORT).show();
                return;
            case R.id.button2_place:
        //        Toast.makeText(CreateActivity.this, "Two", Toast.LENGTH_SHORT).show();
                return;
            case R.id.button1_time:
        //        Toast.makeText(CreateActivity.this, "One", Toast.LENGTH_SHORT).show();
                return;
            case R.id.button2_time:
        //        Toast.makeText(CreateActivity.this, "Two", Toast.LENGTH_SHORT).show();
                return;
            
        }
    }
	
	 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
 
        MenuItem nextItem = menu.add("Next Button");
        MenuItemCompat.setShowAsAction(nextItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        nextItem.setIcon(R.drawable.ic_action_next_item);
        nextItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				SharedPreferences.Editor editor = sp.edit();
				String theme = theme_edit.getText().toString();
    			String thought = thought_edit.getText().toString();
    			editor.putString("theme", theme);
    			editor.putString("thought", thought);
    			editor.commit();
    			Intent intent = new Intent(CreateActivity.this,LaunchActivity.class);
    			startActivity(intent);
    			overridePendingTransition(R.anim.fade_in,R.anim.fade_out);
    			return true;
			}
        });
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Intent intent;
        switch (item.getItemId()) {
    	case android.R.id.home:
    		intent = new Intent(CreateActivity.this,MainActivity.class);
			startActivity(intent);
    		this.finish();
    		overridePendingTransition(0,R.anim.zoom_out);
    		return true;
    	case R.id.action_next:
    		SharedPreferences.Editor editor = sp.edit();
			String theme = theme_edit.getText().toString();
			String thought = thought_edit.getText().toString();
			editor.putString("theme", theme);
			editor.putString("thought", thought);
			editor.commit();
			intent = new Intent(CreateActivity.this,InvitePickActivity.class);
			startActivity(intent);
			
			return true;
    	default:
    		return super.onOptionsItemSelected(item);
        }
    }
	
	
	public void onClick(View v) {
		// TODO Auto-generated method stub
		//Intent intent;
		
		switch (v.getId()) {
/*		case R.id.next_invite:
			
			
			SharedPreferences.Editor editor = sp.edit();
			String theme = theme_edit.getText().toString();
			String thought = thought_edit.getText().toString();
			editor.putString("theme", theme);
			editor.putString("thought", thought);
			editor.commit();
			intent = new Intent(CreateActivity.this,InvitePickActivity.class);
			startActivity(intent);
			finish();
			break;*/
		case R.id.select_btn:
			final String[] time_items = getResources().getStringArray(R.array.select_time_item);
			new AlertDialog.Builder(CreateActivity.this)  
            .setTitle("请点击选择")  
            .setItems(time_items, new DialogInterface.OnClickListener() {
            	public void onClick(DialogInterface dialog, int which){
            		int time = 0;
            		
            		switch(which){
            			case 0:
            				time = 1; select_btn.setText("1");
            				break;
            			case 1:
            				time = 2; select_btn.setText("2");
            				break;
            			case 2:
            				time = 3; select_btn.setText("3");
            				break;
            			case 3:
            				time = 6; select_btn.setText("6");
            				break;
            			case 4:
            				time = 12; select_btn.setText("12");
            				break;
            		}
            		SharedPreferences.Editor editor = sp.edit();
            		editor.putInt("left_time", time);
            		editor.commit();
            	}
            }).show();
			break;
		}
	}
}
