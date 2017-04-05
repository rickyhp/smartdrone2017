#####################################
#   File Name : PiDrone.py   
#   Author      : NayLA  
#   Date         : 29/03/2017
#####################################

import sys
import time
from dronekit import connect , Command , LocationGlobal , VehicleMode
from pymavlink import mavutil
import argparse , math
from PixhawkCmd import PixhawkCmd

class CmdExecutor:

    #cmd = 0
    cmd = 'doNothing'#Set as default
    locMap = []
    #Empty dictionary
    options = {} 
    
    def __init__(self):
        print "Command Executor object initialized!\r\n\n"
        self.pixhawkCmd = PixhawkCmd()
        
    def doNothing():
        print "None\r\n"

    def getLocation(self):
        print "Getting location (lattitude and longitude)...\r\n"

    def goWayPoint():
        print "WayPoint \r\n\n"

    def connect(self, connstring):
        self.pixhawkCmd.connect(connstring)
        
    def disconnect(self):
        self.pixhawkCmd.disconnect()
        
    def arm(self):
        self.pixhawkCmd.arm()

    def takeoff(self, aTargetAltitude):
        self.pixhawkCmd.takeoff(aTargetAltitude)
    
    def land(self):
        self.pixhawkCmd.land()
        
    def disarm(self):
        self.pixhawkCmd.disarm()
            
    def forward(self):
        self.pixhawkCmd.forward()
        
    def reverse(self):
        self.pixhawkCmd.reverse()

    def left(self):
        self.pixhawkCmd.left()

    def right(self):
        self.pixhawkCmd.right()

    def up(self):
        self.pixhawkCmd.up()
        
    def down(self):
        self.pixhawkCmd.down()

    def rotateLeft(self, yaw_degree):
        self.pixhawkCmd.rotateLeft(yaw_degree)

    def rotateRight(self, yaw_degree):
        self.pixhawkCmd.rotateRight(yaw_degree)

    #Store function references in dictionary
    options = {
                'doNothing':doNothing,             
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
                'rotateRight':rotateRight               
            }


    def setCmd(self,cmd):
        self.cmd = cmd

    def getCmd(self):
        #return self.options.get(self.cmd,0)()
        return self.options[self.cmd]()

    def executeCmd(self):
        return self.options.get(self.cmd,0)()
    
# test cases
testCmd = CmdExecutor()
testCmd.connect('udp:127.0.0.1:14549') # connect to sitl
testCmd.arm() # arm
testCmd.takeoff(5) # takeoff 10 meters
time.sleep(3)
#testCmd.land() # land
#testCmd.send_ned_velocity(-5,0,0,5)
#testCmd.forward()
#testCmd.left()
#testCmd.right()
#testCmd.reverse()
#testCmd.up()
#testCmd.down()
#testCmd.rotateLeft(45)
testCmd.rotateRight(45)
#testCmd.reverse()
testCmd.up()
#time.sleep(10) # hovering at the location for 30 secs before RTL (program exit)
#testCmd.disarm() # disarm
testCmd.disconnect() # disconnect