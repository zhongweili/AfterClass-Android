package im.afterclass.android.activity;

import im.afterclass.android.R;
import im.afterclass.android.fragment.LaunchActivityFragment;
import im.afterclass.android.fragment.ParticipateActivityFragment;
import android.app.ActionBar;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.SimpleOnPageChangeListener;

import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

public class MyActivitiesActivity extends FragmentActivity {
	
	private Button[] mTabs;
	private ViewPager pager;
	private Fragment[] fragments;
	private LaunchActivityFragment launchActivityFragment;
	private ParticipateActivityFragment participateActivityFragment;
	private int currentTabIndex = 0;
	private FragmentPagerAdapter adapter;
	private SimpleOnPageChangeListener changeListener;
	
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.myactivitys);
		initViews();
	}
	
	private void initViews(){
		mTabs = new Button[2];
		mTabs[0] = (Button) findViewById(R.id.now);
		mTabs[1] = (Button) findViewById(R.id.history);
		launchActivityFragment = new LaunchActivityFragment();
		participateActivityFragment = new ParticipateActivityFragment();
		fragments = new Fragment[]{launchActivityFragment,participateActivityFragment};
		pager = (ViewPager) findViewById(R.id.viewPager);
		adapter = new FragmentPagerAdapter(
				getSupportFragmentManager()) {

			public int getCount() {
				return fragments.length;
			}

			public Fragment getItem(int position) {
				return fragments[position];
			}
		};
		changeListener=new SimpleOnPageChangeListener(){
			public void onPageSelected(int position) {
				mTabs[currentTabIndex].setSelected(false);
				mTabs[position].setSelected(true);
				currentTabIndex = position;
			}
		};
		pager.setAdapter(adapter);
		pager.setCurrentItem(0);
		pager.setOnPageChangeListener(changeListener);
		mTabs[0].setSelected(true);
		
		ActionBar actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);
	}
	
	 @Override
	    public boolean onOptionsItemSelected(MenuItem item) {
	        switch (item.getItemId()) {
	    	case android.R.id.home:
	    		this.finish();
	    		overridePendingTransition(0,R.anim.zoom_out);
	    		return true;
	    	default:
	    		return super.onOptionsItemSelected(item);
	        }
	    }
	
	public void onTabClick(View v){
		switch(v.getId()){
		case R.id.now:
			pager.setCurrentItem(0);
			break;
		case R.id.history:
			pager.setCurrentItem(1);
			break;
		}
	}
	
	protected void onDestory(){
		super.onDestroy();
	}

}

