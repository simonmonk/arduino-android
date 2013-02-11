
#define soundPin 18
#define zeroDurationFrom 10000 
#define zeroDurationTo 25000
#define oneDurationFrom 35000
#define oneDurationTo 50000
#define resetTimeout 3000

void setup()
{
  pinMode(soundPin, INPUT);
  Serial.begin(9600);
  Serial.println("Ready");
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
    else
    {
       Serial.print("Error pulseLength="); Serial.println(pulseLength);
    }
  }
  if (bitNo == 16)
  {
    Serial.print("Arduino recieved: ");
    Serial.println(result, 16);
    bitNo = 0; result = 0;
  }
}



