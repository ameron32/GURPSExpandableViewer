package com.ameron32.gurpsviewertest;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

public class Downloader extends AsyncTask<String, Integer, String> {

	private static String dlDir = Environment.getExternalStorageDirectory()
			.getPath() + "/ameron32projects/GURPSBattleFlow/";
	private static String[] dlFiles = { "tmp.123" };
	public static String getDlDir() {
		return dlDir;
	}
	public static String[] getDlFiles() {
		return dlFiles;
	}
	private List<String> successfulDownloads;
	public String getDlPath() {
		return dlDir + dlFiles;
	}
	private boolean isUpdate;

	public void setUpdate(boolean isUpdate) {
		this.isUpdate = isUpdate;
	}

	private final ProgressDialog mDownloadDialog;
	private Runnable doNext;
	public Downloader(Context context, Runnable doNext) {
		this.doNext = doNext;
		complete = false;
		isUpdate = false;
		
        mDownloadDialog = new ProgressDialog(context);
        mDownloadDialog.setTitle("Downloading from Dropbox...");
        mDownloadDialog.setMessage("");
        mDownloadDialog.setIndeterminate(false);
        mDownloadDialog.setMax(100);
        mDownloadDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mDownloadDialog.setCancelable(false);
	}

	private String currentDlFile = "";
	@Override
	protected String doInBackground(String... sUrl) {
		File fDF;
		for (short u = 0; u < sUrl.length; u++) {
			fDF = new File(dlDir + dlFiles[u]);
			if ((!fDF.exists()) || (fDF.exists() && isUpdate)) {
				complete = false;
				try {
					currentDlFile = dlFiles[u];
				    URL url = new URL(sUrl[u]);
					URLConnection connection = url.openConnection();
					connection.connect();
					// this will be useful so that you can show a typical 0-100%
					// progress bar
					int fileLength = connection.getContentLength();

					// check if directory structure exists, or make it
					File fD = new File(dlDir);
					if (!fD.isDirectory() || !fD.exists())
						fD.mkdirs();

					// download the file
					InputStream input = new BufferedInputStream(
							url.openStream());
					OutputStream output = new FileOutputStream(dlDir
							+ dlFiles[u]);

					byte data[] = new byte[1024];
					long total = 0;
					int count;
					while ((count = input.read(data)) != -1) {
						total += count;
						// publishing the progress....
						publishProgress((int) (total * 100 / fileLength));
						output.write(data, 0, count);
					}

					output.flush();
					output.close();
					input.close();

					// record the success of the download
					if (successfulDownloads == null)
						successfulDownloads = new ArrayList<String>();
					successfulDownloads.add(dlDir + dlFiles[u]);
				} catch (Exception e) {
					Log.e("Downloader", "Error from: " + sUrl[u] + "\n"
							+ "Download Failed: " + dlDir + dlFiles[u]);
				}
			}
		}
		complete = true;
		return null;
	}

	@Override
	protected void onPreExecute() {
		super.onPreExecute();
		mDownloadDialog.show();
	}

	@Override
	protected void onProgressUpdate(Integer... progress) {
		super.onProgressUpdate(progress);
		mDownloadDialog.setMessage(currentDlFile);
		mDownloadDialog.setProgress(progress[0]);
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (successfulDownloads != null)
			if (successfulDownloads.size() > 0) {
				Log.e("Downloads", successfulDownloads.size()
						+ " file(s) downloaded successfully");
				StringBuilder sb = new StringBuilder();
				for (String s : successfulDownloads)
					sb.append(s + "\n");
				Log.e("Downloads", sb.toString());
			}
        mDownloadDialog.dismiss();
		if (doNext != null)
			doNext.run();
	}

	public void setDlDir(String dlDir) {
		Downloader.dlDir = dlDir;
	}

	public void setDlFiles(String[] dlFiles) {
		Downloader.dlFiles = dlFiles;
	}

	private boolean complete;

	public boolean isCompleted() {
		return complete;
	}

}
