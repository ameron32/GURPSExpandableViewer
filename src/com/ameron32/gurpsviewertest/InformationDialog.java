package com.ameron32.gurpsviewertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.ameron32.libgurps.impl.GURPSObject;
import com.ameron32.libgurps.tools.StringTools;
import com.ameron32.testing.ImportTesting;

public class InformationDialog extends Dialog implements Dialog.OnKeyListener, Dialog.OnClickListener {

	final Context context;
	public InformationDialog(Context context) {
		super(context);
		this.context = context;
	}

	private TextView tvTitle, tvClass, tvContent, tvObjectId;
	private Button bClose;
	private ProgressBar pbLoading;
	private void init() {
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvClass = (TextView) findViewById(R.id.tvClass);
		tvContent = (TextView) findViewById(R.id.tvContent);
		tvObjectId = (TextView) findViewById(R.id.tvObjectId);
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
		pbLoading.setMax(100);
		bClose = (Button) findViewById(R.id.bClose);
		bClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				InformationDialog.this.dismiss();
			}
		});
		setOnKeyListener(this);
	}

	public void set(int resourceId, GURPSObject go) {
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(resourceId);
		init();

		tvTitle.setText(go.getName());
		tvClass.setText(go.getClass().getSimpleName());
		setLinkText(tvContent, StringTools.convertBarsToParagraphs(
				go.getDescription())
				+ "\n" + go.getSID() + "\n" + ((go.getNotes().size() > 0) ? go.getNotes() : "[No Notes]"));
//		tvContent.setText(StringTools.convertBarsToParagraphs(
//				go.getDescription())
//				+ "\n" + go.getSID() + "\n" + ((go.getNotes().size() > 0) ? go.getNotes() : "[No Notes]"));
		tvObjectId.setText("[ " + go.getSID().trim().toUpperCase(Locale.ENGLISH) + " ]");
	}
	
	public void show() {
		super.show();
		pbLoading.setProgress(100);
	}

	@Override
	public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) dialog.dismiss();
		return true;
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		dialog.dismiss();
	}
	
	private static final List<String> allNames = new ArrayList<String>();
	private static final ArrayList<IIIS> spans = new ArrayList<IIIS>();
	private void setLinkText(TextView tv, String s) {
		// load the link names
		allNames.clear();
		for (GURPSObject go : ImportTesting.getEverything()) {
			allNames.add(go.getName());
		}
		
		SpannableString ss = new SpannableString(s);
		
		// analyze the string list for links
		spans.clear();
		for (String name : allNames) {
			if (s.toLowerCase(Locale.ENGLISH).contains(name.toLowerCase(Locale.ENGLISH))) {
				ClickableString clickableString = new ClickableString(new LinkListener(name), name);
				
				int startOfLink = s.toLowerCase(Locale.ENGLISH).indexOf(name.toLowerCase(Locale.ENGLISH));
				int linkLength = name.length();
				
				ss.setSpan(clickableString, startOfLink, startOfLink + linkLength, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
				spans.add(new IIIS(startOfLink, name));
			}
		}

		tv.setText(ss);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		
	}
	
//	View.OnClickListener ocl =
			
//			new View.OnClickListener() {
//		@Override
//		public void onClick(View v) {
//			Toast.makeText(context, "Click", Toast.LENGTH_LONG).show();
//		}
//	};
	
	private class IIIS {
		private final int start;
		private final int end;
		private final int length;
		private final String span;
		
		public IIIS (int start, String span) {
			this.start = start;
			this.length = span.length();
			this.end = start + length;
			this.span = span;
		}

		public int getStart() {
			return start;
		}

		public int getEnd() {
			return end;
		}

		public int getLength() {
			return length;
		}

		public String getSpan() {
			return span;
		}

	}
	
	private static class ClickableString extends ClickableSpan {
	    private View.OnClickListener mListener;          
	    public ClickableString(View.OnClickListener listener, String name) {              
	        mListener = listener;  
	        this.name = name;
	    }
	    
	    private final String name;
	    @Override  
	    public void onClick(View v) {  
	        mListener.onClick(v);  
	    }
	    
	    public String getName() {
	    	return name;
	    }
	}
	
	private class LinkListener implements View.OnClickListener {
		String name;
		public LinkListener(String name) {
			this.name = name;
		}
		
		@Override
		public void onClick(View v) {
			Toast.makeText(context, name, Toast.LENGTH_LONG).show();
			boolean found = false;
			for (GURPSObject go : ImportTesting.getEverything()) {
				if (!found && go.getName().equalsIgnoreCase(name)) {
					found = true;
			    	final InformationDialog inf = new InformationDialog(context);
			    	inf.set(R.layout.information_dialog, go);
			    	inf.show();
				}
			}
		}
		
	}

}