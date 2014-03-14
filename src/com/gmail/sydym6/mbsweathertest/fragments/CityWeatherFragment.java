package com.gmail.sydym6.mbsweathertest.fragments;

import java.io.IOException;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TableLayout;
import android.widget.TextView;

import com.gmail.sydym6.mbsweathertest.MBSWeatherTestApplication;
import com.gmail.sydym6.mbsweathertest.R;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.OwmApi;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityWeather;
import com.gmail.sydym6.mbsweathertest.tasks.DownloadCityWeatherByIdTask;
import com.gmail.sydym6.mbsweathertest.tasks.DownloadCityWeatherTask.IDownloadCityWeatherResultListener;

public class CityWeatherFragment extends Fragment implements
		IDownloadCityWeatherResultListener<CityWeather> {

	private int cityId;
	private CityWeather cityWeather;

	private boolean viewCreated = false;
	private boolean needFillViews = false;
	private TextView tempretureTextView;
	private TextView minTempretureTextView;
	private TextView maxTempretureTextView;
	private TextView humidityTextView;
	private TextView pressureTextView;
	private TextView windSpeedTextView;
	private TextView windDegreesTextView;
	private TextView cloudinessTextView;

	private TableLayout cityWeatherTableLayout;
	private LinearLayout statusLinearLayout;
	private int cityWeatherTableLayoutVisibility = View.GONE;
	private int statusLinearLayoutVisibility = View.GONE;

	private ProgressBar downloadProgressBar;
	private TextView statusTextView;
	private int downloadProgressBarVisibility = View.GONE;
	private int statusTextViewVisibility = View.GONE;
	private int statusTextViewText = R.string.no_data;

	private DownloadCityWeatherByIdTask downloadCityWeatherTask;
	private LruCache<Integer, CityWeather> citiesWeatherCache;

	private OwmApi owmApi;

	public static CityWeatherFragment newInstance(int cityId) {
		CityWeatherFragment cityWeatherFragment = new CityWeatherFragment();
		Bundle args = new Bundle();
		args.putInt("cityId", cityId);
		cityWeatherFragment.setArguments(args);
		return cityWeatherFragment;
	}

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (downloadCityWeatherTask != null) {
			downloadCityWeatherTask.setListener(this);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		setHasOptionsMenu(true);

		cityId = getArguments().getInt("cityId");
		MBSWeatherTestApplication application = (MBSWeatherTestApplication) getActivity()
				.getApplication();
		citiesWeatherCache = application.getCitiesWeatherCache();
		owmApi = application.getOwmApi();

		cityWeather = citiesWeatherCache.get(cityId);
		if (cityWeather == null
				|| cityWeather != null
				&& System.currentTimeMillis() - cityWeather.getDate().getTime() > MBSWeatherTestApplication.WEATHER_TIME_DIFFERENCE_MILLISECONDS) {
			downloadCityWeather();
		} else {
			fillViews();
		}

	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (downloadCityWeatherTask != null) {
			downloadCityWeatherTask.cancel(true);
		}
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.update_action, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.update:
			citiesWeatherCache.remove(cityId);
			downloadCityWeather();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	private void downloadCityWeather() {
		setStatusTextViewVisibility(View.GONE);
		setDownloadProgressBarVisibility(View.VISIBLE);
		cityWeatherTableLayoutVisibility = View.GONE;
		statusLinearLayoutVisibility = View.VISIBLE;
		if (viewCreated) {
			cityWeatherTableLayout
					.setVisibility(cityWeatherTableLayoutVisibility);
			statusLinearLayout.setVisibility(statusLinearLayoutVisibility);
		}
		if (downloadCityWeatherTask != null) {
			downloadCityWeatherTask.cancel(true);
		}
		downloadCityWeatherTask = new DownloadCityWeatherByIdTask(owmApi);
		downloadCityWeatherTask.setListener(this);
		downloadCityWeatherTask.execute(cityId);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		viewCreated = true;

		View view = inflater.inflate(R.layout.fragment_city_weather, container,
				false);

		cityWeatherTableLayout = (TableLayout) view
				.findViewById(R.id.cityWeatherTableLayout);
		statusLinearLayout = (LinearLayout) view
				.findViewById(R.id.statusLinearLayout);
		tempretureTextView = (TextView) view
				.findViewById(R.id.tempretureTextView);
		minTempretureTextView = (TextView) view
				.findViewById(R.id.minTempretureTextView);
		maxTempretureTextView = (TextView) view
				.findViewById(R.id.maxTempretureTextView);
		humidityTextView = (TextView) view.findViewById(R.id.humidityTextView);
		pressureTextView = (TextView) view.findViewById(R.id.pressureTextView);
		windSpeedTextView = (TextView) view
				.findViewById(R.id.windSpeedTextView);
		windDegreesTextView = (TextView) view
				.findViewById(R.id.windDegreesTextView);
		cloudinessTextView = (TextView) view
				.findViewById(R.id.cloudinessTextView);

		downloadProgressBar = (ProgressBar) view
				.findViewById(R.id.downloadProgressBar);
		downloadProgressBar.setVisibility(downloadProgressBarVisibility);
		statusTextView = (TextView) view.findViewById(R.id.statusTextView);
		statusTextView.setVisibility(statusTextViewVisibility);
		statusTextView.setText(statusTextViewText);
		cityWeatherTableLayout.setVisibility(cityWeatherTableLayoutVisibility);
		statusLinearLayout.setVisibility(statusLinearLayoutVisibility);
		if (needFillViews) {
			fillViews();
		}
		return view;
	}

	@Override
	public void onDestroyView() {
		super.onDestroyView();
		viewCreated = false;
	}

	private void fillViews() {
		needFillViews = true;
		statusLinearLayoutVisibility = View.GONE;
		cityWeatherTableLayoutVisibility = View.VISIBLE;
		if (viewCreated) {
			statusLinearLayout.setVisibility(statusLinearLayoutVisibility);
			tempretureTextView.setText(cityWeather.getTempretureString());
			minTempretureTextView.setText(cityWeather.getMinTempretureString());
			maxTempretureTextView.setText(cityWeather.getMaxTempretureString());
			humidityTextView.setText(cityWeather.getHumidityString());
			pressureTextView.setText(cityWeather.getPressureString());
			windSpeedTextView.setText(cityWeather.getWindSpeedString());
			windDegreesTextView.setText(cityWeather.getWindDirection());
			cloudinessTextView.setText(cityWeather.getCloudinessString());
			cityWeatherTableLayout.setVisibility(View.VISIBLE);
		}
	}

	private void setDownloadProgressBarVisibility(int visibility) {
		downloadProgressBarVisibility = visibility;
		if (viewCreated) {
			downloadProgressBar.setVisibility(visibility);
		}
	}

	private void setStatusTextViewVisibility(int visibility) {
		statusTextViewVisibility = visibility;
		if (viewCreated) {
			statusTextView.setVisibility(visibility);
		}
	}

	private void setStatusTextViewText(int text) {
		statusTextViewText = text;
		if (viewCreated) {
			statusTextView.setText(text);
		}
	}

	@Override
	public void onDownloadCityWeatherResult(CityWeather cityWeather,
			Exception er) {
		setDownloadProgressBarVisibility(View.GONE);
		downloadCityWeatherTask = null;
		if (er != null) {
			try {
				throw er;
			} catch (IOException e) {
				statusLinearLayoutVisibility = View.VISIBLE;
				statusLinearLayout.setVisibility(View.VISIBLE);
				setStatusTextViewText(R.string.no_internet_connection);
				setStatusTextViewVisibility(View.VISIBLE);
			} catch (Exception e) {
				statusLinearLayoutVisibility = View.VISIBLE;
				statusLinearLayout.setVisibility(View.VISIBLE);
				setStatusTextViewText(R.string.no_data);
				setStatusTextViewVisibility(View.VISIBLE);
			}
		} else if (cityWeather != null) {
			citiesWeatherCache.put(cityWeather.getId(), cityWeather);
			this.cityWeather = cityWeather;
			fillViews();
		} else if (this.cityWeather != null) {
			fillViews();
		} else {
			statusLinearLayoutVisibility = View.VISIBLE;
			statusLinearLayout.setVisibility(View.VISIBLE);
			setStatusTextViewText(R.string.no_data);
			setStatusTextViewVisibility(View.VISIBLE);
		}
	}

}
