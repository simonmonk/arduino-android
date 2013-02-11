#include <Max3421e.h>
#include <Usb.h>
#include <AndroidAccessory.h>

AndroidAccessory acc("Simon Monk",
		     "OpenAccessoryTest",
		     "DemoKit Arduino Board",
		     "1.0",
		     "http://www.duinodroid.com",
		     "0000000012345678");

void setup()
{
  acc.powerOn();
}

void loop()
{
  byte msg[1];
  if (acc.isConnected()) 
  {
    int len = acc.read(msg, sizeof(msg), 1);
    if (len >= 1)
    {
      byte value = msg[0];
      sendMessage(value + 1);
    }
  }
}

void sendMessage(int value)
{
  if (acc.isConnected()) 
  {
    byte msg[2];
    msg[0] = value >> 8;
    msg[1] = value & 0xff;
    acc.write(msg, 2);
  }
}

