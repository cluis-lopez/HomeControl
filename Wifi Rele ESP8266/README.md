# Relay based on ESP8266 board

To control the boiler we need a wifi actuator to manage two relays, one for the boiler itself, another one for a companion water pump.

To do this we need a ESP8266 Dual WiFi Relay Module made by [lctech](www.lctech.cc). Do not follow that link because although they have an english version of their website there're no info at all.

Fortunately, the board is based in the well known ESP8266 SoC that you may program using the Arduino IDE.

First at all you need to communicate with the ESP8266 module to download the needed code. You may do it with a Raspberry Pi (https://www.instructables.com/id/Connect-an-ESP8266-to-your-RaspberryPi/) or using an USB to UART converter that must work at the 3.3V that the ESP8266 uses. We've used one made by DSD TECH using the FTDI FT232RL chip (https://www.amazon.es/dp/B07BBPX8B8) which is widely documented and has drivers for all the usual operating systems.

In any case, this is a great introductory article about programming the SoC (https://medium.com/@aallan/getting-started-with-the-esp8266-270e30feb4d1).

Our solution is as simple as toast the chip with the WebRele Ardiuno sketch provided here. Remember to change the SSID and the Password to match your home network.

Be careful also to use a static IP address for the device. In Spain main telco providers use 192.168.1.255 for home internal networks leaving addresses 192.168.1.2 up to 31 free from the DHCP pool so I've chosen 192.168.1.18 as the address for my device. Adapt it to your local requirements if needed.
