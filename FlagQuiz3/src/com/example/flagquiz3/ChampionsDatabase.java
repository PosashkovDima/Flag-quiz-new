package com.example.flagquiz3;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class ChampionsDatabase extends SQLiteOpenHelper implements BaseColumns {

	// private static final String DATABASE_NAME = "championsdatabase.db";
	// private static final int DATABASE_VERSION = 1;
	private static final String TABLE_NAME = "champions";
	public static final String RESULT_COLUMN = "result";
	public static final String DATE_COLUMN = "date";
	public static final String NAME_COLUMN = "name";
	private static final String DATABASE_CREATE_SCRIPT = "create table "
			+ TABLE_NAME + " (" + BaseColumns._ID
			+ " integer primary key autoincrement " + RESULT_COLUMN
			+ " real not null, " + NAME_COLUMN + " text not null, "
			+ DATE_COLUMN + " text not null);";

	public ChampionsDatabase(Context context, String name,
			CursorFactory factory, int version) {
		super(context, name, factory, version);

	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(DATABASE_CREATE_SCRIPT);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

		db.execSQL("DROP TABLE IF IT EXIST " + TABLE_NAME);
		onCreate(db);
	}

}
