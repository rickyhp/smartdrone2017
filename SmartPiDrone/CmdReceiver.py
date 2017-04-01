#####################################
#   File Name : PiDrone.py   
#   Author      : NayLA  
#   Date         : 29/03/2017
#####################################

import sys
import time
import json



class CmdReceiver:

    #cmd = None
    cmd = 3#hard-coded temporarily for testing

    options = {
                "doNothing" : 0,
                "startSystem":1,
                "getLoction":2,
                "armMotors":3,
                "goFront":4,
                "goRear":5,
                "goLeft":6,
                "goRight":7,
                "goUp":8,
                "goDown":9,
                "goWayPoint":10
            }

    def __init__(self):
        print "Command Receiver object initialized!\r\n\n"


    def doNothing(self):
        print "None\r\n"


    def parsedJSON(self,json_string):
        parsed_json = json.loads(json_string)
        return self.options.get(parsed_json['doNothing'],0)
        

    def getCmd(self):
        print "Received command : %s"%self.cmd
        return self.cmd
