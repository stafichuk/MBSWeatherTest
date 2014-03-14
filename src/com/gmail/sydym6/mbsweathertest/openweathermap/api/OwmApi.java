package com.gmail.sydym6.mbsweathertest.openweathermap.api;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityForecast;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityWeather;

public class OwmApi {

	private static final String BASE_URL = "http://api.openweathermap.org/data/2.5/";

	private final String apiId;

	public OwmApi(String apiId) {
		this.apiId = apiId;
	}

	private void checkError(JSONObject root, String url) throws JSONException,
			OwmException {
		int code = root.getInt("cod");
		if (code != 200) {
			throw new OwmException(code, root.getString("message"), url);
		}
	}

	private JSONObject sendRequest(String url) throws IOException,
			MalformedURLException, JSONException, OwmException {
		String response = sendRequestInternal(url);
		JSONObject root = new JSONObject(response);
		checkError(root, url);
		return root;
	}

	private String sendRequestInternal(String url) throws IOException,
			MalformedURLException {
		HttpURLConnection connection = null;
		try {
			connection = (HttpURLConnection) new URL(url).openConnection();
			connection.setConnectTimeout(30000);
			connection.setReadTimeout(30000);
			connection.setUseCaches(false);
			connection.setDoInput(true);
			connection.setRequestMethod("GET");
			connection.connect();

			InputStream is = new BufferedInputStream(
					connection.getInputStream(), 8192);
			String response = convertStreamToString(is);
			return response;
		} finally {
			if (connection != null) {
				connection.disconnect();
			}
		}
	}

	private static String convertStreamToString(InputStream is)
			throws IOException {
		InputStreamReader r = new InputStreamReader(is);
		StringWriter sw = new StringWriter();
		char[] buffer = new char[1024];
		try {
			for (int n; (n = r.read(buffer)) != -1;)
				sw.write(buffer, 0, n);
		} finally {
			is.close();
		}
		return sw.toString();
	}

	public CityWeather getCityWeatherById(int id) throws MalformedURLException,
			IOException, JSONException, OwmException {
		JSONObject root = sendRequest(BASE_URL + "weather?id=" + id
				+ "&units=metric" + "&APPID=" + apiId);
		return new CityWeather(root);
	}

	public List<CityWeather> getCityWeatherByName(String name)
			throws MalformedURLException, IOException, JSONException,
			OwmException {
		JSONObject root = sendRequest(BASE_URL + "find?q="
				+ URLEncoder.encode(name, "utf-8") + "&units=metric"
				+ "&APPID=" + apiId);
		JSONArray list = root.getJSONArray("list");
		List<CityWeather> result = new ArrayList<CityWeather>();
		for (int i = 0; i < list.length(); i++) {
			result.add(new CityWeather(list.getJSONObject(i)));
		}
		return result;
	}

	public List<CityForecast> getCityForecastById(int id, int numberOfDays)
			throws MalformedURLException, IOException, JSONException,
			OwmException {
		JSONObject root = sendRequest(BASE_URL + "forecast/daily?id=" + id
				+ "&units=metric&cnt=" + numberOfDays + "&APPID=" + apiId);
		JSONObject city = root.getJSONObject("city");
		String cityName = city.getString("name");
		String countryName = city.getString("country");
		JSONArray list = root.getJSONArray("list");
		List<CityForecast> result = new ArrayList<CityForecast>();
		for (int i = 0; i < list.length(); i++) {
			result.add(new CityForecast(id, cityName, countryName, list
					.getJSONObject(i)));
		}
		return result;
	}
}
