package com.onshift.mobile.fragments;

import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.graphics.Typeface;
import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.onshift.mobile.ContainerActivity;
import com.onshift.mobile.R;
import com.onshift.mobile.helpers.CommonHelper;

/**
 * Created by amosiejko on 9/26/14.
 */
public class ContainerFragment extends Fragment {

	public static ImageButton menuButton;
	public static RelativeLayout allAppContent;
	public static Activity activity;
	public static ContainerFragment contAct;
	public static HomeFragment frHome;
	public static ImageButton msgButton;
	public static Context context;
	public static ImageButton backButton;
	public static ImageView imgTopbarLogo;
	public static TextView txtSection;
	public static RelativeLayout rlAssignOk;
	public static TextView txtShiftAssigned;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View view = inflater.inflate(R.layout.fragment_container, container,
				false);

		allAppContent = (RelativeLayout) view.findViewById(R.id.rl_app_content);
		menuButton = (ImageButton) view.findViewById(R.id.btnMenu);
		msgButton = (ImageButton) view.findViewById(R.id.btnMsg);
		backButton = (ImageButton) view.findViewById(R.id.btnBack);
		imgTopbarLogo = (ImageView) view.findViewById(R.id.imgTopbarLogo);
		txtSection = (TextView) view.findViewById(R.id.txtSection);
		rlAssignOk = (RelativeLayout) view.findViewById(R.id.rlAssignOk);
		txtShiftAssigned = (TextView) view.findViewById(R.id.txtShiftAssigned);
		Typeface fontOpenSans = Typeface.createFromAsset(getActivity()
				.getAssets(), "Fonts/OpenSans-Bold.ttf");
		txtShiftAssigned.setTypeface(fontOpenSans);

		activity = getActivity();
		context = getActivity().getApplicationContext();
		contAct = this;

		backButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

				Fragment currentFragment = getActivity().getFragmentManager()
						.findFragmentById(R.id.framelayout_content);
				if (currentFragment instanceof EmployeeListFragment) {
					if (EmployeeListFragment.rlConfirmReplace.getVisibility() == View.VISIBLE) {
						EmployeeListFragment.rlConfirmReplace
								.setVisibility(View.INVISIBLE);
						EmployeeListFragment.editTxtSearch.setEnabled(true);
						EmployeeListFragment.btnGoodChoice.setEnabled(true);
						EmployeeListFragment.btnViewAll.setEnabled(true);

					} else {
						if (EmployeeListFragment.isFromSendMessage
								|| EmployeeListFragment.isFromAssignReplacement) {
							CalloffsListFragment fragment = new CalloffsListFragment();
							FragmentTransaction ft = getFragmentManager()
									.beginTransaction();
							ft.replace(R.id.framelayout_content, fragment);
							ft.addToBackStack(null).commit();
							ContainerFragment.contAct.backButton
									.setVisibility(View.VISIBLE);
							ContainerFragment.contAct.msgButton
									.setVisibility(View.INVISIBLE);
							ContainerFragment.contAct.imgTopbarLogo
									.setVisibility(View.INVISIBLE);
							ContainerFragment.contAct.txtSection
									.setText("Call Offs");
							ContainerFragment.contAct.txtSection
									.setVisibility(View.VISIBLE);
						} else {
							activity.getFragmentManager().popBackStack();
						}
					}
				} else {
					activity.getFragmentManager().popBackStack();
				}

				if (activity.getFragmentManager().getBackStackEntryCount() == 1) {

					backButton.setVisibility(View.INVISIBLE);
					msgButton.setVisibility(View.VISIBLE);
					imgTopbarLogo.setVisibility(View.VISIBLE);
					txtSection.setVisibility(View.INVISIBLE);
				}

				CommonHelper.hideSoftKeyboard(getActivity());
			}
		});

		msgButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {

			}
		});

		menuButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				((ContainerActivity) getActivity()).showDrawer();
			}
		});

		frHome = new HomeFragment();

		contAct.getActivity().getFragmentManager().beginTransaction()
				.replace(R.id.framelayout_content, frHome)
				.commitAllowingStateLoss();

		return view;
	}

}
