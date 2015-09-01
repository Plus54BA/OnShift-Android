package com.onshift.mobile.helpers;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.onshift.mobile.utils.API;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

public class VersionChecker {

	private static final String TAG = "VersionChecker";
	private Activity activity;
	private AlertDialogButtonListener listener;
	private static final String versionContentUrl = "https://v2.onshift.com/get_app_version";
	private String version;
	private API api;

	public VersionChecker(Activity act) {
		this.activity = act;
		this.listener = new AlertDialogButtonListener();
		this.api = new API(this.activity);
	}

	public void checkVersion() {
		VersionCheckRequest request = new VersionCheckRequest();
		request.execute(versionContentUrl);
	}

	private String getPlayStoreUrl() {
		String id = activity.getApplicationInfo().packageName; 
		return "market://details?id=" + id;
	}

	public String getCurrentVersion() {
		return this.getCurrentVersionName() + "("
				+ this.getCurrentVersionCode() + ")";
	}

	private int getCurrentVersionCode() {
		int currentVersionCode = 0;
		PackageInfo pInfo;
		try {
			pInfo = activity.getPackageManager().getPackageInfo(
					activity.getPackageName(), 0);
			currentVersionCode = pInfo.versionCode;
		} catch (NameNotFoundException e) {
			// return 0
		}
		return currentVersionCode;
	}

	private String getCurrentVersionName() {
		String currentVersionName = "";
		PackageInfo pInfo;
		try {
			pInfo = activity.getPackageManager().getPackageInfo(
					activity.getPackageName(), 0);
			currentVersionName = pInfo.versionName;
		} catch (NameNotFoundException e) {
			// return 0
		}
		return currentVersionName;
	}

	private void showDialog() {
		AlertDialog.Builder builder = new AlertDialog.Builder(activity);
		builder.setTitle("New Update Available");
		builder.setMessage("A new version of OnShift Mobile is available to download");
		builder.setPositiveButton("Download", listener);
		builder.setCancelable(true);
		AlertDialog dialog = builder.create();
		dialog.setCancelable(false);
		if (activity != null && !activity.isFinishing()) {
			dialog.show();
		}
	}

	private void startUpdate() {
		try {
			Uri uri = Uri.parse(getPlayStoreUrl());
			Intent intent = new Intent(Intent.ACTION_VIEW, uri);
			activity.startActivity(intent);
		} catch (Exception e) {
			Log.e(TAG, "is update url correct?" + e);
		}
	}

	private class AlertDialogButtonListener implements
	DialogInterface.OnClickListener {
		@Override
		public void onClick(DialogInterface dialog, int which) {
			startUpdate();
		}
	}

	private class VersionCheckRequest extends AsyncTask<String, Void, String> {
		int statusCode;

		@Override
		protected String doInBackground(String... uri) {
			String responseBody = null;
			HttpResponse httpResponse = null;
			ByteArrayOutputStream out = null;
			try {
				HttpGet httpget = new HttpGet(uri[0]);
				httpResponse = api.getApiClient().execute(httpget);
				statusCode = httpResponse.getStatusLine().getStatusCode();
				if (statusCode == HttpStatus.SC_OK) {
					out = new ByteArrayOutputStream();
					httpResponse.getEntity().writeTo(out);
					responseBody = out.toString();
				}
			} catch (Exception e) {
				Log.e(TAG, e.toString());
			} finally {
				if (out != null) {
					try {
						out.close();
					} catch (IOException e) {
						Log.e(TAG, e.toString());
					}
				}
			}
			return responseBody;
		}

		@Override
		protected void onPostExecute(String result) {
			version = "";
			int playStoreCode = 0;

			if (statusCode != HttpStatus.SC_OK) {
				Log.e(TAG, "Response invalid. status code=" + statusCode);
			} else {
				try {
					// json format from server:
					JSONObject json = (JSONObject) new JSONTokener(result)
					.nextValue();
					version = json.optString("version");
					playStoreCode = Integer.parseInt(version);
					if (playStoreCode > getCurrentVersionCode()) {
						showDialog();
					}
				} catch (JSONException e) {
					Log.e(TAG,
							"is your server response have valid json format?");
				} catch (Exception e) {
					Log.e(TAG, e.toString());
				}
			}
		}
	}
}
