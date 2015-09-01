package com.onshift.mobile.widgets;

import com.onshift.mobile.R;

import java.util.Calendar;
import java.util.Date;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.ViewSwitcher;
import android.widget.DatePicker.OnDateChangedListener;

public class SingleDatePicker extends RelativeLayout implements View.OnClickListener, OnDateChangedListener {
	// DatePicker reference
	private DatePicker	datePicker;
	// ViewSwitcher reference
	private ViewSwitcher viewSwitcher;
	// Calendar reference
	private Calendar mCalendar;

	private TextView currentDate;

	// Constructor start
	public SingleDatePicker(Context context) {
		this(context, null);
	}

	public SingleDatePicker(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public SingleDatePicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);

		// Get LayoutInflater instance
		final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		// Inflate myself
		currentDate = (TextView) inflater.inflate(R.layout.datetimepicker, this, true)
				.findViewById(R.id.CurrentDateTime);

		Date d = new Date();
		CharSequence s = DateFormat.format("MM/dd/yyyy", d.getTime());
		String date =(String) s;

		// Inflate the date and time picker views
		//final LinearLayout datePickerView = (LinearLayout) inflater.inflate(R.layout.datepicker, null);
		final LinearLayout datePickerView = (LinearLayout) inflater.inflate(R.layout.singledatepicker, null);

		// Grab a Calendar instance
		mCalendar = Calendar.getInstance();
		// Grab the ViewSwitcher so we can attach our picker views to it
		viewSwitcher = (ViewSwitcher) this.findViewById(R.id.DateTimePickerVS);

		// Init date picker
		datePicker = (DatePicker) datePickerView.findViewById(R.id.singleDatePicker);
		datePicker.init(mCalendar.get(Calendar.YEAR), mCalendar.get(Calendar.MONTH), mCalendar.get(Calendar.DAY_OF_MONTH), this);

		// Populate ViewSwitcher
		viewSwitcher.addView(datePickerView, 0);
	}
	// Constructor end

	// Called every time the user changes DatePicker values
	public void onDateChanged(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
		// Update the internal Calendar instance
		mCalendar.set(year, monthOfYear, dayOfMonth, mCalendar.get(Calendar.HOUR_OF_DAY), mCalendar.get(Calendar.MINUTE));
	}

	// Handle button clicks
	public void onClick(View v) {
		/*switch (v.getId()) {
			case R.id.SwitchToDate:
				v.setEnabled(false);
				findViewById(R.id.SwitchToTime).setEnabled(true);
				viewSwitcher.showPrevious();
				break;

			case R.id.SwitchToTime:
				v.setEnabled(false);
				findViewById(R.id.SwitchToDate).setEnabled(true);
				viewSwitcher.showNext();
				break;
		}
		 */
	}

	// Convenience wrapper for internal Calendar instance
	public int get(final int field) {
		return mCalendar.get(field);
	}

	// Reset DatePicker, TimePicker and internal Calendar instance
	public void reset() {
		final Calendar c = Calendar.getInstance();
		updateDate(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
	}

	// Convenience wrapper for internal Calendar instance
	public long getDateTimeMillis() {
		return mCalendar.getTimeInMillis();
	}

	// Convenience wrapper for internal DatePicker instance
	public void updateDate(int year, int monthOfYear, int dayOfMonth) {
		datePicker.updateDate(year, monthOfYear, dayOfMonth);
	}
}