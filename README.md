# WhereSafe

This was a project designed and implemented for the course COEN 390 (Computer Engineering Product Design Project). This android application is designed to visualize environmental data from an ESP32 set up with a BME680 sensor.

## Features
- Real-time display of air quality, temperature, pressure, humidity, and altitude data
- Historical data visualization with chart
- User-friendly interface for easy navigation
- Customizable settings for theme and language

## Requirements
- Android device running Android 8.0 (Oreo) or higher
- ESP32 microcontroller
- BME680 gas sensor


## Installation
1. Clone the repository
2. Set up ESP32 with code located in /hardware_code/bme680_ble_server
2.1 install the [BSEC-Arduino-library](https://github.com/boschsensortec/BSEC-Arduino-library)
2.2 Upload bme680_ble_server.ino onto an ESP32
3. Build the project and install the APK onto an Android device

## Credits
- [BSEC-Arduino-library](https://github.com/boschsensortec/BSEC-Arduino-library)
- [MPAndroidChart](https://github.com/PhilJay/MPAndroidChart)
- [Android Simple Gauge Library](https://github.com/Gruzer/simple-gauge-android)