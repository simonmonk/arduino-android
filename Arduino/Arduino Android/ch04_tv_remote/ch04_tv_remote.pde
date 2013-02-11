#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>
#include <EEPROM.h>


#define maxMessageSize 100
#define numSlots 9

int irRxPin = 2;
int irTxPin = 3;
int icPosPin = 5;
int icNegPin = 6;

long lastPing = 0;
long pingDelay = 1000l;
int ledState = LOW;

AndroidAccessory acc("Simon Monk",
		     "DroidTV",
		     "TV Accessory",
		     "1.0",
		     "http://www.duinodroid.com/android",
		     "0000000012345678");


int currentCode = 0;
int buffer[maxMessageSize];


void setup()
{
  pinMode(19, OUTPUT);
  flash(3);
  pinMode(irRxPin, INPUT);
  pinMode(icPosPin, OUTPUT);
  digitalWrite(icPosPin, HIGH);
  pinMode(icNegPin, OUTPUT);
  digitalWrite(icNegPin, LOW);
  pinMode(irTxPin, OUTPUT);
  Serial.begin(9600);
  currentCode = 0;
  acc.powerOn();
  flash(3);
}

void loop()
{
  byte msg[3];
  if (acc.isConnected()) 
  {
    int len = acc.read(msg, sizeof(msg), 1);
    if (len > 0 && msg[0] == 1 && msg[1] == 0) 
    {
      // Message to change channel
        currentCode = msg[2];
        sendIR();
        flash(currentCode + 1);
    }
    else if (len > 0 && msg[0] == 1 && msg[1] == 1)
    {
      // message to put into program mode 
      currentCode = msg[2];
      flash(5);
      int codeLen = readCode();
      storeCode(codeLen);
      flash(5);
      sendMessage('R', currentCode);
    }
  }
//  long timeNow = millis();
//  if (timeNow > lastPing + pingDelay)
//  {
//    ledState = ! ledState;
//    digitalWrite(19, ledState);
//    sendMessage('P', 0);
//    lastPing = timeNow;
//  }
  delay(10);
}


void storeCode(int codeLen)
{
   // write the code to EEPROM, first byte is length
   int startIndex = currentCode * maxMessageSize;
   EEPROM.write(startIndex, (unsigned byte)codeLen);
   for (int i = 0; i < codeLen; i++)
   {
      EEPROM.write(startIndex + i + 1, buffer[i]); 
   }
}

void sendIR()
{
  // construct a buffer from the saved data in EEPROM and send it
  int startIndex = currentCode * maxMessageSize;
  int len = EEPROM.read(startIndex);
  if (len > 0 && len < maxMessageSize)
  {
    for (int i = 0; i < len; i++) 
    {
       buffer[i] = EEPROM.read(startIndex + i + 1);
    }
    sendCode(len);
  }
}

int readCode()
{
  int i = 0;
  unsigned long startTime;
  unsigned long endTime;
  unsigned long lowDuration = 0;
  unsigned long highDuration = 0;
  while(digitalRead(irRxPin) == HIGH) {}; // wait for first pulse
  while(highDuration < 5000l)
  {
    // find low duration
     startTime = micros();
     while(digitalRead(irRxPin) == LOW) {};
     endTime = micros();
     lowDuration = endTime - startTime;
     if (lowDuration < 5000l)
     {
       buffer[i] = (byte)(lowDuration >> 4);
       i ++;
     }
    // find the high duration
     startTime = micros();
     while(digitalRead(irRxPin) == HIGH) {};
     endTime = micros();
     highDuration = endTime - startTime;
     if (highDuration < 5000l)
     {
       buffer[i] = (byte)(highDuration >> 4);
       i ++;
     }
  }
  return i;
}


void sendCode(int n)
{
  for (int i = 0; i < 3; i++)
 {
   writeCode(n);
   delay(90);
 } 
}

void writeCode(int n)
{
   int state = 0;
   unsigned long duration = 0;
   int i = 0;
   while (i < n)
   {
      duration = buffer[i] << 4;
 //Serial.println(duration);
      int cycles = duration / 14;
      if ( ! (i % 2))
      {
        for (int x = 0; x < cycles; x++)
        {
          state = ! state;
          digitalWrite(irTxPin, state);
          delayMicroseconds(10);  // less than 12 to adjust for other instructions
        }
        digitalWrite(irTxPin, LOW);
      }
      else
      {
        digitalWrite(irTxPin, LOW);
        delayMicroseconds(duration);
      }
      i ++;
   }
}


void sendMessage(char flag, int value)
{
  if (acc.isConnected()) 
  {
    byte msg[4];
    msg[0] = 0x04;
    msg[1] = (byte) flag;
    msg[2] = value >> 8;
    msg[3] = value & 0xff;
    acc.write(msg, 4);
  }
}

void flash(int n)
{
  for (int i = 0; i < n; i++)
  {
    digitalWrite(19, HIGH);
    delay(100);
    digitalWrite(19, LOW);
    delay(200);
  }
}

