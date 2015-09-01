package com.onshift.mobile.fragments;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import org.apache.http.NameValuePair;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONObject;

import com.daimajia.swipe.SwipeLayout;
import com.onshift.mobile.EmployeeListAdapter;
import com.onshift.mobile.R;
import com.onshift.mobile.models.Message;
import com.onshift.mobile.models.Employee;
import com.onshift.mobile.helpers.CommonHelper;
import com.onshift.mobile.models.Callbacks;
import com.onshift.mobile.utils.API;
import android.os.Bundle;
import android.app.Fragment;
import android.app.FragmentTransaction;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class EmployeeListFragment extends Fragment {

	private API api;
	private View view;
	private ListView list;
	private EmployeeListAdapter adapter;
	private int cellCount;
	private int cellOffset;
	private int cellTotal;
	private String nextListItems = "";
	private List<NameValuePair> employeeParams, assignParams;
	public static ArrayList<Employee> allEmployeesList;
	private Boolean flagLoading = false;
	private int listIndex;
	private int listTop;
	private String v2shiftParam;
	private String typeParam;
	public static Button btnGoodChoice;
	public static Button btnViewAll;
	private Button btnSelected;
	private String shiftName;
	private String date;
	private String location;
	private String messageId;
	private Timer timer = new Timer();
	private Timer timer2 = new Timer();
	public static EditText editTxtSearch;
	private Boolean isNextPageItems = false;
	private String previousLetter;
	private TextView txtNoEmployees;
	private ImageView imgSearch;
	public static RelativeLayout rlConfirmReplace;
	public static TextView txtEmpName;
	public static TextView txtEmpLastName;
	public static TextView txtDate;
	public static TextView txtLocation;
	public static TextView txtShift;
	public static String assignEmployeeId = "";
	public static Button btnMsgSelect;
	public static ArrayList<String> arrCheckedEmployees;
	public static EmployeeListFragment activity;
	public static LinearLayout llSendMsgBtns;
	public static Button btnCancelMsg;
	public static Button btnSendMsg;
	public static SwipeLayout openCell;
	private Boolean isGetAll = false;
	public static Boolean isFromSendMessage = false;
	public static Boolean isFromAssignReplacement = false;


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		/** Inflating the layout for this fragment **/
		view = inflater.inflate(R.layout.fragment_employeelist, container,false);
		Bundle b = this.getArguments();
		if (b.containsKey("v2shift")) {
			v2shiftParam = b.getString("v2shift");
		}
		if (b.containsKey("shiftName")) {
			shiftName = b.getString("shiftName");
		}
		if (b.containsKey("date")) {
			date = b.getString("date");
		}
		if (b.containsKey("location")) {
			location = b.getString("location");
		}
		if (b.containsKey("message_id")) {
			messageId = b.getString("message_id");
		}
		CommonHelper.hideSoftKeyboard(this.getActivity());
		activity = this;
		arrCheckedEmployees = new ArrayList<String>();
		list = (ListView) view.findViewById(R.id.listCalloffs);
		rlConfirmReplace = (RelativeLayout) view.findViewById(R.id.rlConfirmReplace);
		txtEmpName = (TextView) view.findViewById(R.id.txtEmpName);
		txtEmpLastName = (TextView) view.findViewById(R.id.txtEmpLastName);
		txtDate = (TextView) view.findViewById(R.id.txtDate);
		txtLocation = (TextView) view.findViewById(R.id.txtLocation);
		txtShift = (TextView) view.findViewById(R.id.txtShift);

		Typeface fHelveticaReg = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/HelveticaNeue-Roman.otf");
		Typeface fHelveticaBold = Typeface.createFromAsset(getActivity().getAssets(), "Fonts/HelveticaNeue-Bold.otf");

		txtEmpLastName.setTypeface(fHelveticaBold);
		txtEmpName.setTypeface(fHelveticaReg);
		TextView txtTitle = (TextView) view.findViewById(R.id.txtTitle);
		txtTitle.setTypeface(fHelveticaBold);
		
		employeeParams = new ArrayList<NameValuePair>();
		assignParams = new ArrayList<NameValuePair>();
		
		Button btnAssignShift = (Button) view.findViewById(R.id.btnAssignShift);
		btnAssignShift.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				assignShift();
			}
		});
		
		imgSearch = (ImageView)view.findViewById(R.id.imgSearch);
		txtNoEmployees = (TextView)view.findViewById(R.id.txtNoEmployees);
		txtNoEmployees.setTypeface(fHelveticaReg);
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
		list.setOnTouchListener(new View.OnTouchListener()
		{
			@Override
			public boolean onTouch(View view, MotionEvent ev)
			{
				CommonHelper.hideSoftKeyboard(getActivity());
				return false;
			}
		});

		api = new API(this.getActivity());

		setupButtons();
		typeParam = "goodchoice";
		if (isFromSendMessage) {
			typeParam = "qualified";
			selectCurrentButton(btnViewAll);
			btnSelected = btnViewAll;
			CommonHelper.hideSoftKeyboard(getActivity());
			getQualifiedEmployees(true, false, true); // Show all employees without pagination
		} else {
			getQualifiedEmployees(true, false, false);
		}
		
		return view;
	}

	private void showHideCheckBoxes (Boolean show) {
		// SHOW CHECKBOXES TO SELECT EMPLOYEES
		View cell;
		Employee user;
		for (int i = 0; i < list.getCount(); i++) {
			cell = list.getChildAt(i - list.getFirstVisiblePosition());
			if (cell != null) {
				user = allEmployeesList.get(i);
				if(!user.getIsDividerCell()) {
					ImageButton btnCheck = (ImageButton) cell.findViewById(R.id.btnCheck);
					btnCheck.setSelected(show);
					if (show) {
						if (user.getBoolResponse()) {
							btnCheck.setImageResource(R.drawable.msg_checkbox_off);
							btnCheck.setEnabled(false);
						} else {
							btnCheck.setImageResource(R.drawable.msg_checkbox_on);
							arrCheckedEmployees.add(user.getId());
							btnCheck.setEnabled(true);
						}
						btnCheck.setVisibility(View.VISIBLE);

					} else {
						btnCheck.setVisibility(View.INVISIBLE);
						btnCheck.setImageResource(R.drawable.msg_checkbox_off);
					}
				}
			}
		}
		for (int i = 0; i < allEmployeesList.size(); i++) {
			user = allEmployeesList.get(i);
			if(!user.getIsDividerCell()) {
				if (user.getBoolResponse() != null) {
					user.setShowCheckBox(false);
					user.setChecked(false);
				} else {
					user.setShowCheckBox(show);
					user.setChecked(show);
					if (!arrCheckedEmployees.contains(user.getId())) {
						arrCheckedEmployees.add(user.getId());
					}
				}
			}
		}
		/*Log.d("TAG", "value of show: " + show.toString());
		if (show) {
			llSendMsgBtns.setVisibility(View.VISIBLE);
			//btnMsgSelect.setVisibility(View.INVISIBLE);
		} else {
			llSendMsgBtns.setVisibility(View.INVISIBLE);
			//btnMsgSelect.setVisibility(View.VISIBLE);
			arrCheckedEmployees = new ArrayList<String>();
		}*/
	}

	private void doSearch() {
		this.timer.cancel(); //this will cancel the current task. if there is no active task, nothing happens
		this.timer = new Timer();

		TimerTask action = new TimerTask() {
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						selectCurrentButton(btnViewAll);
						getQualifiedEmployees(true, true, false);
					}
				});
			}
		};

		this.timer.schedule(action, 1000); //this starts the task
	}

	private void selectCurrentButton (Button btn) {
		btn.setSelected(true);
		btn.setBackgroundColor(Color.parseColor("#929292"));

		if (btnSelected != null) {
			btnSelected.setSelected(false);
			btnSelected.setBackgroundColor(Color.parseColor("#646469"));
		}
	}

	private void setupButtons() { 	
		btnGoodChoice = (Button)view.findViewById(R.id.btnGoodChoice);
		btnGoodChoice.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				typeParam = "goodchoice";
				getQualifiedEmployees(true, false, false);
				selectCurrentButton(btnGoodChoice);
				CommonHelper.hideSoftKeyboard(getActivity());
				showHideCheckBoxes(false);
				btnSelected = btnGoodChoice;
			}
		});

		btnViewAll = (Button)view.findViewById(R.id.btnViewAll);
		btnViewAll.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				typeParam = "qualified";
				getQualifiedEmployees(true, false, false);
				selectCurrentButton(btnViewAll);
				CommonHelper.hideSoftKeyboard(getActivity());
				showHideCheckBoxes(false);
				btnSelected = btnViewAll;
			}
		});

		selectCurrentButton(btnGoodChoice);
		btnSelected = btnGoodChoice;
	}

	private void setListAdapter(ArrayList<Employee> data) {
		adapter = new EmployeeListAdapter(this.getActivity(), R.layout.employee_list_cell, data, false);
		list.setAdapter(adapter);
		list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				CommonHelper.hideSoftKeyboard(getActivity());

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
							getQualifiedEmployees(false, false, false);
						}
					}
				}
			}
		});
	}

	private void getQualifiedEmployees(Boolean showProgress, Boolean isSearching, Boolean getAll) {
		isGetAll = getAll;
		if (isSearching) {
			employeeParams.clear();
			String replaced = editTxtSearch.getText().toString().replace(" ", "+");
			employeeParams.add(new BasicNameValuePair("emp_filter", replaced));
			nextListItems = "";
		}
		if(!nextListItems.isEmpty()){
			getEmployeeNextPageParams();
		} else {
			employeeParams.add(new BasicNameValuePair("v2shift", v2shiftParam));
			employeeParams.add(new BasicNameValuePair("limit", "10"));
			if(typeParam.equals("goodchoice")) {
				employeeParams.add(new BasicNameValuePair("good_choice", "true"));
			}
		}
		try {
			api.setApiClient(showProgress);
			api.get(employeeParams, getString(R.string.rest_qualified_employees), new Callbacks() {
				@Override
				public void success(String json) {
					//parse json
					try {
						JSONObject obj = new JSONObject(json);
						JSONArray empArr = obj.getJSONArray("objs");
						JSONObject configObj = obj.getJSONObject("meta");

						cellCount = Integer.parseInt(configObj.getString("count"));
						cellOffset = Integer.parseInt(configObj.getString("offset"));
						cellTotal = Integer.parseInt(configObj.getString("total"));
						nextListItems = configObj.getString("next");

						ArrayList<Employee> employeeList = new ArrayList<Employee>();
						JSONObject empObj;
						if (!isNextPageItems) {
							previousLetter = "";
						}
						for (int i = 0; i < empArr.length(); i++) {
							Employee emp = new Employee();
							empObj = empArr.getJSONObject(i);
							emp.setEmployeeFirstName(empObj.getString("first_name"));
							emp.setEmployeeLastName(empObj.getString("last_name"));
							emp.setHours(empObj.getString("_hours_current"));
							emp.setHoursIncludingShift(empObj.getString("_hours_including_shift"));
							emp.setUserImage(empObj.getString("_employment_type_code_name"));
							emp.setOvertime(Boolean.valueOf((empObj.getString("_is_over_threshold").equals("null")) ? "false" : empObj.getString("_is_over_threshold")));
							emp.setOvertimeThisShift(Boolean.valueOf((empObj.getString("_is_over_threshold_including_shift").equals("null")) ? "false" : empObj.getString("_is_over_threshold_including_shift")));
							emp.setShiftName(shiftName);
							emp.setLocation(location);
							emp.setDate(date);
							emp.setId(empObj.getString("id"));
							emp.setIsDividerCell(false);
							emp.setIsLoader(false);
							emp.setPrimaryRoleType((empObj.getString("title").equals("null")) ? "" : empObj.getString("title"));
							if(!(empObj.getString("_yes_no_response").equals("null"))){
								emp.setBoolResponse(Boolean.valueOf(empObj.getString("_yes_no_response")));
							}
							emp.setHireDate(empObj.getString("hire_date"));
							emp.setMessageId(empObj.getString("_non_boolean_response_message_id"));
							//emp.setMessageDate(objResponses.getString("last_message_date"));
							employeeList.add(emp);
							Log.d("test", "added employee");

							if (cellOffset > 0) {
								Employee lastItem = allEmployeesList.get(allEmployeesList.size()-1);
								if (lastItem.getIsLoader()) {
									allEmployeesList.remove(lastItem);
								}
								if (!isNextPageItems) {
									// Don't add the user to the list if is a new page, cause we'll be merging both lists below.
									allEmployeesList.add(emp);
								}
							}
						}            
						flagLoading = false;

						if (cellOffset == 0) {
							allEmployeesList = employeeList;
						}
						if (isNextPageItems) { 
							// Merge all previous users with next page new users.
							allEmployeesList.addAll(employeeList);
						}

						if (cellOffset + cellCount < cellTotal) {
							Employee loader = new Employee();
							loader.setIsLoader(true);
							loader.setIsDividerCell(false);
							employeeList.add(loader);
						}

						if (allEmployeesList.size() == 0) {
							txtNoEmployees.setVisibility(View.VISIBLE);
						} else {
							txtNoEmployees.setVisibility(View.INVISIBLE);
						}

						setListAdapter(allEmployeesList);

						list.setSelectionFromTop(listIndex, listTop);

						if (isGetAll){
							showHideCheckBoxes(true); // Show checkboxes when Message Select button was tapped.
						}
						selectCurrentButton(btnViewAll);
						
					} catch (Exception e) {
						Log.e("USERS_LIST", Log.getStackTraceString(e));
						CommonHelper.addAlert("Error getting Users list", "error",
								EmployeeListFragment.this.getActivity());
					}
				}

				@Override
				public void failure(String error) {
					Log.e("USERS_LIST", "Error");
					Log.e("USERS_LIST", error);
					CommonHelper.addAlert("Users List connection error", error,
							EmployeeListFragment.this.getActivity());
				}
			});
		} catch(Exception e){
			Log.e("EMPLOYEE_LIST", "NO INTERNET CONNECTION");
			Log.e("EMPLOYEE_LIST", e.getMessage());
			CommonHelper.addAlert("Server not responding", e.getMessage(),
					EmployeeListFragment.this.getActivity());
		}
	}

	private void getEmployeeNextPageParams(){
		employeeParams.clear();
		//parse and set offset and limit for next page
		try {
			List<NameValuePair> encodedParams = URLEncodedUtils.parse(new URI(nextListItems), "UTF-8");
			for (NameValuePair param : encodedParams) {
				employeeParams.add(new BasicNameValuePair(param.getName(), param.getValue()));
			}
		}
		catch (URISyntaxException e) {
		}  
	}

	private void assignShift() {
		assignParams.add(new BasicNameValuePair("employee", assignEmployeeId));
		assignParams.add(new BasicNameValuePair("v2shift", v2shiftParam));

		try {
			api.setApiClient(true);
			api.post(assignParams, getString(R.string.rest_assign_calloff), new Callbacks() {
				@Override
				public void success(String json) {
					//parse json
					try {
						JSONObject jsonObj = new JSONObject(json);
						JSONObject obj = jsonObj.getJSONObject("objs");
						ContainerFragment.rlAssignOk.setVisibility(View.VISIBLE);
						ContainerFragment.txtShiftAssigned.setText("Shift was Assigned");
						Animation animFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in);
						ContainerFragment.rlAssignOk.setAnimation(animFadeIn);

						TimerTask action = new TimerTask() {
							public void run() {
								getActivity().runOnUiThread(new Runnable() {
									@Override
									public void run() {
										Animation animFadeOut = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_out);
										ContainerFragment.rlAssignOk.setAnimation(animFadeOut);

										HomeFragment fragment = new HomeFragment();
										FragmentTransaction ft = getFragmentManager().beginTransaction();
										ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out);
										ft.replace(R.id.framelayout_content, fragment);
										ft.addToBackStack(null).commit();
										editTxtSearch.setEnabled(true);
										ContainerFragment.contAct.backButton.setVisibility(View.INVISIBLE);
										ContainerFragment.contAct.msgButton.setVisibility(View.VISIBLE);
										ContainerFragment.contAct.imgTopbarLogo.setVisibility(View.VISIBLE);
										ContainerFragment.contAct.txtSection.setText("");
										ContainerFragment.contAct.txtSection.setVisibility(View.INVISIBLE);

									}
								});
							}
						};
						timer2.schedule(action, 1500);
					} catch (Exception e) {
						e.printStackTrace();

						Log.e("ASSIGN_SHIFT", e.getMessage());
						CommonHelper.addAlert("Error assigning shift", "Employee already working",
								EmployeeListFragment.this.getActivity());

					}
				}

				@Override
				public void failure(String error) {
					Log.e("ASSIGN_SHIFT", "Error");
					Log.e("ASSIGN_SHIFT", error);
					CommonHelper.addAlert("Users List connection error", error,
							EmployeeListFragment.this.getActivity());
				}
			});

		} catch(Exception e){

			Log.e("ASSIGN_SHIFT", "NO INTERNET CONNECTION");
			Log.e("ASSIGN_SHIFT", e.getMessage());
			CommonHelper.addAlert("Server not responding", e.getMessage(),
					EmployeeListFragment.this.getActivity());
		}
	}

	private void sendMessage() {

		String recipients = "";

		for (String s : arrCheckedEmployees)
		{
			if (recipients == "") {
				recipients += s ;
			} else {
				recipients += "," + s ;
			}

		}
		//String splitted = recipients.substring(0, recipients.length()-3);
		Log.e("RECIPIENTS", recipients);
		Log.e("MESSAGE ID", messageId);
		Log.e("assignEmployeeId", assignEmployeeId);

		String params;
		if (messageId.equals("null")) {
			params = "rest/shift_message?recipients="+ recipients+"&message_text";
		} else {
			params = "rest/shift_message?message_id="+ messageId +"&recipients="+ recipients+"&message_text";
		}

		//Fake sending the message for now
		ContainerFragment.rlAssignOk.setVisibility(View.VISIBLE);
		ContainerFragment.txtShiftAssigned.setText("Message Sent");
		Animation animFadeIn = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_in);
		ContainerFragment.rlAssignOk.setAnimation(animFadeIn);
		TimerTask action = new TimerTask() {
			public void run() {
				getActivity().runOnUiThread(new Runnable() {
					@Override
					public void run() {
						Animation animFadeOut = AnimationUtils.loadAnimation(getActivity().getApplicationContext(), android.R.anim.fade_out);
						ContainerFragment.rlAssignOk.setAnimation(animFadeOut);
						ContainerFragment.rlAssignOk.setVisibility(View.INVISIBLE);
						showHideCheckBoxes(false);
						getQualifiedEmployees(true, false, false); //Reload list with pagination.
					}
				});
			}

		};
		timer2.schedule(action, 1500);
	}
}