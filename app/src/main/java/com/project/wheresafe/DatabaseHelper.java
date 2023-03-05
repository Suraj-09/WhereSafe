package com.project.wheresafe;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.widget.Toast;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private Context context = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss",Locale.getDefault());

    public DatabaseHelper(@Nullable Context context) {
        super(context, Config.DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_BME = "CREATE TABLE " + Config.BME_TABLE + " ("
                + Config.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + Config.COLUMN_TEMPERATURE + " REAL NOT NULL,"
                + Config.COLUMN_HUMIDITY + " REAL NOT NULL,"
                + Config.COLUMN_PRESSURE + " REAL NOT NULL,"
                + Config.COLUMN_GAS + " REAL NOT NULL,"
                + Config.COLUMN_ALTITUDE + " REAL NOT NULL,"
                + Config.COLUMN_TIMESTAMP + " TEXT NOT NULL"
                + ")";
        System.out.println(CREATE_TABLE_BME);
        sqLiteDatabase.execSQL(CREATE_TABLE_BME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + Config.BME_TABLE + ";");
        onCreate(sqLiteDatabase);
    }

    public long insertBmeData(BmeData bmeData) {
        double temperature = bmeData.getTemperature();
        double humidity = bmeData.getHumidity();
        double pressure = bmeData.getPressure();
        double gas = bmeData.getGas();
        double altitude = bmeData.getAltitude();

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(Config.COLUMN_TEMPERATURE, temperature);
        contentValues.put(Config.COLUMN_HUMIDITY, humidity);
        contentValues.put(Config.COLUMN_PRESSURE, pressure);
        contentValues.put(Config.COLUMN_GAS, gas);
        contentValues.put(Config.COLUMN_ALTITUDE, altitude);

        Date date = new Date();
        contentValues.put(Config.COLUMN_TIMESTAMP, dateFormat.format(date));
        try {
            db.insertOrThrow(Config.BME_TABLE, null, contentValues);
            return 1;
        } catch (Exception e) {
            System.out.println(e.getClass());
            Toast.makeText(this.context, "DB Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("DB error in insertBmeData");
            return 0;
        } finally {
            db.close();
        }
    }

    public BmeData getBmeData() {
        BmeData bmeData = null;
        SQLiteDatabase db = getReadableDatabase();
        String condition = null;
        Cursor cursor = null;
        try {
            cursor = db.query(Config.BME_TABLE, null, null, null, null, null, condition);
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_ID));
                    @SuppressLint("Range") double temperature = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_TEMPERATURE));
                    @SuppressLint("Range") double humidity = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_HUMIDITY));
                    @SuppressLint("Range") double pressure = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_PRESSURE));
                    @SuppressLint("Range") double gas = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_GAS));
                    @SuppressLint("Range") double altitude = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_ALTITUDE));
                    @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(Config.COLUMN_TIMESTAMP));


                    bmeData = new BmeData(id, temperature, humidity, pressure, gas, altitude, timestamp);
                } while (cursor.moveToNext());
            }
        } catch (Exception e) {
            Toast.makeText(this.context, "DB Error: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            System.out.println("DB error in getBmeData");
        } finally {
            db.close();
        }
        return bmeData;
    }

}

