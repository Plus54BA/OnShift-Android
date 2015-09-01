package com.onshift.mobile.universal_login;

import com.onshift.mobile.R;
import com.onshift.mobile.helpers.CommonHelper;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class URLPickerActivity extends Activity implements OnClickListener {
	private URLPickerListAdapter urlPickerAdapter;
	private ListView urlPickerListView;
	private String[] urlArray;
	private SharedPreferences prefs;
	private SharedPreferences.Editor editor;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_urlpicker);
		setupButtons();
		setupListView();
		prefs = getSharedPreferences("mysettings", Context.MODE_PRIVATE);
	}

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		switch (v.getId()) {
		case R.id.URLPicker_Set_Button:
			changeUrls();
			finish();
			break;

		case R.id.URLPicker_Back_Button:
			finish();
			break;
		}
	}

	private void setupButtons() {
		Button changeBtn = (Button) findViewById(R.id.URLPicker_Set_Button);
		changeBtn.setOnClickListener(this);

		Button backBtn = (Button) findViewById(R.id.URLPicker_Back_Button);
		backBtn.setOnClickListener(this);
	}

	private void setupListView() {
		urlArray = getResources().getStringArray(R.array.baseURLs);
		urlPickerAdapter = new URLPickerListAdapter();
		urlPickerListView = (ListView) findViewById(R.id.URLPicker_URLList_ListView);
		urlPickerListView.setAdapter(urlPickerAdapter);
	}

	private void changeUrls() {
		editor = prefs.edit();
		editor.putString("authURL", urlPickerListView.getAdapter().getItem(0)
				.toString());
		editor.putString("apiURL", urlPickerListView.getAdapter().getItem(1)
				.toString());
		editor.putString("webURL", urlPickerListView.getAdapter().getItem(2)
				.toString());
		editor.apply();
	}

	public class URLPickerListAdapter extends BaseAdapter {

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return urlArray.length;
		}

		@Override
		public Object getItem(int index) {
			// TODO Auto-generated method stub
			return urlArray[index];
		}

		@Override
		public long getItemId(int index) {
			// TODO Auto-generated method stub
			return index;
		}

		@Override
		public View getView(final int index, View convertView, ViewGroup parent) {
			// TODO Auto-generated method stub

			if (convertView == null) {
				LayoutInflater layoutInflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				convertView = layoutInflater.inflate(R.layout.text_item, null);
			}

			TextView title = (TextView) convertView
					.findViewById(R.id.TextItem_Title_TextView);
			title.setText(urlArray[index]);

			convertView.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					switch (index) {
					case 0:
						editor = prefs.edit();
						editor.putString("authURL", urlArray[index]);
						editor.apply();
						break;

					case 1:
						editor = prefs.edit();
						editor.putString("apiURL", urlArray[index]);
						editor.apply();
						break;

					case 2:
						editor = prefs.edit();
						editor.putString("webURL", urlArray[index]);
						editor.apply();
						break;
					}
					CommonHelper.addAlert("URL Changed to:", urlArray[index],
							URLPickerActivity.this);
				}

			});

			return convertView;
		}
	}
}
