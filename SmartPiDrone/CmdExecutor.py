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


class CmdExecutor:

    #cmd = 0
    cmd = 'doNothing'#Set as default
    locMap = []
    #Empty dictionary
    options = {}
    
    def __init__(self):
        print "Command Executor object initialized!\r\n\n"
##        self.options ={
##                1:self.goFront(),
##                2:self.goRear(),
##                3:self.goLeft(),
##                4:self.goRight(),
##                5:self.goUp(),
##                6:self.goDown(),
##                7:self.goWayPoint()
##        }


    def doNothing():
        print "None\r\n"

    def startSystem():
        print "Starting drone system...\r\n"

    def getLoction():
        print "Getting location (lattitude and longitude)...\r\n"

    def goWayPoint():
        print "WayPoint \r\n\n"

    def connect():
        print "Connecting to Drone.... \r\n\n"

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

    #Store function references in dictionary
##    options = {
##                0:doNothing,
##                1:startSystem,
##                2:getLoction,
##                3:goWayPoint,
##                4:connect,
##                5:arm,
##                6:disarm,
##                7:forward,
##                8:reverse,
##                9:left,
##                10:right,
##                11:up,
##                12:down,
##                13:rotateLeft,
##                14:rotateRight
##            }

    options = {
                'doNothing':doNothing,             
                'startSystem':startSystem,
                'getLoction':getLoction,
                'goWayPoint':goWayPoint,
                'connect':connect,
                'arm':arm,
                'disarm':disarm,
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
    
