#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

int oscPin = 5;
int op = 45; 
int minPulseSep = 50;
long lastEventTime = 0;
long lastTimerTime = 0;
long timerPeriod = 500l;
long lastLogTime = 0;
long logPeriod = 60000l;
int count = 0;

float smoothingFactor = 0.6;
float instantaneousCPM = 0.0;
float smoothedCPM = 0.0;

AndroidAccessory acc("Simon Monk",
		     "DroidGeiger",
		     "Geiger Counter Accessory",
		     "1.0",
		     "http://www.duinodroid.com/android",
		     "0000000012345678");

void setup()
{
  Serial.begin(9600);
  pinMode(oscPin, OUTPUT);
  analogWrite(oscPin, op);
  acc.powerOn();
  attachInterrupt(1, eventInterrupt, RISING);
}

void loop()
{
  if (acc.isConnected()) 
  {
     // every half, take the instantaneous reading and integrate 
     // it with the average reading then send it
     long timeNow = millis();
     if (timeNow > (lastTimerTime + timerPeriod))
     {
       lastTimerTime = timeNow;
       integrateInstantReadingIntoSmooth();
       sendMessage('R', (int) smoothedCPM);
     }
     // every minute, send the accumulated minute total
     timeNow = millis();
     if (timeNow > (lastLogTime + logPeriod))
     {
        lastLogTime = timeNow;
        sendMessage('L', count);
        count = 0;
     }
  }
  delay(100);
}

void eventInterrupt()
{
  // set instantaneosReading
  calculateInstantCPM();
  count ++;
  sendMessage('E', 0);
}

void calculateInstantCPM()
{
  // instantaneous cpm = 60,000 / dt in mS
  long timeNow = millis();
  long dt = timeNow - lastEventTime;
  if (dt > minPulseSep)
  {
    instantaneousCPM = ((float)logPeriod) / dt;
    lastEventTime = timeNow;
  }
}

void integrateInstantReadingIntoSmooth()
{
  smoothedCPM = smoothedCPM * smoothingFactor + instantaneousCPM * (1 - smoothingFactor); 
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

