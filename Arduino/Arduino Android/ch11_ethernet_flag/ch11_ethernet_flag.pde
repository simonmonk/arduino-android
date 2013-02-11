#include <Servo.h>
#include <SPI.h>
#include <Ethernet.h>

#define REQUEST_BUFFER_SIZE 700
#define MIN_ANGLE 30
#define MAX_ANGLE 175


// pins 10, 11, 12 and 13 used by Ethernet 
#define SERVO_1_PIN 2
#define SERVO_2_PIN 3


// MAC address just has to be unique. This should work
byte mac[] = { 0xDE, 0xAD, 0xBE, 0xEF, 0xFE, 0xED };
// The IP address will be dependent on your local network:
byte ip[] = { 192, 168, 1, 30 };

Server server(80);
Servo servo1;
Servo servo2;
int servo1pos = MIN_ANGLE;
int servo2pos = MIN_ANGLE;



char requestBuffer[REQUEST_BUFFER_SIZE];

void setup()
{
  Ethernet.begin(mac, ip);
  server.begin();
  servo1.attach(SERVO_1_PIN);
  servo2.attach(SERVO_2_PIN);
  Serial.begin(9600);
}

void loop()
{
  // listen for incoming clients
  Client client = server.available();
  if (client) 
  {
    while (client.connected()) 
    {
      readHTTPRequest(client, requestBuffer, REQUEST_BUFFER_SIZE);
      char action = getHTTPParam('a', requestBuffer, REQUEST_BUFFER_SIZE);
      // send a standard http response header
      client.println("HTTP/1.1 200 OK");
      client.println("Content-Type: text/html");
      client.println();
      
      Serial.println("about to write body");
      // send the body
      client.println("<html><body>");
      client.println("<script>");
      client.println("function summon(c)");
      client.println("{");
      client.println("	document.location = 'a=' + c;");
      client.println("}");
      client.println("</script>");
      
      client.println("<h1>Minion Summoner</h1>");
      client.println("<input type='button' value='More Wine' onClick='summon(3)'/>");
      client.println("<input type='button' value='Cancel Wine' onClick='summon(4)'/>");
      client.println("<input type='button' value='Warm Towels' onClick='summon(1)'/>");
      client.println("<input type='button' value='Cancel Towels' onClick='summon(2)'/>");
      client.println("<BR/><img src='http://photos-e.ak.fbcdn.net/hphotos-ak-snc6/185209_255805207771296_255804684438015_998863_49064_a.jpg'/>");
      client.println("</body></html>");
      Serial.print("Param a="); Serial.println(action);

      Serial.println("written body");
      client.stop();
      
      if (action == '1')
      {
        servo1pos = MAX_ANGLE;
      }
      else if (action == '2')
      {
        servo1pos = MIN_ANGLE;
      }
      else if (action == '3')
      {
        servo2pos = MAX_ANGLE;
      }
      else if (action == '4')
      {
        servo2pos = MIN_ANGLE;
      }
      
    }
    delay(1);
  }
  servo1.write(servo1pos);
  servo2.write(servo2pos);
  delay(100);
}

void readHTTPRequest(Client client, char* buffer, int bufferSize)
{
  // read the request until you get a blank line or the buffer is full 
  int i = 0;
  int linePos = 0;
  boolean finished = false;
  while (! finished)
  {
    if (client.available())
    {
      char ch = client.read();
      // 2 because blank line might be \n or \r\n
      if ((ch == '\n' && linePos < 2) || i == bufferSize - 1)
      {
        finished = true;
      }
      else
      {
        buffer[i] = ch;
        linePos++;
        if (ch == '\n')
        {
          linePos = 0;
        }
      }
      i++;
    }
  }
}

char getHTTPParam(char name, char* buffer, int bufferSize)
{
  // search the buffer for [name]=[value]
  // and return [value]. Both are chars
  for (int i = 1; i < bufferSize - 1; i++)
  {
    if (buffer[i] == '=' && buffer[i-1] == name)
    {
      return buffer[i+1];
    }
  }
  return '\0';
}
