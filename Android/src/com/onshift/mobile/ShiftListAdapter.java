package com.onshift.mobile;

import android.content.Context;
import android.graphics.Typeface;
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
import com.onshift.mobile.models.Shift;
import com.onshift.mobile.fragments.EmployeeListFragment;
import com.onshift.mobile.fragments.ShiftSelectFragment;
import com.onshift.mobile.helpers.CommonHelper;

/**
 * Created by amosiejko on 10/3/14.
 */
public class ShiftListAdapter extends ArrayAdapter<Shift> {

	private Context mContext;
	private ArrayList<Shift> data;
	private int cellId;
	private Button calloffBtn;
	private SwipeLayout swipeLayout;

	public ShiftListAdapter(Context c, int cellViewId, ArrayList<Shift> list) {

		super(c, R.layout.calloff_list_cell, list);
		mContext = c;
		cellId = cellViewId;
		data = list;
	}

	public int getCount() {
		return data.size();
	}

	public Shift getItem(int position) {
		return data.get(position);
	}

	public long getItemId(int position) {
		return data.get(position).hashCode();
	}

	// create a new ImageView for each item referenced by the Adapter
	public View getView(int position, View convertView, ViewGroup parent)
	{
		View view = convertView;
		Shift shift = data.get(position);

		if (convertView == null)
		{  // if it's not recycled, initialize some attributes

			if (shift.getIsDateDivider()) {

				LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.calloff_list_cell_date, null); 
				view.setEnabled(false);
			} else {
				if (shift.getIsLoader()) {

					LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(R.layout.calloff_list_cell_loading, null);

				} else {

					LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
					view = vi.inflate(cellId, null);

					calloffBtn = (Button) view.findViewById(R.id.btnReplace);
				}
			}
		}

		Typeface fHelveticaMed = Typeface.createFromAsset(mContext.getAssets(), "Fonts/HelveticaNeue-Medium.otf");
		Typeface fHelveticaBold = Typeface.createFromAsset(mContext.getAssets(), "Fonts/HelveticaNeue-Bold.otf");
		Typeface fHelveticaReg = Typeface.createFromAsset(mContext.getAssets(), "Fonts/HelveticaNeue-Roman.otf");

		if (shift.getIsDateDivider()) {
			// DIVIDER CELL WITH DATE

			LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = vi.inflate(R.layout.calloff_list_cell_date, null);
			view.setEnabled(false);
			TextView txtDate = (TextView)view.findViewById(R.id.txtDate);
			txtDate.setTypeface(fHelveticaBold);
			txtDate.setText(CommonHelper.formatDate(shift.getDate(), ""));

		} else {

			if (shift.getIsLoader()) {
				// PROGRESS LOADER CELL
				LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(R.layout.calloff_list_cell_loading, null);

			} else {
				// NORMAL CELL

				LayoutInflater vi = (LayoutInflater)mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
				view = vi.inflate(cellId, null);

				calloffBtn = (Button) view.findViewById(R.id.btnCalloff);

				TextView txtTime = (TextView)view.findViewById(R.id.txtTime);
				txtTime.setText(CommonHelper.splitAndFormatDate(shift.getStartTime()) + " - "+ CommonHelper.splitAndFormatDate(shift.getEndTime()));

				TextView txtShift = (TextView)view.findViewById(R.id.txtShift);
				txtShift.setText(shift.getShiftName());

				TextView txtLocation = (TextView)view.findViewById(R.id.txtLocation);
				txtLocation.setText(shift.getLocation());


				//set fonts
				txtTime.setTypeface(fHelveticaMed);
				txtShift.setTypeface(fHelveticaReg);
				txtLocation.setTypeface(fHelveticaReg);

				calloffBtn.setTag(position);

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

						if (ShiftSelectFragment.activity.openCell != null) {
							ShiftSelectFragment.activity.openCell.close();
						}
						ShiftSelectFragment.activity.openCell = arg0;
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
