package com.onshift.mobile.plugins;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.ByteArrayBuffer;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Environment;
import android.util.Log;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;

import com.onshift.mobile.MainActivity;
import com.onshift.mobile.R;
import com.onshift.mobile.TutorialActivity;
import com.onshift.mobile.universal_login.UniversalLoginActivity;
import com.onshift.mobile.utils.API;

public class OSPlugin extends CordovaPlugin {

	private MainActivity activity = null;
	private API api;
	private static final String DOWNLOADSFOLDER = "/OnShiftReports";
	private static final String DOWNLOADSFULLPATH = "/OnShiftReports/report.pdf";
	private static final String PRINT_MESSAGE = "To print your weekly schedule you must have a printing app installed on your device or use Gmail to email the pdf";

	/**
	 * Override the plugin initialise method and set the Activity as an instance
	 * variable.
	 */
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) {
		super.initialize(cordova, webView);
		// Set the Activity.
		this.activity = (MainActivity) cordova.getActivity();
	}

	@Override
	public boolean execute(String action, final JSONArray args,
			final CallbackContext callbackContext) throws JSONException {
		if ("show".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					activity.showDrawer();
					callbackContext.success(); // Thread-safe.
				}
			});
			return true;
		} else if ("hide".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					activity.closeDrawer();
					callbackContext.success(); // Thread-safe.
				}
			});
			return true;
		} else if ("lock".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					activity.lockDrawer();
					callbackContext.success(); // Thread-safe.
				}
			});
			return true;
		} else if ("startTutorial".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					Intent intent = new Intent(activity, TutorialActivity.class);
					activity.startActivity(intent);
					callbackContext.success(); // Thread-safe.
				}
			});
			return true;
		} else if ("unlock".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					activity.unlockDrawer();
					callbackContext.success(); // Thread-safe.
				}
			});
			return true;
		} else if ("getdevicetype".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					sendBack(
							getDeviceName() + "|"
									+ activity.vc.getCurrentVersion(),
									callbackContext);
					callbackContext.success(); // Thread-safe.
				}
			});
			return true;
		}  else if ("getappconfig".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					SharedPreferences prefs = activity.getSharedPreferences(
							"mysettings", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = prefs.edit();
					JSONObject json = new JSONObject();
					String result = "";
					try {
						json.put("user_name", prefs.getString("user_name", " "));
						json.put("apiURL", prefs.getString("apiURL", "https://api.onshift.com"));
						json.put("authURL", prefs.getString("authURL", "https://auth.onshift.com"));
						json.put("deviceType", getDeviceName());
						json.put("appVersion", activity.vc.getCurrentVersion());
						//Convert the user json object back to json and to string for cordova sanity
						JSONObject jsonObj = new JSONObject(prefs.getString("userJSON", ""));
						json.put("userJson", jsonObj.toString());
						if (prefs.getString("UserID", "").length() == 0) {
							editor.putString("UserID", jsonObj.getString("id"));
							editor.commit();
						}
						result = json.toString();
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sendBack(result, callbackContext);
					callbackContext.success(); // Thread-safe.
				}
			});
			return true;
		} else if ("updateusername".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					final SharedPreferences settings = activity
							.getSharedPreferences("mysettings",
									Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					String username = "";
					try {
						username = args.getString(0);
					} catch (Exception e) {

					}
					editor.putString("user_name", username);
					editor.commit();
					callbackContext.success();
				}
			});
			return true;
		} else if ("showKeyboard".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					InputMethodManager mgr = (InputMethodManager) activity
							.getSystemService(Context.INPUT_METHOD_SERVICE);
					mgr.showSoftInput(webView, InputMethodManager.SHOW_IMPLICIT);
					((InputMethodManager) activity
							.getSystemService(Context.INPUT_METHOD_SERVICE))
							.showSoftInput(webView, 0);
					callbackContext.success(); // Thread-safe.
				}
			});
			return true;
		} else if ("showEmailComposer".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					Intent intent = new Intent(Intent.ACTION_SEND);
					String[] recipients = { "appsupport@onshift.com" };
					intent.putExtra(Intent.EXTRA_EMAIL, recipients);
					intent.putExtra(Intent.EXTRA_SUBJECT, "OnShift");
					intent.setType("text/html");
					activity.startActivity(Intent.createChooser(intent,
							"Send mail"));
				}
			});
			return true;
		} else if ("goBackToLogin".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					final SharedPreferences settings = activity
							.getSharedPreferences("mysettings",
									Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					String UserID = "";
					try {
						UserID = args.getString(0);
					} catch (Exception e) {

					}
					editor.putString("UserID", UserID);
					editor.putString("user_name", "");
					editor.putString("password", "");
					editor.putString("userJSON", "");
					editor.commit();
					Intent mainIntent = new Intent(activity,
							UniversalLoginActivity.class);
					activity.startActivity(mainIntent);
					activity.finish();
				}
			});
			return true;
		} else if ("showPrintDialog".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					showPrintDialog();
					sendBack("done", callbackContext);
					callbackContext.success();
				}
			});
			return true;
		} else if ("printAction".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					Log.d("ShowDrawer", Environment
							.getExternalStorageDirectory().toString()
							+ DOWNLOADSFULLPATH);
					Intent sendIntent = new Intent(
							android.content.Intent.ACTION_SEND);
					Intent viewIntent = new Intent(Intent.ACTION_VIEW);
					sendIntent.setType("application/pdf");
					viewIntent.setType("application/pdf");
					Uri uriFile = Uri.fromFile(new File(Environment
							.getExternalStorageDirectory().toString()
							+ DOWNLOADSFULLPATH));
					sendIntent.putExtra(android.content.Intent.EXTRA_STREAM,
							uriFile);
					sendIntent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
					sendIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
					Intent openInChooser = Intent.createChooser(sendIntent,
							"Print/view report using");
					PackageManager pm = activity.getPackageManager();
					List<ResolveInfo> resInfo = pm.queryIntentActivities(
							viewIntent, 0);
					Intent[] extraIntents = new Intent[resInfo.size()];
					for (int i = 0; i < resInfo.size(); i++) {
						ResolveInfo ri = resInfo.get(i);
						String packageName = ri.activityInfo.packageName;
						Intent intent = new Intent();
						intent.setComponent(new ComponentName(packageName,
								ri.activityInfo.name));
						intent.setAction(Intent.ACTION_VIEW);
						intent.setDataAndType(uriFile, "application/pdf");
						extraIntents[i] = intent;
					}
					openInChooser.putExtra(Intent.EXTRA_INITIAL_INTENTS,
							extraIntents);
					activity.startActivity(openInChooser);
				}
			});
			return true;
		} else if ("download".equals(action)) {
			api = new API(this.activity);
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					DownloadReportTask dt = new DownloadReportTask();
					try {
						dt.execute(args.getString(0), args.getString(1));
					} catch (JSONException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					sendBack("done", callbackContext);
					callbackContext.success();
				}
			});
			return true;
		} else if ("showPicker".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					activity.showDateTimeDialog();
					sendBack("done", callbackContext);
					callbackContext.success();
				}
			});
			return true;
		} else if ("getDeviceVersion".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					sendBack(activity.vc.getCurrentVersion(), callbackContext);
					Log.d("OSPlugin",
							"got app version: "
									+ activity.vc.getCurrentVersion());
					callbackContext.success();
				}
			});
			return true;
		} else if ("showDatePicker".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					activity.showDateDialog();
					sendBack("done", callbackContext);
					callbackContext.success();
				}
			});
			return true;
		} else if ("getServerUrls".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					SharedPreferences prefs = activity.getSharedPreferences(
							"mysettings", Context.MODE_PRIVATE);
					sendBack(
							prefs.getString("apiURL",
									"https://api.onshift.com/")
									+ "|"
									+ prefs.getString("webURL",
											"https://v2.onshift.com/"),
											callbackContext);
					callbackContext.success();
				}
			});
			return true;
		} else if ("callPushMessage".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					SharedPreferences settings = activity.getSharedPreferences(
							"mysettings", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					String PendingMessage = settings.getString(
							"PendingMessage", "");
					editor.putString("PendingMessage", "");
					editor.commit();
					if (PendingMessage.length() > 0) {
						MainActivity.sendNotificationToWebview(PendingMessage);
					}
					sendBack("done", callbackContext);
					callbackContext.success();
				}
			});
			return true;
		} else if ("saveuser".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					final SharedPreferences settings = activity.getSharedPreferences("mysettings", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					String UserID = "";
					try{
						UserID = args.getString(0);
					}catch(Exception e){

					}
					editor.putString("UserID", UserID);
					editor.commit();
					callbackContext.success(); // Thread-safe.
				}
			});
			return true;
		} else if ("updatePending".equals(action)) {
			this.activity.runOnUiThread(new Runnable() {
				public void run() {
					SharedPreferences settings = activity.getSharedPreferences(
							"mysettings", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("PendingMessage", "");
					editor.commit();
					sendBack("done", callbackContext);
					callbackContext.success();
				}
			});
			return true;
		}


		return false;
	}

	public String getDeviceName() {
		String manufacturer = Build.MANUFACTURER;
		String model = Build.MODEL;
		if (model.startsWith(manufacturer)) {
			return capitalize(model);
		} else {
			return capitalize(manufacturer) + " " + model;
		}
	}

	private String capitalize(String s) {
		if (s == null || s.length() == 0) {
			return "";
		}
		char first = s.charAt(0);
		if (Character.isUpperCase(first)) {
			return s;
		} else {
			return Character.toUpperCase(first) + s.substring(1);
		}
	}

	private void sendBack(String message, CallbackContext callbackContext) {
		if (message != null && message.length() > 0) {
			callbackContext.success(message);
		} else {
			callbackContext.error(message);
		}
	}

	public void showPrintDialog() {
		AlertDialog.Builder adb = new AlertDialog.Builder(this.activity);
		LayoutInflater adbInflater = LayoutInflater.from(this.activity);
		View eulaLayout = adbInflater.inflate(R.layout.checkbox, null);
		final CheckBox dontShowAgain = (CheckBox) eulaLayout
				.findViewById(R.id.skip);
		adb.setView(eulaLayout);
		adb.setTitle("OnShift Mobile");
		adb.setMessage(PRINT_MESSAGE);
		adb.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int which) {
				if (dontShowAgain.isChecked()) {
					SharedPreferences settings = activity.getSharedPreferences(
							"mysettings", Context.MODE_PRIVATE);
					SharedPreferences.Editor editor = settings.edit();
					editor.putString("skipPrintMessage", "true");
					editor.commit();
				}
				MainActivity
				.sendMessageToCordovaView("OnShift.Utils.printWeeklyReport()");
			}
		});
		SharedPreferences settings = this.activity.getSharedPreferences(
				"mysettings", Context.MODE_PRIVATE);
		String skipMessage = settings.getString("skipPrintMessage", "false");
		if (skipMessage.equals("false")) {
			adb.show();
		} else {
			MainActivity
			.sendMessageToCordovaView("OnShift.Utils.printWeeklyReport()");
		}
	}

	// Download report async task
	public class DownloadReportTask extends AsyncTask<String, Void, Void> {
		private ProgressDialog dialog1;

		@SuppressLint("SimpleDateFormat")
		@Override
		protected Void doInBackground(String... params) {
			String accessToken = params[0];
			String urlString = params[1];
			Format formatter = new SimpleDateFormat("MM-dd-yyyy");
			Date today = Calendar.getInstance().getTime();
			List<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>(
					13);
			nameValuePairs.add(new BasicNameValuePair("date", formatter
					.format(today)));
			nameValuePairs.add(new BasicNameValuePair("access_token",
					accessToken));
			try {
				HttpClient httpclient = api.getApiClient();
				HttpPost httppost = new HttpPost(urlString);
				httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs,
						HTTP.UTF_8));
				// Execute HTTP Post Request
				HttpResponse response = httpclient.execute(httppost);
				HttpEntity entity = response.getEntity();
				// set the path where we want to save the file
				File SDCardRoot = new File(
						Environment.getExternalStorageDirectory()
						+ DOWNLOADSFOLDER);
				// create a new file, specifying the path, and the filename
				File file = new File(SDCardRoot, "report.pdf");
				// this will be used to write the downloaded data into the file
				// we created
				FileOutputStream fileOutput;
				fileOutput = new FileOutputStream(file);
				// this will be used in reading the data
				InputStream inputStream = entity.getContent();
				BufferedInputStream bis = new BufferedInputStream(inputStream);
				/*
				 * Read bytes to the Buffer until there is nothing more to
				 * read(-1).
				 */
				ByteArrayBuffer baf = new ByteArrayBuffer(50);
				int current = 0;
				while ((current = bis.read()) != -1) {
					baf.append((byte) current);
				}
				Base64.encodeToString(baf.toByteArray(), Base64.DEFAULT);
				fileOutput.write(baf.toByteArray());
				// close the output stream when done
				fileOutput.close();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			return null;
		}

		protected void onPreExecute() {
			super.onPreExecute();
			dialog1 = ProgressDialog.show(activity, "",
					"Downloading file, please wait....", true);
		}

		protected void onPostExecute(Void result) {
			super.onPostExecute(result);
			if (dialog1.isShowing()) {
				dialog1.dismiss();
			}
		}
	}
}
