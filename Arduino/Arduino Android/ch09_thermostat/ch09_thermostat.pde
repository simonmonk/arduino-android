// Thermostat
// Libraries:
//   OneWire - http://www.pjrc.com/teensy/td_libs_OneWire.html
//   DallasTemperatuire - http://www.milesburton.com/?title=Dallas_Temperature_Control_Library
//   VirtualWire - http://www.open.com.au/mikem/arduino/VirtualWire.pdf

#include <OneWire.h>
#include <DallasTemperature.h>
#include <VirtualWire.h>

#define THERMOSTAT_ID 0x41
#define MIN_ON_TIME 120L // seconds
#define RADIO_CHECK_PERIOD 10000L
#define MIN_TEMP 30
#define MAX_TEMP 100

#define rfRxPin 11
#define tempRxPin 10
#define potPin 0
#define ledPin 8
#define relayPin 7
#define overrideSwitchPin 9

OneWire oneWire(tempRxPin);
DallasTemperature sensors(&oneWire);
DeviceAddress thermometer;

int setTemp = 0;
int actualTemp = 0;
long lastCheckedRadio = 0;

void setup(void) 
{
  pinMode(ledPin, OUTPUT);
  pinMode(relayPin, OUTPUT);
  pinMode(overrideSwitchPin, INPUT);
  digitalWrite(overrideSwitchPin, HIGH); //turn on pullup R
  Serial.begin(9600);
  vw_set_ptt_pin(5); // out of the way
  vw_setup(2000);	 
  vw_rx_start();  
  
  sensors.getAddress(thermometer, 0);
  sensors.begin();
  sensors.setResolution(thermometer, 10);
  Serial.println("Ready");
}

void loop()
{
  if (digitalRead(overrideSwitchPin) == LOW)
  {
    // no override, so recieve temperature from Home Controller via RF
    if (millis() > (lastCheckedRadio + RADIO_CHECK_PERIOD))
    {
      checkForMessage();
      lastCheckedRadio = millis();
    }
  }
  else
  {
    setTemp = readSetTemperature();
  }
  actualTemp = readTemperature();
  setPower();
  delay(500);
}

void checkForMessage()
{
  uint8_t buf[VW_MAX_MESSAGE_LEN];
  uint8_t buflen = VW_MAX_MESSAGE_LEN;
  if (vw_get_message(buf, &buflen)) 
  {
    // there was something to recieve.
    // We only care about the first two bytes
    // first byte identifies the reciever of the message
    // the second is the temperature in F
    byte reciever = buf[0];
    byte payload = buf[1];
    if (reciever == THERMOSTAT_ID) // A
    {
      // the message is for me
      setTemp = payload;
      Serial.print("Radio set temp to: "); Serial.println(setTemp);
      flash(2);
    }
  }
}

int readTemperature()
{
  sensors.requestTemperatures();
  float tempC = sensors.getTempC(thermometer);
  return (int)(DallasTemperature::toFahrenheit(tempC));
}

int readSetTemperature()
{
  int raw = analogRead(potPin);
  int t = map(raw, 0, 1023, MIN_TEMP, MAX_TEMP);
  return t;
}

void setPower()
{
  static boolean lastOnOff = false;
  static long powerLastChanged = 0;
  static int lastSetTemp = 0;
  boolean onOff = (actualTemp < setTemp);
  long t = millis();
  long t2 = powerLastChanged + MIN_ON_TIME * 1000L;
  boolean enoughTimeElapsed = (t > t2);
  boolean tempSettingChanged = (abs(setTemp - lastSetTemp) > 1);
  digitalWrite(ledPin, onOff);
  
  if ((onOff != lastOnOff) && (enoughTimeElapsed || tempSettingChanged))
  {
    Serial.print("set:     "); Serial.println(setTemp);
    Serial.print("temp:    "); Serial.println(actualTemp);
    digitalWrite(relayPin, onOff);
    powerLastChanged = t;
    lastOnOff = onOff;
    lastSetTemp = setTemp;
  }
}

void flash(int n)
{
  for (int i = 0; i <= n; i++)
  {
    digitalWrite(ledPin, HIGH);
    delay(100);
    digitalWrite(ledPin, LOW);
    delay(100);
  }
}
