# Relay based on ESP8266 board

To control the boiler we need a Wifi control to activate two relays (one for the boiler itself another for a companion water pump).

To do this we get a ESP8266 Dual WiFi Relay Module made by lctech (www.lctech.cc). Do not follow that link because although they have an english version of their website there're no info at all.

Fortunately, the board is based in the well known ESP8266 SoC that you may program using the Arduino IDE.

First at all you need to communicate with the small ESP8266 module. You may do it with a Raspberry Pi (https://www.instructables.com/id/Connect-an-ESP8266-to-your-RaspberryPi/) or using an USB to UART converter that may work at 3.3V which the ESP8266 uses. We've used one made by DSD TECH using the well known FTDI FT232RL chip (https://www.amazon.es/dp/B07BBPX8B8?ref_=pe_3310721_248816181_302_E_DDE_dt_1).

In any case, this is a fantastic introductory article about programing the SoC (https://medium.com/@aallan/getting-started-with-the-esp8266-270e30feb4d1).

Our solution is as simple as toast the chip with the WebRele Ardiuno sketch. Remember to change the SSID and the Password to match your home network.

Be careful also to use a static IP address for the device. In Spain main telco providers use 192.168.1.255 for the home internal network leaving addresses 192.168.1.2 up to 31 free from the DHCP pool so I've chosen 192.168.1.18 as the address for my device. Adapt it to your local requirements if needed.
