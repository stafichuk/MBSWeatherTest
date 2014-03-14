package com.gmail.sydym6.mbsweathertest.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gmail.sydym6.mbsweathertest.R;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityForecast;

public class CityForecastArrayAdapter extends ArrayAdapter<CityForecast> {

	private final Context context;
	private final static int LIST_ITEM_LAYOUT = R.layout.city_forecast_list_item;

	public CityForecastArrayAdapter(Context context,
			List<CityForecast> cityForecasts) {
		super(context, LIST_ITEM_LAYOUT, cityForecasts);
		this.context = context;
	}

	private static class ViewHolder {
		public TextView dateTextView;
		public TextView morningTempretureTextView;
		public TextView dayTempretureTextView;
		public TextView eveningTempretureTextView;
		public TextView nightTempretureTextView;
		public TextView humidityTextView;
		public TextView pressureTextView;
		public TextView windSpeedTextView;
		public TextView windDegreesTextView;
		public TextView cloudinessTextView;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;

		View listItemView = convertView;

		if (listItemView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			listItemView = inflater.inflate(LIST_ITEM_LAYOUT, parent, false);

			viewHolder = new ViewHolder();

			viewHolder.dateTextView = (TextView) listItemView
					.findViewById(R.id.dateTextView);
			viewHolder.morningTempretureTextView = (TextView) listItemView
					.findViewById(R.id.morningTempretureTextView);
			viewHolder.dayTempretureTextView = (TextView) listItemView
					.findViewById(R.id.dayTempretureTextView);
			viewHolder.eveningTempretureTextView = (TextView) listItemView
					.findViewById(R.id.eveningTempretureTextView);
			viewHolder.nightTempretureTextView = (TextView) listItemView
					.findViewById(R.id.nightTempretureTextView);
			viewHolder.humidityTextView = (TextView) listItemView
					.findViewById(R.id.humidityTextView);
			viewHolder.pressureTextView = (TextView) listItemView
					.findViewById(R.id.pressureTextView);
			viewHolder.windSpeedTextView = (TextView) listItemView
					.findViewById(R.id.windSpeedTextView);
			viewHolder.windDegreesTextView = (TextView) listItemView
					.findViewById(R.id.windDegreesTextView);
			viewHolder.cloudinessTextView = (TextView) listItemView
					.findViewById(R.id.cloudinessTextView);

			listItemView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) listItemView.getTag();
		}

		CityForecast cityForecast = getItem(position);

		viewHolder.dateTextView.setText(cityForecast.getDateString());
		viewHolder.morningTempretureTextView.setText(cityForecast
				.getMorningTempretureString());
		viewHolder.dayTempretureTextView.setText(cityForecast
				.getDayTempretureString());
		viewHolder.eveningTempretureTextView.setText(cityForecast
				.getEveningTempretureString());
		viewHolder.nightTempretureTextView.setText(cityForecast
				.getNightTempretureString());
		viewHolder.humidityTextView.setText(cityForecast.getHumidityString());
		viewHolder.pressureTextView.setText(cityForecast.getPressureString());
		viewHolder.windSpeedTextView.setText(cityForecast.getWindSpeedString());
		viewHolder.windDegreesTextView.setText(cityForecast.getWindDirection());
		viewHolder.cloudinessTextView.setText(cityForecast
				.getCloudinessString());
		return listItemView;
	}

}
