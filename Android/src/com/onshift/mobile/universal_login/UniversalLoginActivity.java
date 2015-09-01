package com.onshift.mobile.universal_login;

import io.fabric.sdk.android.Fabric;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.View.OnLongClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;

import com.crashlytics.android.Crashlytics;
import com.newrelic.agent.android.NewRelic;
import com.onshift.mobile.ContainerActivity;
import com.onshift.mobile.FacebookLoginActivity;
import com.onshift.mobile.MainActivity;
import com.onshift.mobile.R;
import com.onshift.mobile.helpers.CommonHelper;
import com.onshift.mobile.models.Callbacks;
import com.onshift.mobile.utils.API;

@SuppressWarnings("deprecation")
public class UniversalLoginActivity extends Activity implements
OnClickListener, OnLongClickListener {
	private static final String LOG_TAG = "UniversalLoginActivity";
	private static final int FB_LOGIN_ACTIVITY_CODE = 1;
	private API api;
	private EditText user, pass;
	private InputMethodManager imm;
	private SharedPreferences prefs;
	private LinearLayout view;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_universal_login);
		Fabric.with(this, new Crashlytics());
		NewRelic.withApplicationToken(
				"AAef51ad68f1db72ea04ba57a981a1b4d98d2d66d7").start(
						this.getApplication());
		api = new API(this);
		prefs = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
		setupButtons();
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onPause() {
		super.onPause();
	}

	@Override
	public void onResume() {
		super.onResume();
		if (!prefs.getString("userJSON", "").isEmpty()) {
			loginUser(prefs.getString("userJSON", ""),
					prefs.getString("role", ""));
		} else {
			view.setVisibility(View.VISIBLE);
		}
	}

	@Override
	public void onClick(View v) {
		// remove test login info
		switch (v.getId()) {

		case R.id.Login_LogIn_Button:
			Log.d(LOG_TAG, "Log In");
			Log.d(LOG_TAG, user.getText().toString());
			if (user.getText().toString().isEmpty()
					|| pass.getText().toString().isEmpty()) {
				CommonHelper.addAlert("Login Failure",
						"Please provide a user name and password",
						UniversalLoginActivity.this);
			} else {
				validateUser();
			}

			break;

		case R.id.Login_FbLogIn_Button:
			Log.d(LOG_TAG, "Facebook Log In");
			Intent intent = new Intent(UniversalLoginActivity.this,
					FacebookLoginActivity.class);
			startActivityForResult(intent, FB_LOGIN_ACTIVITY_CODE);
			break;

		case R.id.Login_ForgotUsername_Button:
			String usernameUrl = prefs.getString("authURL", "https://auth.onshift.com") + "/forgot_username?platform=Android";
			Intent usernameIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(usernameUrl));
			startActivity(usernameIntent);
			break;

		case R.id.Login_ForgotPassword_Button:
			String passwordUrl = prefs.getString("authURL", "https://auth.onshift.com") + "/forgot_password?platform=Android";
			Intent passwordIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(passwordUrl));
			startActivity(passwordIntent);
			break;
		}

	}

	@Override
	public boolean onLongClick(View v) {

		/*switch (v.getId()) {
		case R.id.Login_Main_Layout:
			Intent urlIntent = new Intent(UniversalLoginActivity.this,
					URLPickerActivity.class);
			startActivity(urlIntent);
			break;
		}*/
		
		return true;
	}

	private void setupButtons() {
		Button forgetUserBtn = (Button) findViewById(R.id.Login_ForgotUsername_Button);
		forgetUserBtn.setOnClickListener(this);

		Button forgetPasswordBtn = (Button) findViewById(R.id.Login_ForgotPassword_Button);
		forgetPasswordBtn.setOnClickListener(this);

		Button loginBtn = (Button) findViewById(R.id.Login_LogIn_Button);
		loginBtn.setOnClickListener(this);

		ImageButton facebookLoginBtn = (ImageButton) findViewById(R.id.Login_FbLogIn_Button);
		facebookLoginBtn.setOnClickListener(this);

		view = (LinearLayout) findViewById(R.id.Login_Main_Layout);
		view.setOnLongClickListener(this);
		view.setVisibility(View.INVISIBLE);

		user = (EditText) findViewById(R.id.Login_Username_Edit);
		pass = (EditText) findViewById(R.id.Login_Password_Edit);

		pass.setOnKeyListener(new OnKeyListener() {
			@Override
			public boolean onKey(View arg0, int keyCode, KeyEvent event) {
				// If the event is a key-down event on the "enter" button
				if ((event.getAction() == KeyEvent.ACTION_DOWN)
						&& (keyCode == KeyEvent.KEYCODE_ENTER)) {
					// Perform action on key press
					if (user.getText().toString().isEmpty()
							|| pass.getText().toString().isEmpty()) {
						CommonHelper.addAlert("Login Failure",
								"Please provide a user name and password",
								UniversalLoginActivity.this);
					} else {
						validateUser();
					}
					return true;
				}
				return false;
			}
		});

		imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}

	private void validateUser() {
		final List<NameValuePair> params = new ArrayList<NameValuePair>();
		try {
			params.add(new BasicNameValuePair("user_name", URLEncoder.encode(user
					.getText().toString(), "UTF-8")));
			params.add(new BasicNameValuePair("password", URLEncoder.encode(pass
					.getText().toString(), "UTF-8")));
			if (!prefs.getString("FB_ACCESS_TOKEN", "").isEmpty()) {
				params.add(new BasicNameValuePair("fb_token", URLEncoder.encode(prefs
						.getString("FB_ACCESS_TOKEN", ""), "UTF-8")));
			}
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			params.add(new BasicNameValuePair("user_name", user
					.getText().toString()));
			params.add(new BasicNameValuePair("password", pass
					.getText().toString()));
			if (!prefs.getString("FB_ACCESS_TOKEN", "").isEmpty()) {
				params.add(new BasicNameValuePair("fb_token", prefs
						.getString("FB_ACCESS_TOKEN", "")));
			}
		}
		api.setClient();
		api.post(params, getString(R.string.auth_api_root), new Callbacks() {
			@Override
			public void success(String json) {
				try {
					JSONObject jObj = new JSONObject(json);
					Log.d(LOG_TAG, "USER SUCCEEDED");
					imm.hideSoftInputFromWindow(pass.getWindowToken(), 0);
					SharedPreferences.Editor editor = prefs.edit();
					if (jObj.has("error")) {
						Log.e(LOG_TAG, "USER  FAILED");
						CommonHelper.addAlert("Login Failure", jObj.getString("error"),
								UniversalLoginActivity.this);
					}else {
						editor.putString("user_name", URLDecoder.decode(params.get(0).getValue()));
						editor.putString("password", params.get(1).getValue());
						editor.putString("userJSON", json);
						editor.putString("accessToken", jObj.getString("access_token"));
						editor.putString("role", jObj.getString("role"));
						//TODO: Change this to actual role when we are ready for scheduler
						editor.putString("root_role", "Employee");
						editor.apply();
						if (!json.contains("502 Bad Gateway")) {
							loginUser(json, jObj.getString("role"));
						} else {
							resetSettings();
							Log.e(LOG_TAG, "USER  FAILED");
							CommonHelper.addAlert("Login Failure", "502 Bad Gateway",
									UniversalLoginActivity.this);
						}
					}
				} catch (JSONException e) {
					resetSettings();
					Log.e(LOG_TAG, "USER FAILED");
					CommonHelper.addAlert("Login Failure", json,
							UniversalLoginActivity.this);
				}
			}

			@Override
			public void failure(String error) {
				resetSettings();
				Log.e(LOG_TAG, "USER FAILED");
				Log.e(LOG_TAG, error);
				CommonHelper.addAlert("Login Failure", error,
						UniversalLoginActivity.this);
			}
		});
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// Handle result we got from the facebook login activity
		if (requestCode == FB_LOGIN_ACTIVITY_CODE) {
			if (resultCode == RESULT_OK) {
				if (!prefs.getString("FB_ACCESS_TOKEN", "").isEmpty()) {
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("fb_token", prefs
							.getString("FB_ACCESS_TOKEN", "")));
					api.setClient();
					api.post(params, getString(R.string.auth_api_root), new Callbacks() {
						@Override
						public void success(String json) {
							try {
								JSONObject jObj = new JSONObject(json);
								Log.d(LOG_TAG, "USER SUCCEEDED");
								if (jObj.has("error")) {
									Log.e(LOG_TAG, "USER  FAILED");
									CommonHelper.addAlert("Login Failure", jObj.getString("error"),
											UniversalLoginActivity.this);
								}else {
									SharedPreferences.Editor editor = prefs
											.edit();
									editor.putString("userJSON", json);
									editor.putString("accessToken",
											jObj.getString("access_token"));
									editor.putString("role", jObj.getString("role"));
									editor.apply();
									loginUser(json, jObj.getString("role"));
								}
							} catch (JSONException e) {
								resetSettings();
								Log.e(LOG_TAG, "USER FAILED");
								CommonHelper.addAlert("Login Failure", json,
										UniversalLoginActivity.this);
							}

						}

						@Override
						public void failure(String error) {
							resetSettings();
							Log.e(LOG_TAG, "USER FAILED");
							Log.e(LOG_TAG, error);
							CommonHelper.addAlert("Login Failure", error,
									UniversalLoginActivity.this);
						}
					});
				} else {
					resetSettings();
					CommonHelper.addAlert("Login Failure",
							"Something went wrong with the Facebook login",
							UniversalLoginActivity.this);
					Log.e(LOG_TAG, "Something went wrong with the JSON: ");
				}
			}
		}
	}

	public void loginUser(final String json, String role) {
		try
		{
			JSONObject jObj = new JSONObject(json);
			// If user needs to reset his password for first time login
			if (jObj.getString("force_password_reset").equals("true")) {
				Intent mainIntent = new Intent(UniversalLoginActivity.this,
						ForcePasswordResetActivity.class);
				mainIntent.putExtra("userJSON", json);
				startActivity(mainIntent);
				finish();
			} else {
				//TODO: Change this to actual role when we are ready for scheduler
				/*if (role.equals("Employee") || role.equals("Messenger")) {
					Intent mainIntent = new Intent(UniversalLoginActivity.this,
							MainActivity.class);
					mainIntent.putExtra("userJSON", json);
					startActivity(mainIntent);
					finish();
				} else {
					Intent mainIntent = new Intent(UniversalLoginActivity.this,
							ContainerActivity.class);
					startActivity(mainIntent);
					finish();
				}*/
				Intent mainIntent = new Intent(UniversalLoginActivity.this,
						MainActivity.class);
				mainIntent.putExtra("userJSON", json);
				startActivity(mainIntent);
				finish();
			}
		} catch (JSONException e) {

		}
	}

	public void resetSettings() {
		CommonHelper.clearUserSettings(prefs);
		if (!view.isShown()) {
			view.setVisibility(View.VISIBLE);
		}
	}

}
