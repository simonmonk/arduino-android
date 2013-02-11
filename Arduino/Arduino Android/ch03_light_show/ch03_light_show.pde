#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

#define redPin 5
#define greenPin 6
#define bluePin 3

#define cycleTime 10

int red = 0; 
int green = 85;
int blue = 170;

boolean randomMode = true;


AndroidAccessory acc("Simon Monk",
		     "DroidLightShow",
		     "Light Show Accessory",
		     "1.0",
		     "http://www.duinodroid.com/android",
		     "0000000012345678");

void setup()
{
  Serial.begin(9600);
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(bluePin, OUTPUT);
  acc.powerOn();
}

void loop()
{
  byte msg[3];
  if (acc.isConnected()) 
  {
    int len = acc.read(msg, sizeof(msg), 1);
    if (len > 2 && msg[0] == 1) // set red
    {
      red = msg[2];
    }
    if (len > 2 && msg[0] == 2) // set green
    {
      green = msg[2];
    }
    if (len > 2 && msg[0] == 3) // set blue
    {
      blue = msg[2];
    }
    if (len > 2 && msg[0] == 4) // test mode on
    {
      randomMode = true;
    }
    if (len > 2 && msg[0] == 5) // test mode off
    {
      randomMode = false;
    }
  }
  if (randomMode)
  {
     changeColors();
  }
  showColors();
  delay(cycleTime);
}

void changeColors()
{
   red ++;
   if (red > 255) red = 0;
   green ++;
   if (green > 255) green = 0;
   blue ++;
   if (blue > 255) blue = 0;
}

void showColors()
{
  analogWrite(redPin, red);
  analogWrite(greenPin, green);
  analogWrite(bluePin, blue);   
}
