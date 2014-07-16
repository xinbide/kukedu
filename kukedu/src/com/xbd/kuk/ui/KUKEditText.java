package com.xbd.kuk.ui;

import java.util.ArrayList;

import com.xbd.kuk.util.KUKEmojiManager;
import com.xbd.kuk.util.KUKUtil;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.text.Editable;
import android.text.Html;
import android.text.Spannable;
import android.text.Spanned;
import android.text.SpannedString;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.EditText;

/**
 * 重写EditText 输入的表情转换为图片显示 支持第三方软件的Emoji表情输入。 本地需要Emoji表情资源
 * 
 * @ClassName: OBEditText
 * @Description: TODO
 * @author zhangchunzhe
 * @date 2013-12-7 下午4:18:29
 * 
 */
public class KUKEditText extends EditText implements TextWatcher {

	private Context mContext;
	private boolean isNeedChange;
	private boolean isChanged;
	private KUKEmojiManager mEmojiManager;
	private int mStartIndex;
	private String mOriginalString;
	private StringBuilder mStringBuilder;
	private ArrayList<CharSequence> mCharArrayList;
	private ArrayList<Spanned> mSpannedArrayList;
	private int mBeforeLength, mAfterLength;

	public KUKEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public KUKEditText(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		init(context);
	}

	public KUKEditText(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
		init(context);
	}

	@Override
	public Editable getText() {
		return super.getText();
	}

	public String getFormatTextString() {
		// TODO Auto-generated method stub
		return mEmojiManager.parseEmojiFaceStrings(getTextString());
	}

	// 一个表情算2个字
	public int getFormatTextLength() {
		// TODO Auto-generated method stub
		int length = 0;
		String textString = getFormatTextString();

		String[] strs = KUKUtil.parseUnicodesFromContent(textString);
		if (strs == null) {
			return textString.length();
		}
		for (int i = 0; i < strs.length; i++) {
			length += KUKUtil.parseForamtFaceLength(strs[i]);
		}
		return length;

	}

	public String getTextString() {
		// TODO Auto-generated method stub
		Editable editable = getText();
		return KUKUtil.convertToMsg(editable, getContext());
	}

	private void init(Context context) {
		mContext = context;
		addTextChangedListener(this);
		mEmojiManager = KUKEmojiManager.getInstance(context);
		mStringBuilder = new StringBuilder();
		mCharArrayList = new ArrayList<CharSequence>();
		mSpannedArrayList = new ArrayList<Spanned>();

	}

	@Override
	public void setText(CharSequence text, BufferType type) {
		// TODO Auto-generated method stub
		super.setText(text, type);
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count,
			int after) {
		// TODO Auto-generated method stub
		if (after > 0) {
			if (isChanged) {
				isNeedChange = false;
			} else {
				isNeedChange = true;
			}
		}

	}

	@Override
	public void onTextChanged(CharSequence text, int start, int lengthBefore,
			int lengthAfter) {
		// TODO Auto-generated method stub
		super.onTextChanged(text, start, lengthBefore, lengthAfter);
		mStartIndex = start;
		mBeforeLength = lengthBefore;
		mAfterLength = lengthAfter;
	}

	@Override
	public void afterTextChanged(Editable s) {
		// TODO Auto-generated method stub
		Spannable spannedString = s;

		if (isNeedChange) {
			if (mStartIndex + mAfterLength > mStartIndex) {
				CharSequence sequence = spannedString.subSequence(mStartIndex,
						mStartIndex + mAfterLength);

				Spanned spanned = getSpannedText(sequence.toString());

				isChanged = true;
				s.replace(mStartIndex, mStartIndex + sequence.length(), spanned);
			}
		} else {
			isChanged = false;
		}

	}

	/**
	 * 
	 * @param str
	 * @return
	 */
	public Spanned getSpannedText(String newAString) {
		// String newString = str.substring(0, str.indexOf(newAString));
		if (newAString == null) {
			return new SpannedString("");
		}
		String faceString = mEmojiManager
				.parseEmojiFaceShowFromString(newAString);
		return KUKUtil.formatEmojiFaceText(faceString, mContext);
	}
}
