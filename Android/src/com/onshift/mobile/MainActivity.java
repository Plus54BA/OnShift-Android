package com.onshift.mobile;

import java.io.File;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.concurrent.ExecutorService;

import org.apache.cordova.Config;
import org.apache.cordova.CordovaInterface;
import org.apache.cordova.CordovaPlugin;
import org.apache.cordova.CordovaWebView;
import android.webkit.WebSettings;

import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;

import com.onshift.mobile.helpers.CommonHelper;
import com.onshift.mobile.helpers.SSLAcceptingWebViewClient;
import com.onshift.mobile.helpers.VersionChecker;
import com.onshift.mobile.widgets.DateTimePicker;
import com.onshift.mobile.widgets.SingleDatePicker;

@SuppressLint("NewApi")
public class MainActivity extends Activity implements CordovaInterface {
	static Calendar calendar = Calendar.getInstance();
	static int year = calendar.get(Calendar.YEAR);


	//FacebookSdk.sdkInitialize(getApplicationContext());

	private static final String copyRight = "Copyright © "
			+ String.valueOf(year) + " OnShift. \nAll rights reserved.";

	static CordovaWebView cwv;

	private String[] menutItems;
	private String[] btnMenutItems;

	private TextView copyrightView;
	private DrawerLayout mDrawerLayout;
	private RelativeLayout mainLayout;
	private View hdrview;

	private ListView mDrawerList;

	public static int NOTIFICATION_COUNT = 1;
	public VersionChecker vc = null;
	private static MainActivity instance;

	// protected Dialog splashDialog;
	private boolean removeSplash = false;
	private ProgressDialog loadDialog;

	private SharedPreferences prefs;

	private static final String DOWNLOADSFOLDER = "/OnShiftReports";

	@SuppressLint("NewApi")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		instance = this;

		setContentView(R.xml.main);
		getWindow().setWindowAnimations(0);

		menutItems = getResources().getStringArray(R.array.menu_items);
		btnMenutItems = getResources()
				.getStringArray(R.array.button_menu_items);
		mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
		mainLayout = (RelativeLayout) findViewById(R.id.main_layout);
		copyrightView = (TextView) findViewById(R.id.copyrightView);

		StringBuilder copyRightText = new StringBuilder();
		copyRightText.append(copyRight);

		vc = new VersionChecker(instance);
		vc.checkVersion();

		copyRightText.append("\nVersion: " + vc.getCurrentVersion());
		copyrightView.setText(copyRightText.toString());

		mDrawerList = (ListView) findViewById(R.id.left_drawer);

		prefs = getSharedPreferences("mysettings", Context.MODE_PRIVATE);

		// Setup Header view for app menu
		hdrview = View.inflate(instance, R.layout.onshiftmenu_header, null);
		mDrawerList.addHeaderView(hdrview);
		setupHeaderButtons();

		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

