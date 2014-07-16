package com.xbd.kuk.util;

import java.util.HashMap;

import com.xbd.kuk.R;

import android.content.Context;
import android.content.res.TypedArray;

public class KUKEmojiManager {

	private Context mContext;
	// 分类的表情数组
	private int[] mCategoryRes = { R.array.emoji_Faces, R.array.emoji_Nature,
			R.array.emoji_Human, R.array.emoji_Artifacts,
			R.array.emoji_Activities, R.array.emoji_Foods,
			R.array.emoji_Concepts };
	// 分类的对应unicode
	private int[] mCategoryStrRes = { R.array.String_Faces,
			R.array.String_Nature, R.array.String_Human,
			R.array.String_Artifacts, R.array.String_Activities,
			R.array.String_Foods, R.array.String_Concepts };

	// 分类对应的unicode
	private HashMap<String, HashMap<Integer, EmojiItem>> mFacesUnicodeMap;

	// 表情分类
	private String[] mCategoryStr;

	private static KUKEmojiManager mEmojiManager;

	public static KUKEmojiManager getInstance(Context context) {
		if (mEmojiManager == null) {
			mEmojiManager = new KUKEmojiManager(context);
		}
		return mEmojiManager;
	}

	private KUKEmojiManager(Context context) {
		mContext = context;
		initEmojiInfo();
	}

	private void initEmojiInfo() {

		mCategoryStr = mContext.getResources().getStringArray(
				R.array.face_string_value);
		mFacesUnicodeMap = new HashMap<String, HashMap<Integer, EmojiItem>>();
		for (int i = 0; i < mCategoryStr.length; i++) {
			String[] unicodes = getUnicodeStrings(mCategoryStrRes[i]);
			TypedArray faceArray = getUnicodeDrawables(mCategoryRes[i]);
			mFacesUnicodeMap.put(mCategoryStr[i],
					parseInitUnicodeMap(unicodes, faceArray));
		}
	}

	/**
	 * 获取emoji表情分类
	 * 
	 * @return
	 */
	public String[] getEmojiCategory() {
		return mCategoryStr;
	}

	/**
	 * 获取表情集合通过分类
	 * 
	 * @param categoryString
	 *            分类
	 * @return 表情集合
	 */
	public TypedArray getEmojiFace(String categoryString) {
		for (int i = 0; i < mCategoryStr.length; i++) {
			if (mCategoryStr[i].equals(categoryString)) {
				return mContext.getResources()
						.obtainTypedArray(mCategoryRes[i]);
			}
		}
		return null;
	}

	/**
	 * 获取Unicode集合通过分类
	 * 
	 * @param categoryString
	 *            分类
	 * @return unicode 集合
	 */
	public String[] getEmojiFaceUnicodes(String categoryString) {
		for (int i = 0; i < mCategoryStr.length; i++) {
			if (mCategoryStr[i].equals(categoryString)) {
				return mContext.getResources().getStringArray(
						mCategoryStrRes[i]);
			}
		}
		return null;
	}

