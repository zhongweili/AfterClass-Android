package im.afterclass.android.activity;

import im.afterclass.android.domain.User;
import im.afterclass.android.fragment.CommunicationFragment;
import im.afterclass.android.fragment.NavigationFragment;
import im.afterclass.android.fragment.NoticeBoardFragment;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import im.afterclass.android.Constant;
import im.afterclass.android.DemoApplication;
import im.afterclass.android.R;
import im.afterclass.android.adapter.ChatHistoryAdapter;
import im.afterclass.android.adapter.DragAdapter;
import im.afterclass.android.widget.DragGridView;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SearchView.OnCloseListener;
import android.widget.SearchView.OnQueryTextListener;

import com.slidingmenu.lib.SlidingMenu;
import com.slidingmenu.lib.app.SlidingFragmentActivity;

import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.AdapterView.OnItemClickListener;

public class MainActivity extends SlidingFragmentActivity implements OnClickListener, AnimationListener,OnQueryTextListener{

	private Button[] mTabs;
	private ImageView more;
	private CommunicationFragment communicationFragment;
	private NoticeBoardFragment noticeBoardFragment;
	private Fragment fragments[];
	private int index;
	private int currentTabIndex;
	private PopupWindow mPopupWindow;
	private PopupWindow mQueryPopupWindow;
	private SharedPreferences sp;
	private static String[] themearray;
	private Animation animPushbottomin;
	private int mSortMode;
	private List<User> contactList;
	private SearchView searchView;


	@Override

	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		initSlidingMenu();

