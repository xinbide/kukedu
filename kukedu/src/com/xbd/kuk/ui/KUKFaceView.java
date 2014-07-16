package com.xbd.kuk.ui;

import java.util.ArrayList;
import java.util.HashMap;

import com.xbd.kuk.R;
import com.xbd.kuk.util.KUKEmojiManager;

import android.R.color;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;

/**
 * 添加表情列表
 * 
 * @ClassName: OBFaceView
 * @Description: TODO
 * @author zhangchunzhe
 * @date 2013-11-23 下午2:57:43
 * 
 */
public class KUKFaceView extends RelativeLayout {

	private Context mContext;
	public static final int CATEGORY_LIST_ID = 0x10001;
	public static final int FACE_LIST_ID = 0x10002;
	public static final int FACE_LIST_DRIVER = 0x10003;
	public static final int FACE_CLUMN_NUMBER = 7;// 显示的列数

	private GridView mGridView;// 显示表情列表
	private ListView mListView;// 表情分类列表
	private FaceGategoryGridAdaper mAdapter;
	private ImageAdapter mImagesAdapter;
	private KUKEmojiManager mEmojiManager;

	private HashMap<String, TypedArray> mFacesMap;
	private HashMap<String, String[]> mFacesUnicodeMap;
	private ArrayList<FaceCategoryBgRes> mFaceBgList;
	private TypedArray mTypedArray;
	private String[] mUnicodeArray;
	private TypedArray mSelectedBgTypedArray;
	private TypedArray mNormlBgTypedArray;

	private int mSelectIndex;

	private OnItemClickListener mFaceClickListener;
	private OnFaceListClickListener mFaceListClickListener;

	private String[] mCategoryStr;

	private int[] mCategoryRes = { R.array.emoji_Faces, R.array.emoji_Nature,
			R.array.emoji_Human, R.array.emoji_Artifacts,
			R.array.emoji_Activities, R.array.emoji_Foods,
			R.array.emoji_Concepts };

	private int[] mCategoryStrRes = { R.array.String_Faces,
			R.array.String_Nature, R.array.String_Human,
			R.array.String_Artifacts, R.array.String_Activities,
			R.array.String_Foods, R.array.String_Concepts };

