package com.keyun.pan.widget;

import android.app.Activity;
import android.content.Context;

public class ProgressDialogUtils {
	
	private static LoadingDialog proDialog;

	public static void showProgressDialog(Context context, Boolean cancelable) {
		if(context != null && !((Activity)context).isFinishing()) {
			if (proDialog == null) {
				proDialog = new LoadingDialog(context);
			}
			proDialog.setCancelable(cancelable);
			if (!proDialog.isShowing()) {
				proDialog.show();
			}
		}
	}

	public static void dismissProgressBar() {
		if (proDialog != null && proDialog.isShowing()) {
			proDialog.dismiss();
		}
		proDialog = null;
	}

}
