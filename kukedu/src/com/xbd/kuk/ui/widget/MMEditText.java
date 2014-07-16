package com.xbd.kuk.ui.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;
import android.widget.EditText;

public class MMEditText extends EditText
{
  private InputConnection jmK;
  int jmL = 0;

  public MMEditText(Context paramContext)
  {
    super(paramContext);
  }

  public MMEditText(Context paramContext, AttributeSet paramAttributeSet)
  {
    super(paramContext, paramAttributeSet);
  }

  public MMEditText(Context paramContext, AttributeSet paramAttributeSet, int paramInt)
  {
    super(paramContext, paramAttributeSet, paramInt);
  }

  private void Bw(String paramString)
  {
    int i = getSelectionStart();
//    setText(b.a(getContext(), paramString, (int)getTextSize(), false));
    setSelection(i);
  }

  public final void Bv(String paramString)
  {
//    int i = d.h(getContext(), getText().toString(), getSelectionStart());
//    int j = d.h(getContext(), getText().toString(), getSelectionEnd());
//    StringBuffer localStringBuffer = new StringBuffer(getText());
//    String str = localStringBuffer.substring(0, i) + paramString + localStringBuffer.substring(j, localStringBuffer.length());
//    setText(b.a(getContext(), str, (int)getTextSize(), false));
//    setSelection(i + paramString.length());
  }

  public final InputConnection aWf()
  {
    return this.jmK;
  }

  public InputConnection onCreateInputConnection(EditorInfo paramEditorInfo)
  {
    this.jmK = super.onCreateInputConnection(paramEditorInfo);
    return this.jmK;
  }

  public boolean onTextContextMenuItem(int paramInt)
  {
    boolean bool = super.onTextContextMenuItem(paramInt);
    String str = "";
    if (paramInt == 16908322)
    {
      this.jmL = 0;
      str = getText().toString();
    }
    try
    {
      Bw(str);
      return bool;
    }
    catch (IndexOutOfBoundsException localIndexOutOfBoundsException)
    {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = Integer.valueOf(this.jmL);
//      y.e("OD", "!!MMEditText Exception %d", arrayOfObject);
      if (this.jmL < 3)
      {
        this.jmL = (1 + this.jmL);
        Bw(" " + str);
        return bool;
      }
      throw localIndexOutOfBoundsException;
    }
  }
}

/* Location:           E:\Crack\微信\classes_dex2jar.jar
 * Qualified Name:     com.tencent.mm.ui.widget.MMEditText
 * JD-Core Version:    0.5.4
 */