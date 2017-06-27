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
        self.pixhawkCmd.connect('udp:127.0.0.1:14549')
        # Connect Pixhawk via UDP (connect to sitl)
        #self.vehicle = connect('udp:127.0.0.1:14549', wait_ready=True)
        #Connect Pixhawk via Serial
        #self.vehicle = self.pixhawkCmd.connect('/dev/ttyS0')
        #pixhawkCmd.connect('/dev/ttyS0')

        
    def disconnect(self):
        print "Disconnecting from Drone.... \r\n\n"
        #self.vehicle.close()
        
        
    def arm(self):
        print "Arming motor.... \r\n\n"
        self.pixhawkCmd.arm()
    def Stabilize():
        print "Stabilizing drone....\r\n\n"
        
    def disarm(self):
        print "Disarm motor.... \r\n\n"
        self.pixhawkCmd.disarm()
        
    def forward(self):
        print "Going forward.... \r\n\n"
        self.pixhawkCmd.forward()
        
    def reverse(self):
        print "Going reverse.... \r\n\n"
        self.pixhawkCmd.reverse()

    def left(self):
        print "Going left.... \r\n\n"
        self.pixhawkCmd.left()

    def right(self):
        print "Going right.... \r\n\n"
        self.pixhawkCmd.right()

    def up(self):
        print "Going up.... \r\n\n"
        self.pixhawkCmd.up()
        
    def down(self):
        print "Going down.... \r\n\n"
        self.pixhawkCmd.down()

    def rotateLeft(self):
        print "Rotate left.... \r\n\n"
        self.pixhawkCmd.rotateLeft(30)
        
    def rotateRight(self):
        print "Rotate right.... \r\n\n"
        self.pixhawkCmd.rotateRight(30)


    def rollLeft():
         print "Roll left.... \r\n\n"

        
    def rollRight():
        print "Roll right.... \r\n\n"


    def takeoff(self):
        print "Taking off.....\r\n\n"
        self.pixhawkCmd.takeoff(10)
        
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
                'Stabilize':Stabilize,
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
#testCmd.connect() #connect to sitl
#testCmd.arm()
#testCmd.takeoff()
#testCmd.rotateLeft()
#testCmd.left()
#time.sleep(5)
#testCmd.right()
#time.sleep(5)
#testCmd.disconnect() #disconnect

