package com.ameron32.gurpsviewertest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
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

import com.ameron32.libgurps.character.stats.Advantage;
import com.ameron32.libgurps.character.stats.Skill;
import com.ameron32.libgurps.impl.GURPSObject;
import com.ameron32.libgurps.tools.ActivityTools;
import com.ameron32.libgurps.tools.StringTools;
import com.ameron32.testing.ImportTesting;

public class InformationDialog extends GURPSDialog {

	private final Activity sourceActivity;
	private final Context context;
//	private final int resourceId;
	public InformationDialog(Context context, Activity sourceActivity) {
		super(context, R.layout.information_dialog);
		this.context = context;
		this.sourceActivity = sourceActivity;

//		this.resourceId = resourceId;
		
		tvTitle = (TextView) findViewById(R.id.tvTitle);
		tvClass = (TextView) findViewById(R.id.tvClass);
		tvContent = (TextView) findViewById(R.id.tvContent);
		tvObjectId = (TextView) findViewById(R.id.tvObjectId);
		tvPage = (TextView) findViewById(R.id.tvPage);
		pbLoading = (ProgressBar) findViewById(R.id.pbLoading);
		bClose = (Button) findViewById(R.id.bClose);
		bBug = (Button) findViewById(R.id.bBug);
	}

	private final TextView tvTitle, tvClass, tvContent, tvObjectId, tvPage;
	private final Button bClose, bBug;
	private final ProgressBar pbLoading;
	private void init() {
		tvContent.setOnClickListener(ocl);
		pbLoading.setMax(100);
		bClose.setOnClickListener(ocl);
		bBug.setOnClickListener(ocl);
		setOnKeyListener(this);
	}
	
	View.OnClickListener ocl = new View.OnClickListener() {
		@Override
		public void onClick(View v) {
			switch(v.getId()) {
			case R.id.bClose:
				InformationDialog.this.dismiss();
				break;
			case R.id.bBug:
				final BugReportDialog brd = new BugReportDialog(context, sourceActivity);
				brd.set(GURPSObject.findGURPSObjectById(goObjectId));
				brd.show();
				break;
			}
		}
	};
	
	long goObjectId;
	public void set(final GURPSObject go) {
		goObjectId = go.getObjectId();
		init();

		if (go instanceof Advantage || go instanceof Skill) {
			String pageNumber = "";
			if (go instanceof Advantage) {
				pageNumber = ((Advantage) go).getiPage() + "";
			} else if (go instanceof Skill) {
				pageNumber = ((Skill) go).getiPage() + "";
			}
			tvPage.setText(pageNumber);
			tvPage.setOnClickListener(new PageLinkListener(go));
		} else {
			tvPage.setText("No Page");
		}
		
		tvTitle.setText(go.getName());
		tvClass.setText(go.getClass().getSimpleName());
		setLinkText(tvContent, StringTools.convertBarsToParagraphs(
				go.getDescription())
//				+ "\n" + go.getSID() 
				+ "\n\n" + ((go.getNotes().size() > 0) ? go.getNotes() : "[No Notes]"));
		tvObjectId.setText("[ " + go.getSID().trim().toUpperCase() + " ]");
	}
	
	public void show() {
		super.show();
		pbLoading.setProgress(100);
	}
	
	private static final List<String> allNames = new ArrayList<String>();
	private static final ArrayList<IIIS> spans = new ArrayList<IIIS>();
	private void setLinkText(TextView tv, String description) {
		// load the link names
		allNames.clear();
		for (final GURPSObject go : ImportTesting.getEverything()) {
			allNames.add(go.getName());
		}
		
		final SpannableString ss = new SpannableString(description);
		
		// analyze the string list for links
		spans.clear();
		for (String name : allNames) {
			int cursor = 0;
			do {
				final String subString = description.substring(cursor);
				cursor += setLinkSpan(new String(subString), cursor, name, ss);
			} while (cursor < description.length());
		}

		tv.setText(ss);
		tv.setMovementMethod(LinkMovementMethod.getInstance());
		
	}
	
	private int setLinkSpan(String s, int startingPosition, String name, SpannableString ss) {
		if (s.contains(name)) {
			final ClickableString clickableString = new ClickableString(new ReferenceLinkListener(name));
			
			final int startOfLink = s.indexOf(name) + startingPosition;
			final int linkLength = name.length();
			final int endOfLink = startOfLink + linkLength;
			
			ss.setSpan(clickableString, startOfLink, endOfLink, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
			spans.add(new IIIS(startOfLink, name));
			
			return endOfLink;
		} else {
			return s.length();
		}
	}
	
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
	    private final View.OnClickListener mListener;          
	    public ClickableString(View.OnClickListener listener) {              
	        mListener = listener;  
	    }
	    @Override  
	    public void onClick(View v) {  
	        mListener.onClick(v);  
	    }
	}
	
	private class ReferenceLinkListener implements View.OnClickListener {
		String name;
		public ReferenceLinkListener(String name) {
			this.name = name;
		}
		
		@Override
		public void onClick(View v) {
//			Toast.makeText(context, name, Toast.LENGTH_LONG).show();
			boolean found = false;
			for (GURPSObject go : ImportTesting.getEverything()) {
				if (!found && go.getName().equalsIgnoreCase(name)) {
					found = true;
			    	final InformationDialog inf = new InformationDialog(context, sourceActivity);
			    	inf.set(go);
			    	inf.show();
				}
			}
		}
		
	}
	
	public class PageLinkListener implements View.OnClickListener {

		final GURPSObject go;
		public PageLinkListener(final GURPSObject go) {
			this.go = go;
		}
		
		@Override
		public void onClick(View v) {
			// TODO change to dynamic
			if (go instanceof Advantage) {
				Advantage adv = (Advantage) go;
				String book = (adv.getDocumentSource().equals("BasicSet")) ? "B" : "";
				String pageNumber = adv.getiPage() + "";
				
				new ActivityTools(sourceActivity, MainActivity.getSDDir()).openInPDF(book, pageNumber);;
			} else if (go instanceof Skill) {
				Skill sk = (Skill) go;
				String book = (sk.getDocumentSource().equals("BasicSet")) ? "B" : "";
				String pageNumber = sk.getiPage() + "";
				
				new ActivityTools(sourceActivity, MainActivity.getSDDir()).openInPDF(book, pageNumber);;
			}
		}
	}
}