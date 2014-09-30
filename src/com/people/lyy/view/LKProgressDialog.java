package com.people.lyy.view;

import com.people.lyy.R;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;


public class LKProgressDialog extends Dialog {
	
	private Context context;
	private String title;
	private String message;
	private boolean cancelable;
	private String positiveButtonText;
	private String negativeButtonText;
	private View contentView;
	private DialogInterface.OnClickListener positiveButtonClickListener;
	private DialogInterface.OnClickListener negativeButtonClickListener;

	public LKProgressDialog(Context context) {
		super(context, R.style.Dialog);
		this.context = context;
	}

	public LKProgressDialog(Context context, int theme) {
		super(context, theme);
		this.context = context;
	}
	
	public void setTitle(String title){
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
		this.negativeButtonText = (String) context
				.getText(negativeButtonText);
		this.negativeButtonClickListener = listener;
	}

	public void setNegativeButton(String negativeButtonText,
			DialogInterface.OnClickListener listener) {
		this.negativeButtonText = negativeButtonText;
		this.negativeButtonClickListener = listener;
	}
	
	public LKProgressDialog create() {
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View layout = inflater.inflate(R.layout.progress_dialog_layout, null);
		this.addContentView(layout, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
		// set the dialog title
		((TextView) layout.findViewById(R.id.title)).setText(title);
		// set the confirm button
		if (positiveButtonText != null) {
			((Button) layout.findViewById(R.id.positiveButton)).setText(positiveButtonText);
			if (positiveButtonClickListener != null) {
				((Button) layout.findViewById(R.id.positiveButton)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						positiveButtonClickListener.onClick(LKProgressDialog.this,DialogInterface.BUTTON_POSITIVE);
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
				((Button) layout.findViewById(R.id.negativeButton)).setOnClickListener(new View.OnClickListener() {
					public void onClick(View v) {
						negativeButtonClickListener.onClick(LKProgressDialog.this,DialogInterface.BUTTON_NEGATIVE);
					}
				});
			}
		} else {
			// if no confirm button just set the visibility to GONE
			layout.findViewById(R.id.negativeButton).setVisibility(View.GONE);
		}
		
		if (null == positiveButtonText && null == negativeButtonText){
			layout.findViewById(R.id.bottomLayout).setVisibility(View.GONE);
		}
		
		// set the content message
		if (message != null) {
			((TextView) layout.findViewById(R.id.message)).setText(message);
		} else if (contentView != null) {
			// if no message set
			// add the contentView to the dialog body
			((LinearLayout) layout.findViewById(R.id.message)).removeAllViews();
			((LinearLayout) layout.findViewById(R.id.message)).addView(contentView, new LayoutParams(
							LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT));
		}
		this.setContentView(layout);
		
		this.setCancelable(cancelable);
		
		return this;
	}
}