		mDrawerList.setAdapter(new ArrayAdapter<String>(instance,
				R.layout.drawer_list_item, menutItems));

		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());

		createReportsDownloadDirectory();

		cwv = (CordovaWebView) findViewById(R.id.mainView);
		Config.init(this);
		cwv.setVisibility(View.INVISIBLE);

		NOTIFICATION_COUNT = 1;

		// Check your sdk version
		int sdkVersion = android.os.Build.VERSION.SDK_INT;
		// If Android 2.3.3 or lower
		if (sdkVersion <= 10) {
			// Use the SSL accepting client as your web client for the cordova
			// web view
			SSLAcceptingWebViewClient myWebViewClient = new SSLAcceptingWebViewClient(
					this);
			myWebViewClient.setWebView(cwv);
			cwv.setWebViewClient(myWebViewClient);
		}

		cwv.loadUrl(Config.getStartUrl());
		cwv.getSettings().setSupportZoom(false);
		cwv.getSettings().setTextZoom(100);

		showLoadDialog();

		// New way of handling push messages
		if ((getIntent().getStringExtra("message") != null)
				&& !(getIntent().getStringExtra("message").isEmpty())) {
			// store it in a shared preference and then access it using the OS
			// plugin class
			SharedPreferences settings = this.getSharedPreferences(
					"mysettings", Context.MODE_PRIVATE);
			SharedPreferences.Editor editor = settings.edit();
			editor.putString("PendingMessage",
					getIntent().getStringExtra("message"));
			editor.commit();
		} else {
			removeSplash = true;
		}
	}

	public void showDateTimeDialog() {
		// Create the dialog
		// final Dialog mDateTimeDialog = new Dialog(this,
		// R.style.CustomDialogTheme);
		final Dialog mDateTimeDialog = new Dialog(this);
		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.date_time_dialog, null);
		// Grab widget instance
		final DateTimePicker mDateTimePicker = (DateTimePicker) mDateTimeDialogView
				.findViewById(R.id.DateTimePicker);
		// Check is system is set to use 24h time (this doesn't seem to work as
		// expected though)
		// final String timeS =
		// android.provider.Settings.System.getString(getContentResolver(),
		// android.provider.Settings.System.TIME_12_24);
		// final boolean is24h = !(timeS == null || timeS.equals("12"));
		// Update demo TextViews when the "OK" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mDateTimePicker.clearFocus();
						mDateTimeDialog.dismiss();
						DecimalFormat mFormat = new DecimalFormat("00");
						if (mDateTimePicker.get(Calendar.HOUR) == 0) {
							sendMessageToCordovaView("OnShift.Utils.setDateTime('"
									+ mDateTimePicker.get(Calendar.YEAR)
									+ "/"
									+ mFormat.format(Double
											.valueOf(mDateTimePicker
													.get(Calendar.MONTH) + 1))
									+ "/"
									+ mFormat.format(Double.valueOf(mDateTimePicker
											.get(Calendar.DAY_OF_MONTH)))
									+ " "
									+ mFormat.format(Double.valueOf(12))
									+ ":"
									+ mFormat.format(Double
											.valueOf(mDateTimePicker
													.get(Calendar.MINUTE)))
									+ " "
									+ (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM"
											: "PM") + "');");
						} else {
							sendMessageToCordovaView("OnShift.Utils.setDateTime('"
									+ mDateTimePicker.get(Calendar.YEAR)
									+ "/"
									+ mFormat.format(Double
											.valueOf(mDateTimePicker
													.get(Calendar.MONTH) + 1))
									+ "/"
									+ mFormat.format(Double.valueOf(mDateTimePicker
											.get(Calendar.DAY_OF_MONTH)))
									+ " "
									+ mFormat.format(Double
											.valueOf(mDateTimePicker
													.get(Calendar.HOUR)))
									+ ":"
									+ mFormat.format(Double
											.valueOf(mDateTimePicker
													.get(Calendar.MINUTE)))
									+ " "
									+ (mDateTimePicker.get(Calendar.AM_PM) == Calendar.AM ? "AM"
											: "PM") + "');");
						}
					}
				});
		// Cancel the dialog when the "Cancel" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDateTimeDialog.cancel();
					}
				});
		// Setup TimePicker
		mDateTimePicker.setIs24HourView(false);
		// No title on the dialog window
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Set the dialog content view
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		// Display the dialog
		mDateTimeDialog.show();
	}

	public void showDateDialog() {
		// Create the dialog
		final Dialog mDateTimeDialog = new Dialog(this);
		// Inflate the root layout
		final RelativeLayout mDateTimeDialogView = (RelativeLayout) getLayoutInflater()
				.inflate(R.layout.date_dialog, null);
		// Grab widget instance
		final SingleDatePicker mDateTimePicker = (SingleDatePicker) mDateTimeDialogView
				.findViewById(R.id.SingleDatePicker);
		// Update demo TextViews when the "OK" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.SetDateTime))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						mDateTimePicker.clearFocus();
						mDateTimeDialog.dismiss();
						DecimalFormat mFormat = new DecimalFormat("00");
						sendMessageToCordovaView("OnShift.Utils.setDateTime('"
								+ mDateTimePicker.get(Calendar.YEAR)
								+ "/"
								+ mFormat.format(Double.valueOf(mDateTimePicker
										.get(Calendar.MONTH) + 1))
								+ "/"
								+ mFormat.format(Double.valueOf(mDateTimePicker
										.get(Calendar.DAY_OF_MONTH))) + "');");
					}
				});
		// Cancel the dialog when the "Cancel" button is clicked
		((Button) mDateTimeDialogView.findViewById(R.id.CancelDialog))
				.setOnClickListener(new OnClickListener() {
					public void onClick(View v) {
						// TODO Auto-generated method stub
						mDateTimeDialog.cancel();
					}
				});
		// No title on the dialog window
		mDateTimeDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		// Set the dialog content view
		mDateTimeDialog.setContentView(mDateTimeDialogView);
		// Display the dialog
		mDateTimeDialog.show();
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int position,
				long id) {
			selectItem(position);
		}
	}

	private void createReportsDownloadDirectory() {
		// create downloads folder
		File dwFolder = new File(Environment.getExternalStorageDirectory()
				+ DOWNLOADSFOLDER);
		// if it doesnt exist
		if (!dwFolder.exists()) {
			// create it
			dwFolder.mkdir();
		}
	}

	protected void showLoadDialog() {
		if (!isFinishing()) {
			loadDialog = ProgressDialog.show(this, "",
					"Logging in, please wait....", true);
		}
	}

	public void removeLoadDialog() {
		// Probable fix for issue with splash screen dismiss..
		try {
			if (loadDialog != null) {
				if (loadDialog.isShowing()) {
					loadDialog.dismiss();
				}
			}
		} catch (Exception e) {

		}
	}

	public void setupHeaderButtons() {
		// Checks if the user is a employee/messenger and hides the App switcher
		// layout if true
		if (prefs.getString("root_role", "").equals("Employee")
				|| prefs.getString("root_role", "").equals("Messenger")) {
			RelativeLayout switcherLayout = (RelativeLayout) hdrview
					.findViewById(R.id.App_Switcher_Layout);
			switcherLayout.setVisibility(View.GONE);
		} else {
			// App switcher scheduler button listener
			Button schedulerBtn = (Button) hdrview
					.findViewById(R.id.Scheduler_Btn);
			schedulerBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					closeDrawer();
					schedulerSwitchHandler();
				}
			});
			// App switcher employee button listener
			Button employeeBtn = (Button) hdrview
					.findViewById(R.id.Employee_Btn);
			employeeBtn.setOnClickListener(new OnClickListener() {
				public void onClick(View v) {
					closeDrawer();
				}
			});
		}
	}

	public void schedulerSwitchHandler() {
		// calls the switching to the employee app
		String userJSON = CommonHelper.updateUserRole(
				getString(R.string.scheduler_role), prefs);
		// Redirect to scheduler app only if we have valid user JSON
		if (!userJSON.isEmpty()) {
			Intent schedulerIntent = new Intent(MainActivity.this,
					ContainerActivity.class);
			schedulerIntent.putExtra("userJSON", userJSON);
			startActivity(schedulerIntent);
			finish();
		}
	}

	public void showPrivacyPolicy(View v) {
		mDrawerLayout.closeDrawer(mainLayout);
		sendMessageToCordovaView("OnShift.Utils.openPrivacyPolicy()");
	}

	public void showHelp(View v) {
		mDrawerLayout.closeDrawer(mainLayout);
		sendMessageToCordovaView("OnShift.Utils.openHelp()");
	}

	public static void sendMessageToCordovaView(String message) {
		cwv.sendJavascript(message);
	}

	public void showDrawer() {
		if (mDrawerLayout.isDrawerOpen(mainLayout)) {
			mDrawerLayout.closeDrawer(mainLayout);
		} else {
			mDrawerLayout.openDrawer(mainLayout);
		}
	}

	public void closeDrawer() {
		mDrawerLayout.closeDrawer(mainLayout);
	}

	public void lockDrawer() {
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
	}

	public void unlockDrawer() {
		mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
	}

	private void selectItem(int position) {
		// The position some times returns -1, handle that situation
		try {
			if (position == -1) {
				sendMessageToCordovaView("OnShift.Utils.changeRoute('"
						+ btnMenutItems[0] + "');");
				// Highlight the selected item, update the title, and close the
				// drawer
				mDrawerList.setItemChecked(0, true);
				mDrawerLayout.closeDrawer(mainLayout);
			} else {
				sendMessageToCordovaView("OnShift.Utils.changeRoute('"
						+ btnMenutItems[position - 1] + "');");
				// Highlight the selected item, update the title, and close the
				// drawer
				mDrawerList.setItemChecked(position, true);
				mDrawerLayout.closeDrawer(mainLayout);
			}
		} catch (Exception e) {
			sendMessageToCordovaView("OnShift.Utils.changeRoute('"
					+ btnMenutItems[0] + "');");
			// Highlight the selected item, update the title, and close the
			// drawer
			mDrawerList.setItemChecked(0, true);
			mDrawerLayout.closeDrawer(mainLayout);
		}
	}

	public static void sendRegistrationToWebview(String value) {
		sendMessageToCordovaView("OnShift.Utils.storeRegistrationId('"
				+ value + "');");
	}

	public static void sendNotificationToWebview(String value) {
		try {
			sendMessageToCordovaView("OnShift.Utils.getNotification('"
					+ value + "');");
		} catch (Exception e) {

		}
	}

	public static void sendForegroundNotificationToWebview(String value) {
		try {
			sendMessageToCordovaView("OnShift.Utils.getForegroundNotification('"
					+ value + "');");
		} catch (Exception e) {

		}
	}

	public static int getNotificationCount() {
		return NOTIFICATION_COUNT;
	}

	public static void resetNotificationCount() {
		NOTIFICATION_COUNT = 1;
	}

	@Override
	public void onStart() {
		super.onStart();
	}

	@Override
	public void onStop() {
		super.onStop();
	}

	public void onPause() {
		super.onPause();
		try {
			ExternalReceiver.myNotificationManager.cancelAll();
		} catch (Exception e) {

		}
		if (cwv != null) {
			cwv.handlePause(false);
		}
	}

	public void onDestroy() {
		super.onDestroy();
		if (cwv != null) {
			try {
				cwv.handleDestroy();
			} catch (Exception e) {

			}
		}
	}

	public void onResume() {
		super.onResume();
		try {
			ExternalReceiver.myNotificationManager.cancelAll();
		} catch (Exception e) {

		}
		if (cwv != null) {
			cwv.handleResume(false, false);
		}
	}

	@Override
	public Activity getActivity() {
		return this;
	}

	@Override
	public ExecutorService getThreadPool() {
		return null;
	}

	@Override
	public Object onMessage(String arg0, Object arg1) {
		if (arg0.equals("exit")) {
			try {
				ExternalReceiver.myNotificationManager.cancelAll();
			} catch (Exception e) {

			}
			finish();
		} else if (arg0.equals("onPageFinished")) {
			if (removeSplash) {
				new Handler().postDelayed(new Runnable() {
					@Override
					public void run() {
						// do your stuff here
						removeLoadDialog();
						cwv.setVisibility(View.VISIBLE);
					}
				}, 1500);
			} else {
				removeLoadDialog();
				cwv.setVisibility(View.VISIBLE);
			}
		}
		return null;
	}

	@Override
	public void setActivityResultCallback(CordovaPlugin arg0) {

	}

	@Override
	public void startActivityForResult(CordovaPlugin arg0, Intent arg1, int arg2) {

	}
}