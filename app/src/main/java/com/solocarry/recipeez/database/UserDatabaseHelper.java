package com.solocarry.recipeez.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class UserDatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "recipeez.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TABLE_USERS = "users";
    private static final String TABLE_USER_SESSION = "user_session";

    // User table columns
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_EMAIL = "email";
    private static final String COLUMN_PASSWORD = "password";

    public UserDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create users table
        String CREATE_USERS_TABLE = "CREATE TABLE " + TABLE_USERS + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT UNIQUE, " +
                COLUMN_PASSWORD + " TEXT)";
        db.execSQL(CREATE_USERS_TABLE);

        // Create user session table
        String CREATE_SESSION_TABLE = "CREATE TABLE " + TABLE_USER_SESSION + " (" +
                COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                COLUMN_EMAIL + " TEXT, " +
                "login_time DATETIME DEFAULT CURRENT_TIMESTAMP)";
        db.execSQL(CREATE_SESSION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER_SESSION);
        onCreate(db);
    }

    public String getLastLoggedInUserEmail(SQLiteDatabase db) {
        String email = null;
        Cursor cursor = db.query(TABLE_USER_SESSION,
                new String[]{COLUMN_EMAIL},
                null,
                null,
                null,
                null,
                COLUMN_ID + " DESC",
                "1");

        if (cursor != null && cursor.moveToFirst()) {
            email = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_EMAIL));
            cursor.close();
        }
        return email;
    }

    public void clearUserSession(SQLiteDatabase db) {
        db.delete(TABLE_USER_SESSION, null, null);
    }

    public void saveUserSession(SQLiteDatabase db, String email) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_EMAIL, email);
        db.insert(TABLE_USER_SESSION, null, values);
    }

    public boolean isUserLoggedIn(SQLiteDatabase db) {
        Cursor cursor = db.query(TABLE_USER_SESSION,
                new String[]{COLUMN_ID},
                null,
                null,
                null,
                null,
                null);

        boolean hasSession = cursor != null && cursor.getCount() > 0;
        if (cursor != null) {
            cursor.close();
        }
        return hasSession;
    }
}