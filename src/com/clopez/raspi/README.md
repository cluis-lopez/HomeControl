# Relay based on ESP8266 board

This package maintains the Java classes involved in HW management:

- **Caldera.java**: gets the status of the boiler/pump through a simple HTTP GET request to the ESP8266 web server. Also allows to change the status of the relays therefore starting up or stopping the boiler/pump

- **SensorPythonWrapper**: reads the temperature and humidity from the attached DHT11 sensor. It just invokes an external Python program which includes the [Adafruit DHT11 library] (https://github.com/adafruit/Adafruit_Python_DHT). 


The original java class (DHT11.java) is kept in the directory for documentation purposes but the Java libraries it uses (pi4j) depends on external C libraries (wiringPi) that is far from stable.

The Pyhton script that read from the DHT11, uses the Adafruit library mentioned above that needs extra permissions when used by a non-root user. In this case the script will be invoked by a Tomcat servlet, run by a "tomcat" user in the Linux distro of the Raspberry. In this case we use Tomcat8 that creates when installed with "apt-get" a user named "tomcat8". You  need to additional steps:

- Give write permission to the `webapp` directory using: `sudo chmod 775 /var/lib/tomcat8/webapp`
- Make the "tomcat" user to belong to the user groups that permit access to the Raspberry GPIO HW doing: `sudo usermod -G gpio,spi, ic2 tomcat8`


