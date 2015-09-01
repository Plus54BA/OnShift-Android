package com.onshift.mobile.fragments;


import java.util.ArrayList;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONObject;

import com.onshift.mobile.R;
import com.onshift.mobile.models.Callbacks;
import com.onshift.mobile.utils.API;
import com.onshift.mobile.helpers.CommonHelper;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


/**
 * Created by mcatalano on 8/25/14.
 */

public class HomeFragment extends Fragment {

	private API api;
	private Button txtCalloffsCount;
	private View view;
	private TextView txtCalloffs;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/** Inflating the layout for this fragment **/
		view = inflater.inflate(R.layout.fragment_home, container,false);

		api = new API(this.getActivity());
		txtCalloffs = (TextView) view.findViewById(R.id.txtCalloffs);		
		txtCalloffsCount = (Button) view.findViewById(R.id.txtCalloffsCount);

		Typeface fontOpenSansBold = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/OpenSans-Bold.ttf");
		Typeface fontOpenSansLight = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/OpenSans-Light.ttf");
		txtCalloffsCount.setTypeface(fontOpenSansBold);
		txtCalloffs.setTypeface(fontOpenSansLight);
		ContainerFragment.rlAssignOk.setVisibility(View.INVISIBLE);

		txtCalloffsCount.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				CalloffsListFragment fragment = new CalloffsListFragment();
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.framelayout_content, fragment);
				ft.addToBackStack(null).commit();
				ContainerFragment.contAct.backButton.setVisibility(View.VISIBLE);
				ContainerFragment.contAct.msgButton.setVisibility(View.INVISIBLE);
				ContainerFragment.contAct.imgTopbarLogo.setVisibility(View.INVISIBLE);
				ContainerFragment.contAct.txtSection.setText("Call Offs");
				ContainerFragment.contAct.txtSection.setVisibility(View.VISIBLE);
			}
		});

		getCallOffsCount();
		return view;
	}

	private void getCallOffsCount() {
		try{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("datacount", "true")); 
			api.setApiClient(true);
			api.get(params, getString(R.string.rest_calloff), new Callbacks() {
				@Override
				public void success(String json) {
					//parse json
					try {
						JSONObject obj = new JSONObject(json);
						String count = obj.getString("objs");
						txtCalloffsCount.setText(count);
						if (count.equals("0") || count.equals("")) {
							txtCalloffsCount.setText("0");
						}
					} catch (Exception e) {
						Log.e("CALLOFFS_COUNT", e.getMessage());
						CommonHelper.addAlert("Dashboard", e.getMessage(),
								HomeFragment.this.getActivity());
					}
					getBootstrapData();
				}

				@Override
				public void failure(String error) {
					Log.e("CALLOFFS_COUNT", "USER FAILED");
					Log.e("CALLOFFS_COUNT", error);
					CommonHelper.addAlert("Dashboard", error,
							HomeFragment.this.getActivity());
					txtCalloffs.setText("");
					txtCalloffsCount.setText("");
					getBootstrapData();
				}
			});

		}catch(Exception e){
			CommonHelper.addAlert("Dashboard", e.getMessage(),
					HomeFragment.this.getActivity());
			txtCalloffs.setText("");
			txtCalloffsCount.setText("");
			getBootstrapData();
		}
	}
	
	private void getBootstrapData(){
		try{
			List<NameValuePair> params = new ArrayList<NameValuePair>();
			params.add(new BasicNameValuePair("target", "login"));
			api.setApiClient(true);
			api.get(params, getString(R.string.rest_bootstrap), new Callbacks() {
				@Override
				public void success(String json) {
					//parse json
					try {
						JSONObject jsonObj = new JSONObject(json);
						JSONObject obj = new JSONObject(jsonObj.getString("objs"));
						SharedPreferences prefs = getActivity().getSharedPreferences("mysettings", Context.MODE_PRIVATE);
						SharedPreferences.Editor editor = prefs.edit();
						editor.putString("locations", obj.getString("locations"));
						editor.putString("ptotypes", obj.getString("ptoStatus"));
						editor.putString("shifttypes", obj.getString("shiftTypes"));
						JSONObject orgObj = new JSONObject(obj.getString("organization"));
						editor.putString("v2organizationID", orgObj.getString("id"));
						editor.commit();
					} catch (Exception e) {
						Log.e("BOOTSTRAP", e.getMessage());
					}
				}

				@Override
				public void failure(String error) {
					Log.e("BOOTSTRAP", error);
				}
			});

		}catch(Exception e){
			CommonHelper.addAlert("Dashboard", e.getMessage(),
					HomeFragment.this.getActivity());
			txtCalloffs.setText("");
			txtCalloffsCount.setText("");
		}

	}
}