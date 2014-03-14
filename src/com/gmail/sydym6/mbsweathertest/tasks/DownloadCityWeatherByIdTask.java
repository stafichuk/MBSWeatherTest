package com.gmail.sydym6.mbsweathertest.tasks;

import com.gmail.sydym6.mbsweathertest.openweathermap.api.OwmApi;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityWeather;

public class DownloadCityWeatherByIdTask extends
		DownloadCityWeatherTask<Integer, CityWeather> {

	public DownloadCityWeatherByIdTask(OwmApi owmApi) {
		super(owmApi);
	}

	@Override
	protected CityWeather doInBackground(Integer... ids) {
		CityWeather result = null;
		try {
			result = owmApi.getCityWeatherById(ids[0]);
		} catch (Exception e) {
			this.e = e;
		}
		return result;
	}

}
