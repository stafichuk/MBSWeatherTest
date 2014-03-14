package com.gmail.sydym6.mbsweathertest.tasks;

import java.util.List;

import android.os.AsyncTask;

import com.gmail.sydym6.mbsweathertest.openweathermap.api.OwmApi;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityForecast;

public class DownloadCityForecastTask extends
		AsyncTask<Integer, Void, List<CityForecast>> {

	public interface IDownloadCityForecastResultListener {

		void onDownloadCityForecastResult(List<CityForecast> cityForecast,
				Exception e);
	}

	private Exception e;
	private IDownloadCityForecastResultListener listener;
	private final OwmApi owmApi;

	public DownloadCityForecastTask(OwmApi owmApi) {
		this.owmApi = owmApi;
	}

	public void setListener(IDownloadCityForecastResultListener listener) {
		this.listener = listener;
	}

	@Override
	protected List<CityForecast> doInBackground(Integer... ids) {
		List<CityForecast> result = null;
		try {
			result = owmApi.getCityForecastById(ids[0], ids[1]);
		} catch (Exception e) {
			e.printStackTrace();
			this.e = e;
		}
		return result;
	}

	@Override
	protected void onPostExecute(List<CityForecast> result) {
		listener.onDownloadCityForecastResult(result, e);
	}
}
