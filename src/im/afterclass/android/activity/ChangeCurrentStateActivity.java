package im.afterclass.android.activity;

import im.afterclass.android.R;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MenuItem.OnMenuItemClickListener;
import android.widget.EditText;

public class ChangeCurrentStateActivity extends ActionBarActivity {

	private EditText changeEdit;
	private SharedPreferences preferences;
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.change_info);
		initViews();
	}
	
	private void initViews(){
		preferences = getSharedPreferences("personalInfo", MODE_PRIVATE);
		changeEdit = (EditText) findViewById(R.id.change_edittext);
		changeEdit.setText(preferences.getString("currentState", ""));
		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	@Override
	protected void onDestroy(){
		super.onDestroy();
	}
	 @Override
    public boolean onCreateOptionsMenu(Menu menu) {
 
        MenuItem saveItem = menu.add("saveButton");
        MenuItemCompat.setShowAsAction(saveItem, MenuItemCompat.SHOW_AS_ACTION_IF_ROOM);
        saveItem.setIcon(R.drawable.ic_action_save);
        saveItem.setOnMenuItemClickListener(new OnMenuItemClickListener() {
		@Override
			public boolean onMenuItemClick(MenuItem arg0) {
				
				Editor editor = preferences.edit();
				editor.putString("currentState", changeEdit.getText().toString());
				editor.commit();
    			Intent intent = new Intent(ChangeCurrentStateActivity.this,PersonalInfoActivity.class);
    			setResult(1, intent);
    			ChangeCurrentStateActivity.this.finish();
    			overridePendingTransition(R.anim.zoom_out,0);
    			return true;
			}
        });
        return true;
    }
	
	public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
    	case android.R.id.home:
    		this.finish();
    		overridePendingTransition(R.anim.zoom_out,0);
    		return true;
    	default:
    		return super.onOptionsItemSelected(item);
        }
	}
	
}
