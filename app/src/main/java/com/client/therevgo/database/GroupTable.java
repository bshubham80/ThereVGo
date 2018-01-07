package com.client.therevgo.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.client.therevgo.utility.PrefManager;

import java.util.Vector;

public class GroupTable {
	
	public  static final  String TABLE_NAME = "groupTable";
	private static final String COL_ID      = "_id";
	private static final String COL_NAME    = "name";
	private static final String COL_USER_ID = "user_id";
	private static final String TAG = GroupTable.class.getSimpleName();
	
    private String user_id;
	private final Object lock = new Object();
	private AppDb appDb;
	
	/**
	 * Constructor for initialize the instance of AppDb class
	 * @param appDb object of {@link AppDb} to start operations
     */
	GroupTable(AppDb appDb) {
		this.appDb = appDb;
	}

	void createTable(SQLiteDatabase db) {
		String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
				                              + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
				                              + COL_NAME + " text,"
											  + COL_USER_ID + " text)";
		
		Log.i(TAG, CREATE_TABLE);
		db.execSQL(CREATE_TABLE);
	}

	void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
	}

	/**
	 * This method is used to create a new group
	 * @param groupName is the name of Group
	 * @return 1 if method successful execute or 0 if operation fails
     */
	public int createGroup(String groupName, Context context) {
		SQLiteDatabase db;
		long returnValue;
		synchronized (lock) {
			db = appDb.getWritableDatabase(TABLE_NAME);

			ContentValues values = new ContentValues();
			values.clear();

			if (groupName != null && !groupName.equalsIgnoreCase("null") && groupName.trim().length() > 0) {
				values.put(COL_NAME, groupName);
			}
			
			user_id = (String) PrefManager.getInstance(context).getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);
			
			values.put(COL_USER_ID, user_id);

			returnValue = db.insert(TABLE_NAME, null, values);
			appDb.closeDB();
		}

		if (returnValue > 0L) {
			
			return (int) returnValue;
		} else {
			return 0;
		}
	}

	/**
	 * This Method is used to update a particular group details
	 * @param obj hold the value that store in database
     * @return 1 if operation is successful or -1 if fails
     */
	public int updateData(GroupBean obj) {
		int rowsAffected = -1;
		SQLiteDatabase db;
		synchronized (lock) {
			try {
				db = appDb.getWritableDatabase(TABLE_NAME);
				ContentValues values = new ContentValues();
				values.put(COL_ID, obj.getId());
				values.put(COL_NAME, obj.getName());

				String selection = COL_ID + "=? ";
				String[] selectionArgs = new String[] { String.valueOf(obj.getId()) };
				try {
					rowsAffected = db.update(TABLE_NAME, values, selection, selectionArgs);
				} catch (Exception e) {
					e.printStackTrace();
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				appDb.closeDB();
			}
		}
		return rowsAffected;
	}

	/**
	 * This method will return a list of groups stores in database
     */
	public Vector<GroupBean> getAllData(Context context) {
		Vector<GroupBean> list = new Vector<>();
		synchronized (lock) {
			SQLiteDatabase db = null;
			Cursor cursor = null;
			try {
                
                user_id = (String) PrefManager.getInstance(context).getDataFromPreference(PrefManager.Key.USER_ID, PrefManager.Type.TYPE_STRING);
				db = appDb.getWritableDatabase(TABLE_NAME);
				String[] columns = new String[]{
						COL_ID,
						COL_NAME,
                        COL_USER_ID
				};
				String[] selectionArg = new  String[] {
					user_id
				};
				String selection = COL_USER_ID + "=? ";
				cursor = db.query(false, TABLE_NAME, columns, selection, selectionArg, null, null, null, null);
				// cursor = db.rawQuery("SELECT * FROM " + TABLE_NAME + " WHERE " + COL_CUSTOMER_DOCUMENT_STATUS + " !=3 UNION ALL SELECT * FROM " + TABLE_NAME + " WHERE " + COL_CUSTOMER_DOCUMENT_STATUS + " ==3", null);

				if (cursor.moveToFirst()) {
					do {
						list.add(getData(cursor));
					} while (cursor.moveToNext());
				}
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				if (cursor != null)
					cursor.close();
				appDb.closeDB();
			}
		}
		return list;
	}

	/**
	 * Method to get data from database cursor
	 * @param cursor is reference of database that have data
	 * @return  or null if operation fails
     */
	private GroupBean getData(Cursor cursor) {
		int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
		String name = cursor.getString(cursor.getColumnIndex(COL_NAME));

		return new GroupBean(id,name);
	}

	/**
	 * Method to delete a group and related contact from database
	 * @param groupID to delete a particular group and all contact that have this
	 *                groupID as a foreign key
     * @return no. of records which has been deleted or
	 *         -1 if operation will fails
     */
	public int deleteGroup(String groupID) {
		SQLiteDatabase db;
		int rowsAffected = -1;
		synchronized (lock) {
			try {
				db = appDb.getWritableDatabase(TABLE_NAME);
				String selection = COL_ID + "=? ";
				String[] selectionArgs = new String[] { groupID };
				rowsAffected = db.delete(TABLE_NAME, selection, selectionArgs);

				ContactTable  contactTable = (ContactTable) appDb.getTableObject(ContactTable.TABLE_NAME);
				rowsAffected = contactTable.deleteContactsByGroup(groupID);

			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				appDb.closeDB();
			}
		}
		return rowsAffected;
	}

	/***
	 * Method to delete all gorup in database and related contact
	 * @return no. of records which has been deleted or
	 *         -1 if operation will fails
     */
	public int deleteAllGroup() {
		SQLiteDatabase db;
		int rowsAffected = -1;
		synchronized (lock) {
			try {
				db = appDb.getWritableDatabase(TABLE_NAME);
				String selection = "1";
				String[] selectionArgs = new String[] {};
				rowsAffected = db.delete(TABLE_NAME, selection, selectionArgs);
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				appDb.closeDB();
			}
		}
		return rowsAffected;
	}
}