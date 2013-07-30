package com.ameron32.gurpsviewertest;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;

public class GURPSDialog extends Dialog implements Dialog.OnKeyListener, Dialog.OnClickListener {

	private final Context context;
	private final int resourceId;

	public GURPSDialog(Context context, int resourceId) {
		super(context);
		this.context = context;
		this.resourceId = resourceId;
		
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(resourceId);
		getWindow().setLayout(
				WindowManager.LayoutParams.MATCH_PARENT,
				WindowManager.LayoutParams.MATCH_PARENT);
	}

	protected static int numberOfOpenDialogs = 0; 
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		numberOfOpenDialogs++;
		super.onCreate(savedInstanceState);

//		/* NOT FINISHED */
////		LayoutInflater li = LayoutInflater.from(context);
//				
////		RelativeLayout rlParent = (RelativeLayout) findViewById(R.id.rlSpacerParent);
//		
//		final FrameLayout flSpacer = (FrameLayout) findViewById(R.id.flSpacer);
//		final LayoutParams flSpacerLP = new LayoutParams(10 * numberOfOpenDialogs, LayoutParams.MATCH_PARENT);
//		flSpacer.setLayoutParams(flSpacerLP);
//		
//		final RelativeLayout rlDialog = (RelativeLayout) findViewById(R.id.rlDialog);
//		rlDialog.addView(
//				LayoutInflater.from(context).inflate(resourceId, rlDialog),
//				LayoutParams.MATCH_PARENT,
//				LayoutParams.MATCH_PARENT);
//		/* END */
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
