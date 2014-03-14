package com.gmail.sydym6.mbsweathertest.openweathermap.api.entities;

import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

public class CityWeather {

	public static final double HPA_TO_MMHG = 0.750061683;

	public static String windDirections[];

	protected int id;
	protected String cityName;
	protected String countryName;
	protected Date date;
	protected double tempreture;
	protected double minTempreture;
	protected double maxTempreture;
	protected int humidity;
	protected int pressure;
	protected double windSpeed;
	protected double windDegrees;
	protected int cloudiness;

	private final SimpleDateFormat dateSimpleDateFormat;
	protected DecimalFormat tempretureFormat;

	public static void setWindDirections(String[] windStrings) {
		windDirections = windStrings;
	}

	public CityWeather(int id, String cityName, String countryName, Date date,
			double tempreture, double minTempreture, double maxTempreture,
			int humidity, int pressure, double windSpeed, double windDegrees,
			int cloudiness) {
		this();
		this.id = id;
		this.cityName = cityName;
		this.countryName = countryName;
		this.date = date;
		this.tempreture = tempreture;
		this.minTempreture = minTempreture;
		this.maxTempreture = maxTempreture;
		this.humidity = humidity;
		this.pressure = pressure;
		this.windSpeed = windSpeed;
		this.windDegrees = windDegrees;
		this.cloudiness = cloudiness;
	}

	protected CityWeather() {
		dateSimpleDateFormat = new SimpleDateFormat("EEEEEEEEE, d",
				Locale.getDefault());
		NumberFormat numberFormat = NumberFormat.getInstance(Locale
				.getDefault());
		if (numberFormat instanceof DecimalFormat) {
			tempretureFormat = (DecimalFormat) numberFormat;
			tempretureFormat.setRoundingMode(RoundingMode.HALF_UP);
			tempretureFormat.setMaximumFractionDigits(1);
			tempretureFormat.setPositivePrefix("+");
			tempretureFormat.setNegativePrefix("-");
		}
	}

	public CityWeather(JSONObject o) throws JSONException {
		this();

		id = o.getInt("id");
		cityName = o.getString("name");
		date = new Date(o.getLong("dt") * 1000l);

		JSONObject sys = o.optJSONObject("sys");
		if (sys != null) {
			countryName = sys.optString("country");
		}

		JSONObject main = o.getJSONObject("main");
		tempreture = main.getDouble("temp");
		double pressurehPa = main.getDouble("pressure");
		pressure = (int) Math.round(pressurehPa * HPA_TO_MMHG);
		humidity = main.getInt("humidity");
		maxTempreture = main.optDouble("temp_max");
		minTempreture = main.optDouble("temp_min");

		JSONObject wind = o.getJSONObject("wind");
		windSpeed = wind.getDouble("speed");
		windDegrees = wind.getDouble("deg");

		cloudiness = o.getJSONObject("clouds").getInt("all");
	}

	public int getId() {
		return id;
	}

	public String getCityName() {
		return cityName;
	}

	public String getCountryName() {
		return countryName;
	}

	public String getFullName() {
		return cityName + ", " + countryName;
	}

	public Date getDate() {
		return date;
	}

	public double getTempreture() {
		return tempreture;
	}

	public double getMinTempreture() {
		return minTempreture;
	}

	public double getMaxTempreture() {
		return maxTempreture;
	}

	public int getHumidity() {
		return humidity;
	}

	public double getPressure() {
		return pressure;
	}

	public double getWindSpeed() {
		return windSpeed;
	}

	public double getWindDegrees() {
		return windDegrees;
	}

	public double getCloudiness() {
		return cloudiness;
	}

	public String getDateString() {
		return dateSimpleDateFormat.format(date);
	}

	public String getTempretureString() {
		String result;
		if (tempretureFormat != null) {
			result = tempretureFormat.format(tempreture);
		} else {
			result = Double.toString(tempreture);
		}
		return result + "°C";
	}

	public String getMinTempretureString() {
		String result;
		if (tempretureFormat != null) {
			result = tempretureFormat.format(minTempreture);
		} else {
			result = Double.toString(minTempreture);
		}
		return result + "°C";
	}

	public String getMaxTempretureString() {
		String result;
		if (tempretureFormat != null) {
			result = tempretureFormat.format(maxTempreture);
		} else {
			result = Double.toString(maxTempreture);
		}
		return result + "°C";
	}

	public String getHumidityString() {
		return Integer.toString(humidity) + "%";
	}

	public String getPressureString() {
		return Integer.toString(pressure);
	}

	public String getWindSpeedString() {
		return String.format(Locale.getDefault(), "%.1f", windSpeed);
	}

	public String getWindDirection() {
		return windDirections[((int) Math.round(((windDegrees % 360) / 45))) % 8];
	}

	public String getCloudinessString() {
		return Integer.toString(cloudiness) + "%";
	}

	@Override
	public String toString() {
		return "CityWeather [id=" + id + ", cityName=" + cityName
				+ ", countryName=" + countryName + ", date=" + date
				+ ", tempreture=" + tempreture + ", minTempreture="
				+ minTempreture + ", maxTempreture=" + maxTempreture
				+ ", humidity=" + humidity + ", pressure=" + pressure
				+ ", windSpeed=" + windSpeed + ", windDegrees=" + windDegrees
				+ ", cloudiness=" + cloudiness + ", dateSimpleDateFormat="
				+ dateSimpleDateFormat + "]";
	}

}
