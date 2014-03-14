package com.gmail.sydym6.mbsweathertest.adapters;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.util.LruCache;
import android.support.v4.widget.CursorAdapter;
import android.util.SparseArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.gmail.sydym6.mbsweathertest.R;
import com.gmail.sydym6.mbsweathertest.database.CityDatabaseOpenHelper;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.OwmApi;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityWeather;
import com.gmail.sydym6.mbsweathertest.tasks.DownloadCityWeatherByIdTask;

public class CityListCursorAdapter extends CursorAdapter {

	private final static int LIST_ITEM_LAYOUT = R.layout.city_list_item;

	private final LruCache<Integer, CityWeather> citiesWeatherCache;
	private final SparseArray<AdapterDownloadCityWeatherTask> tasks;
	private final OwmApi owmApi;

	public CityListCursorAdapter(Context context, Cursor cursor,
			LruCache<Integer, CityWeather> citiesWeatherCache, OwmApi owmApi) {
		super(context, cursor, FLAG_REGISTER_CONTENT_OBSERVER);
		this.citiesWeatherCache = citiesWeatherCache;
		tasks = new SparseArray<AdapterDownloadCityWeatherTask>();
		this.owmApi = owmApi;
	}

	private static class ViewHolder {
		public TextView cityNameTextView;
		public TextView tempretureTextView;
	}

	@Override
	public void bindView(View view, Context context, Cursor cursor) {
		int cityId = cursor.getInt(cursor
				.getColumnIndex(CityDatabaseOpenHelper.CITY_ID));
		fillView(cityId, view);
	}

	@Override
	public View newView(Context context, Cursor cursor, ViewGroup parent) {
		View view = LayoutInflater.from(context).inflate(LIST_ITEM_LAYOUT,
				parent, false);
		ViewHolder viewHolder = new ViewHolder();
		viewHolder.cityNameTextView = (TextView) view
				.findViewById(R.id.cityNameTextView);
		viewHolder.tempretureTextView = (TextView) view
				.findViewById(R.id.tempretureTextView);
		view.setTag(viewHolder);
		return view;
	}

	private void fillView(int cityId, View view) {
		ViewHolder viewHolder = (ViewHolder) view.getTag();
		// viewHolder.downloadProgressBar.setVisibility(View.GONE);
		CityWeather cityWeather = citiesWeatherCache.get(cityId);
		if (cityWeather == null) {
			if (tasks.get(cityId) == null) {
				AdapterDownloadCityWeatherTask task = new AdapterDownloadCityWeatherTask(
						owmApi, cityId, cityWeather);
				tasks.put(cityId, task);
				task.execute(cityId);
			}
		} else {
			viewHolder.cityNameTextView.setText(cityWeather.getFullName());
			viewHolder.tempretureTextView.setText(cityWeather
					.getTempretureString());
		}
	}

	private class AdapterDownloadCityWeatherTask extends
			DownloadCityWeatherByIdTask {

		private final CityWeather cityWeather;
		private final int cityId;

		public AdapterDownloadCityWeatherTask(OwmApi owmApi, int cityId,
				CityWeather cityWeather) {
			super(owmApi);
			this.cityId = cityId;
			this.cityWeather = cityWeather;
		}

		@Override
		protected void onPostExecute(CityWeather result) {
			tasks.remove(cityId);
			if (result != null) {
				citiesWeatherCache.put(result.getId(), result);
				notifyDataSetChanged();
			} else if (cityWeather != null) {
				notifyDataSetChanged();
			}
		}
	}

}
