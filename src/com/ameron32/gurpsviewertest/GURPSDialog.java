package com.ameron32.gurpsviewertest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.WindowManager;

public class GURPSDialog extends Dialog implements Dialog.OnKeyListener, Dialog.OnClickListener {

	Context context;
	public GURPSDialog(Context context) {
		super(context);
		this.context = context;
	}

	protected static int numberOfOpenDialogs = 0; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		numberOfOpenDialogs++;
		getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT);
		super.onCreate(savedInstanceState);
		/* NOT FINISHED */
//		setContentView(R.layout.dialog_spacer);
//		LayoutInflater li = LayoutInflater.from(context);
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			dialog.dismiss();
			return true;
		} else {
			return false;
		}
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
//		dialog.dismiss();
	}

	@Override
	public void dismiss() {
		numberOfOpenDialogs--;
		super.dismiss();
	}	
	
}
