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
        self.sonar1_objdis = 0
        self.sonar2_objdis = 0
        self.sonar3_objdis = 0
        self.sonar4_objdis = 0
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
        ##Add code here for getting altitude data from Pixhawk
        return self.altitude

    def getLatitude(self):
        ##Add code here for getting latitude data from Pixhawk
        return self.latitude

    def getLongitude(self):
        ##Add code here for getting longitude data from Pixhawk
        return self.longitude

    def getLocation(self):
        return [(self.latitude,self.longitude)]

    def getSonar1_ObsDistance(self):
        return self.sonar1_objdis

    def setSonar1_ObsDistance(self,objdis):
        self.sonar1_objdis = objdis


    def getSonar2_ObsDistance(self):
        return self.sonar2_objdis

    def setSonar2_ObsDistance(self,objdis):
        self.sonar2_objdis = objdis


    def getSonar3_ObsDistance(self):
        return self.sonar3_objdis

    def setSonar3_ObsDistance(self,objdis):
        self.sonar3_objdis = objdis


    def getSonar4_ObsDistance(self):
        return self.sonar4_objdis

    def setSonar4_ObsDistance(self,objdis):
        self.sonar4_objdis = objdis
        
