package im.afterclass.android.fragment;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import im.afterclass.android.R;
import im.afterclass.android.adapter.ExpandableActivitiesListAdapter;
import im.afterclass.android.domain.MyActivity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.Toast;
import android.widget.ExpandableListView.OnChildClickListener;

public class LaunchActivityFragment extends Fragment {

	private List<String> groupList;
    private List<MyActivity> childList;
    private Map<String, List<MyActivity>> laptopCollection;
    private ExpandableListView expListView;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View v = inflater.inflate(R.layout.myactivities_list, container, false);
		expListView = (ExpandableListView) v.findViewById(R.id.laptop_list);
		return v;
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		createGroupList();
		 
        createCollection();
 
        final ExpandableActivitiesListAdapter expListAdapter = new ExpandableActivitiesListAdapter(
                getActivity(), groupList, laptopCollection);
        expListView.setAdapter(expListAdapter);
 
        //setGroupIndicatorToRight();
 
        expListView.setOnChildClickListener(new OnChildClickListener() {
 
            public boolean onChildClick(ExpandableListView parent, View v,
                    int groupPosition, int childPosition, long id) {
                final String selected = (String) expListAdapter.getChild(
                        groupPosition, childPosition);
                Toast.makeText(getActivity(), selected, Toast.LENGTH_LONG)
                        .show();
 
                return true;
            }
        });
	}
	
	private void createGroupList() {
    	groupList = new ArrayList<String>();
    	groupList.add("Now");
    	groupList.add("历史活动");
    }
 
    private void createCollection() {
    	//get活动列表
    	
        // preparing laptops collection(child)
    	MyActivity myact1 = new MyActivity();
    	myact1.settheme("桌游");
    	myact1.settime("09-09");
    	myact1.setlocation("新开湖西");
    	myact1.setthought("一起来新开湖边玩杀人吧");
    	myact1.setshowtime("2:13:45");
    	myact1.setimage(R.drawable.defaultpic);
    	List<MyActivity> nowActivities = new ArrayList<MyActivity>();
    	nowActivities.add(myact1);
    	
    	MyActivity myact2 = new MyActivity();
    	myact2.settheme("游泳");
    	myact2.settime("周末");
    	myact2.setlocation("新馆");
    	myact2.setthought("刚学会，求组队");
    	myact2.setshowtime("已结束");
    	myact2.setimage(R.drawable.defaultpic3);
    	List<MyActivity> hisActivities = new ArrayList<MyActivity>();
    	hisActivities.add(myact2);
    	
        laptopCollection = new LinkedHashMap<String, List<MyActivity>>();
        for (String laptop : groupList) {
        	if (laptop.equals("Now")) {
        		loadChild(nowActivities); 
        	}
        	else if(laptop.equals("历史活动")) {
        		loadChild(hisActivities);
        	}
            laptopCollection.put(laptop, childList);
        }
    }
    
    private void loadChild(List<MyActivity> laptopModels) {
        childList = new ArrayList<MyActivity>();
        for (MyActivity model : laptopModels)
            childList.add(model);
    }
	
	
}
