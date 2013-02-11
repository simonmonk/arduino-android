#include <EEPROM.h>
#include <VirtualWire.h>
#define CODE_1 0xA0
#define CODE_2 0x45
#define doorOpenTime 5000 // 5 seconds
#define numCodes 16
#define codeSize 14
#define redPin 9
#define greenPin 10
#define doorReleasePin 13
#define addButtonPin 11
#define removeButtonPin 12
#define rfRxPin 4

#define RED 1
#define GREEN 2
#define ORANGE 3

char code[codeSize + 1];

char *EMPTY_CODE = "00000000000000";

void setup()
{
  Serial.begin(9600);
  pinMode(redPin, OUTPUT);
  pinMode(greenPin, OUTPUT);
  pinMode(doorReleasePin, OUTPUT);
  pinMode(addButtonPin, INPUT);
  digitalWrite(addButtonPin, HIGH);
  pinMode(removeButtonPin, INPUT);
  digitalWrite(removeButtonPin, HIGH);
  flash(RED, 2, 200);
  flash(GREEN, 2, 200);
  flash(ORANGE, 2, 200);
  
  vw_setup(2000);	 
  vw_set_ptt_pin(5); // out of the way
  vw_set_tx_pin(6); // out of the way
  vw_set_rx_pin(rfRxPin);
  vw_rx_start();  
}

void loop()
{  
  if (Serial.available() > codeSize)
  {
    readCard();
    checkCode();
  }
  
  if (!digitalRead(removeButtonPin))
  {
    clearAllCodes();
  }
  if (!digitalRead(addButtonPin))
  {
    addCode(code); 
  }
  checkForMessage();
  //delay(100);
}

void readCard()
{
  int i = 0;
  char ch = Serial.read();
  while (ch != '\n' && i <= codeSize) 
  {
    code[i] = ch;
    ch = Serial.read();
    i ++;
  }
  Serial.flush();
}

void checkForMessage()
{
  uint8_t buf[VW_MAX_MESSAGE_LEN];
  uint8_t buflen = VW_MAX_MESSAGE_LEN;
  if (vw_get_message(buf, &buflen)) 
  {
    // there was something to recieve.
//    Serial.print("Got Message "); Serial.println((char*)buf);
    flash(ORANGE, 1, 200);
    if (buf[0] == CODE_1 && buf[1] == CODE_2)
    {
      flash(ORANGE, 2, 200);
      unlockDoor();      
    }
  }
}

void checkCode()
{
 if (isValidCode(code))
 {
     unlockDoor();
 }
 else
 {
   flash(RED, 1, 500);
 }
}

void unlockDoor()
{
 clearLastCode();
 flash(GREEN, 1, 200); 
 digitalWrite(doorReleasePin, HIGH);
 delay(doorOpenTime);
 digitalWrite(doorReleasePin, LOW); 
 Serial.flush();
}

void clearLastCode()
{
   for (int i = 0; i < codeSize; i++)
   {
     code[i] = 'F';
   }
}

void flash(int color, int times, int duration)
{
  int red = color & 0x01;
  int green = color >> 1;
  for (int i = 0; i < times; i++)
  {
    digitalWrite(redPin, red);
    digitalWrite(greenPin, green);
    delay(duration / 2);
    digitalWrite(redPin, LOW);
    digitalWrite(greenPin, LOW);
    delay(duration / 2);    
  }
}

boolean isValidCode(char *code)
{
   return (findCodePosition(code) < numCodes);
}

void addCode(char *code)
{
  if (isValidCode(code))
  {
    // code already stored
    flash(GREEN, 2, 500);
  }
  else 
  {
    int pos = findCodePosition(EMPTY_CODE);
    if (pos != (codeSize + 1))
    {
      writeCode(pos, code);
      flash(GREEN, 4, 500);
    }
    else
    {
      // no room to store code
      flash(RED, 5, 500);
    }
  }
}


int findCodePosition(char *code)
{
  int pos = 0;
  while (pos < numCodes && !codesEqual(code, pos))
  {
    pos ++; 
  }
  return pos;
}

void writeCode(int pos, char *code)
{
   for (int i = 0; i < codeSize; i++)
   {
     EEPROM.write(pos * 16 + i, code[i]);
   }
}


void clearAllCodes()
{
  for (int pos = 0; pos < numCodes; pos++)
  {
    writeCode(pos, EMPTY_CODE);
  }
  flash(RED, 10, 50); 
}



boolean codesEqual(char *code, int pos)
{
   for (int i = 0; i < codeSize; i++)
   {
     char ch = (char)EEPROM.read(pos * 16 + i);
     if (code[i] != ch)
     {
       return false;
     }
   }
   return true;
}
