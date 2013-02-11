

#include <VirtualWire.h>


#define soundPin 18
#define zeroDurationFrom 10000 
#define zeroDurationTo 25000
#define oneDurationFrom 35000
#define oneDurationTo 50000
#define resetTimeout 3000

#define AonPin 16
#define AoffPin 17
#define BonPin 14
#define BoffPin 15
#define ConPin 8
#define CoffPin 9
#define DonPin 11
#define DoffPin 10

#define ButtonPressPeriod 1000

int onPins[] = {AonPin, BonPin, ConPin, DonPin};
int offPins[] = {AoffPin, BoffPin, CoffPin, DoffPin};
int remote = 0;

void setup()
{
  pinMode(soundPin, INPUT);
  for (int i = 0; i < 4; i++)
  {
    pinMode(onPins[i], OUTPUT);
    pinMode(offPins[i], OUTPUT);
  }
  vw_set_ptt_pin(5); // out of the way
  vw_set_rx_pin(4); // out of the way
  vw_set_ptt_inverted(true); 
  vw_setup(2000);
  Serial.begin(9600);
}

unsigned int result;
int bitNo = 0;
long lastPulseTime = 0;

void loop()
{
  long pulseLength = pulseIn(soundPin, HIGH, oneDurationTo * 2);
  long timeSinceLastPulse = millis() - lastPulseTime;
  lastPulseTime = millis();
  if (pulseLength == 0 || timeSinceLastPulse > resetTimeout)
  {
    bitNo = 0; result = 0;
  }
  else 
  {
    if (pulseLength >= zeroDurationFrom && pulseLength <= zeroDurationTo)
    {
      result = result << 1;
      bitNo ++;
    }
    else if (pulseLength >= oneDurationFrom && pulseLength <= 50000)
    {
      result = (result << 1) + 1;
      bitNo ++;
    }
  }
  if (bitNo == 16)
  {
    processWord(result);
    bitNo = 0; result = 0;
  }
}

void processWord(int message)
{
    int device = message >> 8;
    int action = message & 0x00FF;
    Serial.print("Device: "); Serial.print(device);
    Serial.print(" action: "); Serial.println(action);
    if (device > 0 && device <= 4)
    {
      processPower(device, action);
    }
    else
    {
      processRadio(device, action); 
    }
}

void processPower(int device, int action)
{
  if (action)
  {
    pressButton(device, onPins);
  }
  else
  {
    pressButton(device, offPins);
  }
}

void processRadio(int device, int action)
{
    uint8_t msg[2];
    msg[0] = (uint8_t)device;
    msg[1] = (uint8_t)action;
    
    vw_send((uint8_t *)msg, 2);
   delay(400);
}

void pressButton(int channel, int column[])
{
  int pin = column[channel - 1];
  digitalWrite(column[channel-1], HIGH);
  delay(ButtonPressPeriod);
  digitalWrite(column[channel-1], LOW);
  delay(ButtonPressPeriod);
}