	/**
	 * 解析带表情的字符串，显示为表情
	 * 
	 * @param faceStr
	 * @return
	 */
	public String parseEmojiFaceStrings(String faceStr) {
		StringBuffer buffer = new StringBuffer();
		int[] pointCode = KUKUtil.toCodePointArray(faceStr);
		boolean isEmoji = false;
		for (int i = 0; i < pointCode.length; i++) {
			for (int j = 0; j < mCategoryStr.length; j++) {
				HashMap<Integer, EmojiItem> hashMap = mFacesUnicodeMap
						.get(mCategoryStr[j]);
				isEmoji = hashMap.containsKey(pointCode[i]);
				if (isEmoji) {
					EmojiItem emojiItem = hashMap.get((pointCode[i]));
					buffer.append(KUKUtil.convertFaceString(emojiItem.unicode));
					break;
				}

			}
			if (!isEmoji) {
				buffer.append(Character.toChars(pointCode[i]));
			}

		}
		return buffer.toString();

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
	 * 解析表情信息
	 * 
	 * @param resId
	 * @return
	 */
	private HashMap<Integer, EmojiItem> parseInitUnicodeMap(String[] unicodes,
			TypedArray mFaceArray) {
		HashMap<Integer, EmojiItem> hashMap = new HashMap<Integer, KUKEmojiManager.EmojiItem>();
		for (int i = 0; i < unicodes.length; i++) {
			int unicodeInt = Integer.parseInt(unicodes[i], 16);
			EmojiItem emojiItem = new EmojiItem();
			emojiItem.unicode = unicodes[i];
			emojiItem.unicodeInt = unicodeInt;
			emojiItem.mResID = mFaceArray.getResourceId(i, -1);
			hashMap.put(unicodeInt, emojiItem);
		}
		return hashMap;
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
	 * @param resID
	 * @return
	 */
	public String getEmojiInfoBy(int resID) {

		for (int i = 0; i < mCategoryStr.length; i++) {
			String[] unicodes = getUnicodeStrings(mCategoryStrRes[i]);
			TypedArray faceArray = getUnicodeDrawables(mCategoryRes[i]);
			for (int j = 0; j < faceArray.length(); j++) {
				int res = faceArray.getResourceId(j, -1);
				if (res == resID) {
					return unicodes[j];
				}
			}

		}
		return null;
	}

	/**
	 * 根据Unicode 获取相对应的表情信息
	 * 
	 * @param categoryIndex
	 * @param faceIndex
	 * @return
	 */
	public EmojiItem getUnicodeFaceItem(String unicode) {
		Object[] array = mFacesUnicodeMap.keySet().toArray();
		EmojiItem emojiItem = null;
		if (unicode == null || "".equals(unicode)) {
			return null;
		}
		for (int i = 0; i < array.length; i++) {
			HashMap<Integer, EmojiItem> hashMap = mFacesUnicodeMap
					.get(array[i]);
			int unicodeInt = Integer.parseInt(unicode, 16);
			if (hashMap.containsKey(unicodeInt)) {
				emojiItem = hashMap.get(unicodeInt);
				break;
			}
			continue;
		}

		return emojiItem;

	}

	/**
	 * 转换表情为字符串
	 * 
	 * @param s
	 * @return
	 */
	public String parseEmojiFaceShowFromString(String s) {
		StringBuffer buffer = new StringBuffer();
		String emojiString = parseEmojiFaceStrings(s);
		String[] unicodes = KUKUtil.parseUnicodesFromContent(emojiString);
		if (unicodes != null && unicodes.length > 0) {
			for (int i = 0; i < unicodes.length; i++) {
				if (KUKUtil.isContainsFaceCode(unicodes[i])) {
					String unicode = KUKUtil
							.parseUnicodeFromContent(unicodes[i]);

					KUKEmojiManager.EmojiItem emojiItem = getUnicodeFaceItem(unicode);
					if (emojiItem != null) {
						if (emojiItem.getmResID() != -1) {
							String str = KUKUtil.formatFaceString(unicodes[i],
									emojiItem.getmResID());
							buffer.append(str);
						} else {
							buffer.append(unicodes[i]);
						}
					} else {
						buffer.append(unicodes[i]);
					}
				} else {
					buffer.append(unicodes[i]);
				}
			}
		} else {
			buffer.append(s);
		}
		return buffer.toString();

	}

	/**
	 * 
	 * @ClassName: EmojiItem
	 * @Description: TODO
	 * @author zhangchunzhe
	 * @date 2013-12-6 下午1:19:43
	 * 
	 */
	public class EmojiItem {

		// unicode表示码
		private String unicode;
		// unicode的16进制的值
		private int unicodeInt;
		// unicode 对应的表情符号
		private int mResID;

		public String getUnicode() {
			return unicode;
		}

		public void setUnicode(String unicode) {
			this.unicode = unicode;
		}

		public int getUnicodeInt() {
			return unicodeInt;
		}

		public void setUnicodeInt(int unicodeInt) {
			this.unicodeInt = unicodeInt;
		}

		public int getmResID() {
			return mResID;
		}

		public void setmResID(int mResID) {
			this.mResID = mResID;
		}

	}

}
