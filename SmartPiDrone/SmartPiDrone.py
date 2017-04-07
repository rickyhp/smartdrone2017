#####################################
#   File Name : SmartPiDrone.py   
#   Author      : NayLA  
#   Date         : 11/03/2017
#####################################
#import asyncio
from threading import Thread
import RPi.GPIO as GPIO
import sys
import time
import thread
import datetime
import socket
from Tasks import *
from MavLinkSerialComm import *
from dronekit import connect , Command , LocationGlobal , VehicleMode
from pymavlink import mavutil
import argparse , math
from MavLinkCmd import *
from UltrasonicSensor import *
import Adafruit_DHT
from Server import *
from DroneData import *
from CmdExecutor import *


global vehicle

global cycle

MAV_MODE_AUTO   = 4
home_position_set = False

appcmd = 'doNothing'

        
################################################
def main():

    #mavlinkdata = MavLinkSerialComm()
    mavlinkcmd = MavLinkCmd()
    sonar1 = UltrasonicSensor(4,17)
    sonar2 = UltrasonicSensor(23,24)
    sonar3 = UltrasonicSensor(27,22)
    sonar4 = UltrasonicSensor(12,16)
    HudTempSensor= Adafruit_DHT.AM2302
    webserver = Webserver()
    timestamp = TimeStamp()
    dronedata = DroneData()
    commandexecutor = CmdExecutor()


    #************************************************************************#

   # Wait until client connection is established and successful.
    webserver.WaitForConnectionEstablishment()

    #For testing purpose
    #commandexecutor.cmd = 11
    #commandexecutor .executeCmd()

##    # Connect to the Vehicle
##    print "Connecting Pixhawk.....\r\n\n"
##    #vehicle = connect('127.0.0.1:14550', wait_ready=True)
##    #vehicle = connect('/dev/ttyS0', wait_ready=True,baud=57600)  
##    vehicle = mavlinkcmd.connect_vehicle()
##    print "Vehicle connected!"
##    print "Smart Drone is ready."
    
    #************************************************************************#

    try:

        try:

            thread.start_new_thread(SocketServeContinuousThread, (webserver, dronedata, timestamp ,commandexecutor, 2, ))
            thread.start_new_thread(performOperationThread, (commandexecutor, )) 

            thread.start_new_thread(HumidityAM2302Thread, (HudTempSensor, )) 
            thread.start_new_thread(Sonar1Thread, (sonar1, ))
            thread.start_new_thread(Sonar2Thread, (sonar2, ))
            thread.start_new_thread(Sonar3Thread, (sonar3, ))
            thread.start_new_thread(Sonar4Thread, (sonar4, ))
            #thread.start_new_thread(GetAllSensorDataThread, (sonar1,sonar2,sonar3,sonar4,HudTempSensor, ))       
            #thread.start_new_thread(SocketServerThread, (webserver, dronedata, timestamp ,5, ))
            #thread.start_new_thread(MavLinkSerialCommThread, (mavlinkdata, ))
               
                        
        except:
            print "Error: unable to start thread"

    except KeyboardInterrupt: # If CTRL+C is pressed, exit cleanly:
        mavlinkdata.commClose()
        webserver.closeConnection()
        GPIO.cleanup() # cleanup all GPIO

                        
    while 1:
        pass # Busy-wait for keyboard interrupt ( Ctrl + C)



if __name__ == "__main__":
	main()
