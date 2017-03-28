#!/usr/bin/env python
# -*- coding: utf-8 -*-

from dronekit import connect, VehicleMode, LocationGlobalRelative
import time
from pymavlink import *


# Connect to the Vehicle
print 'Connecting to vehicle ....' 
vehicle = connect('/dev/ttyS0', wait_ready=True,baud=57600)


##def arm_and_takeoff(aTargetAltitude):
##    """
##    Arms vehicle and fly to aTargetAltitude.
##    """
##
##    print "Basic pre-arm checks"
##    # Don't try to arm until autopilot is ready
##    while not vehicle.is_armable:
##        print " Waiting for vehicle to initialise..."
##        time.sleep(1)
##
##        
##    print "Arming motors"
##    # Copter should arm in GUIDED mode
##    #vehicle.mode = VehicleMode("GUIDED")
##    vehicle.mode = VehicleMode("ACRO")
##    vehicle.armed = True    

##    # Confirm vehicle armed before attempting to take off
##    while not vehicle.armed:      
##        print " Waiting for arming..."
##        time.sleep(1)
##
##    print "Taking off!"
##    vehicle.simple_takeoff(aTargetAltitude) # Take off to target altitude
##
##    # Wait until the vehicle reaches a safe height before processing the goto (otherwise the command 
##    #  after Vehicle.simple_takeoff will execute immediately).
##    while True:
##        print " Altitude: ", vehicle.location.global_relative_frame.alt 
##        #Break and return from function just below target altitude.        
##        if vehicle.location.global_relative_frame.alt>=aTargetAltitude*0.95: 
##            print "Reached target altitude"
##            break
##        time.sleep(1)
##
##arm_and_takeoff(10)

########################################
#vehicle.parameters['ARMING_CHECK']=-9    
vehicle.armed = True
time.sleep(1)
#vehicle.simple_takeoff(10)

#vehicle.channels.overrides = {'1':900,'2':900,'3':900,'4':900}
#vehicle.flush()

# Override channels 1 and 4 (only).
vehicle.channel_override = { "1" : 900,"4" : 900 }
vehicle.flush()

# Cancel override on channel 1 and 4 by sending 0
#vehicle.channel_override = { "1" : 0, "4" : 0 }
#vehicle.flush()

# Change the parameter value to something different.
#vehicle.parameters['THR_MIN']=300
#vehicle.flush()

########################################

#print "Set default/target airspeed to 3"
vehicle.airspeed = 3

##print "Going towards first point for 30 seconds ..."
##point1 = LocationGlobalRelative(-35.361354, 149.165218, 20)
##vehicle.simple_goto(point1)
##
### sleep so we can see the change in map
##time.sleep(30)
##
##print "Going towards second point for 30 seconds (groundspeed set to 10 m/s) ..."
##point2 = LocationGlobalRelative(-35.363244, 149.168801, 20)
##vehicle.simple_goto(point2, groundspeed=10)

# sleep so we can see the change in map
#time.sleep(30)


# create the CONDITION_YAW command using command_long_encode()
##msg = vehicle.message_factory.command_long_encode(
##    0, 0,    # target system, target component
##    mavutil.mavlink.MAV_CMD_CONDITION_YAW, #command
##    0, #confirmation
##    heading,    # param 1, yaw in degrees
##    0,          # param 2, yaw speed deg/s
##    1,          # param 3, direction -1 ccw, 1 cw
##    is_relative, # param 4, relative offset 1, absolute angle 0
##    0, 0, 0)    # param 5 ~ 7 not used
### send command to vehicle
##vehicle.send_mavlink(msg)
##vehicle.flush()

#print "Returning to Launch"
#vehicle.mode = VehicleMode("RTL")

#Close vehicle object before exiting script
##print "Close vehicle object"
##vehicle.close()

# Shut down simulator if it was started.
##if sitl is not None:
##    sitl.stop()
