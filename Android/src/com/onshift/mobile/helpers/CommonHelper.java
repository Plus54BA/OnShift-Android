package com.onshift.mobile.helpers;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

@SuppressLint("SimpleDateFormat")
public class CommonHelper {

	private static final String NUMBER_PATTERN = ".*[0-9]+.*";
	private static final String LOWERCASE_PATTERN = ".*[a-z]+.*";
	private static final String UPPERCASE_PATTERN = ".*[A-Z]+.*";
	private static final String VALID = "Valid";
	private static final String LENGTH_ERROR = "The new password must be at least 6 characters long";
	private static final String NUMBER_ERROR = "The new password must contain at least one number";
	private static final String LOWERCASE_ERROR = "The new password must contain at least one lower case character";
	private static final String UPPERCASE_ERROR = "The new password must contain at least one upper case character";

	public static String formatDate(String date, String strFormat) {

		if (strFormat == "" || strFormat == null) {
			strFormat = "EEEE, MMMM d, yyyy";
		}

		String[] splitted = date.split(" ");
		String splitDate = splitted[0];
		String strdate = "";
		SimpleDateFormat dateFormat = new SimpleDateFormat(strFormat);
		Date newDate;
		try {
			SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");
			newDate = parseFormat.parse(splitDate);
			strdate = dateFormat.format(newDate);
		} catch (ParseException e) {

			e.printStackTrace();
		}

		return strdate;
	}

	public static void hideSoftKeyboard(Activity activity) {
		InputMethodManager inputMethodManager = (InputMethodManager) activity
				.getSystemService(Activity.INPUT_METHOD_SERVICE);

		View focusedView = activity.getCurrentFocus();
		if (focusedView != null)
			inputMethodManager.hideSoftInputFromWindow(activity
					.getCurrentFocus().getWindowToken(), 0);
	}

	public static String validatePassword(String password) {
		// Set the patterns
		Pattern numberPattern = Pattern.compile(NUMBER_PATTERN);
		Pattern lowercasePattern = Pattern.compile(LOWERCASE_PATTERN);
		Pattern uppercasePattern = Pattern.compile(UPPERCASE_PATTERN);

		// Check password for length, and if it contains a number, lower case
		// and upper case character
		if (password.length() < 6) {
			return LENGTH_ERROR;
		} else if (!numberPattern.matcher(password).matches()) {
			return NUMBER_ERROR;
		} else if (!lowercasePattern.matcher(password).matches()) {
			return LOWERCASE_ERROR;
		} else if (!uppercasePattern.matcher(password).matches()) {
			return UPPERCASE_ERROR;
		}

		// just return VALID otherwise
		return VALID;
	}

	public static String updateUserRole(String role, SharedPreferences prefs) {
		String userJSON = "";
		// Read user json stored in preferences and update the stored user role
		SharedPreferences.Editor editor = prefs.edit();
		if (!prefs.getString("userJSON", "").isEmpty()) {
			try {
				JSONObject jObj = new JSONObject(
						prefs.getString("userJSON", ""));
				jObj.put("role", role);
				userJSON = jObj.toString();
				editor.putString("userJSON", userJSON);
				editor.putString("role", role);
				editor.apply();
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return userJSON;
	}

	public static void clearUserSettings(SharedPreferences prefs) {
		SharedPreferences.Editor editor = prefs.edit();
		editor.putString("user_name", "");
		editor.putString("password", "");
		editor.putString("userJSON", "");
		editor.putString("accessToken", "");
		editor.putString("role", "");
		editor.putString("root_role", "");
		editor.apply();
	}

	public static void addAlert(String title, String msg, Activity act){
		AlertDialog.Builder alertAdd = new AlertDialog.Builder(act);
		alertAdd.setTitle(title);
		alertAdd
		.setMessage(msg)
		.setCancelable(true)
		.setPositiveButton("OK.", new DialogInterface.OnClickListener() {

			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
			}
		});

		AlertDialog alertDialog = alertAdd.create();
		alertDialog.show();
	}

	public static String splitAndFormatDate (String date) {

		String startTime = date;
		String[] startTimeSplit = startTime.split(" ");
		String startHour = startTimeSplit[1];
		String[] startHourSplit = startHour.split(":");
		startHour = startHourSplit[0]+":"+startHourSplit[1];

		try {
			final SimpleDateFormat sdf = new SimpleDateFormat("H:mm");
			final Date dateObj = sdf.parse(startHour);
			startHour = new SimpleDateFormat("K:mmaa").format(dateObj);

		} catch (final ParseException e) {
			e.printStackTrace();
		}


		return startHour.toLowerCase();
	}
}
