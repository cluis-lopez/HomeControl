# Relay based on ESP8266 board

This package maintains the HW related classes

- **Caldera.java**: gets the status of the boiler/pump through a simple HTTP GET request to the ESP8266 web server. Allows also to change the status of the relays therefore starting up or stopping the boiler/pump

- **SensorPythonWrapper**: reads the temperature and humidity from the attached DHT11 sensor. It just invokes an external Python program which includes the [Adafruit DHT11 library] (https://github.com/adafruit/Adafruit_Python_DHT). 


The original java class (DHT11.java) is kept in the directory for documentation purposes but the Java libraries it uses (pi4j) depends on external C libraries (wiringPi) that is far from stable. 
