// 7-segemnt delayed start timer

int segmentPins[] = {11, 10, 7, 8, 9, 12, 13};


int switchPin = 2;
int relayPin = 4;

byte digits[11][7] = {
//  a  b  c  d  e  f  g  
  { 1, 1, 1, 1, 1, 1, 0},  // 0
  { 0, 1, 1, 0, 0, 0, 0},  // 1
  { 1, 1, 0, 1, 1, 0, 1},  // 2
  { 1, 1, 1, 1, 0, 0, 1},  // 3
  { 0, 1, 1, 0, 0, 1, 1},  // 4
  { 1, 0, 1, 1, 0, 1, 1},  // 5
  { 1, 0, 1, 1, 1, 1, 1},  // 6
  { 1, 1, 1, 0, 0, 0, 0},  // 7
  { 1, 1, 1, 1, 1, 1, 1},  // 8  
  { 1, 1, 1, 1, 0, 1, 1},  // 9  
  { 0, 0, 0, 0, 0, 0, 0}  // Blank  
};

#define BLANK 10

#define STANDBY 0
#define SETTING_DELAY 1
#define WAITING 2

int state;
long hours;

void setup()
{
  for (int i = 0; i < 7; i++)
  {
    pinMode(segmentPins[i], OUTPUT);
  }
  pinMode(switchPin, INPUT);
  digitalWrite(switchPin, HIGH); // turn on pullup resistor
  pinMode(relayPin, OUTPUT);
  state = STANDBY;
  show(BLANK);
}

void loop()
{
  static long lastButtonPress = 0;
  static long startTime = 0;
  static long endTime = 0;
  boolean buttonPressed = ! digitalRead(switchPin);
  if (state == STANDBY)
  {
    if (buttonPressed)
    {
      lastButtonPress = millis();
      state = SETTING_DELAY;
      hours = 3;
      flash(hours);
    }
  }
  else if (state == SETTING_DELAY)
  {
    if (buttonPressed)
    {
      lastButtonPress = millis();
      hours++;
      if (hours == 10)
      { 
        hours = 1;
      }
      flash(hours);
    }
    else if ((millis() - lastButtonPress) > 3000)
    {
      startTime = millis();
      endTime = startTime + (hours * 60 * 60 * 1000);
      state = WAITING;
    }
  }
  else if (state == WAITING)
  {
    long timeNow = millis();
    hours = (endTime - timeNow) / 1000 / 60 / 60;
    if (buttonPressed)
    {
      state = SETTING_DELAY;
      lastButtonPress = millis();
      flash(hours);
    }
    else if (hours < 0)
    {
      state = STANDBY;
      toggleRelay();
      show(BLANK);
    }
    else
    {
      flash(hours);
    }
  }
}

void show(int n)
{
  for (int i=0; i < 7; i++)
  {
    digitalWrite(segmentPins[i], digits[n][i]);
  } 
}

void flash(int n)
{
  show(BLANK);
  show(n);
  delay(200);
  show(BLANK);
  delay(200);
  show(n);
}

void toggleRelay()
{
  digitalWrite(relayPin, HIGH);
  delay(100);
  digitalWrite(relayPin, LOW);
}

