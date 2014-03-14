package com.gmail.sydym6.mbsweathertest.fragments;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;

import com.gmail.sydym6.mbsweathertest.MBSWeatherTestApplication;
import com.gmail.sydym6.mbsweathertest.R;
import com.gmail.sydym6.mbsweathertest.adapters.CityForecastArrayAdapter;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.OwmApi;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityForecast;
import com.gmail.sydym6.mbsweathertest.tasks.DownloadCityForecastTask;
import com.gmail.sydym6.mbsweathertest.tasks.DownloadCityForecastTask.IDownloadCityForecastResultListener;

public class CityForecastFragment extends ListFragment implements
		IDownloadCityForecastResultListener {

	public final static String CITY_ID = "cityId";
	public final static String NUMBER_OF_DAYS = "numberOfDays";

	private int cityId;
	private int numberOfDays;
	private List<CityForecast> cityForecast;

	private CityForecastArrayAdapter adapter;
	private boolean viewCreated = false;

	private DownloadCityForecastTask downloadCityForecastTask;

	private OwmApi owmApi;

	public static CityForecastFragment newInstance(int cityId, int numberOfDays) {
		CityForecastFragment cityWeatherFragment = new CityForecastFragment();
		Bundle args = new Bundle();
		args.putInt(CITY_ID, cityId);
		args.putInt(NUMBER_OF_DAYS, numberOfDays);
		cityWeatherFragment.setArguments(args);
		return cityWeatherFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (downloadCityForecastTask != null) {
			downloadCityForecastTask.setListener(this);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		cityId = getArguments().getInt(CITY_ID);
		numberOfDays = getArguments().getInt(NUMBER_OF_DAYS);
		MBSWeatherTestApplication application = (MBSWeatherTestApplication) getActivity()
				.getApplication();
		owmApi = application.getOwmApi();
		downloadCityForecast();
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);
		viewCreated = true;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewCreated = false;
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.update_action, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.update:
			downloadCityForecast();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void setListShown(boolean shown) {
		if (viewCreated) {
			super.setListShown(shown);
		}
	}

	private void downloadCityForecast() {
		setListShown(false);
		if (downloadCityForecastTask != null) {
			downloadCityForecastTask.cancel(true);
		}
		downloadCityForecastTask = new DownloadCityForecastTask(owmApi);
		downloadCityForecastTask.setListener(this);
		downloadCityForecastTask.execute(cityId, numberOfDays);
	}

	private void initListAdapter() {
		adapter = new CityForecastArrayAdapter(getActivity(), cityForecast);
		setListAdapter(adapter);
		setListShown(true);
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (downloadCityForecastTask != null) {
			downloadCityForecastTask.cancel(true);
		}
	}

	@Override
	public void onDownloadCityForecastResult(List<CityForecast> cityForecast,
			Exception er) {
		downloadCityForecastTask = null;
		if (er != null) {
			try {
				throw er;
			} catch (IOException e) {
				setListAdapter(new ArrayAdapter<Integer>(getActivity(),
						android.R.layout.simple_list_item_1));

				setEmptyText(getActivity().getString(
						R.string.no_internet_connection));

				setListShown(true);
			} catch (Exception e) {
				setListAdapter(new ArrayAdapter<Integer>(getActivity(),
						android.R.layout.simple_list_item_1));
				setEmptyText(getActivity().getString(R.string.no_data));
				setListShown(true);
			}
		} else if (cityForecast != null) {
			this.cityForecast = cityForecast;
			initListAdapter();
		} else {
			setListAdapter(new ArrayAdapter<Integer>(getActivity(),
					android.R.layout.simple_list_item_1));
			setEmptyText(getActivity().getString(R.string.no_data));
			setListShown(true);
		}
	}
}
