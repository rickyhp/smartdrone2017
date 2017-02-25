#!/usr/bin/env python3

"""test-send.py: Test script to send RC commands to a MultiWii Board."""

__author__ = "Aldo Vargas"
__copyright__ = "Copyright 2016 Altax.net"

__license__ = "GPL"
__version__ = "1"
__maintainer__ = "Aldo Vargas"
__email__ = "alduxvm@gmail.com"
__status__ = "Development"

from pyMultiwii import MultiWii
import time

if __name__ == "__main__":

    #board = MultiWii("/dev/tty.usbserial-AM016WP4")
    board = MultiWii("/dev/tty.SLAB_USBtoUART")
    try:
        board.arm()
        print "Armed"
        time.sleep(2)
        
        board.throttle(1250)
        print "Throttle 1250"
        time.sleep(2)
        
        board.disarm()
        print "Disarmed"
        time.sleep(2)
        
    except Exception,error:
        print "Error on Main: "+str(error)
