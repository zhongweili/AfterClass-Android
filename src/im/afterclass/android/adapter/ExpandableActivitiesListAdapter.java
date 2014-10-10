package im.afterclass.android.adapter;
 
import java.util.List;
import java.util.Map;
 

import im.afterclass.android.R;

import im.afterclass.android.domain.MyActivity;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;
 
public class ExpandableActivitiesListAdapter extends BaseExpandableListAdapter {
 
    private Activity context;
    private Map<String, List<MyActivity>> laptopCollections;
    private List<String> laptops;
 
    public ExpandableActivitiesListAdapter(Activity context, List<String> laptops,
            Map<String, List<MyActivity>> laptopCollections) {
        this.context = context;
        this.laptopCollections = laptopCollections;
        this.laptops = laptops;
    }
 
    public Object getChild(int groupPosition, int childPosition) {
        return laptopCollections.get(laptops.get(groupPosition)).get(childPosition);
    }
 
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }
     
     
    public View getChildView(final int groupPosition, final int childPosition,
            boolean isLastChild, View convertView, ViewGroup parent) {
   //     final String laptop = (String) getChild(groupPosition, childPosition);
    	final MyActivity laptop = (MyActivity) getChild(groupPosition, childPosition);
    	LayoutInflater inflater = context.getLayoutInflater();
         
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.myactivities_child_item, null);
        }
        
        TextView item_theme = (TextView) convertView.findViewById(R.id.launch_theme);
        TextView item_time = (TextView) convertView.findViewById(R.id.launch_time);
        TextView item_location = (TextView) convertView.findViewById(R.id.launch_address);
        TextView item_thought = (TextView) convertView.findViewById(R.id.launch_thought);
        TextView item_showtime = (TextView) convertView.findViewById(R.id.show_time);
        ImageView item_image = (ImageView) convertView.findViewById(R.id.image_theme);
        
        item_theme.setText(laptop.gettheme());
        item_time.setText(laptop.gettime());
        item_location.setText(laptop.getlocation());
        item_thought.setText(laptop.getthought());
        item_showtime.setText(laptop.getshowtime());
   //     item_image.setImageDrawable((Drawable)laptop.getimage());
        item_image.setImageResource(laptop.getimage());
        
        return convertView;
    }
 
    public int getChildrenCount(int groupPosition) {
        return laptopCollections.get(laptops.get(groupPosition)).size();
    }
 
    public Object getGroup(int groupPosition) {
        return laptops.get(groupPosition);
    }
 
    public int getGroupCount() {
        return laptops.size();
    }
 
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }
 
    public View getGroupView(int groupPosition, boolean isExpanded,
            View convertView, ViewGroup parent) {
        String laptopName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater infalInflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = infalInflater.inflate(R.layout.myactivities_group_item,
                    null);
        }
        TextView item = (TextView) convertView.findViewById(R.id.laptop);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(laptopName);
        return convertView;
    }
 
    public boolean hasStableIds() {
        return true;
    }
 
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}