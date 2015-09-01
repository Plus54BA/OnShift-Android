package com.onshift.mobile.plugins;

import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaWebView;
import org.json.JSONArray;
import org.json.JSONException;
//import org.json.JSONObject;


import android.app.NotificationManager;
import android.content.Context;
import android.util.Log;

import com.google.android.gcm.GCMRegistrar;
import com.onshift.mobile.MainActivity;
import com.onshift.mobile.R;

public class GCMPlugin extends CordovaPlugin {
	private static final String TAG = "GCM";
	//private static final String SENDER_ID = "441422613471";
	public static NotificationManager myNotificationManager;
	private MainActivity activity = null;

	/** 
	 * Override the plugin initialise method and set the Activity as an 
	 * instance variable.
	 */
	@Override
	public void initialize(CordovaInterface cordova, CordovaWebView webView) 
	{
		super.initialize(cordova, webView);
		// Set the Activity.
		this.activity = (MainActivity) cordova.getActivity();
	}

	@Override
	public boolean execute(String action, JSONArray args,
			CallbackContext callbackContext) throws JSONException {
		String message = "Registered";

		if (action.equals("register")) {
			try {
				GCMRegistrar.checkDevice(this.cordova.getActivity());
				GCMRegistrar.checkManifest(this.cordova.getActivity());

				final String regId = GCMRegistrar
						.getRegistrationId(this.cordova.getActivity());
				Log.i(TAG, "registration id = " + regId);

				if (regId.equals("")) {
					GCMRegistrar.register(this.cordova.getActivity(), this.cordova.getActivity().getResources().getString(R.string.project_number));		
				} else {
					Log.v(TAG, "Already registered");
				}

				message = "Registered";
				this.sendBack(message, callbackContext);
				return true;
			} catch (Exception ex) {
				message = "RegistrationFailed";
				this.sendBack(message, callbackContext);
				return true;
			}
		} else if (action.equals("dismissNotification")) {
			try {
				myNotificationManager = (NotificationManager) this.cordova
						.getActivity().getSystemService(
								Context.NOTIFICATION_SERVICE);
				myNotificationManager.cancelAll();
				//this.activity.resetNotificationCount();
				MainActivity.resetNotificationCount();
				return true;
			} catch (Exception ex) {
				message = "dismissFailed";
				this.sendBack(message, callbackContext);
				return true;
			}
		} else {
			try {
				GCMRegistrar.checkDevice(this.cordova.getActivity());
				GCMRegistrar.checkManifest(this.cordova.getActivity());

				final String regId = GCMRegistrar
						.getRegistrationId(this.cordova.getActivity());
				Log.i(TAG, "registration id = " + regId);

				if (!(regId.equals(""))) {
					GCMRegistrar.unregister(this.cordova.getActivity());
				} else {
					Log.v(TAG, "Device is not registered. Cannot unregister");
				}

				message = "UnRegistered";
				this.sendBack(message, callbackContext);
				return true;
			} catch (Exception ex) {
				message = "UnRegistrationFailed";
				this.sendBack(message, callbackContext);
				return true;
			}
		}

		// return false;
	}

	private void sendBack(String message, CallbackContext callbackContext) {
		if (message != null && message.length() > 0) {
			callbackContext.success(message);
		} else {
			callbackContext.error(message);
		}
	}
}
