package com.onshift.mobile.fragments;


import java.net.URI;
import java.net.URISyntaxException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.daimajia.swipe.SwipeLayout;
import com.onshift.mobile.CustomListAdapter;
import com.onshift.mobile.R;
import com.onshift.mobile.models.Calloff;
import com.onshift.mobile.models.Callbacks;
import com.onshift.mobile.utils.API;
import com.onshift.mobile.helpers.CommonHelper;

import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;


public class CalloffsListFragment extends Fragment {
	private API api;
	private View view;
	private ListView list;
	private CustomListAdapter adapter;
	private int cellCount;
	private int cellOffset;
	private int cellTotal;
	private String nextListItems="";
	private List<NameValuePair> params;
	public static ArrayList<Calloff> allCallOffList;
	private Boolean flagLoading = false;
	private int listIndex;
	private int listTop;
	private Boolean isNextPageItems = false;
	private Date previousDate;
	private Boolean isFirstTime = true;
	private TextView txtNoCalloffs;
	public static CalloffsListFragment activity;
	public static SwipeLayout openCell;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/** Inflating the layout for this fragment **/
		if (isFirstTime) {			
			isFirstTime = false;
			view = inflater.inflate(R.layout.fragment_calloffslist, container,false);
			list = (ListView) view.findViewById(R.id.listCalloffs);	
			txtNoCalloffs = (TextView)view.findViewById(R.id.txtNoCalloffs);
			Typeface fHelveticaReg = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/HelveticaNeue-Roman.otf");
			txtNoCalloffs.setTypeface(fHelveticaReg);

			Button btnEnterNew = (Button) view.findViewById(R.id.btnEnterNew);
			btnEnterNew.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Bundle b = new Bundle();
					EnterNewCalloffFragment fragment = new EnterNewCalloffFragment();
					fragment.setArguments(b);
					FragmentTransaction ft = getFragmentManager().beginTransaction();
					ft.replace(R.id.framelayout_content, fragment);
					ft.addToBackStack(null).commit();
				}
			});

			api = new API(this.getActivity());
			params = new ArrayList<NameValuePair>();
			getCallOffs(true);
			activity = this;
		}

		ContainerFragment.contAct.txtSection.setText("Call Offs");
		return view;
	}

	private void setListAdapter(ArrayList<Calloff> data) {
		adapter = new CustomListAdapter(this.getActivity(), R.layout.calloff_list_cell, data);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
			}
		});
		list.setOnScrollListener(new OnScrollListener() {
			public void onScrollStateChanged(AbsListView view, int scrollState) {

			}
			public void onScroll(AbsListView view, int firstVisibleItem,
					int visibleItemCount, int totalItemCount) {
				if(firstVisibleItem+visibleItemCount == totalItemCount && totalItemCount!=0)
				{
					if(!flagLoading)
					{
						flagLoading = true;
						if (cellOffset + cellCount < cellTotal) {
							// Save list index and top position to restore scroll position when refreshing content.
							listIndex = list.getFirstVisiblePosition();
							View v = list.getChildAt(0);
							listTop = (v == null) ? 0 : v.getTop();
							getCallOffs(false);
						}
					}

				}
			}
		});
	}

	private void getCallOffs(Boolean showProgress) {
		if(!nextListItems.isEmpty()){
			getNextPageParams();
		} else {
			params.add(new BasicNameValuePair("datacount", "false"));
			params.add(new BasicNameValuePair("limit", "10"));
		}
		try{
			api.setApiClient(showProgress);
			api.get(params, getString(R.string.rest_calloff), new Callbacks() {
				@Override
				public void success(String json) {
					//parse json
					try {
						JSONObject obj = new JSONObject(json);
						JSONArray callOffArr = obj.getJSONArray("objs");
						JSONObject configObj = obj.getJSONObject("meta");

						cellCount = Integer.parseInt(configObj.getString("count"));
						cellOffset = Integer.parseInt(configObj.getString("offset"));
						cellTotal = Integer.parseInt(configObj.getString("total"));
						nextListItems = configObj.getString("next");

						ArrayList<Calloff> callOffList = new ArrayList<Calloff>();
						JSONObject callOffObj;
						if (!isNextPageItems) {
							previousDate = new Date();
						}
						for (int i = 0; i < callOffArr.length(); i++) {
							Calloff calloff = new Calloff();
							callOffObj = callOffArr.getJSONObject(i);
							calloff.setDate(callOffObj.getString("shift_start_day"));
							calloff.setStartTime(callOffObj.getString("shift_start_time"));
							calloff.setEndTime(callOffObj.getString("shift_stop_time"));
							calloff.setShiftName(callOffObj.getString("shift_type_display_name"));
							calloff.setResponses(callOffObj.getString("_response_count"));
							calloff.setLocation(callOffObj.getString("location_display_name"));
							calloff.setEmployeeLastName(callOffObj.getString("last_name"));
							calloff.setEmployeeName(callOffObj.getString("first_name"));
							calloff.setNotice(callOffObj.getString("_notice"));
							calloff.setReason(callOffObj.getString("_time_off_type_name"));
							calloff.setAssigned(callOffObj.getString("_assigned"));
							calloff.setRequired(callOffObj.getString("_required"));
							calloff.setIsDateDivider(false);
							calloff.setIsLoader(false);
							calloff.setV2shiftId(callOffObj.getString("_v2shift_id"));
							calloff.setMessageId(callOffObj.getString("message_id"));

							String date = calloff.getDate();
							String[] splitted = date.split(" ");
							String splitDate = splitted[0];
							SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");
							Date newDate = parseFormat.parse(splitDate);

							if ( (i == 0 && !isNextPageItems) || previousDate.before(newDate)) {
								previousDate = newDate;
								Calloff calloffGroup = new Calloff();
								calloffGroup.setDate(date);
								calloffGroup.setIsDateDivider(true);
								calloff.setIsLoader(false);
								callOffList.add(calloffGroup);
							} 

							callOffList.add(calloff);

							if (cellOffset > 0) {
								Calloff lastItem = allCallOffList.get(allCallOffList.size()-1);
								if (lastItem.getIsLoader()) {
									allCallOffList.remove(lastItem);
								}
								if (!isNextPageItems) {
									// Don't add the calloff to the list if is a new page, cause we'll be merging both lists below.
									allCallOffList.add(calloff);
								}
							}

						}
						flagLoading = false;
						if (cellOffset == 0) {
							allCallOffList = callOffList;
						}
						if (isNextPageItems) { 
							// Merge all previous calloffs with next page new calloffs.
							allCallOffList.addAll(callOffList);
						}
						if (cellOffset + cellCount < cellTotal) {
							Calloff calloffLoader = new Calloff();
							calloffLoader.setIsLoader(true);
							calloffLoader.setIsDateDivider(false);
							callOffList.add(calloffLoader);
						}
						if (allCallOffList.size() == 0) {
							txtNoCalloffs.setVisibility(View.VISIBLE);
						} else {
							txtNoCalloffs.setVisibility(View.INVISIBLE);
						}
						setListAdapter(allCallOffList);
						list.setSelectionFromTop(listIndex, listTop);
					} catch (Exception e) {
						Log.e("CALLOFFS_LIST", e.getMessage());
						CommonHelper.addAlert("Call Offs List Failure", e.getMessage(),
								CalloffsListFragment.this.getActivity());
					}
				}

				@Override
				public void failure(String error) {
					Log.e("CALLOFFS_LIST", "Error");
					Log.e("CALLOFFS_LIST", error);
					CommonHelper.addAlert("Call Offs List connection error", error,
							CalloffsListFragment.this.getActivity());
				}
			});
		} catch (Exception e) {
			Log.e("CALLOFFS_LIST", "NO INTERNET CONNECTION");
			Log.e("CALLOFFS_LIST", e.getMessage());
			CommonHelper.addAlert("Server not responding", e.getMessage(),
					CalloffsListFragment.this.getActivity());
		}   	
	}

	private void getNextPageParams(){
		params.clear();
		//parse and set offset and limit for next page
		try {
			List<NameValuePair> encodedParams = URLEncodedUtils.parse(new URI(nextListItems), "UTF-8");
			for (NameValuePair param : encodedParams) {
				params.add(new BasicNameValuePair(param.getName(), param.getValue()));
			}
		}
		catch (URISyntaxException e) {
			//Default args 
			params.add(new BasicNameValuePair("datacount", "false"));
			params.add(new BasicNameValuePair("limit", "10"));
		}  
	}

}