package com.xbd.kuk;

import java.util.ArrayList;

import com.xbd.kuk.R;
import com.xbd.kuk.app.Chat2Activity;
import com.xbd.kuk.app.ChatActivity;
import com.xbd.kuk.app.ExitActivity;
import com.xbd.kuk.app.MainTopRightDialog;
import com.xbd.kuk.bean.ChatRecordItem;
import com.xbd.kuk.model.ChatRecordAdapter;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;

public class MainActivity extends Activity {

	public static MainActivity instance = null;
	 
	private ViewPager mTabPager;	
	private ImageView mTabImg;// 动画图片
	private ImageView mTab0, mTab1,mTab2,mTab3,mTab4;
	private int zero = 0;// 动画图片偏移量
	private int currIndex = 0;// 当前页卡编号
	private int one;//单个水平动画位移
	private int two;
	private int three;
	private int four;
	private LinearLayout mClose;
    private LinearLayout mCloseBtn;
    private View layout;	
	private boolean menu_display = false;
	private PopupWindow menuWindow;
	private LayoutInflater inflater;
	//private Button mRightBtn;
	private ListView listChatRecord;	//聊天记录
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_main);
         //启动activity时不自动弹出软键盘
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN); 
        instance = this;
        /*
        mRightBtn = (Button) findViewById(R.id.right_btn);
        mRightBtn.setOnClickListener(new Button.OnClickListener()
		{	@Override
			public void onClick(View v)
			{	showPopupWindow (MainWeixin.this,mRightBtn);
			}
		  });*/
        
        mTabPager = (ViewPager)findViewById(R.id.tabpager);
        mTabPager.setOnPageChangeListener(new MyOnPageChangeListener());
        
        mTab0 = (ImageView) findViewById(R.id.img_message);
        mTab1 = (ImageView) findViewById(R.id.img_course);
        mTab2 = (ImageView) findViewById(R.id.img_contracts);
        mTab3 = (ImageView) findViewById(R.id.img_friends);
        mTab4 = (ImageView) findViewById(R.id.img_settings);
        mTabImg = (ImageView) findViewById(R.id.img_tab_now);
        mTab0.setOnClickListener(new MyOnClickListener(0));
        mTab1.setOnClickListener(new MyOnClickListener(1));
        mTab2.setOnClickListener(new MyOnClickListener(2));
        mTab3.setOnClickListener(new MyOnClickListener(3));
        mTab4.setOnClickListener(new MyOnClickListener(4));
        Display currDisplay = getWindowManager().getDefaultDisplay();//获取屏幕当前分辨率
        int displayWidth = currDisplay.getWidth();
        int displayHeight = currDisplay.getHeight();
        one = displayWidth/5; //设置水平动画平移大小
        two = one*2;
        three = one*3;
        four = one*4;
        //Log.i("info", "获取的屏幕分辨率为" + one + two + three + "X" + displayHeight);
        
        //InitImageView();//使用动画
        //将要分页显示的View装入数组中
        LayoutInflater mLi = LayoutInflater.from(this);
        View view0 = mLi.inflate(R.layout.layout_main_tab_message, null);
        View view1 = mLi.inflate(R.layout.layout_main_tab_course, null);
        View view2 = mLi.inflate(R.layout.layout_main_tab_contracts, null);
        View view3 = mLi.inflate(R.layout.layout_main_tab_friends, null);
        View view4 = mLi.inflate(R.layout.layout_main_tab_settings, null);
        
        //每个页面的view数据
        final ArrayList<View> views = new ArrayList<View>();
        views.add(view0);
        views.add(view1);
        views.add(view2);
        views.add(view3);
        views.add(view4);
        //填充ViewPager的数据适配器
        PagerAdapter mPagerAdapter = new PagerAdapter() {
			
			@Override
			public boolean isViewFromObject(View arg0, Object arg1) {
				return arg0 == arg1;
			}
			
			@Override
			public int getCount() {
				return views.size();
			}

			@Override
			public void destroyItem(View container, int position, Object object) {
				((ViewPager)container).removeView(views.get(position));
			}
			
			//@Override
			//public CharSequence getPageTitle(int position) {
				//return titles.get(position);
			//}
			
			@Override
			public Object instantiateItem(View container, int position) {
				((ViewPager)container).addView(views.get(position));
				return views.get(position);
			}
		};
		
		mTabPager.setAdapter(mPagerAdapter);
		
		//绑定每个用户的最后一条聊天记录到 呈现列表中
		listChatRecord = (ListView)view0.findViewById(R.id.listChatRecord);
		ArrayList<ChatRecordItem> listItems = getChatRecordData();
		ChatRecordAdapter chatAdapter = new ChatRecordAdapter(this, listItems);
		listChatRecord.setAdapter(chatAdapter);
		listChatRecord.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> adapterview, View view, int position, long id) {
//				FriendItem friend = (FriendItem) adapterview.getAdapter().getItem(position);
				ChatRecordItem friend = (ChatRecordItem) adapterview.getAdapter().getItem(position);
				Intent intent = new Intent (MainActivity.this, Chat2Activity.class);
				intent.putExtra("friendName", friend.getName());
				intent.putExtra("sessionId", friend.getUserId() + "." + friend.getUserId());
				intent.putExtra("friendicon", friend.getAvatarUrl());
				startActivity(intent);
			}
		});
    }
    /**
	 * 头标点击监听
	 */
	public class MyOnClickListener implements View.OnClickListener {
		private int index = 0;

		public MyOnClickListener(int i) {
			index = i;
		}
		@Override
		public void onClick(View v) {
			mTabPager.setCurrentItem(index);
		}
	};
    
	 /* 页卡切换监听(原作者:D.Winter)
	 */
	public class MyOnPageChangeListener implements OnPageChangeListener {
		@Override
		public void onPageSelected(int arg0) {
			Animation animation = null;
			switch (arg0) {
			case 0:
				mTab0.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_pressed));
				if (currIndex == 1) {
					animation = new TranslateAnimation(one, 0, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_course_normal));
				}
				else if (currIndex == 2) {
					animation = new TranslateAnimation(two, 0, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, 0, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
				}
				else if (currIndex == 4) {
					animation = new TranslateAnimation(four, 0, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
				}
				break;
			case 1:
				mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_course_pressed));
				switch(currIndex){
				case 0:
					animation = new TranslateAnimation(zero, one, 0, 0);
					mTab0.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
					break;
				case 2:
					animation = new TranslateAnimation(two, one, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
					break;
				case 3:
					animation = new TranslateAnimation(three, one, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
					break;
				case 4:
					animation = new TranslateAnimation(four, one, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
					break;
				}
				break;
			case 2:
				mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, two, 0, 0);
					mTab0.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, two, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_course_normal));
				} else if (currIndex == 3) {
					animation = new TranslateAnimation(three, two, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
				} else if (currIndex == 4) {
					animation = new TranslateAnimation(four, two, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
				}
				break;
			case 3:
				mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, three, 0, 0);
					mTab0.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, three, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_course_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, three, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
				}
				else if (currIndex == 4) {
					animation = new TranslateAnimation(four, three, 0, 0);
					mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_normal));
				}
				break;
			case 4:
				mTab4.setImageDrawable(getResources().getDrawable(R.drawable.tab_settings_pressed));
				if (currIndex == 0) {
					animation = new TranslateAnimation(zero, four, 0, 0);
					mTab0.setImageDrawable(getResources().getDrawable(R.drawable.tab_weixin_normal));
				} else if (currIndex == 1) {
					animation = new TranslateAnimation(one, four, 0, 0);
					mTab1.setImageDrawable(getResources().getDrawable(R.drawable.tab_course_normal));
				} else if (currIndex == 2) {
					animation = new TranslateAnimation(two, four, 0, 0);
					mTab2.setImageDrawable(getResources().getDrawable(R.drawable.tab_address_normal));
				}
				else if (currIndex == 3) {
					animation = new TranslateAnimation(three, four, 0, 0);
					mTab3.setImageDrawable(getResources().getDrawable(R.drawable.tab_find_frd_normal));
				}
				break;
			}
			currIndex = arg0;
			animation.setFillAfter(true);// True:图片停在动画结束位置
			animation.setDuration(150);
			mTabImg.startAnimation(animation);
		}
		
		@Override
		public void onPageScrolled(int arg0, float arg1, int arg2) {
		}

		@Override
		public void onPageScrollStateChanged(int arg0) {
		}
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
    	if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {  //获取 back键
    		
        	if(menu_display){         //如果 Menu已经打开 ，先关闭Menu
        		menuWindow.dismiss();
        		menu_display = false;
        		}
        	else {
        		Intent intent = new Intent();
            	intent.setClass(MainActivity.this, ExitActivity.class);
            	startActivity(intent);
        	}
    	}
    	
    	else if(keyCode == KeyEvent.KEYCODE_MENU){   //获取 Menu键			
			if(!menu_display){
				//获取LayoutInflater实例
				inflater = (LayoutInflater)this.getSystemService(LAYOUT_INFLATER_SERVICE);
				//这里的main布局是在inflate中加入的哦，以前都是直接this.setContentView()的吧？呵呵
				//该方法返回的是一个View的对象，是布局中的根
				layout = inflater.inflate(R.layout.layout_main_menu, null);
				
				//下面我们要考虑了，我怎样将我的layout加入到PopupWindow中呢？？？很简单
				menuWindow = new PopupWindow(layout,LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT); //后两个参数是width和height
				//menuWindow.showAsDropDown(layout); //设置弹出效果
				//menuWindow.showAsDropDown(null, 0, layout.getHeight());
				menuWindow.showAtLocation(this.findViewById(R.id.mainweixin), Gravity.BOTTOM|Gravity.CENTER_HORIZONTAL, 0, 0); //设置layout在PopupWindow中显示的位置
				//如何获取我们main中的控件呢？也很简单
				mClose = (LinearLayout)layout.findViewById(R.id.menu_close);
				mCloseBtn = (LinearLayout)layout.findViewById(R.id.menu_close_btn);
				
				
				//下面对每一个Layout进行单击事件的注册吧。。。
				//比如单击某个MenuItem的时候，他的背景色改变
				//事先准备好一些背景图片或者颜色
				mCloseBtn.setOnClickListener (new View.OnClickListener() {					
					@Override
					public void onClick(View arg0) {						
						//Toast.makeText(Main.this, "退出", Toast.LENGTH_LONG).show();
						Intent intent = new Intent();
			        	intent.setClass(MainActivity.this, ExitActivity.class);
			        	startActivity(intent);
			        	menuWindow.dismiss(); //响应点击事件之后关闭Menu
					}
				});				
				menu_display = true;				
			}else{
				//如果当前已经为显示状态，则隐藏起来
				menuWindow.dismiss();
				menu_display = false;
				}
			
			return false;
		}
    	return false;
    }
	
	//----- ----- 
	public ArrayList<ChatRecordItem> getChatRecordData(){
		ArrayList<ChatRecordItem> items = new ArrayList<ChatRecordItem>();
		ChatRecordItem item = new ChatRecordItem();
		item.setUserId(10241);
		item.setAvatarUrl("http://www.baidu.com/img/baidu_sylogo1.gif");
		item.setAvatar(Uri.parse(item.getAvatarUrl()));
		item.setName("小黑");
		item.setTimeAgo("昨天");
		item.setConten("DoTa又赢了一局");
		items.add(item);
		item = new ChatRecordItem();
		item.setUserId(10242);
		item.setAvatarUrl("http://6.su.bdimg.com/bigicon/24/101.png?6");
		item.setAvatar(Uri.parse(item.getAvatarUrl()));
		item.setName("MIT");
		item.setTimeAgo("2小时前");
		item.setConten("刀塔为何物");
		items.add(item);
		return items;
	}
	
	//设置标题栏右侧按钮的作用
	public void btnmainright(View v) {  
		Intent intent = new Intent (MainActivity.this, MainTopRightDialog.class);
		startActivity(intent);
    }
	public void startchat(View v) {      //小黑  对话界面
		//Intent intent = new Intent (MainActivity.this, ChatActivity.class);
		//startActivity(intent);
    }
	public void exit_settings(View v) {                           //退出  伪“对话框”，其实是一个activity
//		Intent intent = new Intent (MainActivity.this, ExitFromSettings.class);
//		startActivity(intent);
	}
	public void btn_shake(View v) {                                   //手机摇一摇
//		Intent intent = new Intent (MainActivity.this, ShakeActivity.class);
//		startActivity(intent);
	}
}
