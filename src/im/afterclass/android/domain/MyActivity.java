package im.afterclass.android.domain;

public class MyActivity {

	private String act_theme;
	private String act_time;
	private String act_location;
	private String act_name;
	private String act_thought;
	private String act_showtime;
	private int act_image;
	
	
	public String gettheme() {
		return act_theme; 
	}
	
	public String gettime() {
		return act_time; 
	}
	
	public String getlocation() {
		return act_location; 
	}
	
	public String getname() {
		return act_name; 
	}
	
	public String getthought() {
		return act_thought; 
	}
	
	public String getshowtime() {
		return act_showtime; 
	}
	
	public int getimage() {
		return act_image; 
	}
	
	public void settheme(String theme) {
		this.act_theme = theme;
	}
	
	public void settime(String time) {
		this.act_time = time;
	}
	
	public void setlocation(String location) {
		this.act_location = location;
	}
	
	public void setthought(String thought) {
		this.act_thought = thought;
	}
	
	public void setname(String name) {
		this.act_name = name;
	}
	
	public void setshowtime(String showtime) {
		this.act_showtime = showtime;
	}
	
	public void setimage(int image) {
		this.act_image = image;
	}
	
}
