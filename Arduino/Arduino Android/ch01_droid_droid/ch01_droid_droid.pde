#include <MeetAndroid.h>

#define supplyVolts 6
#define motorVolts 5
#define baudRate 9600

MeetAndroid phone;

int left = 255; // midpoint 
int right = 255; 


int pwmLeftPin = 3;  
int pwmRightPin = 11;  
int directionLeftPin = 12;  
int directionRightPin = 13;  


void setup()  
{
  pinMode(pwmLeftPin, OUTPUT);
  pinMode(pwmRightPin, OUTPUT);
  pinMode(directionLeftPin, OUTPUT);
  pinMode(directionRightPin, OUTPUT);
  setMotors();
  
  // use the baud rate your bluetooth module is configured to 
  Serial.begin(baudRate); 
  phone.registerFunction(setLeft, 'l');  
  phone.registerFunction(setRight, 'r');  
}

void loop()
{
  phone.receive();
}

void setLeft(byte ignore, byte count)
{
  int value = phone.getInt();
  left = value;
  setMotors();
}


void setRight(byte ignore, byte count)
{
  int value = phone.getInt();
  right = value;
  setMotors();
}

void setMotors()
{
   int vLeft = abs(left - 255) * motorVolts / supplyVolts;
   int vRight = abs(right - 255) * motorVolts / supplyVolts;
   int dLeft = (left > 255);
   int dRight = (right > 255);
   if (vLeft < 50)
   {
     vLeft = 0; 
   }
   if (vRight < 50)
   {
     vRight = 0; 
   }
   analogWrite(pwmLeftPin, vLeft);
   analogWrite(pwmRightPin, vRight);
   digitalWrite(directionLeftPin, dLeft);
   digitalWrite(directionRightPin, dRight);
}
