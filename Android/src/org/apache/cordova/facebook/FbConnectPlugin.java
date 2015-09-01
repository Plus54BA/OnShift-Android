package org.apache.cordova.facebook;

import org.apache.cordova.CallbackContext;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.PluginResult;
import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.onshift.mobile.FacebookLoginActivity;

public class FbConnectPlugin extends CordovaPlugin {

	@Override
	public boolean execute(String action, JSONArray args, final CallbackContext callbackContext) throws JSONException {
	    PluginResult pr = new PluginResult(PluginResult.Status.NO_RESULT);
	    pr.setKeepCallback(true);

	    //Handle login in a separate activity
	    if (action.equals("login")) {
	         cordova.getActivity().runOnUiThread(new Runnable() {
	    	      @Override
	    	      public void run() {
	    	          Context context = cordova.getActivity()
	    	                    .getApplicationContext();
	    	          Intent intent = new Intent(context, FacebookLoginActivity.class);
	    	          cordova.getActivity().startActivity(intent);
	    	      }
	    	 });
			
			return true;
	    } else if(action.equals("getToken")) {
	    	final SharedPreferences settings = cordova.getActivity().getSharedPreferences("mysettings", Context.MODE_PRIVATE);
	      	String token = settings.getString("FB_ACCESS_TOKEN", "");
	    	this.sendBack(token, callbackContext);
			return true;
	    }
	    
		return false;
	}
	
	private void sendBack(String message, CallbackContext callbackContext) {
		if (message != null && message.length() > 0) {
			callbackContext.success(message);
		} else {
			callbackContext.error(message);
		}
	}

}
