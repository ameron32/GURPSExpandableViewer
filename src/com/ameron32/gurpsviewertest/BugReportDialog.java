package com.ameron32.gurpsviewertest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import com.ameron32.libgurps.impl.GURPSObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

public class BugReportDialog extends GURPSDialog {

	private final Activity sourceActivity;
	
	private EditText etExplain;
	private Button bSubmit;
	private CheckBox cb1BR, cb2BR, cb3BR, cb4BR;
	private TextView tvGO;
	private ProgressBar pbTitle;
	
	public BugReportDialog(Context context, Activity sourceActivity) {
		super(context);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.bug_report_dialog);
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
		etExplain.setOnClickListener(ocl);
		bSubmit = (Button) findViewById(R.id.bSubmitBR);
		bSubmit.setOnClickListener(ocl);
		tvGO = (TextView) findViewById(R.id.tvGO);
		cb1BR = (CheckBox) findViewById(R.id.cb1BR);
		cb1BR.setOnClickListener(ocl);
		cb2BR = (CheckBox) findViewById(R.id.cb2BR);
		cb2BR.setOnClickListener(ocl);
		cb3BR = (CheckBox) findViewById(R.id.cb3BR);
		cb3BR.setOnClickListener(ocl);
		cb4BR = (CheckBox) findViewById(R.id.cb4BR);
		cb4BR.setOnClickListener(ocl);
		pbTitle = (ProgressBar) findViewById(R.id.pbTitleBR);
	}
	
	public void show() {
		super.show();
		pbTitle.setProgress(100);
	}
	
//	public void onClick(View v) {
//		// TODO Auto-generated method stub
//		convertEditTextVarsIntoStringsAndYesThisIsAMethodWeCreated();
//		String emailaddress[] = { emailAdd };
//		String message = "Well hello "
//				+ name
//				+ " I just wanted to say "
//				+ beginning
//				+ ".  Not only that but I hate when you "
//				+ stupidAction
//				+ ", that just really makes me crazy.  I just want to make you "
//				+ hatefulAct
//				+ ".  Welp, thats all I wanted to chit-chatter about, oh and"
//				+ out
//				+ ".  Oh also if you get bored you should check out www.mybringback.com"
//				+ '\n' + "PS. I think I love you...    ";
//		
//		Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
//		emailIntent.putExtra(android.content.Intent.EXTRA_EMAIL, emailaddress);
//		emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "I hate you!");
//		emailIntent.setType("plain/text");
//		emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, message);
//		startActivity(emailIntent);
//	}
//
//	private void convertEditTextVarsIntoStringsAndYesThisIsAMethodWeCreated() {
//		// TODO Auto-generated method stub
//		emailAdd = personsEmail.getText().toString();
//		beginning = intro.getText().toString();
//		name = personsName.getText().toString();
//		stupidAction = stupidThings.getText().toString();
//		hatefulAct = hatefulAction.getText().toString();
//		out = outro.getText().toString();
//	}

	View.OnClickListener ocl = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			if (v.getId() == R.id.bSubmitBR) {
				String message = "Bug Report for " 
						+ new SimpleDateFormat("MM/dd/yyyy").format(System.currentTimeMillis());
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
	};
}
