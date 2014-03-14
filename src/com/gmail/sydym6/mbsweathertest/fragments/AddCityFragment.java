package com.gmail.sydym6.mbsweathertest.fragments;

import java.io.IOException;
import java.util.List;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.util.LruCache;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.gmail.sydym6.mbsweathertest.MBSWeatherTestApplication;
import com.gmail.sydym6.mbsweathertest.R;
import com.gmail.sydym6.mbsweathertest.activities.CityActivity;
import com.gmail.sydym6.mbsweathertest.adapters.AddCityArrayAdapter;
import com.gmail.sydym6.mbsweathertest.database.CityDatabaseOpenHelper;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.OwmApi;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityWeather;
import com.gmail.sydym6.mbsweathertest.tasks.DownloadCityWeatherByNameTask;
import com.gmail.sydym6.mbsweathertest.tasks.DownloadCityWeatherTask.IDownloadCityWeatherResultListener;

public class AddCityFragment extends ListFragment implements
		IDownloadCityWeatherResultListener<List<CityWeather>> {

	private EditText searchEditText;
	private Button searchButton;
	private ProgressBar downloadProgressBar;
	private TextView statusTextView;

	private DownloadCityWeatherByNameTask task;

	private AddCityArrayAdapter adapter;
	private SQLiteDatabase db;
	private CityDatabaseOpenHelper cityDatabaseOpenHelper;
	private List<CityWeather> cityWeather;
	private Exception taskException;

	private LruCache<Integer, CityWeather> citiesWeatherCache;
	private OwmApi owmApi;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (task != null) {
			task.setListener(this);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
		cityDatabaseOpenHelper = new CityDatabaseOpenHelper(getActivity());
		db = cityDatabaseOpenHelper.getWritableDatabase();
		MBSWeatherTestApplication application = (MBSWeatherTestApplication) getActivity()
				.getApplication();
		citiesWeatherCache = application.getCitiesWeatherCache();
		owmApi = application.getOwmApi();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_add_city, container,
				false);

		searchEditText = (EditText) view.findViewById(R.id.searchEditText);
		searchButton = (Button) view.findViewById(R.id.searchButton);
		downloadProgressBar = (ProgressBar) view
				.findViewById(R.id.downloadProgressBar);
		statusTextView = (TextView) view.findViewById(R.id.statusTextView);

		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				if (searchEditText.getText() != null) {
					String searchText = searchEditText.getText().toString()
							.trim();
					if (searchText != "") {
						statusTextView.setVisibility(View.GONE);
						downloadProgressBar.setVisibility(View.VISIBLE);
						if (task != null) {
							task.cancel(true);
						}
						task = new DownloadCityWeatherByNameTask(owmApi);
						task.setListener(AddCityFragment.this);
						task.execute(searchText);
					}
				}
			}
		});

		onDownloadCityWeatherResult(cityWeather, taskException);

		return view;
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (task != null) {
			task.cancel(true);
		}
		if (cityDatabaseOpenHelper != null) {
			cityDatabaseOpenHelper.close();
		}
	}

	@Override
	public void onDownloadCityWeatherResult(List<CityWeather> cityWeather,
			Exception ex) {
		downloadProgressBar.setVisibility(View.GONE);
		this.cityWeather = cityWeather;
		taskException = ex;
		task = null;
		if (ex != null) {
			try {
				throw ex;
			} catch (IOException e) {
				statusTextView.setText(R.string.no_internet_connection);
				statusTextView.setVisibility(View.VISIBLE);
			} catch (Exception e) {
				statusTextView.setText(R.string.no_data);
				statusTextView.setVisibility(View.VISIBLE);
			}
		} else if (cityWeather != null) {
			if (cityWeather.size() != 0) {
				adapter = new AddCityArrayAdapter(getActivity(), cityWeather);
				setListAdapter(adapter);
			} else {
				statusTextView.setText(R.string.city_not_found);
				statusTextView.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Integer cityId = adapter.getItem(position).getId();
		citiesWeatherCache.put(cityId, adapter.getItem(position));
		ContentValues values = new ContentValues();
		values.put(CityDatabaseOpenHelper.CITY_ID, cityId);
		db.insert(CityDatabaseOpenHelper.CITIES_TABLE, null, values);
		Intent intent = new Intent(getActivity(), CityActivity.class);
		intent.putExtra("com.gmail.sydym6.mbsweathertest.cityid", cityId);
		intent.putExtra("com.gmail.sydym6.mbsweathertest.cityname", adapter
				.getItem(position).getCityName());
		startActivity(intent);
	}
}
