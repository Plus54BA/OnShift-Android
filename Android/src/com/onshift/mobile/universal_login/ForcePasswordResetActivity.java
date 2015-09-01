package com.onshift.mobile.universal_login;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.onshift.mobile.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;


import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.onshift.mobile.helpers.CommonHelper;
import com.onshift.mobile.models.Callbacks;
import com.onshift.mobile.utils.API;

public class ForcePasswordResetActivity extends Activity implements OnClickListener{
	private SharedPreferences prefs;
	private RelativeLayout errorView;
	private TextView errorText;
	private EditText password, confirmPassword;
	private static final String MATCH_ERROR = "The two password fields didn't match";
	private static final String ENTER_ALL_ERROR = "Please enter all password fields";
	private API api;

	public void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_force_password_reset);
		prefs = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
		api = new API(this);
		setupView();
	}

	private void setupView() {
		Button forceBtn = (Button) findViewById(R.id.Force_Password_Button);
		forceBtn.setOnClickListener(this);

		errorView = (RelativeLayout) findViewById(R.id.Force_Password_Errors);
		errorView.setVisibility(View.INVISIBLE);

		errorText = (TextView) findViewById(R.id.Force_Password_ErrorText);

		password = (EditText) findViewById(R.id.Force_Password_Edit);
		confirmPassword = (EditText) findViewById(R.id.Force_Password_Confirm_Edit);

		confirmPassword.setOnEditorActionListener(new OnEditorActionListener() {
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if ((event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) || (actionId == EditorInfo.IME_ACTION_DONE)) {
					forcePasswordReset();
				}
				return false;
			}
		});
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.Force_Password_Button:
			forcePasswordReset();
			break;
		}
	}

	public void forcePasswordReset() {
		//check if any of the fields are empty
		String user_id = "";
		try {
			JSONObject jObj = new JSONObject(prefs
					.getString("userJSON", ""));
			user_id = jObj.getString("id");
		} catch (JSONException e) {
		
		}
		if (password.getText().toString().isEmpty()
				|| confirmPassword.getText().toString().isEmpty()) {
			errorText.setText(ENTER_ALL_ERROR);
			setEditTextErrorClass();
			errorView.setVisibility(View.VISIBLE);
		} else {
			//check if both fields are equal
			if(password.getText().toString().equals(confirmPassword.getText().toString())) {
				//if password matches validation rules, do the reset
				if(CommonHelper.validatePassword(password.getText().toString()).equals("Valid")){
					removeEditTextErrorClass();
					errorView.setVisibility(View.INVISIBLE);
					List<NameValuePair> params = new ArrayList<NameValuePair>();
					params.add(new BasicNameValuePair("user_id",user_id));
					try {
						params.add(new BasicNameValuePair("password", URLEncoder.encode(password.getText().toString(), "UTF-8")));
					} catch (UnsupportedEncodingException e) {
						params.add(new BasicNameValuePair("password", password.getText().toString()));
					}
					api.setClient();
					api.post(params, "/api/v1.0/user/change_password", new Callbacks() {
						@Override
						public void success(String json) {
							try {
								SharedPreferences.Editor editor = prefs
										.edit();
								editor.putString("userJSON", json);
								editor.apply();
								Intent mainIntent = new Intent(ForcePasswordResetActivity.this,
										UniversalLoginActivity.class);
								startActivity(mainIntent);
								finish();
							} catch (Exception e) {
								
							}
						}
						@Override
						public void failure(String error) {
							CommonHelper.addAlert("Password ResetFailure", error,
									ForcePasswordResetActivity.this);
						}
					});
								
				} else { //else display the reset error
					errorText.setText(CommonHelper.validatePassword(password.getText().toString()));
					setEditTextErrorClass();
					errorView.setVisibility(View.VISIBLE);
				}
			} else {
				//display non-matching password fields error
				errorText.setText(MATCH_ERROR);
				setEditTextErrorClass();
				errorView.setVisibility(View.VISIBLE);
			}
		}
	}

	//This surrounds the edit text fields with a border to indicate that an error occurred
	public void setEditTextErrorClass() {
		password.setBackgroundResource(R.drawable.error_background);
		confirmPassword.setBackgroundResource(R.drawable.error_background);
	}

	//This removes the edit text fields borders
	public void removeEditTextErrorClass() {
		password.setBackgroundResource(android.R.drawable.edit_text);
		confirmPassword.setBackgroundResource(android.R.drawable.edit_text);
	}
}
