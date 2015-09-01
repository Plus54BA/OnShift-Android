package com.onshift.mobile;

import java.util.Arrays;

//import com.facebook.Session;
//import com.facebook.SessionState;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

public class FacebookLoginActivity extends Activity{
	private static final String TAG = "FbActivity";
	//private Session.StatusCallback statusCallback = new SessionStatusCallback();
	private FacebookLoginActivity instance;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.login);

		instance = this;

		AlertDialog.Builder builder1 = new AlertDialog.Builder(this);
		builder1.setMessage("OnShift would like to access your public profile and friend list");
		builder1.setCancelable(false);
		builder1.setPositiveButton("Ok",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {

				/* update facebook
				Session session = Session.getActiveSession();
				if(!(session == null)){

					Session.getActiveSession().closeAndClearTokenInformation();

					if (!session.isOpened() && !session.isClosed()) {
						Log.d(TAG, "session is not null, session already open/close");
						session.openForRead(new Session.OpenRequest(instance)
						.setPermissions(Arrays.asList("basic_info", "email"))
						.setCallback(statusCallback));
					} else {
						Log.d(TAG, "session is not null, opening new session");
						session = Session.openActiveSession(instance, true, Arrays.asList("basic_info", "email"), statusCallback);
					}
				}
				else{
					Log.d(TAG, "session is null, opening session");
					session = Session.openActiveSession(instance, true, Arrays.asList("basic_info", "email"), statusCallback);
				}
				dialog.cancel();
				*/
			}

		});
		builder1.setNegativeButton("Cancel",
				new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				dialog.cancel();
				SavePreference("FB_ACCESS_TOKEN", "");
				finish();
			}
		});

		AlertDialog alert11 = builder1.create();
		alert11.show();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		Log.d(TAG, "got session result");
		super.onActivityResult(requestCode, resultCode, data);
		//Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		Log.d(TAG, "key intercept");
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Log.d(TAG, "clear fb access token");
			SavePreference("FB_ACCESS_TOKEN", "");
			finish();
			return true;
		}
		return super.onKeyUp(keyCode, event);
	}

	private void SavePreference(String key, String value){
		SharedPreferences sharedPreferences = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, value);
		editor.commit();
	}
/*
	private class SessionStatusCallback implements Session.StatusCallback {

		private static final String TAG = "FbConnectCb";

		@Override
		public void call(Session session, SessionState state, Exception exception) {
			// TODO Auto-generated method stub
			Log.d(TAG, "session state: " + state.toString());
			if(state.toString().equals("OPENED")){
				Log.d(TAG, "Saving access token");
				SavePreference("FB_ACCESS_TOKEN", session.getAccessToken());
				session.closeAndClearTokenInformation();
				setResult(RESULT_OK);
				finish();
			}
			else if(state.toString().equals("CLOSED_LOGIN_FAILED")){
				Log.d(TAG, "Saving access token");
				SavePreference("FB_ACCESS_TOKEN", "");
				setResult(RESULT_OK);
				finish();
			}
		}

	}

	*/
}
