/*********
  Based on previous work by Rui Santos
  Complete project details at http://randomnerdtutorials.com  
*********/

// Load Wi-Fi library
#include <ESP8266WiFi.h>  

// Replace with your network credentials
const char* ssid     = "vodafone8773";
const char* password = "XXXXXXXXXXXXX";

// Other's House
//  const char* ssid = "vodafone8773";
//  const char* password = "XXXXXXXXXXX";

IPAddress ip(192, 168, 1, 18);
IPAddress gateway(192, 168, 1, 1);
IPAddress subnet(255, 255, 255, 0);

// Set web server port number to 80
WiFiServer server(80);

//Hex command to send to serial for close relay
byte rel1ON[]  = {0xA0, 0x01, 0x01, 0xA2};

//Hex command to send to serial for open relay
byte rel1OFF[] = {0xA0, 0x01, 0x00, 0xA1};

//Hex command to send to serial for close relay
byte rel2ON[]  = {0xA0, 0x02, 0x01, 0xA3};

//Hex command to send to serial for open relay
byte rel2OFF[] = {0xA0, 0x02, 0x00, 0xA2};

// Variable to store the HTTP request
String header;

// Auxiliar variables to store the current output state
String rele1Stat = "OFF";
String rele2Stat = "OFF";

void setup() {
  Serial.begin(115200);
  
  // Connect to Wi-Fi network with SSID and password
  Serial.print("Connecting to ");
  Serial.println(ssid);
  WiFi.begin(ssid, password);
  while (WiFi.status() != WL_CONNECTED) {
    delay(500);
    Serial.print(".");
  }

  WiFi.config(ip, gateway, subnet);
  
  // Print local IP address and start web server
  Serial.println("");
  Serial.println("WiFi connected.");
  Serial.println("IP address: ");
  Serial.println(WiFi.localIP());
  server.begin();
}

void loop(){
  WiFiClient client = server.available();   // Listen for incoming clients

  if (client) {                             // If a new client connects,
    Serial.println("New Client.");          // print a message out in the serial port
    String currentLine = "";                // make a String to hold incoming data from the client
    while (client.connected()) {            // loop while the client's connected
      if (client.available()) {             // if there's bytes to read from the client,
        char c = client.read();             // read a byte, then
        Serial.write(c);                    // print it out the serial monitor
        header += c;
        if (c == '\n') {                    // if the byte is a newline character
          // if the current line is blank, you got two newline characters in a row.
          // that's the end of the client HTTP request, so send a response:
          if (currentLine.length() == 0) {
            // HTTP headers always start with a response code (e.g. HTTP/1.1 200 OK)
            // and a content-type so the client knows what's coming, then a blank line:
            client.println("HTTP/1.1 200 OK");
            client.println("Content-type:text/html");
            client.println("Connection: close");
            client.println();
            
            // turns the Relays on and off
            if (header.indexOf("GET /1/on") >= 0) {
              Serial.println("Rele 1 ON");
              rele1Stat = "ON";
              Serial.write (rel1ON, sizeof(rel1ON));
            } else if (header.indexOf("GET /1/off") >= 0) {
              Serial.println("Rele 1 OFF");
              rele1Stat = "OFF";
              Serial.write (rel1OFF, sizeof(rel1OFF));
            } else if (header.indexOf("GET /2/on") >= 0) {
              Serial.println("Rele 2 ON");
              rele2Stat = "ON";
              Serial.write (rel2ON, sizeof(rel2ON));
            } else if (header.indexOf("GET /2/off") >= 0) {
              Serial.println("Rele 2 OFF");
              rele2Stat = "OFF";
              Serial.write (rel2OFF, sizeof(rel2OFF));
            }
            
            // Display the HTML web page
            client.println("<!DOCTYPE html><html>");
            client.println("<head><meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">");
            client.println("<link rel=\"icon\" href=\"data:,\">");
            // CSS to style the on/off buttons 
            // Feel free to change the background-color and font-size attributes to fit your preferences
            client.println("<style>html { font-family: Helvetica; display: inline-block; margin: 0px auto; text-align: center;}");
            client.println(".button { background-color: #195B6A; border: none; color: white; padding: 16px 40px;");
            client.println("text-decoration: none; font-size: 30px; margin: 2px; cursor: pointer;}");
            client.println(".button2 {background-color: #e26b1b;}</style></head>");
            
            // Web Page Heading
            client.println("<body><h1>ESP8266 Web Server</h1>");
            
            // Display current state, and ON/OFF buttons for Rele 1  
            client.println("<p>Rele 1 (Caldera) - State " + rele1Stat + "</p>");
            // If the state is off, it displays the ON button       
            if (rele1Stat=="OFF") {
              client.println("<p><a href=\"/1/on\"><button class=\"button\">Caldera ON</button></a></p>");
            } else {
              client.println("<p><a href=\"/1/off\"><button class=\"button button2\">Caldera OFF</button></a></p>");
            } 
               
            // Display current state, and ON/OFF buttons for Rele 2 
            client.println("<p>Rele 2 (Bomba) - State " + rele2Stat + "</p>");
            // If the state is off, it displays the ON button       
            if (rele2Stat=="OFF") {
              client.println("<p><a href=\"/2/on\"><button class=\"button\">Bomba ON</button></a></p>");
            } else {
              client.println("<p><a href=\"/2/off\"><button class=\"button button2\">Bomba OFF</button></a></p>");
            }
            client.println("</body></html>");
            
            // The HTTP response ends with another blank line
            client.println();
            // Break out of the while loop
            break;
          } else { // if you got a newline, then clear currentLine
            currentLine = "";
          }
        } else if (c != '\r') {  // if you got anything else but a carriage return character,
          currentLine += c;      // add it to the end of the currentLine
        }
      }
    }
    // Clear the header variable
    header = "";
    // Close the connection
    client.stop();
    Serial.println("Client disconnected.");
    Serial.println("");
  }
}