	public KUKFaceView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public KUKFaceView(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	public KUKFaceView(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		initView(context);
	}

	private void initView(Context context) {
		mContext = context;
		mEmojiManager = KUKEmojiManager.getInstance(context);
		initFaceData();
		initFaceView();
	}

	/**
	 * 
	 */
	private void initFaceData() {
		mSelectedBgTypedArray = getResources().obtainTypedArray(
				R.array.face_category_selected);
		mNormlBgTypedArray = getResources().obtainTypedArray(
				R.array.face_category_norml);
		mFaceBgList = new ArrayList<KUKFaceView.FaceCategoryBgRes>();

		mCategoryStr = mEmojiManager.getEmojiCategory();

		mFacesMap = new HashMap<String, TypedArray>();
		mFacesUnicodeMap = new HashMap<String, String[]>();
		for (int i = 0; i < mCategoryStr.length; i++) {
			mFacesMap
					.put(mCategoryStr[i], getUnicodeDrawables(mCategoryRes[i]));
			mFacesUnicodeMap.put(mCategoryStr[i],
					getUnicodeStrings(mCategoryStrRes[i]));

			FaceCategoryBgRes categoryBgRes = new FaceCategoryBgRes();
			if (mSelectIndex == i) {
				categoryBgRes.isSelected = true;
			} else {

			}
			categoryBgRes.mCategroySelectBg = mSelectedBgTypedArray
					.getDrawable(i);
			categoryBgRes.mCategroyNormlBg = mNormlBgTypedArray.getDrawable(i);
			mFaceBgList.add(categoryBgRes);
		}
	}

	/**
	 * 
	 * @param resId
	 * @return
	 */
	private TypedArray getUnicodeDrawables(int resId) {
		return mContext.getResources().obtainTypedArray(resId);
	}

	/**
	 * 
	 * @param resId
	 * @return
	 */
	private String[] getUnicodeStrings(int resId) {
		return mContext.getResources().getStringArray(resId);
	}

	/**
	 * 
	 * @param width
	 */
	public void setCategoryWidth(int width) {

	}

	/**
	 * 
	 */
	private void initFaceView() {
		mGridView = new GridView(mContext);
		mListView = new ListView(mContext);
		View driverView = new View(mContext);
		mListView.setId(CATEGORY_LIST_ID);
		mGridView.setId(FACE_LIST_ID);
		driverView.setId(FACE_LIST_DRIVER);
		addView(mListView);
		addView(driverView);
		addView(mGridView);

		//
		RelativeLayout.LayoutParams categorylayoutParams = new RelativeLayout.LayoutParams(
				(int) getResources().getDimension(
						R.dimen.friend_face_catgory_width),
				RelativeLayout.LayoutParams.FILL_PARENT);
		categorylayoutParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);

		mListView.setLayoutParams(categorylayoutParams);
		mListView.setVerticalScrollBarEnabled(false);
		mListView.setDivider(getResources().getDrawable(
				R.drawable.shape_face_category_driver));
		mListView.setDividerHeight(3);

		//
		RelativeLayout.LayoutParams faceListDriverlayoutParams = new RelativeLayout.LayoutParams(
				3, RelativeLayout.LayoutParams.FILL_PARENT);
		driverView.setLayoutParams(faceListDriverlayoutParams);
		faceListDriverlayoutParams.addRule(RIGHT_OF, CATEGORY_LIST_ID);
		driverView.setBackgroundResource(R.drawable.shape_face_list_driver);
		//
		RelativeLayout.LayoutParams faceListlayoutParams = new RelativeLayout.LayoutParams(
				RelativeLayout.LayoutParams.FILL_PARENT,
				RelativeLayout.LayoutParams.FILL_PARENT);
		faceListlayoutParams.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
		faceListlayoutParams.addRule(RIGHT_OF, FACE_LIST_DRIVER);
		// faceListlayoutParams.setMargins(10, 10, 10, 10);
		mGridView.setLayoutParams(faceListlayoutParams);
		// mGridView.setVerticalSpacing(5);

		mGridView.setNumColumns(FACE_CLUMN_NUMBER);
		mGridView.setBackgroundResource(R.drawable.shape_face_grid_list_bg);
		initViewData();

	}

	private void initViewData() {

		mAdapter = new FaceGategoryGridAdaper(mContext);
		mAdapter.setFaceCategoryList(mFaceBgList);
		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(mCatetoryClickListener);

		mTypedArray = mFacesMap.get(mCategoryStr[0]);
		mUnicodeArray = mFacesUnicodeMap.get(mCategoryStr[0]);
		mImagesAdapter = new ImageAdapter(mContext);
		mImagesAdapter.setDrawableRes(mTypedArray);
		mImagesAdapter.setmUnicodes(mUnicodeArray);
		mGridView.setAdapter(mImagesAdapter);
		mGridView.setOnItemClickListener(mFacItemClickListener);
		mGridView.setBackgroundColor(Color.parseColor("#e4e4e4"));
	}

	/**
	 * 添加表情
	 * 
	 * @param title
	 * @param drawable
	 */
	public void addFaceListItem(String title, String drawable) {

	}

	/**
	 * 
	 * @param itemClickListener
	 */
	public void setFaceItemClickListener(
			OnFaceListClickListener faceListClickListener) {
		mFaceListClickListener = faceListClickListener;
	}

	/**
	 * 
	 */
	private OnItemClickListener mCatetoryClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			mTypedArray = mFacesMap.get(mCategoryStr[position]);
			mUnicodeArray = mFacesUnicodeMap.get(mCategoryStr[position]);
			mImagesAdapter.setDrawableRes(mTypedArray);
			mImagesAdapter.setmUnicodes(mUnicodeArray);
			mImagesAdapter.notifyDataSetChanged();

