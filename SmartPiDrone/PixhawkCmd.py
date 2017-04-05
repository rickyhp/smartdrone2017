#####################################
#   File Name   :   PixhawkCmd.py   
#   Author      :   Ricky Putra  
#   Date        :   05/04/2017
#   Version     :   1.0
#   Description :   This class contains useful pixhawk/mavlink high level commands
#####################################

import sys
import time
from dronekit import connect , Command , LocationGlobal , VehicleMode
from pymavlink import mavutil
import argparse , math


class PixhawkCmd:

    cmd = 'doNothing'#Set as default
    locMap = []
    #Empty dictionary
    options = {}
    #Global variables
    vehicle = None
    velocity = 2
    velocity_duration = 1
    
    def __init__(self):
        print "PixhawkCmd object initialized!\r\n\n"
        
    def doNothing():
        print "None\r\n"

    def getLocation(self):
        print "Getting location (lattitude and longitude)...\r\n"

    def goWayPoint():
        print "WayPoint \r\n\n"

    def connect(self,connstring):
        print "Connecting to Drone.... \r\n\n"
        self.vehicle = connect(connstring, wait_ready=True)
        
    def disconnect(self):
        print "Disconnecting from Drone.... \r\n\n"
        self.vehicle.close()
        
    def arm(self):
        self.vehicle.mode = VehicleMode("GUIDED")
        print "Basic pre-arm checks"
        # Don't let the user try to fly autopilot is booting
        if self.vehicle.mode.name == "INITIALISING":
            print "Waiting for vehicle to initialise"
            time.sleep(1)
        while self.vehicle.gps_0.fix_type < 2:
            print "Waiting for GPS...:", vehicle.gps_0.fix_type
            time.sleep(1)
        print "Arming motors..."
        self.vehicle.armed   = True
        while not self.vehicle.armed:
            print "Waiting for arming..."
            time.sleep(1)
        print "Motors Armed \r\n\n"

    def takeoff(self, aTargetAltitude):
        print "Taking off!"
        self.vehicle.mode = VehicleMode("GUIDED")
        self.vehicle.simple_takeoff(aTargetAltitude) # Take off to target altitude
        time.sleep(2)
        try:
            while self.vehicle.mode.name=="GUIDED":
                print " -> Alt:", self.vehicle.location.global_relative_frame.alt
                if abs(self.vehicle.location.global_relative_frame.alt-aTargetAltitude) < 0.05: 
                    print "\n\tReached %0.1f m\n" % (aTargetAltitude)
                    break
                time.sleep(1)
        except KeyboardInterrupt:
            print "Keyboard Interrupt on takeoff."
    
    def land(self):
        print "Landing..."
        self.vehicle.mode = VehicleMode("LAND")
        time.sleep(2)
        try:
            while self.vehicle.mode.name=="LAND":
                print " -> Alt:", self.vehicle.location.global_relative_frame.alt
                if self.vehicle.location.global_relative_frame.alt < 0.05: 
                    print "\n\tReached 0 m\n"
                    break
                time.sleep(1)
        except KeyboardInterrupt:
            print "Keyboard Interrupt on landing."
        
    def disarm(self):
        if(self.vehicle.armed):
            print "Disarming motor.... \r\n\n"
            self.vehicle.armed   = False
            while self.vehicle.armed:
                print "Waiting for disarming..."
                time.sleep(1)
            print "Motors Disarmed \r\n\n"
        else:
            print "Motors not armed"
            
    def forward(self):
        print "Going forward.... \r\n\n"
        self.send_ned_velocity(self.velocity,0,0,self.velocity_duration) # vx=5, duration=1 sec
        
    def reverse(self):
        print "Going reverse.... \r\n\n"
        self.send_ned_velocity(0-(self.velocity),0,0,self.velocity_duration) # vx=-5, duration=1 sec

    def left(self):
        print "Going left.... \r\n\n"
        self.send_ned_velocity(0,0-(self.velocity),0,self.velocity_duration) # vy=-5, duration=1 sec

    def right(self):
        print "Going right.... \r\n\n"
        self.send_ned_velocity(0,self.velocity,0,self.velocity_duration) # vy=5, duration=1 sec

    def up(self):
        print "Going up.... \r\n\n"
        self.send_ned_velocity(0,0,self.velocity,self.velocity_duration) # vz=5, duration=1 sec
        
    def down(self):
        print "Going down.... \r\n\n"
        self.send_ned_velocity(0,0,0-(self.velocity),self.velocity_duration) # vz=-5, duration=1 sec

    def rotateLeft(self, yaw_degree):
        print "Rotate left.... \r\n\n"
        self.condition_yaw(yaw_degree,relative=False,rotation=-1) # rotate 30 deg left relative to current heading

    def rotateRight(self, yaw_degree):
        print "Rotate right.... \r\n\n"
        self.condition_yaw(yaw_degree,relative=False,rotation=1) # rotate 30 deg right relative to current heading

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
    
    def send_ned_velocity(self,velocity_x, velocity_y, velocity_z, duration):
        """
        Move vehicle in direction based on specified velocity vectors.
        """
        msg = self.vehicle.message_factory.set_position_target_local_ned_encode(
            0,       # time_boot_ms (not used)
            0, 0,    # target system, target component
            mavutil.mavlink.MAV_FRAME_LOCAL_NED, # frame
            0b0000111111000111, # type_mask (only speeds enabled)
            0, 0, 0, # x, y, z positions (not used)
            velocity_x, velocity_y, velocity_z, # x, y, z velocity in m/s
            0, 0, 0, # x, y, z acceleration (not supported yet, ignored in GCS_Mavlink)
            0, 0)    # yaw, yaw_rate (not supported yet, ignored in GCS_Mavlink)

        # send command to vehicle on 1 Hz cycle
        for x in range(0,duration):
            self.vehicle.send_mavlink(msg)
            time.sleep(1)
        
    def condition_yaw(self, heading, relative=False, rotation=1):
        """
        Send MAV_CMD_CONDITION_YAW message to point vehicle at a specified heading (in degrees).
        This method sets an absolute heading by default, but you can set the `relative` parameter
        to `True` to set yaw relative to the current yaw heading.
        """
        if relative:
            is_relative = 1 #yaw relative to direction of travel
        else:
            is_relative = 0 #yaw is an absolute angle
        # create the CONDITION_YAW command using command_long_encode()
        msg = self.vehicle.message_factory.command_long_encode(
            0, 0,    # target system, target component
            mavutil.mavlink.MAV_CMD_CONDITION_YAW, #command
            0, #confirmation
            heading,    # param 1, yaw in degrees
            0,          # param 2, yaw speed deg/s
            rotation,          # param 3, direction -1 ccw, 1 cw
            is_relative, # param 4, relative offset 1, absolute angle 0
            0, 0, 0)    # param 5 ~ 7 not used
        # send command to vehicle
        self.vehicle.send_mavlink(msg)
    
# test cases
#testCmd = PixhawkCmd()
#testCmd.connect('udp:127.0.0.1:14549') # connect to sitl
#testCmd.arm() # arm
#testCmd.takeoff(10) # takeoff 10 meters
#testCmd.land() # land
#testCmd.send_ned_velocity(-5,0,0,5)
#testCmd.forward()
#testCmd.left()
#testCmd.right()
#testCmd.reverse()
#testCmd.up()
#testCmd.down()
#testCmd.rotateLeft(30)
#time.sleep(3)
#testCmd.rotateRight()
#time.sleep(30) # hovering at the location for 30 secs before RTL (program exit)
#testCmd.disarm() # disarm
#testCmd.disconnect() # disconnect