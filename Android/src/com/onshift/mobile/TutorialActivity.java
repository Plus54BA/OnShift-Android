package com.onshift.mobile;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class TutorialActivity extends Activity {

	private ViewPager mViewPager;
	private SwipeAdapter adapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial);

		mViewPager = (ViewPager) findViewById(R.id.pager);
		adapter = new SwipeAdapter(TutorialActivity.this);
		mViewPager.setAdapter(adapter);
	}

	public void exitTutorial(View v) {
		finish();
	}

	public void nextSlide(View v) {
		mViewPager.setCurrentItem(mViewPager.getCurrentItem() + 1);
	}
	private class SwipeAdapter extends PagerAdapter {

		private LayoutInflater mInflater;
		private int[] mLayouts = {R.layout.tutorial_screen1, R.layout.tutorial_screen2, R.layout.tutorial_screen3, R.layout.tutorial_screen4};

		public SwipeAdapter(Context context) {
			mInflater = LayoutInflater.from(context);
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			ViewGroup pageView = (ViewGroup) mInflater.inflate(mLayouts[position],
					container, false);
			container.addView(pageView);
			getItemPosition(pageView);
			return pageView; 
		}

		@Override
		public int getCount() {
			return mLayouts.length;
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
	}
}