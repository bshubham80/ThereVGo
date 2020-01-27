package com.client.therevgo.services.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.concurrent.atomic.AtomicInteger;

public class AppDb {

	private static final int DATABASE_VERSION = 5;
	public static final String DATABASE_NAME = "tvg_db";
	private SQLiteOpenHelper helper;
	private static AppDb instance = null;
	private AtomicInteger mOpenCounter = new AtomicInteger();
	private SQLiteDatabase mDatabase;

	private GroupTable groupTable ;
    private ContactTable contactTable ;

	public synchronized SQLiteDatabase getWritableDatabase(String tableName)
    {
		if (mOpenCounter.incrementAndGet() == 1 && helper != null) {
			mDatabase = helper.getWritableDatabase();
		}
		return mDatabase;
	}

	public synchronized SQLiteDatabase getReadableDatabase(String tableName)
	{
		if (mOpenCounter.incrementAndGet() == 1 && helper != null) {
			mDatabase = helper.getReadableDatabase();
		}
		return mDatabase;
	}

	private AppDb(final Context context) {
		helper = new SQLiteOpenHelper(context, DATABASE_NAME, null, DATABASE_VERSION) {
			@Override
			public void onCreate(SQLiteDatabase db) {
				groupTable.createTable(db);
                contactTable.createTable(db);
			}

			@Override
			public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
                groupTable.onUpgrade(db,oldVersion,newVersion);
                contactTable.onUpgrade(db,oldVersion, newVersion);
				onCreate(db);
			}

		};
		groupTable = new GroupTable(this);
        contactTable = new ContactTable(this);
	}

	public static synchronized AppDb getInstance(Context context) {
		if (instance == null) {
			instance = new AppDb(context);
		}
		return instance;
	}

	public Object getTableObject(String tableName) {

		if(tableName.equalsIgnoreCase(GroupTable.TABLE_NAME)) {
            return groupTable;
        }

        else if(tableName.equalsIgnoreCase(ContactTable.TABLE_NAME)) {
          return contactTable;
        }

        return null;
	}

	public void closeDB() {
		if (mOpenCounter.decrementAndGet() == 0 && helper != null && mDatabase != null) {
			mDatabase.close();
		}
	}
}