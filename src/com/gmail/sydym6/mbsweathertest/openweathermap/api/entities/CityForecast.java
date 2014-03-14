package com.gmail.sydym6.mbsweathertest.openweathermap.api.entities;

import java.util.Date;

import org.json.JSONException;
import org.json.JSONObject;

public class CityForecast extends CityWeather {

	private final double morningTempreture;
	private final double dayTempreture;
	private final double eveningTempreture;
	private final double nightTempreture;

	public CityForecast(int id, String cityName, String countryName, Date date,
			double tempreture, double minTempreture, double maxTempreture,
			int humidity, int pressure, double windSpeed, double windDegrees,
			int cloudiness, double morningTempreture, double dayTempreture,
			double eveningTempreture, double nightTempreture) {
		super(id, cityName, countryName, date, tempreture, minTempreture,
				maxTempreture, humidity, pressure, windSpeed, windDegrees,
				cloudiness);
		this.morningTempreture = morningTempreture;
		this.dayTempreture = dayTempreture;
		this.eveningTempreture = eveningTempreture;
		this.nightTempreture = nightTempreture;
	}

	public CityForecast(int id, String cityName, String countryName,
			JSONObject o) throws JSONException {
		super();

		this.id = id;
		this.cityName = cityName;
		this.countryName = countryName;

		JSONObject temp = o.getJSONObject("temp");
		maxTempreture = temp.getDouble("max");
		minTempreture = temp.getDouble("min");
		morningTempreture = temp.getDouble("morn");
		dayTempreture = temp.getDouble("day");
		eveningTempreture = temp.getDouble("eve");
		nightTempreture = temp.getDouble("night");

		date = new Date(o.getLong("dt") * 1000l);
		double pressurehPa = o.getDouble("pressure");
		pressure = (int) Math.round(pressurehPa * HPA_TO_MMHG);
		humidity = o.getInt("humidity");
		windSpeed = o.getDouble("speed");
		windDegrees = o.getDouble("deg");
		cloudiness = o.getInt("clouds");
	}

	public double getMorningTempreture() {
		return morningTempreture;
	}

	public double getDayTempreture() {
		return dayTempreture;
	}

	public double getEveningTempreture() {
		return eveningTempreture;
	}

	public double getNightTempreture() {
		return nightTempreture;
	}

	public String getMorningTempretureString() {
		String result;
		if (tempretureFormat != null) {
			result = tempretureFormat.format(morningTempreture);
		} else {
			result = Double.toString(morningTempreture);
		}
		return result + "°C";
	}

	public String getDayTempretureString() {
		String result;
		if (tempretureFormat != null) {
			result = tempretureFormat.format(dayTempreture);
		} else {
			result = Double.toString(dayTempreture);
		}
		return result + "°C";
	}

	public String getEveningTempretureString() {
		String result;
		if (tempretureFormat != null) {
			result = tempretureFormat.format(eveningTempreture);
		} else {
			result = Double.toString(eveningTempreture);
		}
		return result + "°C";
	}

	public String getNightTempretureString() {
		String result;
		if (tempretureFormat != null) {
			result = tempretureFormat.format(nightTempreture);
		} else {
			result = Double.toString(nightTempreture);
		}
		return result + "°C";
	}

}
