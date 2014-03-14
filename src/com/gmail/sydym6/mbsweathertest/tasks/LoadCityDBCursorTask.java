package com.gmail.sydym6.mbsweathertest.tasks;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

import com.gmail.sydym6.mbsweathertest.database.CityDatabaseOpenHelper;

public class LoadCityDBCursorTask extends
		AsyncTask<SQLiteDatabase, Void, Cursor> {

	public interface ILoadCityDBResultListener {

		void onLoadCityDBResult(Cursor cursor);
	}

	private ILoadCityDBResultListener listener;

	public void setListener(ILoadCityDBResultListener listener) {
		this.listener = listener;
	}

	@Override
	protected Cursor doInBackground(SQLiteDatabase... dbs) {
		return dbs[0].query(CityDatabaseOpenHelper.CITIES_TABLE, null, null,
				null, null, null, null);
	}

	@Override
	protected void onPostExecute(Cursor result) {
		if (listener != null) {
			listener.onLoadCityDBResult(result);
		}
	}
}
