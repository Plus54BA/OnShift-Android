package com.onshift.mobile.plugins;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.json.JSONArray;
import org.json.JSONException;

import android.app.ProgressDialog;
import android.util.Log;

public class WaitingDialog extends CordovaPlugin {
	private static final String TAG = "WaitDialog";
	private ProgressDialog waitingDialog = null;
	int sdkVersion = android.os.Build.VERSION.SDK_INT;
	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
		if ("show".equals(action)) {
			final String text = args.getString(0);
			cordova.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					showWaitingDialog(text);
					callbackContext.success(); // Thread-safe.
				}
			});
			return true;
		} else if ("hide".equals(action)) {
			cordova.getActivity().runOnUiThread(new Runnable() {
				public void run() {
					hideWaitingDialog();
					callbackContext.success(); // Thread-safe.
				}
			});		
		}
		return false;
	}

	public void showWaitingDialog(String text) {	
		if(!this.cordova.getActivity().isFinishing()){
			waitingDialog = ProgressDialog.show(this.cordova.getActivity(), "", text, false, false);
		}
	}

	public void hideWaitingDialog() {
		if (waitingDialog != null) {
			waitingDialog.dismiss();
			Log.d(TAG, "Dialog dismissed");
			waitingDialog = null;
		} else {
			Log.d(TAG, "Nothing to dismiss");
		}
	}

}
