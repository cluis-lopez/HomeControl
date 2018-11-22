import Adafruit_DHT
import time

def Temp():
    sensor = Adafruit_DHT.DHT11
    pin = 22

    humidity, temperature = Adafruit_DHT.read_retry(sensor, pin)


    if humidity is not None and temperature is not None:
        return temperature, humidity
    else:
        return 0, 0

if __name__ == '__main__':
    #t, h =Temp()
    #print "Temperature", t, "Humidity:", h, "%"
    # print('Temp={0:0.1f}*C  Humidity={1:0.1f}%'.format(t,h))
    tt = hh = 0
    for i in range(3):
        t , h = Temp()
        tt = tt +t
        hh = hh + h
        time.sleep (1)

    print ('{0:0.1f}:{1:0.1f}'.format(tt/3,hh/3))
       
