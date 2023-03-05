package com.project.wheresafe.models;

import static org.junit.Assert.*;


import com.project.wheresafe.utils.BmeData;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.RuntimeEnvironment;

@RunWith(RobolectricTestRunner.class)
public class DatabaseHelperTest {
    DatabaseHelper databaseHelper;
    BmeData bmeData;
    private static double temperature;
    private static double humidity;
    private static double pressure;
    private static double gas;
    private static double altitude;

    @Before
    public void setUp() throws Exception {
        databaseHelper = new DatabaseHelper(RuntimeEnvironment.getApplication());

        temperature = 25.49;
        humidity = 31.43;
        pressure = 1015.30;
        gas = 36.41;
        altitude = 25.24;

        bmeData = new BmeData(temperature, humidity, pressure, gas, altitude);
    }

    @Test
    public void insertBmeData() {
        long success = databaseHelper.insertBmeData(bmeData);
        assertEquals(1, success);
    }

    @Test
    public void getBmeData() {
        databaseHelper.insertBmeData(bmeData);
        BmeData retrievedData = databaseHelper.getBmeData();

        assertNotNull(retrievedData);
        assertEquals(temperature, retrievedData.getTemperature(), 0);
        assertEquals(humidity, retrievedData.getHumidity(), 0);
        assertEquals(pressure, retrievedData.getPressure(), 0);
        assertEquals(gas, retrievedData.getGas(), 0);
        assertEquals(altitude, retrievedData.getAltitude(), 0);
    }
}