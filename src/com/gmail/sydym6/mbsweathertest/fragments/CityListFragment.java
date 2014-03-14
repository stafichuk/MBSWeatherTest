package com.gmail.sydym6.mbsweathertest.fragments;

import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.util.LruCache;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.gmail.sydym6.mbsweathertest.MBSWeatherTestApplication;
import com.gmail.sydym6.mbsweathertest.R;
import com.gmail.sydym6.mbsweathertest.activities.CityActivity;
import com.gmail.sydym6.mbsweathertest.adapters.CityListCursorAdapter;
import com.gmail.sydym6.mbsweathertest.database.CityDatabaseOpenHelper;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.OwmApi;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityWeather;
import com.gmail.sydym6.mbsweathertest.tasks.DownloadCityWeatherByIdTask;
import com.gmail.sydym6.mbsweathertest.tasks.DownloadCityWeatherTask.IDownloadCityWeatherResultListener;
import com.gmail.sydym6.mbsweathertest.tasks.LoadCityDBCursorTask;
import com.gmail.sydym6.mbsweathertest.tasks.LoadCityDBCursorTask.ILoadCityDBResultListener;

public class CityListFragment extends ListFragment implements
		ILoadCityDBResultListener,
		IDownloadCityWeatherResultListener<CityWeather> {

	private CityDatabaseOpenHelper cityDatabaseOpenHelper;
	private SQLiteDatabase db;
	private LoadCityDBCursorTask loadCityDBCursorTask;
	private Cursor cursor;

	private DownloadCityWeatherByIdTask downloadCityWeatherByIdTask;
	private LruCache<Integer, CityWeather> citiesWeatherCache;

	private CityListCursorAdapter adapter;
	private boolean viewCreated = false;

	private OwmApi owmApi;

	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if (loadCityDBCursorTask != null) {
			loadCityDBCursorTask.setListener(this);
		}
		if (downloadCityWeatherByIdTask != null) {
			downloadCityWeatherByIdTask.setListener(this);
		}
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
		setRetainInstance(true);
		cityDatabaseOpenHelper = new CityDatabaseOpenHelper(getActivity());
		db = cityDatabaseOpenHelper.getReadableDatabase();
		MBSWeatherTestApplication application = (MBSWeatherTestApplication) getActivity()
				.getApplication();
		citiesWeatherCache = application.getCitiesWeatherCache();
		owmApi = application.getOwmApi();
		loadCityDBCursor();
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
	public void onDestroy() {
		super.onDestroy();
		if (cityDatabaseOpenHelper != null) {
			cityDatabaseOpenHelper.close();
		}
		if (loadCityDBCursorTask != null) {
			loadCityDBCursorTask.cancel(true);
		}
		if (downloadCityWeatherByIdTask != null) {
			downloadCityWeatherByIdTask.cancel(true);
		}
	}

	@Override
	public void setListShown(boolean shown) {
		if (viewCreated) {
			super.setListShown(shown);
		}
	}

	private void loadCityDBCursor() {
		setListShown(false);
		if (loadCityDBCursorTask != null) {
			loadCityDBCursorTask.cancel(true);
		}
		loadCityDBCursorTask = new LoadCityDBCursorTask();
		loadCityDBCursorTask.setListener(this);
		loadCityDBCursorTask.execute(db);
	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		inflater.inflate(R.menu.update_action, menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.update:
			citiesWeatherCache.evictAll();
			loadCityDBCursor();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	@Override
	public void onLoadCityDBResult(Cursor cursor) {
		this.cursor = cursor;
		if (cursor.moveToFirst()) {
			if (downloadCityWeatherByIdTask != null) {
				downloadCityWeatherByIdTask.cancel(true);
			}
			downloadCityWeatherByIdTask = new DownloadCityWeatherByIdTask(
					owmApi);
			downloadCityWeatherByIdTask.setListener(this);
			downloadCityWeatherByIdTask.execute(cursor.getInt(cursor
					.getColumnIndex(CityDatabaseOpenHelper.CITY_ID)));

		}
	}

	@Override
	public void onDownloadCityWeatherResult(CityWeather cityWeather, Exception e) {
		if (e != null) {
			try {
				throw e;
			} catch (IOException er) {
				setListAdapter(new ArrayAdapter<Integer>(getActivity(),
						android.R.layout.simple_list_item_1));
				setEmptyText(getString(R.string.no_internet_connection));
				setListShown(true);
			} catch (Exception er) {
				setListAdapter(new ArrayAdapter<Integer>(getActivity(),
						android.R.layout.simple_list_item_1));
				setEmptyText(getString(R.string.no_data));
				setListShown(true);
			}
		} else {
			if (cityWeather != null) {
				citiesWeatherCache.put(cityWeather.getId(), cityWeather);
			}
			adapter = new CityListCursorAdapter(getActivity(), cursor,
					citiesWeatherCache, owmApi);
			setListAdapter(adapter);
			setListShown(true);
		}
	}

	@Override
	public void onListItemClick(ListView l, View v, int position, long id) {
		Cursor cursor = (Cursor) adapter.getItem(position);
		Integer cityId = cursor.getInt(cursor
				.getColumnIndex(CityDatabaseOpenHelper.CITY_ID));
		Intent intent = new Intent(getActivity(), CityActivity.class);
		intent.putExtra("com.gmail.sydym6.mbsweathertest.cityid", cityId);
		CityWeather cityWeather = citiesWeatherCache.get(cityId);
		if (cityWeather != null) {
			intent.putExtra("com.gmail.sydym6.mbsweathertest.cityname",
					cityWeather.getCityName());
		}
		startActivity(intent);
	}

}
