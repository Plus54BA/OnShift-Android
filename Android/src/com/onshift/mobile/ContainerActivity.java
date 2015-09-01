package com.onshift.mobile;

import com.onshift.mobile.models.Calloff;
import com.onshift.mobile.models.Employee;
import com.onshift.mobile.fragments.CalloffsListFragment;
import com.onshift.mobile.fragments.ContainerFragment;
import com.onshift.mobile.fragments.EmployeeListFragment;
import com.onshift.mobile.fragments.ShiftSelectFragment;
import com.onshift.mobile.helpers.CommonHelper;

import android.app.FragmentTransaction;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

public class ContainerActivity extends DrawerActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_container);

		if (savedInstanceState == null) {

			ContainerFragment fragment = new ContainerFragment();

			FragmentTransaction ft = getFragmentManager().beginTransaction();
			ft.replace(R.id.container, fragment);
			ft.commit();
		}
	}

	public void replaceBtnClickHandler(View v) {
		String tag = String.valueOf(v.getTag());
		Calloff calloff = CalloffsListFragment.allCallOffList.get(Integer
				.valueOf(tag));

		Bundle b = new Bundle();
		b.putString("v2shift", calloff.getV2shiftId());
		b.putString("shiftName", calloff.getShiftName());
		b.putString("location", calloff.getLocation());
		b.putString("date", calloff.getDate());
		b.putString("message_id", calloff.getMessageId());

		EmployeeListFragment fragment = new EmployeeListFragment();
		fragment.setArguments(b);
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.replace(R.id.framelayout_content, fragment);
		ft.addToBackStack(null).commit();
		fragment.isFromSendMessage = false;
		fragment.isFromAssignReplacement = false;
		ContainerFragment.contAct.backButton.setVisibility(View.VISIBLE);
		ContainerFragment.contAct.msgButton.setVisibility(View.INVISIBLE);
		ContainerFragment.contAct.imgTopbarLogo.setVisibility(View.INVISIBLE);
		ContainerFragment.contAct.txtSection.setText("Assign");
		ContainerFragment.contAct.txtSection.setVisibility(View.VISIBLE);
	}

	public void assignBtnClickHandler(View v) {
		String tag = String.valueOf(v.getTag());
		Employee entity = EmployeeListFragment.allEmployeesList.get(Integer
				.valueOf(tag));

		EmployeeListFragment.txtEmpName.setText(entity.getEmployeeFirstName());
		EmployeeListFragment.txtEmpLastName.setText(entity
				.getEmployeeLastName() + ", ");
		EmployeeListFragment.editTxtSearch.setEnabled(false);
		EmployeeListFragment.btnGoodChoice.setEnabled(false);
		EmployeeListFragment.btnViewAll.setEnabled(false);

		EmployeeListFragment.txtDate.setText(CommonHelper.formatDate(
				entity.getDate(), "MM/dd/yyyy"));
		EmployeeListFragment.txtLocation.setText(entity.getLocation());
		EmployeeListFragment.txtShift.setText(entity.getShiftName());
		EmployeeListFragment.rlConfirmReplace.setVisibility(View.VISIBLE);
		EmployeeListFragment.assignEmployeeId = entity.getId();

		Log.e("TAG ", tag);

	}

	public void calloffBtnClickHandler(View v) {
		String tag = String.valueOf(v.getTag());
		ShiftSelectFragment.showPopup(Integer.valueOf(tag));
	}
}
