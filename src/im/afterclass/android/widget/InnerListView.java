package im.afterclass.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.ListView;
import android.widget.ScrollView;

public class InnerListView extends ListView {

	private ScrollView scrollView;
	
	public InnerListView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}
	
	public InnerListView(Context context, AttributeSet attrs) {
	super(context, attrs);
	// TODO Auto-generated constructor stub
	}
	
	public InnerListView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean onInterceptTouchEvent(MotionEvent ev){
		switch(ev.getAction()){
		case MotionEvent.ACTION_DOWN:
			setScrollViewAble(false);
			break;
		case MotionEvent.ACTION_MOVE:
			break;
		case MotionEvent.ACTION_UP:
			break;
		case MotionEvent.ACTION_CANCEL:
			setScrollViewAble(true);
		}
		return super.onInterceptTouchEvent(ev);
	}
	
	private void setScrollViewAble(boolean flag){
		scrollView = (ScrollView) getParent().getParent().getParent();
		scrollView.requestDisallowInterceptTouchEvent(!flag);
	}
}
