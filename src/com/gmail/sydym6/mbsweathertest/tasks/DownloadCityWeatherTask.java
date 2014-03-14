package com.gmail.sydym6.mbsweathertest.tasks;

import android.os.AsyncTask;

import com.gmail.sydym6.mbsweathertest.openweathermap.api.OwmApi;

public abstract class DownloadCityWeatherTask<T, K> extends
		AsyncTask<T, Void, K> {

	public interface IDownloadCityWeatherResultListener<K> {

		void onDownloadCityWeatherResult(K cityWeather, Exception e);
	}

	protected IDownloadCityWeatherResultListener<K> listener;
	protected Exception e;
	protected OwmApi owmApi;

	public DownloadCityWeatherTask(OwmApi owmApi) {
		this.owmApi = owmApi;
	}

	public void setListener(IDownloadCityWeatherResultListener<K> listener) {
		this.listener = listener;
	}

	@Override
	protected void onPostExecute(K result) {
		if (listener != null) {
			listener.onDownloadCityWeatherResult(result, e);
		}
	}
}
