/**
  
  Base class which we should extend to add the app menu to an Activity 

 **/

package com.onshift.mobile;

import java.util.Calendar;

import com.onshift.mobile.helpers.CommonHelper;
import com.onshift.mobile.helpers.VersionChecker;
import com.onshift.mobile.universal_login.UniversalLoginActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.widget.DrawerLayout;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

@SuppressLint({ "InlinedApi", "InflateParams" })
public abstract class DrawerActivity extends Activity {

	private String[] menutItems;
	private DrawerLayout mDrawerLayout;
	private ListView mDrawerList;
	private TextView copyrightView;
	private View hdrview;
	protected LinearLayout fullLayout;
	protected FrameLayout actContent;
	public VersionChecker vc = null;
	static Calendar calendar = Calendar.getInstance();
	static int year = calendar.get(Calendar.YEAR);
	private static final String copyRight = "Copyright © "
			+ String.valueOf(year) + " OnShift. \nAll rights reserved.";
	private static DrawerActivity instance;
	private SharedPreferences prefs;

	@Override
	public void setContentView(final int layoutResID) {
		fullLayout = (LinearLayout) getLayoutInflater().inflate(
				R.layout.drawer_activity, null);
		actContent = (FrameLayout) fullLayout.findViewById(R.id.Container);
		instance = this;
		setupDrawerView();
		setupButtons();
		prefs = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
		// Setting the content of layout your provided to the act_content frame
		getLayoutInflater().inflate(layoutResID, actContent, true);
		super.setContentView(fullLayout);
	}

	public void setupDrawerView() {
		// set up the copyright and version text views and add header to app
		// list view
		menutItems = getResources()
				.getStringArray(R.array.scheduler_menu_items);
		copyrightView = (TextView) fullLayout
				.findViewById(R.id.Drawer_Copyright_Lbl);
		StringBuilder copyRightText = new StringBuilder();
		copyRightText.append(copyRight);
		vc = new VersionChecker(this);
		vc.checkVersion();
		copyRightText.append("\nVersion: " + vc.getCurrentVersion());
		copyrightView.setText(copyRightText.toString());
		mDrawerLayout = (DrawerLayout) fullLayout
				.findViewById(R.id.Drawer_Layout);
		mDrawerList = (ListView) fullLayout.findViewById(R.id.Drawer_Listview);
		hdrview = View.inflate(this, R.layout.scheduler_onshiftmenu_header,
				null);
		mDrawerList.addHeaderView(hdrview);
		mDrawerList.setAdapter(new ArrayAdapter<String>(this,
				R.layout.scheduler_drawer_list_item, menutItems));
		// Set the list's click listener
		mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
	}

	public void setupButtons() {
		// App menu button click handlers
		Button schedulerBtn = (Button) hdrview
				.findViewById(R.id.Sc_Scheduler_Btn);
		schedulerBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				closeDrawer();
			}
		});

		Button employeeBtn = (Button) hdrview
				.findViewById(R.id.Sc_Employee_Btn);
		employeeBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				closeDrawer();
				employeeSwitchHandler();
			}
		});

		Button logoutBtn = (Button) fullLayout
				.findViewById(R.id.Drawer_Logout_Btn);
		logoutBtn.setOnClickListener(new OnClickListener() {
			public void onClick(View v) {
				closeDrawer();
				logOutHandler();
			}
		});
	}

	public void showDrawer() {
		// toggle app menu
		if (mDrawerLayout.isDrawerOpen(Gravity.START)) {
			mDrawerLayout.closeDrawer(Gravity.START);
		} else {
			mDrawerLayout.openDrawer(Gravity.START);
		}
	}

	public void closeDrawer() {
		// close the app menu
		mDrawerLayout.closeDrawer(Gravity.START);
	}

	public void showPrivacyPolicy(View v) {
		// this does nothing for now, just close app menu
		closeDrawer();
	}

	public void showHelp(View v) {
		// this does nothing for now, just close app menu
		closeDrawer();
	}

	public void employeeSwitchHandler() {
		// calls the switching to the employee app
		String userJSON = CommonHelper.updateUserRole(
				getString(R.string.employee_role), prefs);
		// Redirect to employee app only if we have valid user JSON
		if (!userJSON.isEmpty()) {
			Intent mainIntent = new Intent(DrawerActivity.this,
					MainActivity.class);
			mainIntent.putExtra("userJSON", userJSON);
			startActivity(mainIntent);
			finish();
		}
	}

	public void logOutHandler() {
		// Clear user preferences and go back to the login Activity
		CommonHelper.clearUserSettings(prefs);
		Intent mainIntent = new Intent(instance, UniversalLoginActivity.class);
		instance.startActivity(mainIntent);
		instance.finish();
	}

	private class DrawerItemClickListener implements
			ListView.OnItemClickListener {
		@Override
		public void onItemClick(AdapterView<?> parent, View arg1, int position,
				long id) {
			selectItem(position);
		}
	}

	private void selectItem(int position) {
		// The position some times returns -1, handle that situation
		// just close the drawer for now
		try {
			if (position == -1) {
				mDrawerList.setItemChecked(0, true);
				mDrawerLayout.closeDrawer(Gravity.START);
			} else {
				mDrawerList.setItemChecked(position, true);
				mDrawerLayout.closeDrawer(Gravity.START);
			}
		} catch (Exception e) {
			mDrawerList.setItemChecked(0, true);
			mDrawerLayout.closeDrawer(Gravity.START);
		}
	}

}
