#####################################
#   File Name : CmdExecutor.py   
#   Author      : NayLA  
#   Date         : 29/03/2017
#####################################

import sys
import time
from dronekit import connect , Command , LocationGlobal , VehicleMode
from pymavlink import mavutil
import argparse , math
import json
from PixhawkCmd import *


class CmdExecutor:

    #cmd = 0
    cmd = 'doNothing'#Set as default
    #Empty location map array
    locMap = []
    #Empty absolute altitude array
    #absAlt = []
    #Empty relative altitude array
    #relAlt = []
    #Empty dictionary
    options = {}

    pixhawkCmd = None

    vehicle = None
    
    def __init__(self):
        print "Command Executor object initialized!\r\n\n"
        self.pixhawkCmd = PixhawkCmd()
        

    def doNothing():
        print "None\r\n"

    def startSystem():
        print "Starting drone system...\r\n"

    def getDroneInfo():
        print "Getting Drone's data...\r\n"

    def getLocation():
        print "Getting location (lattitude and longitude)...\r\n"

    def goWayPoint():
        print "WayPoint \r\n\n"

    def connect(self):
        print "Connecting to Pixhawk.... \r\n\n"
        #self.pixhawkCmd.connect(connstring)
        # Connect Pixhawk via UDP (connect to sitl)
        #self.vehicle = connect('udp:127.0.0.1:14549', wait_ready=True)
        #Connect Pixhawk via Serial
        self.vehicle = self.pixhawkCmd.connect('/dev/ttyS0')
        #pixhawkCmd.connect('/dev/ttyS0')

        
    def disconnect():
        print "Disconnecting from Drone.... \r\n\n"
        #self.vehicle.close()
        
        
    def arm():
        print "Arming motor.... \r\n\n"

    def disarm():
        print "Disarm motor.... \r\n\n"

    def forward():
        print "Going forward.... \r\n\n"

    def reverse():
        print "Going reverse.... \r\n\n"

    def left():
        print "Going left.... \r\n\n"

    def right():
        print "Going right.... \r\n\n"

    def up():
        print "Going up.... \r\n\n"

    def down():
        print "Going down.... \r\n\n"

    def rotateLeft():
        print "Rotate left.... \r\n\n"

    def rotateRight():
        print "Rotate right.... \r\n\n"


    def rollLeft():
         print "Roll left.... \r\n\n"

        
    def rollRight():
        print "Roll right.... \r\n\n"


    def takeoff():
        print "Taking off.....\r\n\n"

    def land():
        print "Landing.....\r\n\n"     
        
    def autoTakeoff():
        print "Auto takeoff.... \r\n\n"

        
    def autoLand():
        print "Auto landing.... \r\n\n"

        
    def returnHome():
        print "Returning home location.... \r\n\n"

        
    def video():
        print "Taking video.... \r\n\n"

        
    def picture():
        print "Taking picture.... \r\n\n"
        

    #Store function references in dictionary
    options = {
                'doNothing':doNothing,             
                'startSystem':startSystem,
                'getLocation':getLocation,
                'goWayPoint':goWayPoint,
                'connect':connect,
                'arm':arm,
                'disarm':disarm,
                'takeoff':takeoff,
                'land':land,
                'forward':forward,
                'reverse':reverse,
                'left':left,
                'right':right,
                'up':up,
                'down':down,
                'rotateLeft':rotateLeft,
                'rotateRight':rotateRight,
                'rollLeft':rollLeft,
                'rollRight':rollRight,
                'autoTakeoff':autoTakeoff,
                'autoLand':autoLand,
                'returnHome':returnHome,
                'video':video,
                'picture':picture
            }


    def setCmd(self,cmd):
        self.cmd = cmd

    def getCmd(self):
        #return self.options.get(self.cmd,0)()
        return self.options[self.cmd]()


    def executeCmd(self):
        return self.options.get(self.cmd,0)()
    
# test cases
#testCmd = CmdExecutor()
#testCmd.connect('udp:127.0.0.1:14549') #connect to sitl
#testCmd.disconnect() #disconnect
