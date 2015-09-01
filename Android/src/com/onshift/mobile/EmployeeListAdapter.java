package com.onshift.mobile;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import java.text.DecimalFormat;
import java.util.ArrayList;

import com.daimajia.swipe.SwipeLayout;
import com.onshift.mobile.R;
import com.onshift.mobile.models.Employee;
import com.onshift.mobile.fragments.EmployeeListFragment;

/**
 * Created by amosiejko on 10/3/14.
 */
public class EmployeeListAdapter extends ArrayAdapter<Employee> {

	private Context mContext;
	private ArrayList<Employee> data;
	private int cellId;
	private Button replaceBtn;
	public static ImageButton btnCheck;
	private String userId;
	private Employee entity;
	private boolean isEnterNewCalloff;    
	private SwipeLayout swipeLayout;

	public EmployeeListAdapter(Context c, int cellViewId, ArrayList<Employee> list, boolean enterNewCalloff) {

		super(c, cellViewId, list);
		mContext = c;
		cellId = cellViewId;
		data = list;
		isEnterNewCalloff = enterNewCalloff;
	}

	public int getCount() {
		return data.size();
	}

	public Employee getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return data.get(position).hashCode();
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent)
	{
		
		View view = convertView;
		entity = data.get(position);

		if (!entity.getIsDividerCell()) {
			userId = entity.getId();
		}

		if (convertView == null)
		{  // if it's not recycled, initialize some attributes
			if (entity.getIsLoader()) {
				// PROGRESS LOADER CELL

				LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.calloff_list_cell_loading, null);

			} else {
				// NORMAL CELL

				LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(cellId, null);

				replaceBtn = (Button) view.findViewById(R.id.btnReplace);
				btnCheck = (ImageButton) view.findViewById(R.id.btnCheck);
				if (!isEnterNewCalloff) {
					btnCheck.setTag(userId);
				}
			}
		}

		Typeface fHelveticaMed = Typeface.createFromAsset(mContext.getAssets(), "Fonts/HelveticaNeue-Medium.otf");
		Typeface fHelveticaBold = Typeface.createFromAsset(mContext.getAssets(), "Fonts/HelveticaNeue-Bold.otf");
		Typeface fHelveticaReg = Typeface.createFromAsset(mContext.getAssets(), "Fonts/HelveticaNeue-Roman.otf");

		if (entity.getIsLoader()) {
			// PROGRESS LOADER CELL
			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.calloff_list_cell_loading, null);

		} else {
			// NORMAL CELL

			LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(cellId, null);

			replaceBtn = (Button) view.findViewById(R.id.btnReplace);
			if (!isEnterNewCalloff) {
				btnCheck = (ImageButton) view.findViewById(R.id.btnCheck);
				btnCheck.setSelected(entity.isChecked());

				btnCheck.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						ImageButton btn = (ImageButton)v;

						Employee checkedEntity = null;

						String currentId = String.valueOf(v.getTag());
						for (int k=0;k<data.size();k++){
							if (currentId.equals(data.get(k).getId())){
								checkedEntity=data.get(k);
								break;
							}
						}

						if(!btn.isSelected()) {
							btn.setSelected(true);
							btn.setImageResource(R.drawable.msg_checkbox_on);
							checkedEntity.setChecked(true);

							// ADD employee to message recipients array
							EmployeeListFragment.activity.arrCheckedEmployees.add(btn.getTag().toString());
							if (!EmployeeListFragment.activity.btnSendMsg.isEnabled()) {
								EmployeeListFragment.activity.btnSendMsg.setEnabled(true);
							}
						} else {
							btn.setSelected(false);
							btn.setImageResource(R.drawable.msg_checkbox_off);
							checkedEntity.setChecked(false);
							// REMOVE employee from message recipients array
							for (int i = 0; i < EmployeeListFragment.activity.arrCheckedEmployees.size();i++) {
								if (EmployeeListFragment.activity.arrCheckedEmployees.get(i).contains(btn.getTag().toString())) {
									EmployeeListFragment.activity.arrCheckedEmployees.remove(i);
								}
							}

							if (EmployeeListFragment.activity.arrCheckedEmployees.size() == 0) {
								EmployeeListFragment.activity.btnSendMsg.setEnabled(false);
							} 
						}
					}
				});

				swipeLayout = (SwipeLayout) view.findViewById(R.id.swipeLayout);
				swipeLayout.setTag(position);
				swipeLayout.addSwipeListener(new SwipeLayout.SwipeListener() {

					@Override
					public void onUpdate(SwipeLayout arg0, int arg1, int arg2) {
					}

					@Override
					public void onStartOpen(SwipeLayout arg0) {
					}

					@Override
					public void onStartClose(SwipeLayout arg0) {
					}

					@Override
					public void onOpen(SwipeLayout arg0) {

						if (EmployeeListFragment.activity.openCell != null) {
							EmployeeListFragment.activity.openCell.close();
						}
						EmployeeListFragment.activity.openCell = arg0;
					}

					@Override
					public void onHandRelease(SwipeLayout arg0, float arg1, float arg2) {
					}

					@Override
					public void onClose(SwipeLayout arg0) {
					}
				});
			}

			if (entity.getShowCheckBox()) {
				btnCheck.setVisibility(View.VISIBLE);
			}

			ImageView imgUser = (ImageView)view.findViewById(R.id.imgUser);
			Resources res = mContext.getResources();
			int resID = res.getIdentifier(entity.getUserImage() , "drawable", mContext.getPackageName());
			Drawable drawable = res.getDrawable(resID );
			imgUser.setImageDrawable(drawable);

			TextView txtName = (TextView)view.findViewById(R.id.txtName);
			if (isEnterNewCalloff) {
				txtName.setText(entity.getEmployeeLastName()+", "+entity.getEmployeeFirstName());
			} else {
				txtName.setText(entity.getEmployeeLastName()+", "+entity.getEmployeeFirstName()+" "+entity.getPrimaryRoleType());
			}

			DecimalFormat decimalFormat = new DecimalFormat("#");

			double hours = Double.parseDouble(entity.getHours());

			TextView txtHours = (TextView)view.findViewById(R.id.txtHours);
			if (isEnterNewCalloff) {
				txtHours.setText(hours + "hrs "+entity.getShiftName());
			} else {
				txtHours.setText(decimalFormat.format(hours) + " Hours/pay period");
			}

			TextView txtShiftEmpHours = (TextView)view.findViewById(R.id.txtShiftEmpHours);
			if (!isEnterNewCalloff) {
				double empHours = Double.parseDouble(entity.getHoursIncludingShift());
				txtShiftEmpHours.setText(decimalFormat.format(empHours) + " including this shift");

				TextView txtOTHours = (TextView)view.findViewById(R.id.txtOTHours);
				ImageView imgThresholdHours = (ImageView)view.findViewById(R.id.imgThresholdShift);

				TextView txtOT = (TextView)view.findViewById(R.id.txtOT);
				ImageView imgThreshold = (ImageView)view.findViewById(R.id.imgThreshold);

				if (entity.getOvertimeThisShift()) {
					txtOT.setVisibility(View.VISIBLE);
				} else {
					txtOT.setVisibility(View.INVISIBLE);
				}
				if (entity.getOvertime()) {
					txtOTHours.setVisibility(View.VISIBLE);
				} else {
					txtOTHours.setVisibility(View.INVISIBLE);
				}

				/*if (entity.getThresholdThisShift().equals("threshold")) {
					imgThreshold.setVisibility(View.VISIBLE);
				} else {
					imgThreshold.setVisibility(View.INVISIBLE);
				}
				if (entity.getThreshold().equals("threshold")) {
					imgThresholdHours.setVisibility(View.VISIBLE);
				} else {
					imgThresholdHours.setVisibility(View.INVISIBLE);
				}*/

				txtOT.setTypeface(fHelveticaBold);
				txtOTHours.setTypeface(fHelveticaBold);
				txtShiftEmpHours.setTypeface(fHelveticaReg);
				replaceBtn.setTag(position);
				btnCheck.setTag(userId);


				if (btnCheck.isSelected()) {		        		
					btnCheck.setImageResource(R.drawable.msg_checkbox_on);
				} else {		        		
					btnCheck.setImageResource(R.drawable.msg_checkbox_off);
				}
				
				ImageButton imgResponse = (ImageButton) view.findViewById(R.id.imgResponse);
				TextView txtTime = (TextView) view.findViewById(R.id.txtTime);
				TextView txtDate = (TextView) view.findViewById(R.id.txtDate);
				TextView txtHired = (TextView) view.findViewById(R.id.txtHired);
				TextView txtHiredDate = (TextView) view.findViewById(R.id.txtHiredDate);
				
				if ((entity.getBoolResponse() == null) && (entity.getMessageId().equals("null"))) {
					// Don't show response icons nor hire and message dates
					imgResponse.setVisibility(View.INVISIBLE);
					txtTime.setVisibility(View.INVISIBLE);
					txtDate.setVisibility(View.INVISIBLE);
					txtHired.setVisibility(View.INVISIBLE);
					txtHiredDate.setVisibility(View.INVISIBLE);
					ListView.LayoutParams params = new ListView.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, 190);
		            view.setLayoutParams(params);
				} 
				
				if (!entity.getMessageId().equals("null")) {
					// Show Message icon
					imgResponse.setImageResource(R.drawable.icon_viewmsg);
				}  
				
				if(entity.getBoolResponse() != null) {
					if (entity.getBoolResponse()) {
						// Show YES icon
						imgResponse.setImageResource(R.drawable.icon_yes);
					} else {
						// Show NO icon
						imgResponse.setImageResource(R.drawable.icon_no);
					}
				}
			}

			txtName.setTypeface(fHelveticaMed);
			txtHours.setTypeface(fHelveticaReg);
			
			/*TextView txtHired = (TextView)view.findViewById(R.id.txtHired);
			Log.d("EmplayeeListAdapter", "txtHired::" + txtHired);
			txtHired.setTypeface(fHelveticaBold);
			TextView txtHiredDate = (TextView)view.findViewById(R.id.txtHiredDate);
			txtHiredDate.setTypeface(fHelveticaReg);
			TextView txtDate = (TextView)view.findViewById(R.id.txtDate);
			txtDate.setTypeface(fHelveticaReg);
			TextView txtTime = (TextView)view.findViewById(R.id.txtTime);
			txtTime.setTypeface(fHelveticaReg);
			txtHiredDate.setText(entity.getHireDate());
			if (txtHiredDate.getText().equals("null")) {
				txtHiredDate.setVisibility(View.INVISIBLE);
				txtHired.setVisibility(View.INVISIBLE);
			}*/
			/*String [] splitted = entity.getMessageDate().split(" "); // Split date and time
			txtDate.setText(splitted[0]);
			if (splitted.length > 1) {
				String [] time = splitted[1].split(":"); // Remove seconds
				txtTime.setText(time[0]+":"+time[1]);
			}*/
		}
		
		return view;
	}
}