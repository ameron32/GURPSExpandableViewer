package com.ameron32.gurpsviewertest;

import com.ameron32.testing.ImportTesting;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;

public class ProgressMonitor extends AsyncTask<String, Integer, String> {

	private final ProgressDialog pDialog;
	private final ImportTesting it;
	private final Runnable completionTask;
	
	public ProgressMonitor(Context context, ImportTesting it, Runnable completionTask) {
		this.it = it;
		this.completionTask = completionTask;
		pDialog = new ProgressDialog(context);
        pDialog.setTitle("Importing CSV data...");
        pDialog.setMessage("");
        pDialog.setIndeterminate(false);
        pDialog.setMax(totalStages);
        pDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        pDialog.setCancelable(false);
	}
	

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		initStages();
		
		pDialog.setMessage(stages[stage]);
		pDialog.show();
	}
	
	private void initStages() {
		// update me as stages are added
		totalStages = 5;
		stages = new String[totalStages];
		
		stages[0] = "Importing...";
		stages[1] = "Generating Display...";
		stages[2] = "Loading Options...";
		stages[3] = "Generating Final Text...";
		stages[4] = "Complete!";
	}

	private int stage = 0;
	private int totalStages = 0;
	private String[] stages;
	@Override
	protected String doInBackground(String... params) {

		it.importer();
		stage++;
		publishProgress(stage * 25);

		it.display1();
		stage++;
		publishProgress(stage * 25);
		
		it.attackOptionLoading();
		stage++;
		publishProgress(stage * 25);
		
		it.display2();
		stage++;
		publishProgress(stage * 25);
		
		return null;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		pDialog.dismiss();
		if (completionTask != null) 
			completionTask.run();
	}


	@Override
	protected void onProgressUpdate(Integer... values) {
		super.onProgressUpdate(values);
		pDialog.setMessage(stages[stage]);
		pDialog.setProgress(values[0]);
	}
	
}
