#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define trigPin 3
#define echoPin 2
#define supplyPin 6
#define laserPin 5

long lastTimerTime = 0;
long timerPeriod = 500l;
long lastDistance = 0;

AndroidAccessory acc("Simon Monk",
		     "DroidRangeFinder",
		     "Range Finder Accessory",
		     "1.0",
		     "http://www.duinodroid.com/android",
		     "0000000012345678");

void setup()
{
  Serial.begin(9600);
  pinMode(trigPin, OUTPUT);
  pinMode(echoPin, INPUT);
  pinMode(supplyPin, OUTPUT);
  pinMode(laserPin, OUTPUT);
  digitalWrite(supplyPin, HIGH);
  acc.powerOn();
}

void loop()
{
  byte msg[3];
  if (acc.isConnected()) 
  {
    int len = acc.read(msg, sizeof(msg), 1);
    if (len > 0 && msg[0] == 1) 
    {
      digitalWrite(laserPin, msg[2]);
    }
    long timeNow = millis();
    if (timeNow > (lastTimerTime + timerPeriod))
    {
      lastTimerTime = timeNow;
      sendMessage('R', (int) takeSounding());
    }
  }
  delay(100);
}

void sendMessage(char flag, int cpm)
{
  if (acc.isConnected()) 
  {
    byte msg[4];
    msg[0] = 0x04;
    msg[1] = (byte) flag;
    msg[2] = cpm >> 8;
    msg[3] = cpm & 0xff;
    acc.write(msg, 4);
  }
}

long takeSounding()
{
  digitalWrite(trigPin, LOW); 
  delayMicroseconds(2); 
  digitalWrite(trigPin, HIGH); 
  delayMicroseconds(10); 
  digitalWrite(trigPin, LOW); 
  delayMicroseconds(2); 
  long duration = pulseIn(echoPin, HIGH); 
  long distance = microsecondsToCentimeters(duration);
  if (distance > 500)
  {
    return lastDistance;
  }
  else
  {
    lastDistance = distance;
    return distance;
  }
}

long microsecondsToCentimeters(long microseconds) 
{ 
  return microseconds / 29 / 2; 
}


