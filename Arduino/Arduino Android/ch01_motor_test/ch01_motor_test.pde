
#define supplyVolts 6
#define motorVolts 5

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
  setMotors(0, 0);
}

void loop()
{
  // forward
  setMotors(255, 255);
  delay(1000);
  // stop
  setMotors(0, 0);
  delay(1000);
  // back
  setMotors(-255, -255);
  delay(1000);
  // left
  setMotors(255, -255);
  delay(1000);
  // right
  setMotors(-255, 255);
  delay(1000);
  // stop
  setMotors(0, 0);
  delay(5000);
}

void setMotors(int left, int right)
{
   int vLeft = abs(left) * motorVolts / supplyVolts;
   int vRight = abs(right) * motorVolts / supplyVolts;
   int dLeft = (left > 0);
   int dRight = (right > 0);
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
