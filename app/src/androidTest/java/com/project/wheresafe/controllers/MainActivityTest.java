package com.project.wheresafe.controllers;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.junit.Assert.*;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothManager;
import android.content.Context;
import android.os.Build;

import androidx.test.espresso.Espresso;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;

import androidx.test.rule.GrantPermissionRule;


import com.project.wheresafe.R;
import com.project.wheresafe.models.BleEspService;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;



public class MainActivityTest {
//    @Rule
//    public ActivityScenarioRule<MainActivity> activityRule;
//
//    @Test
//    public void homeInView() {
//        activityRule = new ActivityScenarioRule<>(MainActivity.class);
//        onView(withId(R.id.fragment_home_id)).check(matches(isDisplayed()));
//    }

    private static final String DEVICE_NAME = "WhereSafe";
    private static final int REQUEST_LOCATION_PERMISSION = 1;
    private static final int REQUEST_BLUETOOTH_SCAN_PERMISSION = 2;
    private static final int REQUEST_BLUETOOTH_CONNECT_PERMISSION = 3;

    @Rule
    public ActivityScenarioRule<MainActivity> activityScenarioRule = new ActivityScenarioRule<>(MainActivity.class);

    @Rule
    public GrantPermissionRule permissionRule;

    @Before
    public void setUp() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            permissionRule = GrantPermissionRule.grant(
                    android.Manifest.permission.ACCESS_FINE_LOCATION,
                    android.Manifest.permission.BLUETOOTH_SCAN,
                    android.Manifest.permission.BLUETOOTH_CONNECT);
        } else {
            permissionRule = GrantPermissionRule.grant(
                    android.Manifest.permission.ACCESS_FINE_LOCATION);
        }

        // Make sure Bluetooth is supported and enabled
        activityScenarioRule.getScenario().onActivity(activity -> {
            Context context = activity.getApplicationContext();
            BluetoothManager bluetoothManager = (BluetoothManager) context.getSystemService(Context.BLUETOOTH_SERVICE);
            BluetoothAdapter bluetoothAdapter = bluetoothManager.getAdapter();

            assertNotNull(bluetoothAdapter);
            assertTrue(bluetoothAdapter.isEnabled());
        });

    }

    @Test
    public void testNavigationDrawer() {
        // Open navigation drawer
        Espresso.onView(ViewMatchers.withContentDescription(R.string.navigation_drawer_open)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.nav_view)).check(matches(isDisplayed()));
    }

    @Test
    public void testNavigationDrawerAdvanced() {
        // Open navigation drawer
        Espresso.onView(ViewMatchers.withContentDescription(R.string.navigation_drawer_open)).perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.navigation_advanced_data)).perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.fragment_advanced_id)).check(matches(isDisplayed()));

    }

    @Test
    public void testNavigationDrawerSettings() {
        // Open navigation drawer
        Espresso.onView(ViewMatchers.withContentDescription(R.string.navigation_drawer_open)).perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.navigation_settings)).perform(ViewActions.click());

        Espresso.onView(ViewMatchers.withId(R.id.fragment_settings_id)).check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavigationView() {
        Espresso.onView(ViewMatchers.withId(R.id.nav_view_bottom)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.nav_view_bottom)).check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavigationPersonal() {
        Espresso.onView(ViewMatchers.withId(R.id.navigation_personal)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.fragment_personal_id)).check(matches(isDisplayed()));
    }

    @Test
    public void testBottomNavigationTeam() {
        Espresso.onView(ViewMatchers.withId(R.id.navigation_team)).perform(ViewActions.click());
        Espresso.onView(ViewMatchers.withId(R.id.fragment_team_id)).check(matches(isDisplayed()));
    }

    @Test
    public void testActionBar() {
        // Check that action bar is displayed
        Espresso.onView(ViewMatchers.withId(R.id.toolbar)).check(matches(isDisplayed()));
    }

//    @Test
//    public void testBleEspService() {
//        // Start BLE ESP service
//        Activity activity = activityScenarioRule.getScenario().getActivity();
////        Activity activity = activityScenarioRule.getScenario().onActivity()
//        BleEspService bleEspService = new BleEspService(activity.getApplicationContext(), activity);
//        bleEspService.run();
//
//        // TODO: Add assertions for the BleEspService
//    }

}