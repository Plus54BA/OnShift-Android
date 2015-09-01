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
import com.onshift.mobile.R;
import com.onshift.mobile.ShiftListAdapter;
import com.onshift.mobile.models.Shift;
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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.AbsListView.OnScrollListener;

public class ShiftSelectFragment extends Fragment {

	private ListView list;
	private View view;
	public static ShiftSelectFragment activity;
	private API api;
	private Boolean isNextPageItems = false;
	private int cellCount;
	private int cellOffset;
	private int cellTotal;
	private String nextListItems;
	private List<NameValuePair> shiftParams;
	public static ArrayList<Shift> allShiftsList;
	private Boolean flagLoading = false;
	private int listIndex;
	private int listTop;
	private ShiftListAdapter adapter;
	private String userId;
	private String yearCount;
	private String hours;
	private String employeeName;
	private String role;
	private Date previousDate;
	public static SwipeLayout openCell;
	private int selectedShiftIndex;
	private RelativeLayout rlAssignPopup;
	private TextView txtPopupTitle;
	private TextView txtRequiredAssigned;
	private Spinner spinnerReason;
	private Button btnAssign;
	private Button btnSendMsg;
    private Button btnRemove;
    private String v2shiftParam;
    private ArrayList<String> ptoList;
    private ArrayList<String> ptoIDList;
    private String selectedReason = "";
    private EditText editTxtAddNote;
    private Boolean isSendMsg;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/** Inflating the layout for this fragment **/
		view = inflater.inflate(R.layout.fragment_shiftselect, container,false);

		Bundle b = this.getArguments();
		if (b.containsKey("userId")) {
			userId = b.getString("userId");
		}
		if (b.containsKey("yearCount")) {
			yearCount = b.getString("yearCount");
		}
		if (b.containsKey("hours")) {
			hours = b.getString("hours");
		}
		if (b.containsKey("name")) {
			employeeName = b.getString("name");
		}
		if (b.containsKey("role")) {
			role = b.getString("role");
		}
		TextView txtName = (TextView) view.findViewById(R.id.txtName);
		txtName.setText(employeeName);
		TextView txtShift = (TextView) view.findViewById(R.id.txtShift);
		txtShift.setText(role);
		TextView txtCalloffs = (TextView) view.findViewById(R.id.txtCalloffs);
		txtCalloffs.setText(yearCount+" Call offs");
		TextView txtHours = (TextView) view.findViewById(R.id.txtHours);
		txtHours.setText(hours+" Hours/work period");

		activity = this;
		list = (ListView) view.findViewById(R.id.list);

		Typeface fHelveticaReg = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/HelveticaNeue-Roman.otf");
		Typeface fHelveticaBold = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/HelveticaNeue-Bold.otf");
		Typeface fHelveticaMed = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/HelveticaNeue-Medium.otf");
		txtName.setTypeface(fHelveticaMed);
		txtShift.setTypeface(fHelveticaReg);
		txtHours.setTypeface(fHelveticaReg);
		txtCalloffs.setTypeface(fHelveticaMed);

