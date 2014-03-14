package com.gmail.sydym6.mbsweathertest.activities;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBar.OnNavigationListener;
import android.support.v7.app.ActionBarActivity;
import android.widget.ArrayAdapter;
import android.widget.SpinnerAdapter;

import com.gmail.sydym6.mbsweathertest.R;
import com.gmail.sydym6.mbsweathertest.fragments.CityForecastFragment;
import com.gmail.sydym6.mbsweathertest.fragments.CityWeatherFragment;

public class CityActivity extends ActionBarActivity {

	private static final String CITY_WEATHER_FRAGMENT_TAG = "cityWeatherFragment";
	private static final String CITY_FORECAST_FRAGMENT_TAG = "cityForecastFragment";

	private static final String CURRENT_FRAGMENT_TAG = "fragmentPosition";

	private int cityId;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_city);

		cityId = getIntent().getIntExtra(
				"com.gmail.sydym6.mbsweathertest.cityid", 0);

		String cityName = getIntent().getStringExtra(
				"com.gmail.sydym6.mbsweathertest.cityname");
		if (cityName != null) {
			setTitle(cityName);
		}

		ActionBar actionBar = getSupportActionBar();
		actionBar.setDisplayHomeAsUpEnabled(true);

		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		SpinnerAdapter spinnerAdapter = ArrayAdapter.createFromResource(this,
				R.array.city_weather_options, R.layout.actiobar_spinner);
		OnNavigationListener onNavigationListener = new OnNavigationListener() {

			@Override
			public boolean onNavigationItemSelected(int itemPosition,
					long itemId) {
				FragmentManager fragmentManager = getSupportFragmentManager();
				if (itemPosition == 0) {
					CityWeatherFragment cityWeatherFragment = (CityWeatherFragment) fragmentManager
							.findFragmentByTag(CITY_WEATHER_FRAGMENT_TAG);
					if (cityWeatherFragment == null) {
						cityWeatherFragment = CityWeatherFragment
								.newInstance(cityId);
						FragmentTransaction fragmentTransaction = fragmentManager
								.beginTransaction();
						fragmentTransaction.replace(android.R.id.content,
								cityWeatherFragment, CITY_WEATHER_FRAGMENT_TAG);
						fragmentTransaction.commit();
					}
				} else {
					int numberOfDays = itemPosition == 1 ? 3 : 7;
					CityForecastFragment cityForecastFragment = (CityForecastFragment) fragmentManager
							.findFragmentByTag(CITY_FORECAST_FRAGMENT_TAG
									+ numberOfDays);
					if (cityForecastFragment == null) {
						cityForecastFragment = CityForecastFragment
								.newInstance(cityId, numberOfDays);
						FragmentTransaction fragmentTransaction = fragmentManager
								.beginTransaction();
						fragmentTransaction.replace(android.R.id.content,
								cityForecastFragment,
								CITY_FORECAST_FRAGMENT_TAG + numberOfDays);
						fragmentTransaction.commit();
					}
				}
				return false;
			}
		};
		actionBar.setListNavigationCallbacks(spinnerAdapter,
				onNavigationListener);
		actionBar
				.setSelectedNavigationItem(savedInstanceState != null ? savedInstanceState
						.getInt(CURRENT_FRAGMENT_TAG) : 0);
	}

	@Override
	protected void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putInt(CURRENT_FRAGMENT_TAG, getSupportActionBar()
				.getSelectedNavigationIndex());
	}

}
