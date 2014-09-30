package com.people.lyy.view;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.people.lyy.R;

public class ADProgressDialog extends Dialog {

	private Context context;
	private String title;
	private String message;
	private boolean cancelable;
	private String positiveButtonText;
	private String negativeButtonText;
	private View contentView;
	private DialogInterface.OnClickListener positiveButtonClickListener;
	private DialogInterface.OnClickListener negativeButtonClickListener;
	private ImageView imageView1, imageView2;
	private int i = 0;
	private int[] image = { R.drawable.quan1, R.drawable.quan2,
			R.drawable.quan3, R.drawable.quan1, R.drawable.quan2,
			R.drawable.quan3, R.drawable.quan1, R.drawable.quan2,
			R.drawable.quan3, R.drawable.quan1, R.drawable.quan2,
			R.drawable.quan3 };
	private Animation anim1, anim2;
	private boolean juage = true;
	private int count = 0;
	private Handler handler = new Handler();
	public Runnable runnable = new Runnable() {
		public void run() {
			AnimationSet animationSet1 = new AnimationSet(true);
			AnimationSet animationSet2 = new AnimationSet(true);
			imageView2.setVisibility(0);
			TranslateAnimation ta = new TranslateAnimation(
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					-1f, Animation.RELATIVE_TO_SELF, 0f,
					Animation.RELATIVE_TO_SELF, 0f);
			ta.setDuration(300);
			animationSet1.addAnimation(ta);
			animationSet1.setFillAfter(true);
			ta = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 1.0f,
					Animation.RELATIVE_TO_SELF, 0f, Animation.RELATIVE_TO_SELF,
					0f, Animation.RELATIVE_TO_SELF, 0f);
			ta.setDuration(500);
			animationSet2.addAnimation(ta);
			animationSet2.setFillAfter(true);
			imageView1.startAnimation(animationSet1);
			imageView2.startAnimation(animationSet2);
			imageView1.setBackgroundResource(image[count % 5]);
			count++;
			imageView2.setBackgroundResource(image[count % 5]);
			if (juage)
				handler.postDelayed(runnable, 4000);

		}
	};

	public ADProgressDialog(Context context) {
		super(context, R.style.Dialog);
		this.context = context;
	}

	public ADProgressDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	/**
	 * Set the positive button resource and it's listener
	 * 
	 * @param positiveButtonText
	 * @return
	 */
	public void setPositiveButton(int positiveButtonText,
			DialogInterface.OnClickListener listener) {
		this.positiveButtonText = (String) context.getText(positiveButtonText);
		this.positiveButtonClickListener = listener;
	}

	public void setPositiveButton(String positiveButtonText,
			DialogInterface.OnClickListener listener) {
		this.positiveButtonText = positiveButtonText;
		this.positiveButtonClickListener = listener;
	}

	public void setNegativeButton(int negativeButtonText,
			DialogInterface.OnClickListener listener) {
		this.negativeButtonText = (String) context.getText(negativeButtonText);
		this.negativeButtonClickListener = listener;
	}

	public void setNegativeButton(String negativeButtonText,
			DialogInterface.OnClickListener listener) {
		this.negativeButtonText = negativeButtonText;
		this.negativeButtonClickListener = listener;
	}

	public ADProgressDialog create() {
		LayoutInflater inflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.progress_addialog_layout, null);
		this.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT,
				LayoutParams.WRAP_CONTENT));
		// set the dialog title
		((TextView) layout.findViewById(R.id.title)).setText(title);
		imageView1 = (ImageView) layout.findViewById(R.id.imageview1);
		imageView2 = (ImageView) layout.findViewById(R.id.imageview2);
		imageView2.setVisibility(4);
		handler.postDelayed(runnable, 2000);
		// set the confirm button
		if (positiveButtonText != null) {
			((Button) layout.findViewById(R.id.positiveButton))
					.setText(positiveButtonText);
			if (positiveButtonClickListener != null) {
				((Button) layout.findViewById(R.id.positiveButton))
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								positiveButtonClickListener.onClick(
										ADProgressDialog.this,
										DialogInterface.BUTTON_POSITIVE);
							}
						});
			}
		} else {
			// if no confirm button just set the visibility to GONE
			layout.findViewById(R.id.positiveButton).setVisibility(View.GONE);
		}
		// set the cancel button
		if (negativeButtonText != null) {
			((Button) layout.findViewById(R.id.negativeButton))
					.setText(negativeButtonText);
			if (negativeButtonClickListener != null) {
				((Button) layout.findViewById(R.id.negativeButton))
						.setOnClickListener(new View.OnClickListener() {
							public void onClick(View v) {
								negativeButtonClickListener.onClick(
										ADProgressDialog.this,
										DialogInterface.BUTTON_NEGATIVE);
							}
						});
			}
		} else {
			// if no confirm button just set the visibility to GONE
			layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
		}

		if (null == positiveButtonText && null == negativeButtonText) {
			layout.findViewById(R.id.bottomLayout).setVisibility(View.GONE);
		}

		// set the content message
		if (message != null) {
			// ((TextView) layout.findViewById(R.id.message)).setText(message);
		} else if (contentView != null) {
			// if no message set
			// add the contentView to the dialog body
			((LinearLayout) layout.findViewById(R.id.message)).removeAllViews();
			((LinearLayout) layout.findViewById(R.id.message)).addView(
					contentView, new LayoutParams(LayoutParams.WRAP_CONTENT,
							LayoutParams.WRAP_CONTENT));
		}
		this.setContentView(layout);

		this.setCancelable(cancelable);

		return this;
	}
}
