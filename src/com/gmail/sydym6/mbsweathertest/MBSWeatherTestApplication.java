package com.gmail.sydym6.mbsweathertest;

import android.app.Application;
import android.support.v4.util.LruCache;

import com.gmail.sydym6.mbsweathertest.openweathermap.api.OwmApi;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityWeather;

public class MBSWeatherTestApplication extends Application {

	public final static long WEATHER_TIME_DIFFERENCE_MILLISECONDS = 10 * 60 * 1000;
	private final static int CITY_WEATHER_CACHE_SIZE = 50;
	private final static String OWM_API_ID = "47fa5c472d586f9ee770f33f6c92b3f5";

	private LruCache<Integer, CityWeather> citiesWeatherCache;
	private OwmApi owmApi;

	@Override
	public void onCreate() {
		CityWeather.setWindDirections(getResources().getStringArray(
				R.array.wind_directions));
		citiesWeatherCache = new LruCache<Integer, CityWeather>(
				CITY_WEATHER_CACHE_SIZE);
		owmApi = new OwmApi(OWM_API_ID);
	}

	public LruCache<Integer, CityWeather> getCitiesWeatherCache() {
		return citiesWeatherCache;
	}

	public OwmApi getOwmApi() {
		return owmApi;
	}

}
