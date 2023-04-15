#include <Wire.h>
#include "bsec.h"
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>
#include <cmath>

//BLE server name
#define bleServerName "WhereSafe"

// Define the standard sea-level pressure in hPa
#define SEALEVELPRESSURE_HPA (1013.25)

// Define the service and characteristic UUIDs
#define SERV_ENV 0x181A   // Environmental Sensing Service
#define CHARACTERISTIC_UUID "605ddbf0-0540-4c6e-be65-62626797ffe9"

// Create the BLE server
BLEServer* pServer = NULL;
BLECharacteristic* pCharacteristic = NULL;
bool deviceConnected = false;
bool oldDeviceConnected = false;
uint32_t value = 0;

class MyServerCallbacks: public BLEServerCallbacks {
    void onConnect(BLEServer* pServer) {
      deviceConnected = true;
    };

    void onDisconnect(BLEServer* pServer) {
      deviceConnected = false;
    }
};

// Initialize the BME680 sensor 
// Helper functions declarations
void checkIaqSensorStatus(void);
void errLeds(void);
 
// Create an object of the class Bsec
Bsec iaqSensor;
 
String output;
 
void setup() {
  Serial.begin(115200);
  Wire.begin();

  iaqSensor.begin(BME68X_I2C_ADDR_HIGH, Wire);
  output = "\nBSEC library version " + String(iaqSensor.version.major) + "." + String(iaqSensor.version.minor) + "." + String(iaqSensor.version.major_bugfix) + "." + String(iaqSensor.version.minor_bugfix);
  Serial.println(output);
  checkIaqSensorStatus();

  bsec_virtual_sensor_t sensorList[10] = {
    BSEC_OUTPUT_RAW_TEMPERATURE,
    BSEC_OUTPUT_RAW_PRESSURE,
    BSEC_OUTPUT_RAW_HUMIDITY,
    BSEC_OUTPUT_RAW_GAS,
    BSEC_OUTPUT_IAQ,
    BSEC_OUTPUT_STATIC_IAQ,
    BSEC_OUTPUT_CO2_EQUIVALENT,
    BSEC_OUTPUT_BREATH_VOC_EQUIVALENT,
    BSEC_OUTPUT_SENSOR_HEAT_COMPENSATED_TEMPERATURE,
    BSEC_OUTPUT_SENSOR_HEAT_COMPENSATED_HUMIDITY,
  };
 
  iaqSensor.updateSubscription(sensorList, 10, BSEC_SAMPLE_RATE_LP);
  checkIaqSensorStatus();

  Serial.println("BME680 sensor initialized");
 
  // Create the BLE server
  BLEDevice::init(bleServerName);
  pServer = BLEDevice::createServer();
  pServer->setCallbacks(new MyServerCallbacks());

  // Create the BLE service and characteristic
  BLEService *pService = pServer->createService(BLEUUID((uint16_t)SERV_ENV));
  pCharacteristic = pService->createCharacteristic(CHARACTERISTIC_UUID,BLECharacteristic::PROPERTY_READ | BLECharacteristic::PROPERTY_NOTIFY);

  // Add a descriptor to the characteristic
  pCharacteristic->addDescriptor(new BLE2902());

  // Start the service
  pService->start();

  // Advertise the service
  BLEAdvertising *pAdvertising = BLEDevice::getAdvertising();
  pAdvertising->addServiceUUID(BLEUUID((uint16_t)SERV_ENV));
  pAdvertising->setScanResponse(true);
  pAdvertising->setMinPreferred(0x06);
  pAdvertising->setMinPreferred(0x12);
  BLEDevice::startAdvertising();

  Serial.println("BLE device started");

  // Wait for a connection
  Serial.println("Waiting a client connection to notify...");
  while (!deviceConnected) {
    delay(1000);
  }

  // Initialize the oldDeviceConnected variable
  oldDeviceConnected = deviceConnected;
}
void loop() {
  // Check if a device is connected
  if (deviceConnected) {
    // Read the sensor data
    if (iaqSensor.run()) {
      float temperature = iaqSensor.temperature;
      float humidity = iaqSensor.humidity;
      float pressure = iaqSensor.pressure / 100.0;
      float gas = iaqSensor.iaq;
      float altitude = altitudeFromPressure(iaqSensor.pressure);


      uint16_t uTime = millis();
      uint16_t uTemperature = temperature * 100;
      uint16_t uHumidity = humidity * 100;
      uint16_t uPressure = pressure * 10;
      uint16_t uGas = gas * 100;
      uint16_t uAltitude = altitude * 100;
  
      // Convert the sensor data to a string
      String sensorData = String(uTime) + "|" + String(uTemperature) + "|" + String(uHumidity) + "|" + String(uPressure) + "|" + String(uGas) + "|"+ String(uAltitude);
      Serial.println(sensorData);
      
      // Send the sensor data over Bluetooth
      pCharacteristic->setValue(sensorData.c_str());
      pCharacteristic->notify();
        
      // Wait for some time before reading the sensor data again
      delay(15000);
    } else {
      checkIaqSensorStatus();
    }

  }

  // Disconnect the device if it was connected but now isn't
  if (!deviceConnected && oldDeviceConnected) {
    delay(1000); // Delay a bit to avoid a rapid connect/disconnect cycle
    pServer->startAdvertising(); // Restart advertising
    Serial.println("Device disconnected, advertising restarted");
    oldDeviceConnected = deviceConnected;
  }

  // Attempt to connect to a device if one isn't connected
  if (!deviceConnected && !oldDeviceConnected) {
    delay(1000); // Delay a bit to avoid a rapid connect/disconnect cycle
    if(pServer->getConnectedCount() == 0){ // Check if there are no connected devices
      pServer->startAdvertising(); // Restart advertising
      Serial.println("No device connected, advertising restarted");
    }
    oldDeviceConnected = deviceConnected;
  }
}

// Helper function definitions
void checkIaqSensorStatus(void)
{
  if (iaqSensor.bsecStatus != BSEC_OK) {
    if (iaqSensor.bme68xStatus == 2) {
      delay(3000); // wait until there is data in registers
    }

    
    if (iaqSensor.bsecStatus < BSEC_OK) {
      output = "BSEC error code : " + String(iaqSensor.bme68xStatus);
      Serial.println(output);
      for (;;)
        errLeds(); /* Halt in case of failure */
    } else {
      output = "BSEC warning code : " + String(iaqSensor.bme68xStatus);
      Serial.println(output);
    }
  }
 
  if (iaqSensor.bme68xStatus != BME68X_OK) {
    if (iaqSensor.bme68xStatus < BME68X_OK) {
      output = "BME680 error code : " + String(iaqSensor.bme68xStatus);
      Serial.println(output);
      for (;;)
        errLeds(); /* Halt in case of failure */
    } else {
      output = "BME680 warning code : " + String(iaqSensor.bme68xStatus);
      Serial.println(output);
    }
  }
}
 
void errLeds(void)
{
  pinMode(LED_BUILTIN, OUTPUT);
  digitalWrite(LED_BUILTIN, HIGH);
  delay(200);
  digitalWrite(LED_BUILTIN, LOW);
  delay(200);
}

float altitudeFromPressure(float pressure) {
  return 44330 * (1 - pow((pressure / 101325), (1/5.255)));

}
