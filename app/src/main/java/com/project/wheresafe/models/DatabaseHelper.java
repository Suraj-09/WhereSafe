package com.project.wheresafe.models;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.icu.text.SimpleDateFormat;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.project.wheresafe.utils.BmeData;
import com.project.wheresafe.utils.DbConfig;

import java.util.Date;
import java.util.Locale;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private Context context = null;
    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd @ HH:mm:ss",Locale.getDefault());

    public DatabaseHelper(@Nullable Context context) {
        super(context, DbConfig.DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String CREATE_TABLE_BME = "CREATE TABLE " + DbConfig.BME_TABLE + " ("
                + DbConfig.COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + DbConfig.COLUMN_TEMPERATURE + " REAL NOT NULL,"
                + DbConfig.COLUMN_HUMIDITY + " REAL NOT NULL,"
                + DbConfig.COLUMN_PRESSURE + " REAL NOT NULL,"
                + DbConfig.COLUMN_GAS + " REAL NOT NULL,"
                + DbConfig.COLUMN_ALTITUDE + " REAL NOT NULL,"
                + DbConfig.COLUMN_TIMESTAMP + " TEXT NOT NULL"
                + ")";
        System.out.println(CREATE_TABLE_BME);
        sqLiteDatabase.execSQL(CREATE_TABLE_BME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + DbConfig.BME_TABLE + ";");
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
        contentValues.put(DbConfig.COLUMN_TEMPERATURE, temperature);
        contentValues.put(DbConfig.COLUMN_HUMIDITY, humidity);
        contentValues.put(DbConfig.COLUMN_PRESSURE, pressure);
        contentValues.put(DbConfig.COLUMN_GAS, gas);
        contentValues.put(DbConfig.COLUMN_ALTITUDE, altitude);

        Date date = new Date();
        contentValues.put(DbConfig.COLUMN_TIMESTAMP, dateFormat.format(date));
        try {
            db.insertOrThrow(DbConfig.BME_TABLE, null, contentValues);
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
            cursor = db.query(DbConfig.BME_TABLE, null, null, null, null, null, condition);
            if (cursor.moveToFirst()) {
                do {
                    @SuppressLint("Range") int id = cursor.getInt(cursor.getColumnIndex(DbConfig.COLUMN_ID));
                    @SuppressLint("Range") double temperature = cursor.getDouble(cursor.getColumnIndex(DbConfig.COLUMN_TEMPERATURE));
                    @SuppressLint("Range") double humidity = cursor.getDouble(cursor.getColumnIndex(DbConfig.COLUMN_HUMIDITY));
                    @SuppressLint("Range") double pressure = cursor.getDouble(cursor.getColumnIndex(DbConfig.COLUMN_PRESSURE));
                    @SuppressLint("Range") double gas = cursor.getDouble(cursor.getColumnIndex(DbConfig.COLUMN_GAS));
                    @SuppressLint("Range") double altitude = cursor.getDouble(cursor.getColumnIndex(DbConfig.COLUMN_ALTITUDE));
                    @SuppressLint("Range") String timestamp = cursor.getString(cursor.getColumnIndex(DbConfig.COLUMN_TIMESTAMP));

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

