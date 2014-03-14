package com.gmail.sydym6.mbsweathertest.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.gmail.sydym6.mbsweathertest.R;
import com.gmail.sydym6.mbsweathertest.openweathermap.api.entities.CityWeather;

public class AddCityArrayAdapter extends ArrayAdapter<CityWeather> {

	private final Context context;
	private final static int LIST_ITEM_LAYOUT = R.layout.city_list_item;

	public AddCityArrayAdapter(Context context, List<CityWeather> citiesWeather) {
		super(context, LIST_ITEM_LAYOUT, citiesWeather);
		this.context = context;
	}

	private static class ViewHolder {
		public TextView cityNameTextView;
		public TextView tempretureTextView;
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

			viewHolder.cityNameTextView = (TextView) listItemView
					.findViewById(R.id.cityNameTextView);
			viewHolder.tempretureTextView = (TextView) listItemView
					.findViewById(R.id.tempretureTextView);

			listItemView.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) listItemView.getTag();
		}

		CityWeather cityWeather = getItem(position);

		viewHolder.cityNameTextView.setText(cityWeather.getFullName());
		viewHolder.tempretureTextView.setText(cityWeather
				.getMinTempretureString());
		return listItemView;
	}

}
