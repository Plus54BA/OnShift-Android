package com.onshift.mobile.fragments;

import java.net.URI;
import java.net.URISyntaxException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.onshift.mobile.EmployeeListAdapter;
import com.onshift.mobile.R;
import com.onshift.mobile.models.Employee;
import com.onshift.mobile.helpers.CommonHelper;
import com.onshift.mobile.models.Callbacks;
import com.onshift.mobile.utils.API;
import com.onshift.mobile.utils.ConnectionDetector;

import android.app.Fragment;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class EnterNewCalloffFragment extends Fragment {

	private ImageView imgSearch;
	private ListView list;
	private EditText editTxtSearch;
	private View view;
	public static EnterNewCalloffFragment activity;
	private Timer timer = new Timer();
	private API api;
	private String v2shiftParam;
	private Boolean isNextPageItems = false;
	private int cellCount;
	private int cellOffset;
	private int cellTotal;
	private String nextListItems = "";
	public static ArrayList<Employee> allUsersList;
	private Boolean flagLoading = false;
	private TextView txtNoEmployees;
	private int listIndex;
	private int listTop;
	private EmployeeListAdapter adapter;
	private List<NameValuePair> params;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/** Inflating the layout for this fragment **/
		view = inflater.inflate(R.layout.fragment_enternewcalloff, container,false);

		Bundle b = this.getArguments();
		if (b.containsKey("v2shift")) {
			v2shiftParam = b.getString("v2shift");
		}

		activity = this;
		list = (ListView) view.findViewById(R.id.list);

		Typeface fHelveticaReg = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/HelveticaNeue-Roman.otf");

		txtNoEmployees = (TextView)view.findViewById(R.id.txtNoEmployees);
		txtNoEmployees.setTypeface(fHelveticaReg);
		imgSearch = (ImageView)view.findViewById(R.id.imgSearch);
		editTxtSearch = (EditText)view.findViewById(R.id.editTxtSearch);
		editTxtSearch.setTypeface(fHelveticaReg);
		editTxtSearch.addTextChangedListener(new TextWatcher() {

			@Override
			public void afterTextChanged(Editable s) {

			}

			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {

				doSearch();
				if (editTxtSearch.getText().length() > 0) {
					imgSearch.setVisibility(View.INVISIBLE);
				} else {
					imgSearch.setVisibility(View.VISIBLE);
				}
			} 

		});

		view.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent ev)
			{
				CommonHelper.hideSoftKeyboard(getActivity());
				return false;
			}
		});


		api = new API(this.getActivity());
		params = new ArrayList<NameValuePair>();
		ContainerFragment.contAct.txtSection.setText("");

		return view;
	}


	private void doSearch() {
		this.timer.cancel(); //this will cancel the current task. if there is no active task, nothing happens
		this.timer = new Timer();

		TimerTask action = new TimerTask() {
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						getEmployees(true, true);

					}
				});
			}
		};

		this.timer.schedule(action, 1000); //this starts the task
	}

	private void getEmployees(Boolean showProgress, Boolean isSearching) {
		if(!nextListItems.isEmpty()){
			getNextPageParams();
		} else {
			params.add(new BasicNameValuePair("check_ot", "true"));
			params.add(new BasicNameValuePair("include_hours", "true"));
			isNextPageItems = true;
		}
		if (isSearching) {			
			params.clear();
			params.add(new BasicNameValuePair("check_ot", "true"));
			params.add(new BasicNameValuePair("include_hours", "true"));
			
			String replaced = editTxtSearch.getText().toString().replace(" ", "+");
			params.add(new BasicNameValuePair("emp_filter", replaced));
			
			SharedPreferences prefs = this.getActivity().getSharedPreferences("mysettings", Context.MODE_PRIVATE);
			params.add(new BasicNameValuePair("organization", prefs.getString("v2organizationID", "")));
		}
		try {
			api.setApiClient(showProgress);
			api.get(params, getString(R.string.rest_user), new Callbacks() {
				@Override
				public void success(String json) {
					//parse json
					try {
						JSONObject obj = new JSONObject(json);

						JSONArray usersArr = obj.getJSONArray("objs");
						JSONObject configObj = obj.getJSONObject("meta");
						cellOffset = Integer.parseInt(configObj.getString("offset"));
						cellTotal = Integer.parseInt(configObj.getString("total"));
						nextListItems = configObj.getString("next");
						
						ArrayList<Employee> usersList = new ArrayList<Employee>();

						JSONObject usersObj;

						for (int i = 0; i < usersArr.length(); i++) {

							Employee user = new Employee();
							
							usersObj = usersArr.getJSONObject(i);

							user.setEmployeeFirstName(usersObj.getString("first_name"));
							user.setEmployeeLastName(usersObj.getString("last_name"));
							if(usersObj.has("_hours_current"))
								user.setHours(usersObj.getString("_hours_current"));
							if(usersObj.has("_employment_type_code_name"))
								user.setUserImage(usersObj.getString("_employment_type_code_name"));
							if(usersObj.has("title"))
								user.setShiftName(usersObj.getString("title"));
							user.setId(usersObj.getString("id"));
							user.setIsDividerCell(false);
							user.setIsLoader(false);
							if(usersObj.has("_year_calloff_count"))
								user.setYearCalloffCount(usersObj.getString("_year_calloff_count"));
							String lastName = user.getEmployeeLastName();
							//String letter = lastName.substring(0, 1);


							usersList.add(user);

							if (cellOffset > 0) {
								Employee lastItem = allUsersList.get(allUsersList.size()-1);
								if (lastItem.getIsLoader()) {
									allUsersList.remove(lastItem);
								}
								if (!isNextPageItems) {
									// Don't add the user to the list if is a new page, cause we'll be merging both lists below.
									allUsersList.add(user);
								}
							}
						}            
						flagLoading = false;

						if (cellOffset == 0) {
							allUsersList = usersList;
						}
						if (isNextPageItems) {
							allUsersList.addAll(usersList);
						}
						if (cellOffset + cellCount < cellTotal) {

							Employee loader = new Employee();
							loader.setIsLoader(true);
							loader.setIsDividerCell(false);
							usersList.add(loader);
						}
						if (allUsersList.size() == 0) {
							txtNoEmployees.setVisibility(View.VISIBLE);
						} else {
							txtNoEmployees.setVisibility(View.INVISIBLE);
						}

						setListAdapter(allUsersList);

						list.setSelectionFromTop(listIndex, listTop);

					} catch (Exception e) {
						e.printStackTrace();
						CommonHelper.addAlert("Error getting Users list", e.getMessage(),
								EnterNewCalloffFragment.this.getActivity());
					}
				}

				@Override
				public void failure(String error) {
					Log.e("USERS_LIST", "Error");
					Log.e("USERS_LIST", error);
					CommonHelper.addAlert("Users List connection error", error,
							EnterNewCalloffFragment.this.getActivity());
				}
			});

		} catch(Exception e){

			Log.e("EMPLOYEE_LIST", "NO INTERNET CONNECTION");
			Log.e("EMPLOYEE_LIST", e.getMessage());
			CommonHelper.addAlert("Server not responding", e.getMessage(),
					EnterNewCalloffFragment.this.getActivity());
		}
	}


	private void setListAdapter(ArrayList<Employee> data) {
		Log.d("Enter New Call Off", "setListAdapter");
		adapter = new EmployeeListAdapter(this.getActivity(), R.layout.enter_new_calloff_cell, data, true);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CommonHelper.hideSoftKeyboard(getActivity());

				Log.e("POSITION", ""+position);
				Employee user = allUsersList.get(position);
				Log.e("TAG", "USER ID "+user.getId());
				
				DecimalFormat decimalFormat = new DecimalFormat("#");
				double hours = Double.parseDouble(user.getHours());
				editTxtSearch.setText("");
				
				Bundle b = new Bundle();
				b.putString("userId",user.getId()); 
				b.putString("name", user.getEmployeeLastName()+", "+user.getEmployeeFirstName());
				b.putString("hours", decimalFormat.format(hours));
				b.putString("role", user.getPrimaryRoleType());
				b.putString("yearCount", user.getYearCalloffCount());
				b.putString("v2shift", v2shiftParam);
				
				ShiftSelectFragment fragment = new ShiftSelectFragment();
				fragment.setArguments(b);
				FragmentTransaction ft = getFragmentManager().beginTransaction();
				ft.replace(R.id.framelayout_content, fragment);
				ft.addToBackStack(null).commit();
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

							getEmployees(false, false);
						}
					}

				}
			}
		});
		list.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent ev)
			{
				CommonHelper.hideSoftKeyboard(getActivity());
				return false;
			}
		});
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
			params.add(new BasicNameValuePair("check_ot", "true"));
			params.add(new BasicNameValuePair("include_hours", "true"));
		}  
	}
}
