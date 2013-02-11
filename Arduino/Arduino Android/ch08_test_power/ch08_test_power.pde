// Power Control test sketch

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

void setup()
{
  for (int i = 0; i < 4; i++)
  {
    pinMode(onPins[i], OUTPUT);
    pinMode(offPins[i], OUTPUT);
  }
  Serial.begin(9600);
  Serial.println("Ready enter one of: A a B b C c D d");
}

void loop()
{
  int channel = 0;
  if (Serial.available())
  {
    char ch = Serial.read();
    if (ch >= 'a' && ch <= 'd')
    {
      channel = ch - 'a';
      pressButton(channel, offPins);
    }
    else if (ch >= 'A' && ch <= 'D')
    {
      channel = ch - 'A';
      pressButton(channel, onPins);
    }
  }
}

void pressButton(int channel, int column[])
{
  digitalWrite(column[channel], HIGH);
  delay(ButtonPressPeriod);
  digitalWrite(column[channel], LOW);
  delay(ButtonPressPeriod);    
}

