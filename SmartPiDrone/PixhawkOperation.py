#####################################
#   File Name : PixhawkOperation.py   
#   Author      : NayLA  
#   Date         : 04/04/2017
#####################################

import sys
import time
from dronekit import connect , Command , LocationGlobal , VehicleMode
from pymavlink import mavutil
import argparse , math
import json
from DroneData import *



    
class PixhawkOperation:

    #Global variables
    vehicle = None

    def __init__(self):
        print "Pixhawk vehicle object initialized!\r\n\n"


    def connectPixhawk(self):
        print "Connecting to Drone.... \r\n\n"
        #self.vehicle = connect('udp:127.0.0.1:14549', wait_ready=True)
        self.vehicle = connect('/dev/ttyS0', wait_ready=True,baud=57600)
        

    def disconnectPixhawk(self):
        print "Disconnecting from Drone.... \r\n\n"
        self.vehicle.close()



