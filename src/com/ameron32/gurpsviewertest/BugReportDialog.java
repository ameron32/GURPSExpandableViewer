package com.ameron32.gurpsviewertest;

import java.text.SimpleDateFormat;
import java.util.Locale;

import com.ameron32.libgurps.impl.GURPSObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BugReportDialog extends GURPSDialog implements View.OnClickListener {

	private final Activity sourceActivity;
	
	private EditText etExplain;
	private Button bSubmit;
	private CheckBox cb1BR, cb2BR, cb3BR, cb4BR;
	private TextView tvGO;
	private ProgressBar pbTitle;
	
	public BugReportDialog(Context context, Activity sourceActivity) {
		super(context, R.layout.bug_report_dialog);
		this.sourceActivity = sourceActivity;
	}
	
	long goObjectId;
	public void set(final GURPSObject go) {
		init();
		goObjectId = go.getObjectId();
		tvGO.setText(go.getName() + ", " + go.getSID());
	}
	
	public void init() {
		setOnKeyListener(this);
		etExplain = (EditText) findViewById(R.id.etExplain);
		etExplain.setOnClickListener(this);
		bSubmit = (Button) findViewById(R.id.bSubmitBR);
		bSubmit.setOnClickListener(this);
		tvGO = (TextView) findViewById(R.id.tvGO);
		cb1BR = (CheckBox) findViewById(R.id.cb1BR);
		cb1BR.setOnClickListener(this);
		cb2BR = (CheckBox) findViewById(R.id.cb2BR);
		cb2BR.setOnClickListener(this);
		cb3BR = (CheckBox) findViewById(R.id.cb3BR);
		cb3BR.setOnClickListener(this);
		cb4BR = (CheckBox) findViewById(R.id.cb4BR);
		cb4BR.setOnClickListener(this);
		pbTitle = (ProgressBar) findViewById(R.id.pbTitleBR);
	}
	
	public void show() {
		super.show();
		pbTitle.setProgress(100);
	}
	
	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.bSubmitBR) {
			String message = "Bug Report for " 
					+ new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(System.currentTimeMillis());
			if (cb1BR.isChecked())
				message += "\nInfo Not Displaying Correctly";
			if (cb2BR.isChecked())
				message += "\nInfo Is Wrong";
			if (cb3BR.isChecked())
				message += "\nInfo Is Missing";
			if (cb4BR.isChecked())
				message += "\nOther (see below)";
			
			String etString = etExplain.getText().toString().trim();
			if (!(etString.length() == 0)) {
				message += "\n\n" + etString;
			}
			
			final GURPSObject go = GURPSObject.findGURPSObjectById(goObjectId);
			message += "\n\n\nDetails:\n" 
					+ "\n" + go.getSID() 
					+ "\n" + go.getClass().getSimpleName()
					+ "\n" + go.getName()
					+ "\n" + go.toString();
			
			Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
			emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, 
					new String[] { "ameron32@gmail.com" });
			emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, 
					"Bug Report");
			emailIntent.setType("plain/text");
			emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, 
					message);
			sourceActivity.startActivity(emailIntent);
		}
	}
}
