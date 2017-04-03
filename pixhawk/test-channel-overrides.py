#!/usr/bin/env python
""" Drone Pilot - Control of MRUAV """
""" test-channel-overrides.py -> Script that makes a pixhawk take off in a secure way and doing left and right roll before land. DroneKit 2.0 related. """

__author__ = "Ricky Putra"
__copyright__ = "Copyright 2017"

__license__ = "GPL"
__version__ = "1.0"
__maintainer__ = "Ricky Putra"
__email__ = "rhpmail@gmail.com"
__status__ = "Development"

import time, threading
from dronekit import connect, VehicleMode
from time import sleep
from modules.utils import *
from modules.pixVehicle import *

update_rate = 0.01 # 100 hertz update rate
roll = 1500;
pitch = 1500;
throttle = 1000;
yaw = 1500;

# Connection to the vehicle
# SITL via TCP
#vehicle = connect('tcp:127.0.0.1:5760', wait_ready=True)
# SITL/vehicle via UDP (connection coming from mavproxy.py)
vehicle = connect('udp:127.0.0.1:14549', wait_ready=True)
# Direct UART communication to Pixhawk
#vehicle = connect('/dev/ttyAMA0', wait_ready=True)


def sendCommands():
    try:
        while True:
            current = time.time()
            elapsed = 0
            vehicle.channels.overrides = { "1" : roll, "2" : pitch, "3" : throttle, "4" : yaw }
            #print "%s" % vehicle.attitude
            #print "%s" % vehicle.channels
            # hz loop
            while elapsed < update_rate:
                elapsed = time.time() - current
    except Exception,error:
        print "Error on sendCommands thread: "+str(error)
        sendCommands()    
    
""" Section that starts the threads """
try:
    vehicleThread = threading.Thread(target=sendCommands)
    vehicleThread.daemon=True
    vehicleThread.start()
    """ Mission starts here """

    print "\n\nAttempting to start take off!!\n\n"
    arm_and_takeoff(vehicle, 10)
    
    vehicle.mode = VehicleMode("LOITER")
    
    print "roll left, mid throttle to maintain altitude in loiter mode"
    throttle = 1500
    roll = 1200
    sleep(1)
    print " Channel overrides: %s" % vehicle.channels.overrides
    
    sleep(5)
    
    print "roll right, mid throttle to maintain altitude in loiter mode"
    throttle = 1500
    roll = 1750
    sleep(1)
    print " Channel overrides: %s" % vehicle.channels.overrides
    
    sleep(5)
    
    vehicle.mode = VehicleMode("GUIDED")
    
    while vehicle.armed:
        print "Current altitude: ", vehicle.location.global_relative_frame.alt
        
        if vehicle.mode.name == "LAND" or vehicle.mode.name == "RTL":
            print "clear override at channel 1"
            del vehicle.channels.overrides['1']
            del vehicle.channels.overrides['3']
            roll = 1500
            throttle = 1000
            sleep(1)
            print " Channel overrides: %s" % vehicle.channels.overrides
            break;
        
        time.sleep(0.5)
    
except Exception,error:
    print "Error on main script thread: "+str(error)
    vehicle.close()
    
print "\n\nMission complete successfully\n\n"
vehicle.close()