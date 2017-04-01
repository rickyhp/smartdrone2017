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

    def armMotors():
        print "Started arming...\r\n"

    def goFront():
        print "Front \r\n\n"


    def goRear():
        print "Rear \r\n\n"


    def goLeft():
        print "Left \r\n\n"


    def goRight():
        print "Right \r\n\n"


    def goUp():
        print "Up \r\n\n"


    def goDown():
        print "Down \r\n\n"


    def goWayPoint():
        print "WayPoint \r\n\n"

    #Store function references in dictionary
    options = {
                0:doNothing,
                1:startSystem,
                2:getLoction,
                3:armMotors,
                4:goFront,
                5:goRear,
                6:goLeft,
                7:goRight,
                8:goUp,
                9:goDown,
                10:goWayPoint
            }


    def getCmd(self,cmd):
        #print "Received command : %s"%self.options.get(cmd)
        return self.options.get(cmd,0)()
        #return self.options[cmd]()


    def executeCmd(self,cmd):
        return self.options.get(cmd,0)()
    
