// Temperature Logger
// Libraries:
//   OneWire - http://www.pjrc.com/teensy/td_libs_OneWire.html
//   DallasTemperatuire - http://www.milesburton.com/?title=Dallas_Temperature_Control_Library

#include <OneWire.h>
#include <DallasTemperature.h>
#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define tempRxPin 2
#define posSupplyPin 6
#define samplePeriod 2000l

OneWire oneWire(tempRxPin);
DallasTemperature sensors(&oneWire);
DeviceAddress thermometer;
AndroidAccessory acc("Simon Monk",
		     "DroidTempLogger",
		     "Temperature Logger Accessory",
		     "1.0",
		     "http://www.duinodroid.com/android",
		     "0000000012345678");

long lastSendTime = 0;

void setup() 
{
  pinMode(posSupplyPin, OUTPUT);
  digitalWrite(posSupplyPin, HIGH);
  pinMode(tempRxPin, INPUT);
  Serial.begin(9600);  
  sensors.getAddress(thermometer, 0);
  Serial.print("dev count"); Serial.println(sensors.getDeviceCount(), 10);
  sensors.begin();
  Serial.print("dev count"); Serial.println(sensors.getDeviceCount(), 10);
  sensors.setResolution(thermometer, 10);
  acc.powerOn();
  Serial.println("Ready");
}

void loop()
{
  long timeNow = millis();
  if (timeNow > lastSendTime + samplePeriod)
  {
    lastSendTime = timeNow;
    int t = readTemperature();
    Serial.println(t);
    sendMessage('R', t);
  }
}


int readTemperature()
{
  sensors.requestTemperatures();
  float tempC = sensors.getTempC(thermometer);
  return (int)(tempC * 10.0f);
}

void sendMessage(char flag, int temp)
{
  if (acc.isConnected()) 
  {
    byte msg[4];
    msg[0] = 0x04;
    msg[1] = (byte) flag;
    msg[2] = temp >> 8;
    msg[3] = temp & 0xff;
    acc.write(msg, 4);
  }
}

