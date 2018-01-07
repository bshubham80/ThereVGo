package com.client.therevgo.database;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.Vector;

/**
 * Created by shubham on 7/11/16.
 */

public class ContactTable {
    public static final String TABLE_NAME = "contactTable";
    public static final String COL_ID = "_id";
    public static final String COL_GROUP_ID = "group_id";
    public static final String COL_NAME = "name";
    public static final String COL_NUMBER = "number";

    final Object lock = new Object();
    private AppDb appDb;

    /**
     * Constructor for initialize the instance of Appdb class
     * @param appDb
     */
    public ContactTable(AppDb appDb) {
        this.appDb = appDb;
    }

    public void createTable(SQLiteDatabase db) {
        String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + " ("
                + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COL_GROUP_ID + " text,"
                + COL_NAME + " text,"
                + COL_NUMBER + " text)" ;

        db.execSQL(CREATE_TABLE);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    /**
     * This method is used to create a new group
     * @param bean instance of Contact Model class
     * @return 1 if method successful execute or 0 if operation fails
     */
    public int createContact(ContactBean bean, int groupID) {
        SQLiteDatabase db = null;
        long returnValue = 0;
        int dateTime_Millis = 1;
        synchronized (lock) {
            db = appDb.getWritableDatabase(TABLE_NAME);

            ContentValues values = new ContentValues();
            values.clear();

            values.put(COL_GROUP_ID,groupID);

            if (bean.getName() != null && !bean.getName().equalsIgnoreCase("null") && bean.getName().trim().length() > 0) {
                values.put(COL_NAME, bean.getName());
            }

            if (bean.getNumber() != null && !bean.getNumber().equalsIgnoreCase("null") && bean.getNumber().trim().length() > 0) {
                values.put(COL_NUMBER, bean.getNumber());
            }

            returnValue = db.insert(TABLE_NAME, null, values);
            appDb.closeDB();
        }

        if (returnValue > 0l) {
            return dateTime_Millis;
        } else {
            return 0;
        }
    }

    /**
     * This method will return a list of groups stores in database
     */
    public Vector<ContactBean> getAllData() {
        Vector<ContactBean> list = new Vector<ContactBean>();
        synchronized (lock) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = appDb.getWritableDatabase(TABLE_NAME);
                String[] columns = new String[]{COL_ID, COL_NAME, COL_NUMBER};
                cursor = db.query(false, TABLE_NAME, columns, null, null, null, null, null, null);
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
     * This method will return a list of contact acc to groupID
     */
    public Vector<ContactBean> getAllDataByID(String groupID) {
        Vector<ContactBean> list = new Vector<ContactBean>();
        synchronized (lock) {
            SQLiteDatabase db = null;
            Cursor cursor = null;
            try {
                db = appDb.getWritableDatabase(TABLE_NAME);
                String[] columns = new String[]{COL_ID, COL_GROUP_ID, COL_NAME, COL_NUMBER};

                String selection =  COL_GROUP_ID+"=?" ;

                String[] args = new String[]{ groupID };
                cursor = db.query(false, TABLE_NAME, columns, selection, args, null, null, null, null);
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
     * @param cursor is reference of databse that have data
     * @return  or null if operation fails
     */
    private ContactBean getData(Cursor cursor) {
        int id = cursor.getInt(cursor.getColumnIndex(COL_ID));
        String name = cursor.getString(cursor.getColumnIndex(COL_NAME));
        String number = cursor.getString(cursor.getColumnIndex(COL_NUMBER));

        return new ContactBean(id, name, number);
    }

    /**
     * Method to delete a group and related contact from database
     * @param contactID to delete a particular contact
     * @return no. of records which has been deleted or
     *         -1 if operation will fails
     */
    public int deleteContact(String contactID) {
        SQLiteDatabase db = null;
        int rowsAffected = -1;
        synchronized (lock) {
            try {
                db = appDb.getWritableDatabase(TABLE_NAME);
                String selection = COL_ID + "=? ";
                String[] selectionArgs = new String[] { contactID };
                rowsAffected = db.delete(TABLE_NAME, selection, selectionArgs);
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                appDb.closeDB();
            }
        }
        return rowsAffected;
    }

    /**
     * Method to delete a group and related contact from database
     * @param groupID to delete a particular group
     * @return no. of records which has been deleted or
     *         -1 if operation will fails
     */
    public int deleteContactsByGroup(String groupID) {
        SQLiteDatabase db = null;
        int rowsAffected = -1;
        synchronized (lock) {
            try {
                db = appDb.getWritableDatabase(TABLE_NAME);
                String selection = COL_GROUP_ID + "=? ";
                String[] selectionArgs = new String[] { groupID };
                rowsAffected = db.delete(TABLE_NAME, selection, selectionArgs);
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
        SQLiteDatabase db = null;
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
