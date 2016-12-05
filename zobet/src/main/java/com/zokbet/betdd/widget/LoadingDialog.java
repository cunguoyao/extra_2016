package com.zokbet.betdd.widget;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.zokbet.betdd.R;


public class LoadingDialog extends Dialog {
	

	public LoadingDialog(Context context) {
		super(context, R.style.loadingDialogStyle);
	}

	private LoadingDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.custom_dialog_loading);
		LinearLayout linearLayout = (LinearLayout)this.findViewById(R.id.LinearLayout);
		linearLayout.getBackground().setAlpha(210);
	}
}
