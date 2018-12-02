import Adafruit_DHT
import time
import sys

def Temp(pin):
    sensor = Adafruit_DHT.DHT11

    humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)


    if humidity is not None and temperature is not None:
        return temperature, humidity
    else:
        return 0, 0

if __name__ == '__main__':
    #t, h =Temp()
    #print "Temperature", t, "Humidity:", h, "%"
    # print('Temp={0:0.1f}*C  Humidity={1:0.1f}%'.format(t,h))
    pin = sys.argv[1]
    t , h = Temp(pin)

    print ('{0:0.1f}:{1:0.1f}'.format(t,h))  
