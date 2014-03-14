package com.gmail.sydym6.mbsweathertest.tasks;

import java.util.List;

import com.gmail.sydym6.mbsweathertest.openweathermap.api.OwmApi;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityWeather;

public class DownloadCityWeatherByNameTask extends
		DownloadCityWeatherTask<String, List<CityWeather>> {

	public DownloadCityWeatherByNameTask(OwmApi owmApi) {
		super(owmApi);
	}

	@Override
	protected List<CityWeather> doInBackground(String... ids) {
		List<CityWeather> result = null;
		try {
			result = owmApi.getCityWeatherByName(ids[0]);
		} catch (Exception e) {
			this.e = e;
		}
		return result;
	}
}
