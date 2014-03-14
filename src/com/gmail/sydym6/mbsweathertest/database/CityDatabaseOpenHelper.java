package com.gmail.sydym6.mbsweathertest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CityDatabaseOpenHelper extends SQLiteOpenHelper {

	private static final int VERSION = 1;
	private static final String NAME = "cities";

	public static final String CITIES_TABLE = "CITIES";
	public static final String CITY_ID = "_id";

	private static final String CREATE = "CREATE TABLE " + CITIES_TABLE + " ("
			+ CITY_ID + " INTEGER PRIMARY KEY);";

	public CityDatabaseOpenHelper(Context context) {
		super(context, NAME, null, VERSION);
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(CREATE);
		ContentValues spbCV = new ContentValues();
		spbCV.put(CITY_ID, 498817);
		db.insert(CITIES_TABLE, null, spbCV);
		ContentValues mscCV = new ContentValues();
		mscCV.put(CITY_ID, 524901);
		db.insert(CITIES_TABLE, null, mscCV);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
	}

}
