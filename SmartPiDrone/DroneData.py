#####################################
#   File Name : PiDrone.py   
#   Author      : NayLA  
#   Date         : 27/03/2017
#####################################
import sys
import argparse , math
import time
import datetime
from TimeStampLib import *
from dronekit import connect , Command , LocationGlobal , VehicleMode
from pymavlink import mavutil
from UltrasonicSensor import *
import Adafruit_DHT


class DroneData:

    global HmdTempSensor
    global temperature
    global humidity
    global altitude
    global latitude
    global longitude
    global sonar1_objdis
    global sonar2_objdis
    global sonar3_objdis
    global sonar4_objdis
    

    def __init__(self):
        
        timestamp = TimeStamp()
        #HudTempSensor= Adafruit_DHT.AM2302
        # Try to grab a sensor reading.  Use the read_retry method which will retry up
        # to 15 times to get a sensor reading (waiting 2 seconds between each retry).
        #hud, temp = Adafruit_DHT.read_retry(sensor, 25)
        self.temperature = 0
        self.humidity = 0
        self.altitude = 0
        self.latitude = 0
        self.longitude = 0
        self.sonar1_objdis = UltrasonicSensor(4,17)
        self.sonar2_objdis = UltrasonicSensor(23,24)
        self.sonar3_objdis = UltrasonicSensor(27,22)
        self.sonar4_objdis = UltrasonicSensor(12,16)
        self.HudTempSensor = Adafruit_DHT.AM2302



    def getTemperature(self):
        # Try to grab a sensor reading.  Use the read_retry method which will retry up
        # to 15 times to get a sensor reading (waiting 2 seconds between each retry).
        self.humidity, self.temperature = Adafruit_DHT.read_retry(self.HudTempSensor, 25)
        return  self.temperature

    def getHumidity(self):
        # Try to grab a sensor reading.  Use the read_retry method which will retry up
        # to 15 times to get a sensor reading (waiting 2 seconds between each retry).
        self.humidity, self.temperature = Adafruit_DHT.read_retry(self.HudTempSensor, 25)
        return self.humidity

    def getAltitude(self):
        ##Add code here
        return self.altitude

    def getLatitude(self):
        ##Add code here
        return self.latitude

    def getLongitude(self):
        ##Add code here
        return self.longitude

    def getSonar1_ObsDistance():
        return self.sonar1_objdis.raw_distance(sample_size=7,sample_wait=0.1)

    def getSonar2_ObsDistance():
        return self.sonar2_objdis.raw_distance(sample_size=7,sample_wait=0.1)


    def getSonar3_ObsDistance():
        return self.sonar3_objdis.raw_distance(sample_size=7,sample_wait=0.1)


    def getSonar4_ObsDistance():
        return self.sonar4_objdis.raw_distance(sample_size=7,sample_wait=0.1)


        