		rlAssignPopup = (RelativeLayout) view.findViewById(R.id.rlAssignPopup);
		ImageButton btnClose = (ImageButton) view.findViewById(R.id.btnClose);
		btnClose.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				rlAssignPopup.setVisibility(View.INVISIBLE);
			}
		});
		txtPopupTitle = (TextView) view.findViewById(R.id.txtPopupTitle);
		txtPopupTitle.setTypeface(fHelveticaMed);
		txtRequiredAssigned = (TextView) view.findViewById(R.id.txtRequiredAssigned);
		txtRequiredAssigned.setTypeface(fHelveticaMed);
		TextView txtRequirements = (TextView) view.findViewById(R.id.txtRequirements);
		txtRequirements.setTypeface(fHelveticaMed);
		editTxtAddNote = (EditText) view.findViewById(R.id.editTxtAddNote);
		spinnerReason = (Spinner) view.findViewById(R.id.spinnerReason);
		spinnerReason.setOnItemSelectedListener(new OnItemSelectedListener() {
		    @Override
		    public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
		    	Log.e("onItemSelected",ptoList.get(position));
		    	if (position == 0) {
					btnRemove.setEnabled(false);
					btnSendMsg.setEnabled(false);
					btnAssign.setEnabled(false);
					selectedReason = "";
				} else {
					btnRemove.setEnabled(true);
					btnSendMsg.setEnabled(true);
					btnAssign.setEnabled(true);
					selectedReason = ptoIDList.get(position);
				}
		    }
		    @Override
		    public void onNothingSelected(AdapterView<?> parentView) {
		    	Log.e("onNothingSelected","onNothingSelected");
		    	btnRemove.setEnabled(false);
				btnSendMsg.setEnabled(false);
				btnAssign.setEnabled(false);
		    }

		});
		
		
		btnRemove = (Button) view.findViewById(R.id.btnRemove);
		btnRemove.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				Log.e("btnRemove", "btnRemove");
			}
		});
		btnSendMsg = (Button) view.findViewById(R.id.btnSendMsg);
		btnSendMsg.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isSendMsg = true;
				createCalloff();
			}
		});
		btnAssign = (Button) view.findViewById(R.id.btnAssign);
		btnAssign.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				isSendMsg = false;
				createCalloff();
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
		shiftParams = new ArrayList<NameValuePair>();

		ContainerFragment.contAct.txtSection.setText("Enter Call Offs");

		getUpcomingSchedule(true);

		return view;
	}

	private void getUpcomingSchedule(Boolean showProgress) {

		if(!nextListItems.isEmpty()){
			getShiftNextPageParams();
			isNextPageItems = true;
		} else {
			shiftParams.add(new BasicNameValuePair("user", userId));
		}

		try {
			api.setApiClient(showProgress);
			api.get(shiftParams, getString(R.string.rest_shift), new Callbacks() {
				@Override
				public void success(String json) {
					//parse json
					try {
						JSONObject obj = new JSONObject(json);

						JSONArray shiftArr = obj.getJSONArray("objs");

						JSONObject configObj = obj.getJSONObject("meta");

						cellCount = Integer.parseInt(configObj.getString("count"));
						cellOffset = Integer.parseInt(configObj.getString("offset"));
						cellTotal = Integer.parseInt(configObj.getString("total"));
						nextListItems = configObj.getString("next");

						ArrayList<Shift> shiftList = new ArrayList<Shift>();

						JSONObject shiftObj;
						JSONArray periodArr;
						if (!isNextPageItems) {
							previousDate = new Date();
						}

						for (int i = 0; i < shiftArr.length(); i++) {

							Shift shift = new Shift();

							JSONObject arrObj = shiftArr.getJSONObject(i);
							shiftObj = arrObj.getJSONObject("Shift");
							periodArr = shiftObj.getJSONArray("shift_time_period");

							shift.setDate(periodArr.getString(0));
							shift.setStartTime(periodArr.getString(0));
							shift.setEndTime(periodArr.getString(1));
							shift.setShiftName(shiftObj.getString("shift_typeID"));
							shift.setId(shiftObj.getString("id"));
							shift.setIsDateDivider(false);
							shift.setIsLoader(false);
							shift.setLocation(shiftObj.getString("locationID"));

							String date = shift.getDate();
							String[] splitted = date.split(" ");
							String splitDate = splitted[0];
							SimpleDateFormat parseFormat = new SimpleDateFormat("yyyy-MM-dd");
							Date newDate = parseFormat.parse(splitDate);

							if ( (i == 0 && !isNextPageItems) || previousDate.before(newDate)) {
								previousDate = newDate;
								Shift shiftGroup = new Shift();
								shiftGroup.setDate(date);
								shiftGroup.setIsDateDivider(true);
								shift.setIsLoader(false);
								shiftList.add(shiftGroup);
							}

							shiftList.add(shift);

							if (cellOffset > 0) {

								Shift lastItem = allShiftsList.get(allShiftsList.size()-1);

								if (lastItem.getIsLoader()) {
									allShiftsList.remove(lastItem);
								}
								if (!isNextPageItems) {
									// Don't add the shift to the list if is a new page, cause we'll be merging both lists below.
									allShiftsList.add(shift);
								}
							}
						}
						flagLoading = false;

						if (cellOffset == 0) {
							allShiftsList = shiftList;
						}
						if (isNextPageItems) {
							// Merge all previous users with next page new users.
							allShiftsList.addAll(shiftList);
						}

						if (cellOffset + cellCount < cellTotal) {

							Shift loader = new Shift();
							loader.setIsLoader(true);
							loader.setIsDateDivider(false);
							shiftList.add(loader);
						}

						setListAdapter(allShiftsList);

						list.setSelectionFromTop(listIndex, listTop);

					} catch (Exception e) {
						e.printStackTrace();

						Log.e("UPCOMING_SCHEDULE", e.getMessage());
						CommonHelper.addAlert("Error getting Upcoming Schedule", e.getMessage(),
								ShiftSelectFragment.this.getActivity());

					}
				}

				@Override
				public void failure(String error) {
					Log.e("UPCOMING_SCHEDULE", "Error");
					Log.e("UPCOMING_SCHEDULE", error);
					CommonHelper.addAlert("Upcoming Schedule connection error", error,
							ShiftSelectFragment.this.getActivity());
				}
			});

		} catch(Exception e){

			Log.e("UPCOMING_SCHEDULE", "NO INTERNET CONNECTION");
			Log.e("UPCOMING_SCHEDULE", e.getMessage());
			CommonHelper.addAlert("Server not responding", e.getMessage(),
					ShiftSelectFragment.this.getActivity());
		}
	}

	private void getShiftNextPageParams(){
		shiftParams.clear();
		//parse and set offset and limit for next page
		try {
			List<NameValuePair> encodedParams = URLEncodedUtils.parse(new URI(nextListItems), "UTF-8");
			for (NameValuePair param : encodedParams) {
				shiftParams.add(new BasicNameValuePair(param.getName(), param.getValue()));
			}
		}
		catch (URISyntaxException e) {
			shiftParams.add(new BasicNameValuePair("user", userId));
		}
	}

	private void setListAdapter(ArrayList<Shift> data) {

		adapter = new ShiftListAdapter(this.getActivity(), R.layout.shift_list_cell, data);
		list.setAdapter(adapter);

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

							getUpcomingSchedule(false);

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
	
	private void getPtoTypes () {
		SharedPreferences settings = this.getActivity().getSharedPreferences("mysettings",
				Context.MODE_PRIVATE);
		ptoList = new ArrayList<String>();
		ptoIDList = new ArrayList<String>();
		ptoList.add("Reason");
		ptoIDList.add("0");
		
		//Populate this from stored shared preferences
	}

	private String getLocationById() {
		//Look up the location name from the id for stored locations
		return "";
	}

	private String getShiftTypeById(){
		//Look up the shift type from the id for stored shift types
		return "";
	}
	
	public static void showPopup (int index) {
		
		Shift entity = (Shift) allShiftsList.get(index);
		
		activity.selectedShiftIndex = Integer.valueOf(index);
		activity.rlAssignPopup.setVisibility(View.VISIBLE);
		activity.txtPopupTitle.setText(activity.employeeName+ " is removed from Schedule: "+entity.getLocation()+"\n"+CommonHelper.splitAndFormatDate(entity.getStartTime()) + " - "+ CommonHelper.splitAndFormatDate(entity.getEndTime()));
		activity.txtRequiredAssigned.setText(entity.getAssigned() + " A - " + entity.getRequired() + " R");
		activity.v2shiftParam = entity.getId();
		activity.getPtoTypes();
	}
	
	private void createCalloff () {
		
		String replaced = editTxtAddNote.getText().toString().replace(" ", "+");
		String params = "rest/calloff?shift="+v2shiftParam+"&time_off_type_id="+selectedReason+"&note="+replaced;

		ConnectionDetector cd = new ConnectionDetector(getActivity().getApplicationContext());
		Boolean isInternetPresent = cd.isConnectingToInternet();
		if (isInternetPresent)
		{       
			try {

				api.setApiClient(true);
				api.post(new ArrayList<NameValuePair>(), params, new Callbacks() {
					@Override
					public void success(String json) {
						//parse json

						try {
							JSONObject obj = new JSONObject(json);
							JSONArray objs = obj.getJSONArray("objs");
							//JSONObject calloffObj = objs.getJSONObject(0);
							//JSONObject calloff = calloffObj.getJSONObject("Calloff");
							
							if (objs.length() > 0) {
								Bundle b = new Bundle();
								/*b.putString("v2shift",calloff.getString("v2shift_id")); 
								b.putString("shiftName",calloff.getString("shift_type_display_name")); 
								b.putString("location",calloff.getString("location_display_name")); 
								b.putString("date",calloff.getString("shift_start_day")); 
								b.putString("message_id", calloff.getString("message_id"));
								*/
								CommonHelper.hideSoftKeyboard(activity.getActivity());
								EmployeeListFragment fragment = new EmployeeListFragment();
								fragment.setArguments(b);
								FragmentTransaction ft = getFragmentManager().beginTransaction();
								ft.replace(R.id.framelayout_content, fragment);
								ft.addToBackStack(null).commit();
								if (isSendMsg) {
									fragment.isFromSendMessage = true;
								} else {
									fragment.isFromAssignReplacement = true;
								}
							}
							
							
							
						} catch (Exception e) {
							e.printStackTrace();

							Log.e("CREATE_CALLOFF", e.getMessage());
							CommonHelper.addAlert("Error Creating Calloff", e.getMessage(),
									ShiftSelectFragment.this.getActivity());

						}
					}

					@Override
					public void failure(String error) {
						Log.e("CREATE_CALLOFF", "Error");
						Log.e("CREATE_CALLOFF", error);
						CommonHelper.addAlert("Create Calloff connection error", error,
								ShiftSelectFragment.this.getActivity());
					}
				});

			} catch(Exception e){

				Log.e("CREATE_CALLOFF", "NO INTERNET CONNECTION");
				Log.e("CREATE_CALLOFF", e.getMessage());
				CommonHelper.addAlert("Server not responding", e.getMessage(),
						ShiftSelectFragment.this.getActivity());
			}

		} else {

			CommonHelper.addAlert("CONNECTION ERROR", "No internet connection",
					ShiftSelectFragment.this.getActivity());
		}
		
	}
}
