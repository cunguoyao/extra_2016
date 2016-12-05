package com.keyun.pan.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by cunguoyao on 2016/6/17.
 */
public class SoftKeyboardLinearLayout extends LinearLayout {

    public SoftKeyboardLinearLayout(final Context context, final AttributeSet attrs) {
        super(context, attrs);
    }

    public SoftKeyboardLinearLayout(Context context) {
        super(context);
    }

    private OnSoftKeyboardListener onSoftKeyboardListener;

    @Override
    protected void onMeasure(final int widthMeasureSpec, final int heightMeasureSpec) {
        if (onSoftKeyboardListener != null) {
            final int newSpec = MeasureSpec.getSize(heightMeasureSpec);
            final int oldSpec = getMeasuredHeight();
            // If layout became smaller, that means something forced it to resize. Probably soft keyboard :)
            if (oldSpec > newSpec){
                onSoftKeyboardListener.onShown();
            } else {
                onSoftKeyboardListener.onHidden();
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    public final void setOnSoftKeyboardListener(final OnSoftKeyboardListener listener) {
        this.onSoftKeyboardListener = listener;
    }

    // Simplest possible listener :)
    public interface OnSoftKeyboardListener {
        public void onShown();
        public void onHidden();
    }
}
