package com.onshift.mobile.utils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;

import android.os.Handler;
import android.os.AsyncTask;
import android.os.Looper;
import android.util.Log;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;

import com.onshift.mobile.fragments.CalloffsListFragment;
import com.onshift.mobile.helpers.SSLHttpClient;
import com.onshift.mobile.helpers.CommonHelper;
import com.onshift.mobile.models.Callbacks;
import com.onshift.mobile.utils.ConnectionDetector;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;

public class API {
	public static final String API_CALLOFFS_COUNT = "/count";
	
	private static final String LOG_TAG = "API";
	private Activity activity;
	private HttpClient client;
	private HttpPost post;
	private HttpGet get;
	private HttpEntity results = null;
	private Callbacks callbacks;
	private SharedPreferences prefs;
	private String apiUrl;
	private Boolean showProgress = true;
	private ConnectionDetector cd;
	Boolean isInternetPresent;
	private Handler mainHandler = new Handler(Looper.getMainLooper());
	

	public API(Activity activity) {
		this.activity = activity;
		prefs = this.activity.getSharedPreferences("mysettings",
				Context.MODE_PRIVATE);
		cd = new ConnectionDetector(this.activity.getApplicationContext());
		isInternetPresent = cd.isConnectingToInternet();
	}

	public void setClient() {
		this.apiUrl = prefs.getString("authURL", "https://auth.onshift.com");
		client = new SSLHttpClient(this.activity.getApplicationContext(), this.apiUrl);
	}
	
	public HttpClient getApiClient(){
		this.apiUrl = prefs.getString("apiURL", "https://api.onshift.com");
		client = new SSLHttpClient(this.activity.getApplicationContext(), this.apiUrl);
		return client;
	}

	public void setApiClient(Boolean showProg) {
		showProgress = showProg;
		SharedPreferences.Editor editor = prefs.edit();
		this.apiUrl = prefs.getString("apiURL", "https://api.onshift.com");
		client = new SSLHttpClient(this.activity.getApplicationContext(), this.apiUrl);
	}

	public void post(List<NameValuePair> params, String path,
			Callbacks callbacks) {
		this.callbacks = callbacks;
		try {	
			if (isInternetPresent)
			{
				new postOperation().execute(buildURLWithParams(params, path));
			}else{
				callbacks.failure("No internet connection");
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, "Error: " + e.toString() + " ---> " + path);
		}
	}
	
	public void get(List<NameValuePair> params, String path,
			Callbacks callbacks) {
		this.callbacks = callbacks;
		try {			
			if (isInternetPresent)
			{
				new getOperation().execute(buildURLWithParams(params, path));
			}else{
				callbacks.failure("No internet connection");
			}
		} catch (Exception e) {
			Log.d(LOG_TAG, "Error: " + e.toString() + " ---> " + path);
		}
	}

	private String buildURLWithParams(List<NameValuePair> params, String path) {
		String url = this.apiUrl + path;

		if (params.size() > 0) {
			url += "?";
			int cnt = 0;
			for (NameValuePair nvp : params) {
				url += nvp.getName() + "=" + nvp.getValue();
				cnt++;

				if (cnt < params.size()) {
					url += "&";
				}
			}
		}
		
		Log.d(LOG_TAG, "URL: " + url);
		
		return url;
	}

	// Async Classes
	private class postOperation extends AsyncTask<String, Void, String> {
		private ProgressDialog loadDialog;
		String jsonStr;

		@Override
		protected String doInBackground(String... _path) {
			try {
				post = new HttpPost(_path[0]);
				if(prefs.getString("accessToken", "") != null && !prefs.getString("accessToken", "").isEmpty()){
					post.setHeader("Access-Token", prefs.getString("accessToken", ""));
				}
				HttpResponse response = client.execute(post);
				jsonStr = EntityUtils.toString(response.getEntity());
			} catch (Exception e) {
				Log.d(LOG_TAG, "Error: " + e.toString());
				mainHandler.post(new Runnable(){
					@Override
					public void run(){
						callbacks.failure("No JSON Response");
					}
				});
			} finally {
				if (results != null) {
					try {
						results.consumeContent();
					} catch (IOException e) {
						Log.d(LOG_TAG, "Error: " + e.toString());
						mainHandler.post(new Runnable(){
							@Override
							public void run(){
								callbacks.failure("No JSON Response");
							}
						});
					}
				}
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (showProgress) {
				loadDialog = ProgressDialog.show(activity, "",
						"Please wait....", true);
			}
		}

		@Override
		protected void onPostExecute(String res) {
			Log.d(LOG_TAG, "PostExecute: " + jsonStr);
			super.onPostExecute(res);
			if (loadDialog != null) {
				if (loadDialog.isShowing()) {
					loadDialog.dismiss();
				}
			}
			if (jsonStr != null) {
				mainHandler.post(new Runnable(){
					@Override
					public void run(){
						callbacks.success(jsonStr);
					}
				});
			} else {
				mainHandler.post(new Runnable(){
					@Override
					public void run(){
						callbacks.failure("No JSON Response");
					}
				});
			}
		}
	}
	
	private class getOperation extends AsyncTask<String, Void, String> {
		private ProgressDialog loadDialog;
		String jsonStr;

		@Override
		protected String doInBackground(String... _path) {
			try {
				get = new HttpGet(_path[0]);
				if(prefs.getString("accessToken", "") != null && !prefs.getString("accessToken", "").isEmpty()){
					get.setHeader("Access-Token", prefs.getString("accessToken", ""));
				}
				HttpResponse response = client.execute(get);
				jsonStr = EntityUtils.toString(response.getEntity());
			} catch (Exception e) {
				Log.d(LOG_TAG, "Error: " + e.toString());
				mainHandler.post(new Runnable(){
					@Override
					public void run(){
						callbacks.failure("No JSON Response");
					}
				});
			} finally {
				if (results != null) {
					try {
						results.consumeContent();
					} catch (IOException e) {
						Log.d(LOG_TAG, "Error: " + e.toString());
						callbacks.failure(e.toString());
					}
				}
			}
			return null;
		}

		@Override
		protected void onPreExecute() {
			super.onPreExecute();
			if (showProgress) {
				loadDialog = ProgressDialog.show(activity, "",
						"Please wait....", true);
			}
		}

		@Override
		protected void onPostExecute(String res) {
			Log.d(LOG_TAG, "PostExecute: " + jsonStr);
			super.onPostExecute(res);
			if (loadDialog != null) {
				if (loadDialog.isShowing()) {
					loadDialog.dismiss();
				}
			}
			if (jsonStr != null) {
				mainHandler.post(new Runnable(){
					@Override
					public void run(){
						callbacks.success(jsonStr);
					}
				});
			} else {
				mainHandler.post(new Runnable(){
					@Override
					public void run(){
						callbacks.failure("No JSON Response");
					}
				});
			}
		}
	}

}
