package com.zokbet.betdd.widget;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.zokbet.betdd.R;

public class CustomDialog extends Dialog {

	public CustomDialog(Context context) {
		super(context);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	public static class Builder {
		private Context context; //上下文对象
		private String title; //对话框标题
		private String message; //对话框内容
		private String ok_btnText; //按钮名称“确定”
		private String confirm_btnText; //按钮名称“确定”
		private String cancel_btnText; //按钮名称“取消”
		private View contentView; //对话框中间加载的其他布局界面
		/*按钮坚挺事件*/
		private DialogInterface.OnClickListener ok_btnClickListener;
		private DialogInterface.OnClickListener confirm_btnClickListener;
		private DialogInterface.OnClickListener cancel_btnClickListener;

		public Builder(Context context) {
			this.context = context;
		}

		/*设置对话框信息*/
		public Builder setMessage(String message) {
			this.message = message;
			return this;
		}

		/**
		 * Set the Dialog message from resource
		 *
		 * @param message
		 * @return
		 */
		public Builder setMessage(int message) {
			this.message = (String) context.getText(message);
			return this;
		}

		/**
		 * Set the Dialog title from resource
		 *
		 * @param title
		 * @return
		 */
		public Builder setTitle(int title) {
			this.title = (String) context.getText(title);
			return this;
		}

		/**
		 * Set the Dialog title from String
		 *
		 * @param title
		 * @return
		 */
		public Builder setTitle(String title) {
			this.title = title;
			return this;
		}

		/**
		 * 设置对话框界面
		 * @param v View
		 * @return
		 */
		public Builder setContentView(View v) {
			this.contentView = v;
			return this;
		}

		/**
		 * Set the positive button resource and it's listener
		 *
		 * @param confirm_btnText
		 * @return
		 */
		public Builder setPositiveButton(int confirm_btnText,
										 DialogInterface.OnClickListener listener) {
			this.confirm_btnText = (String) context
					.getText(confirm_btnText);
			this.confirm_btnClickListener = listener;
			return this;
		}

		/**
		 * Set the positive button and it's listener
		 *
		 * @param confirm_btnText
		 * @return
		 */
		public Builder setPositiveButton(String confirm_btnText,
										 DialogInterface.OnClickListener listener) {
			this.confirm_btnText = confirm_btnText;
			this.confirm_btnClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button resource and it's listener
		 *
		 * @param cancel_btnText
		 * @return
		 */
		public Builder setNegativeButton(int cancel_btnText,
										 DialogInterface.OnClickListener listener) {
			this.cancel_btnText = (String) context
					.getText(cancel_btnText);
			this.cancel_btnClickListener = listener;
			return this;
		}

		/**
		 * Set the negative button and it's listener
		 *
		 * @param cancel_btnText
		 * @return
		 */
		public Builder setNegativeButton(String cancel_btnText,
										 DialogInterface.OnClickListener listener) {
			this.cancel_btnText = cancel_btnText;
			this.cancel_btnClickListener = listener;
			return this;
		}

		public CustomDialog create() {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			// instantiate the dialog with the custom Theme
			final CustomDialog dialog = new CustomDialog(context, R.style.iosDialogStyle);
			View layout = inflater.inflate(R.layout.custom_dialog, null);
			dialog.addContentView(layout, new LayoutParams(
					LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
			// set the dialog title
			((TextView) layout.findViewById(R.id.title)).setText(title);
			((TextView) layout.findViewById(R.id.title)).getPaint().setFakeBoldText(true);

			LinearLayout oneBtnLayout = (LinearLayout)layout.findViewById(R.id.oneBtn_layout);
			LinearLayout twoBtnLayout = (LinearLayout)layout.findViewById(R.id.twoBtn_layout);

			if(confirm_btnText != null && cancel_btnText == null) {
				oneBtnLayout.setVisibility(View.VISIBLE);
				twoBtnLayout.setVisibility(View.GONE);
				((Button) layout.findViewById(R.id.ok_btn))
						.setText(confirm_btnText);
				if (confirm_btnClickListener != null) {
					((Button) layout.findViewById(R.id.ok_btn))
							.setOnClickListener(new View.OnClickListener() {
								public void onClick(View v) {
									confirm_btnClickListener.onClick(dialog,
											DialogInterface.BUTTON_POSITIVE);
								}
							});
				}
			}else {
				oneBtnLayout.setVisibility(View.GONE);
				twoBtnLayout.setVisibility(View.VISIBLE);
				// set the confirm button
				if (confirm_btnText != null) {
					((Button) layout.findViewById(R.id.confirm_btn))
							.setText(confirm_btnText);
					if (confirm_btnClickListener != null) {
						((Button) layout.findViewById(R.id.confirm_btn))
								.setOnClickListener(new View.OnClickListener() {
									public void onClick(View v) {
										confirm_btnClickListener.onClick(dialog,
												DialogInterface.BUTTON_POSITIVE);
									}
								});
					}
				} else {
					// if no confirm button just set the visibility to GONE
					layout.findViewById(R.id.confirm_btn).setVisibility(
							View.GONE);
				}
				// set the cancel button
				if (cancel_btnText != null) {
					((Button) layout.findViewById(R.id.cancel_btn))
							.setText(cancel_btnText);
					if (cancel_btnClickListener != null) {
						((Button) layout.findViewById(R.id.cancel_btn))
								.setOnClickListener(new View.OnClickListener() {
									public void onClick(View v) {
										cancel_btnClickListener.onClick(dialog,
												DialogInterface.BUTTON_NEGATIVE);
									}
								});
					}
				} else {
					// if no confirm button just set the visibility to GONE
					layout.findViewById(R.id.cancel_btn).setVisibility(
							View.GONE);
				}
			}
			// set the content message
			if (message != null) {
				((TextView) layout.findViewById(R.id.message)).setText(message);
			} else if (contentView != null) {
				// if no message set
				// add the contentView to the dialog body
				((LinearLayout) layout.findViewById(R.id.message))
						.removeAllViews();
				((LinearLayout) layout.findViewById(R.id.message)).addView(
						contentView, new LayoutParams(
								LayoutParams.WRAP_CONTENT,
								LayoutParams.WRAP_CONTENT));
			}
			dialog.setContentView(layout);
			return dialog;
		}

	}
}
