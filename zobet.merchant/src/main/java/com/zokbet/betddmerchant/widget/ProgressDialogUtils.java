package com.zokbet.betddmerchant.widget;

import android.content.Context;

import com.zokbet.betddmerchant.R;

public class ProgressDialogUtils {
	
	private static LoadingDialog proDialog;
	private static RunManProgressDialog proDialog2;

	public static void showProgressDialog(Context context, Boolean cancelable) {
		proDialog = new LoadingDialog(context);
		proDialog.setCancelable(cancelable);
		proDialog.show();
	}

	public static void dismissProgressBar() {
		if (proDialog != null && proDialog.isShowing()) {
			proDialog.dismiss();
		}
	}

	public static void showRunManProgressDialog(Context context, Boolean cancelable) {
		proDialog2 = new RunManProgressDialog(context, "正在加载...", R.anim.progress_running_man);
		proDialog2.setCancelable(cancelable);
		proDialog2.show();
	}

	public static void dismissRunManProgressBar() {
		if (proDialog2 != null && proDialog2.isShowing()) {
			proDialog2.dismiss();
		}
	}
}