			for (int i = 0; i < mFaceBgList.size(); i++) {
				FaceCategoryBgRes bgRes = mFaceBgList.get(i);
				if (position == i) {
					bgRes.isSelected = true;
				} else {
					bgRes.isSelected = false;
				}
			}
			mAdapter.notifyDataSetChanged();
			mGridView.setSelection(0);

		}
	};

	/**
	 * 
	 */
	private OnItemClickListener mFacItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			Drawable drawable = (Drawable) parent.getAdapter()
					.getItem(position);
			String unicode = ((ImageAdapter) parent.getAdapter())
					.getmUnicode(position);
			mFaceListClickListener.onFaceClick(drawable, unicode);

		}
	};

	/**
	 * 
	 * @ClassName: ImageAdapter
	 * @Description: TODO
	 * @author zhangchunzhe
	 * @date 2013-11-25 上午10:32:59
	 * 
	 */

	public class ImageAdapter extends BaseAdapter {
		private Context mContext;
		private TypedArray mDrawables;
		private String[] mUnicodes;

		/**
		 * 
		 * @return
		 */
		public String[] getmUnicodes() {
			return mUnicodes;
		}

		/**
		 * 
		 * @param postion
		 * @return
		 */
		public String getmUnicode(int postion) {
			return mUnicodes[postion];
		}

		/**
		 * 
		 * @param mUnicodes
		 */
		public void setmUnicodes(String[] unicodes) {
			this.mUnicodes = unicodes;
		}

		public ImageAdapter(Context c) {
			mContext = c;

		}

		public void setDrawableRes(TypedArray unicodeDrawables) {
			mDrawables = unicodeDrawables;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mDrawables.length();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mDrawables.getDrawable(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.face_list_item, null);
			}
			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.faceImageView);

			imageView.setImageDrawable(mTypedArray.getDrawable(position));
			return convertView;
		}
	}

	/**
	 * 
	 * @ClassName: FaceCategoryBgRes
	 * @Description: TODO
	 * @author zhangchunzhe
	 * @date 2013-11-26 下午8:55:57
	 * 
	 */
	public class FaceCategoryBgRes {
		public Drawable mCategroyNormlBg;
		public Drawable mCategroySelectBg;
		public boolean isSelected;

	}

	/**
	 * 
	 * @ClassName: FaceGategoryGridAdaper
	 * @Description: TODO
	 * @author zhangchunzhe
	 * @date 2013-11-27 上午10:48:46
	 * 
	 */
	public class FaceGategoryGridAdaper extends BaseAdapter {

		private Context mContext;
		private ArrayList<FaceCategoryBgRes> mArrayList;

		public FaceGategoryGridAdaper(Context context) {
			mContext = context;
		}

		public void setFaceCategoryList(ArrayList<FaceCategoryBgRes> arrayList) {
			mArrayList = arrayList;
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return mArrayList.size();
		}

		@Override
		public Object getItem(int position) {
			// TODO Auto-generated method stub
			return mArrayList.get(position);
		}

		@Override
		public long getItemId(int position) {
			// TODO Auto-generated method stub
			return position;
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub
			if (convertView == null) {
				convertView = LayoutInflater.from(mContext).inflate(
						R.layout.friend_face_category_layout, null);

			}
			FaceCategoryBgRes bgRes = mArrayList.get(position);
			View selectBgView = convertView.findViewById(R.id.categoryBgView);
			ImageView imageView = (ImageView) convertView
					.findViewById(R.id.face_category_icon);

			if (bgRes.isSelected) {
				imageView.setBackgroundDrawable(bgRes.mCategroySelectBg);
				selectBgView
						.setBackgroundResource(R.drawable.img_face_category_selected_bg);
			} else {
				imageView.setBackgroundDrawable(bgRes.mCategroyNormlBg);
				selectBgView.setBackgroundColor(Color.parseColor("#e4e4e4"));
			}
			return convertView;
		}
	}

	/**
	 * 
	 * @ClassName: OnFaceListClickListener
	 * @Description: TODO
	 * @author zhangchunzhe
	 * @date 2013-11-27 下午3:03:54
	 * 
	 */
	public interface OnFaceListClickListener {
		void onFaceClick(Drawable drawable, String unicode);
	}

}
