#####################################
#   File Name : PiDrone.py   
#   Author      : NayLA  
#   Date         : 07/03/2017
#####################################


import serial
from TimeStampLib import *
import sys
from datetime import datetime




class MavLinkSerialComm:

    pktSize = 19 # Important #

    def __init__(self, baudrate = 57600, timeout =4.0):
        self.port = serial.Serial("/dev/ttyS0", baudrate=57600 , timeout =4.0)
        

    def readPacket(self):
        rcv = self.port.read(self.pktSize).encode('hex')
        return rcv

    def commClose(self):
            self.port.close()
            print "Comm Port closed!"


    def connect_vehicle(self):
        vehicle = connect('/dev/ttyS0', wait_ready=True,baud=57600)
        return vehicle

    def PX4setMode(self,mavMode,vhc):
        vehicle = vhc
        vehicle._master.mav.command_long_send(vehicle._master.target_system, vehicle._master.target_component,
                                                   mavutil.mavlink.MAV_CMD_DO_SET_MODE, 0,
                                                   mavMode,0, 0, 0, 0, 0, 0)
