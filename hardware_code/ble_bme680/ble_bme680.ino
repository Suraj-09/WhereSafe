#include <Wire.h>
#include <Adafruit_Sensor.h>
#include <Adafruit_BME680.h>
#include <BLEDevice.h>
#include <BLEServer.h>
#include <BLEUtils.h>
#include <BLE2902.h>


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
Adafruit_BME680 bme;

void setup() {
  Serial.begin(115200);

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

  // Initialize the BME680 sensor
  if (!bme.begin()) {
    Serial.println("Could not find a valid BME680 sensor, check wiring!");
    while (1);
  }

  Serial.println("BME680 sensor initialized");

  // Initialize the oldDeviceConnected variable
  oldDeviceConnected = deviceConnected;
}

void loop() {
  // Check if a device is connected
  if (deviceConnected) {
    // Read the sensor data

    if (!bme.performReading()) {
      Serial.println("Failed to perform reading");
      return;
    }
    float temperature = bme.temperature;
    float humidity = bme.humidity;
    float pressure = bme.pressure / 100.0;
    float gas = bme.gas_resistance / 1000.0;
    float altitude = bme.readAltitude(SEALEVELPRESSURE_HPA);

    uint16_t uTemperature = temperature * 100;
    uint16_t uHumidity = humidity * 100;
    uint16_t uPressure = pressure * 10;
    uint16_t uGas = gas * 100;
    uint16_t uAltitude = altitude * 100;

    // Convert the sensor data to a string
    String sensorData = "1|" + String(uTemperature) + "|" + String(uHumidity) + "|" + String(uPressure);
    Serial.println(sensorData);
    // Send the sensor data over Bluetooth
    pCharacteristic->setValue(sensorData.c_str());
    pCharacteristic->notify();

    delay(100);
    sensorData = "2|" + String(uGas) + "|"+ String(uAltitude);
    Serial.println(sensorData);
    pCharacteristic->setValue(sensorData.c_str());
    pCharacteristic->notify();
    
    // Wait for some time before reading the sensor data again
    delay(2000);
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
