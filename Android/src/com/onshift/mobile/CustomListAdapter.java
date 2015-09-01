package com.onshift.mobile;

import android.content.Context;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;

import com.daimajia.swipe.SwipeLayout;
import com.onshift.mobile.R;
import com.onshift.mobile.models.Calloff;
import com.onshift.mobile.fragments.CalloffsListFragment;
import com.onshift.mobile.helpers.CommonHelper;

/**
 * Created by amosiejko on 10/3/14.
 */
public class CustomListAdapter extends ArrayAdapter<Calloff> {

	private Context mContext;
	private ArrayList<Calloff> data;
	private int cellId;
	private Button replaceBtn;
	private SwipeLayout swipeLayout;

	public CustomListAdapter(Context c, int cellViewId, ArrayList<Calloff> list) {

		super(c, R.layout.calloff_list_cell, list);
		mContext = c;
		cellId = cellViewId;
		data = list;
	}

	public int getCount() {
		return data.size();
	}

	public Calloff getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return data.get(position).hashCode();
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		Calloff calloff = data.get(position);

		if (convertView == null)
		{  // if it's not recycled, initialize some attributes

			if (calloff.getIsDateDivider()) {

				LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.calloff_list_cell_date, null); 
				view.setEnabled(false);
			} else {
				if (calloff.getIsLoader()) {

					LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.calloff_list_cell_loading, null);

				} else {

					LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(cellId, null);

					replaceBtn = (Button) view.findViewById(R.id.btnReplace);

				}
			}
		}

		Typeface fHelveticaMed = Typeface.createFromAsset(mContext.getAssets(), "Fonts/HelveticaNeue-Medium.otf");
		Typeface fHelveticaBold = Typeface.createFromAsset(mContext.getAssets(), "Fonts/HelveticaNeue-Bold.otf");
		Typeface fHelveticaReg = Typeface.createFromAsset(mContext.getAssets(), "Fonts/HelveticaNeue-Roman.otf");

		if (calloff.getIsDateDivider()) {
			// DIVIDER CELL WITH DATE

			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.calloff_list_cell_date, null);
			view.setEnabled(false);
			TextView txtDate = (TextView)view.findViewById(R.id.txtDate);
			txtDate.setTypeface(fHelveticaBold);
			txtDate.setText(CommonHelper.formatDate(calloff.getDate(), ""));

		} else {

			if (calloff.getIsLoader()) {
				// PROGRESS LOADER CELL
				LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.calloff_list_cell_loading, null);

			} else {
				// NORMAL CELL

				LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(cellId, null);

				replaceBtn = (Button) view.findViewById(R.id.btnReplace);

				TextView txtTime = (TextView)view.findViewById(R.id.txtTime);
				txtTime.setText(CommonHelper.splitAndFormatDate(calloff.getStartTime()) + " - "+ CommonHelper.splitAndFormatDate(calloff.getEndTime()) + " " + calloff.getShiftName());

				//TextView txtResponses = (TextView)view.findViewById(R.id.txtResponses);
				//txtResponses.setText(calloff.getResponses()+" Yes Responses");

				TextView txtLocation = (TextView)view.findViewById(R.id.txtLocation);
				txtLocation.setText(calloff.getLocation());

				TextView txtName = (TextView)view.findViewById(R.id.txtName);
				txtName.setText(calloff.getEmployeeLastName() + ", " + calloff.getEmployeeName());

				TextView txtNotice = (TextView)view.findViewById(R.id.txtNotice);
				txtNotice.setText("Notice given: "+ calloff.getNotice());

				TextView txtReason = (TextView)view.findViewById(R.id.txtReason);
				txtReason.setText("Reason: " + calloff.getReason());

				TextView txtRequiredAssigned = (TextView)view.findViewById(R.id.txtRequiredAssigned);
				txtRequiredAssigned.setText(calloff.getAssigned() + " A - " + calloff.getRequired() + " R");

				//set fonts
				txtTime.setTypeface(fHelveticaMed);
				txtLocation.setTypeface(fHelveticaMed);
				txtName.setTypeface(fHelveticaReg);
				txtNotice.setTypeface(fHelveticaReg);
				txtReason.setTypeface(fHelveticaReg);
				txtRequiredAssigned.setTypeface(fHelveticaMed);

				replaceBtn.setTag(position);

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

						if (CalloffsListFragment.activity.openCell != null) {
							CalloffsListFragment.activity.openCell.close();
						}
						CalloffsListFragment.activity.openCell = arg0;
					}

					@Override
					public void onHandRelease(SwipeLayout arg0, float arg1, float arg2) {
					}

					@Override
					public void onClose(SwipeLayout arg0) {
					}
				});
			}
		}


		return view;
	}


}