		initViews();
		communicationFragment = new CommunicationFragment();
		noticeBoardFragment = new NoticeBoardFragment();
		fragments = new Fragment[]{communicationFragment, noticeBoardFragment};
		getSupportFragmentManager().beginTransaction().add(R.id.fragment_container, communicationFragment)
									.add(R.id.fragment_container, noticeBoardFragment).hide(noticeBoardFragment)
									.show(communicationFragment).commit();

	}
	
	private void initViews(){
		mTabs = new Button[2];
		mTabs[0] = (Button) findViewById(R.id.communication);
		mTabs[1] = (Button) findViewById(R.id.noticeboard);
		mTabs[0].setSelected(true);
		more = (ImageView) findViewById(R.id.more);
		more.setOnClickListener(this);
		contactList = new ArrayList<User>();
		sp = getSharedPreferences("afterclass", MODE_PRIVATE);
		animPushbottomin = AnimationUtils.loadAnimation(getApplicationContext(),
                R.anim.push_bottom_in);
         
        animPushbottomin.setAnimationListener(this);
        
        getActionBar().setDisplayHomeAsUpEnabled(true);
	}
	
	
	public void initSlidingMenu() {
        
		//getSupportFragmentManager().beginTransaction().replace(R.id.content_frame, new NavigationFragment()).commit(); 
        SlidingMenu sm = getSlidingMenu();
		sm.setMode(SlidingMenu.LEFT);
		setBehindContentView(R.layout.menu_frame);
		sm.setSlidingEnabled(true);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);
		sm.setShadowWidthRes(R.dimen.shadow_width);
		//sm.setShadowDrawable(R.drawable.shadow);
		getSupportFragmentManager()
		.beginTransaction()
		.replace(R.id.left_menu_frame, new NavigationFragment())
		.commit();
		sm.setBehindOffsetRes(R.dimen.slidingmenu_offset);
		sm.setBehindScrollScale(0.333f);
		sm.setFadeEnabled(true);
		sm.setFadeDegree(0.25f);
	}

	public void onTabClicked(View view) {
		switch (view.getId()) {
		case R.id.communication:
			index = 0;
			break;
		case R.id.noticeboard:
			index = 1;
			break;
		}
		if (currentTabIndex != index) {
			FragmentTransaction trx = getSupportFragmentManager().beginTransaction();
			trx.hide(fragments[currentTabIndex]);
			if (!fragments[index].isAdded()) {
				trx.add(R.id.fragment_container, fragments[index]);
			}
			trx.show(fragments[index]).commit();
		}
		mTabs[currentTabIndex].setSelected(false);
		// 把当前tab设为选中状态
		mTabs[index].setSelected(true);
		currentTabIndex = index;
	}

	 @Override
	    public boolean onCreateOptionsMenu(Menu menu) {
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.actionbar_main, menu);
	        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
	        //searchView.setIconified(false);
	        searchView.setOnCloseListener(new OnCloseListener() {
				
				@Override
				public boolean onClose() {
					// TODO Auto-generated method stub
					if(mQueryPopupWindow != null&&mQueryPopupWindow.isShowing()){
						mQueryPopupWindow.dismiss();
						mQueryPopupWindow = null;
					}
					return false;
				}
			});
	        searchView.setOnQueryTextListener(this);
	        return true;
	    }
	
	/*   @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if (mSortMode != -1) {
            Drawable icon = menu.findItem(mSortMode).getIcon();
            menu.findItem(R.id.action_sort).setIcon(icon);
        }
        return super.onPrepareOptionsMenu(menu);
    }
*/
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	//Intent intent;
        switch (item.getItemId()) {
        	case android.R.id.home:
        		toggle();
        		return true;
        	default:
        		return super.onOptionsItemSelected(item);
        }
    }

    // This method is specified as an onClick handler in the menu xml and will
    // take precedence over the Activity's onOptionsItemSelected method.
    // See res/menu/actions.xml for more info.
    public void onSort(MenuItem item) {
        mSortMode = item.getItemId();
        Intent intent;
        switch (mSortMode) {
        	case R.id.action_add_person:
        		intent = new Intent(MainActivity.this,AddFriendActivity.class);
    			startActivity(intent);
    			overridePendingTransition(R.anim.slide_in_from_left,0);
    			break;
        	case R.id.action_add_group:
        		intent = new Intent(MainActivity.this,AddFriendActivity.class);
    			startActivity(intent);
    			break;
        	case R.id.action_sort_scan:
        		intent = new Intent(MainActivity.this,AddFriendActivity.class);
    			startActivity(intent);
    			break;
        	default:
        		// Request a call to onPrepareOptionsMenu so we can change the sort icon
        		invalidateOptionsMenu();
        }
        
        
    }

    public boolean onQueryTextChange(String searchString) {
    	if(mQueryPopupWindow != null && mQueryPopupWindow.isShowing()){
    		mQueryPopupWindow.dismiss();
    		mQueryPopupWindow = null;
    	}
    	//if(!searchView.isIconified()){
    	getSearchContactList(searchString);
    	showQueryPopupWindow();
    	//}
        return true;
    }

    public boolean onQueryTextSubmit(String query) {
        return true;
    }
	
    private void showQueryPopupWindow(){
    	LayoutInflater inflater = LayoutInflater.from(this);
    	View view = inflater.inflate(R.layout.vertical_popupwindow, null);
    	ListView searchList = (ListView) view.findViewById(R.id.searchlist);
    	ChatHistoryAdapter adapter = new ChatHistoryAdapter(this, 1, contactList);
    	searchList.setAdapter(adapter);
    	mQueryPopupWindow = new PopupWindow(view);
		//view.startAnimation(animPushbottomin);
		mQueryPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
		DisplayMetrics dm = new DisplayMetrics();       
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenHeight = dm.heightPixels;
		Rect frame = new Rect();  
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
		int statusBarHeight = frame.top;
		mQueryPopupWindow.setHeight(screenHeight-getActionBar().getHeight()-statusBarHeight);
		mQueryPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
		mQueryPopupWindow.setTouchable(true);
		mQueryPopupWindow.setFocusable(false);
		mQueryPopupWindow.setOutsideTouchable(false);
		mQueryPopupWindow.setInputMethodMode(PopupWindow.INPUT_METHOD_NEEDED);          
		mQueryPopupWindow.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
		mQueryPopupWindow.setBackgroundDrawable(new ColorDrawable(0));
		mQueryPopupWindow.showAtLocation(findViewById(R.id.main_bottom), Gravity.BOTTOM, 0, 0);
    }
    
    private void getSearchContactList(String searchString){
    	contactList.clear();
    	if(searchString == null || searchString.length()<=0){
    		return;
    	}
		Map<String, User> users = DemoApplication.getInstance().getSearchContactList(searchString);
		Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, User> entry = iterator.next();
			if(!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME) && !entry.getKey().equals(Constant.GROUP_USERNAME))
				contactList.add(entry.getValue());
		}
		//排序
		Collections.sort(contactList, new Comparator<User>() {

			@Override
			public int compare(User lhs, User rhs) {
				return lhs.getUsername().compareTo(rhs.getUsername());
			}
		});
	}
    
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch(v.getId()){
		case R.id.more:
	//		more.setImageResource(R.drawable.toolbar_plusback);
			showBottomPopupWindow(v);
		}
	}
	
	private void showBottomPopupWindow(View v){
		LayoutInflater inflater = LayoutInflater.from(this);
		View parent = findViewById(R.id.main_button);
		View view = inflater.inflate(R.layout.horizontal_popupwindow, null);  
	        
		String themelist = sp.getString("themelist", "自定义*吃*桌游*聚会*运动*游泳*排球*滑冰*自习*网吧*咖啡*酒吧*电影*KTV*逛街*会议*公园*演唱会*话剧");
		themearray = themelist.split(Pattern.quote("*"));
		
	    //生成动态数组，并且转入数据  
	    List<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();  	    
    	
        //Object  itemimage[] = {R.drawable.defaultpic0,R.drawable.defaultpic,R.drawable.defaultpic2,R.drawable.defaultpic3,R.drawable.defaultpic4};
        for(int i=0;i<themearray.length;i++)  
        { 
        	HashMap<String, Object> map = new HashMap<String, Object>();  
            map.put("ItemText", themearray[i]);//按序号做ItemText 
            if(themearray[i].equals("吃"))
            	map.put("ItemImage", R.drawable.defaultpic0);
            else if(themearray[i].equals("桌游"))
            	map.put("ItemImage", R.drawable.defaultpic);
            else if(themearray[i].equals("聚会"))
            	map.put("ItemImage", R.drawable.defaultpic2);
            else if(themearray[i].equals("运动"))
            	map.put("ItemImage", R.drawable.defaultpic3);
            else 
            	map.put("ItemImage", R.drawable.defaultpic4);
            lstImageItem.add(map);  
        }

	    DragGridView mDragGridView = (DragGridView) view.findViewById(R.id.dragGridView);
	    final DragAdapter mDragAdapter = new DragAdapter(this, lstImageItem);
		mDragGridView.setAdapter(mDragAdapter);
		mDragGridView.setOnItemClickListener(new ItemClickListener()); 
		
		mPopupWindow = new PopupWindow(view);
		//view.startAnimation(animPushbottomin);
		mPopupWindow.setAnimationStyle(R.style.mypopwindow_anim_style);
		DisplayMetrics dm = new DisplayMetrics();       
		getWindowManager().getDefaultDisplay().getMetrics(dm);
		int screenHeight = dm.heightPixels;
		int[] location = new int[2];
		parent.getLocationInWindow(location);
		Rect frame = new Rect();  
		getWindow().getDecorView().getWindowVisibleDisplayFrame(frame);  
		int statusBarHeight = frame.top;
	
		mPopupWindow.setHeight(location[1]-getActionBar().getHeight()-statusBarHeight);
		mPopupWindow.setWidth(LayoutParams.MATCH_PARENT);
		mPopupWindow.setTouchable(true);
		mPopupWindow.setFocusable(true);
		mPopupWindow.setOutsideTouchable(true);
		mPopupWindow.setBackgroundDrawable(new ColorDrawable());
		mPopupWindow.showAtLocation(findViewById(R.id.main_bottom), Gravity.BOTTOM, 0, screenHeight - location[1]);

//		MyLocation mylocation = (MyLocation)getApplicationContext();
//		mylocation.setLocation(location[0], location[1]); 

	}
	
	class  ItemClickListener implements OnItemClickListener  {  
		public void onItemClick(AdapterView<?> arg0,//The AdapterView where the click happened   
	                                  View arg1,//The view within the AdapterView that was clicked  
	                                  int arg2,//The position of the view in the adapter  
	                                  long arg3//The row id of the item that was clicked  
	                                  ) {  
	    //arg2=arg3  
		//HashMap<String, Object> item=(HashMap<String, Object>) arg0.getItemAtPosition(arg2);  
	    //显示所选Item的ItemText  
	    
	    SharedPreferences.Editor editor = sp.edit();
	    String themelist = sp.getString("themelist", "自定义*吃*桌游*聚会*运动*游泳*排球*滑冰*自习*网吧*咖啡*酒吧*电影*KTV*逛街*会议*公园*演唱会*话剧");
		themearray = themelist.split(Pattern.quote("*"));
	//    editor.putInt("theme_type", arg2);
	    editor.putString("theme_type", themearray[arg2]);
	    editor.commit();
	    //setTitle((String)item.get("ItemText"));  
	    Intent intent = new Intent(MainActivity.this,CreateActivity.class);
		mPopupWindow.dismiss();
		startActivity(intent);
		overridePendingTransition(R.anim.head_in,R.anim.head_out);
		}
	}

	@Override
	public void onAnimationEnd(Animation arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAnimationStart(Animation animation) {
		// TODO Auto-generated method stub
		
	}
}
