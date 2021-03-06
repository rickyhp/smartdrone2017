#!/usr/bin/env python3

"""droneserver.py: Handles incoming socket request and send command to FC using Multiwii Serial Protocol."""

__author__ = "Ricky Putra"
__copyright__ = "Copyright 2017"

__license__ = "GPL"
__version__ = "1.0"
__maintainer__ = "Ricky Putra"
__email__ = "rhpmail@gmail.com"
__status__ = "Development"

import tornado.web
import tornado.websocket
import tornado.httpserver
import tornado.ioloop
import json
import ast
import random
import RPi.GPIO as GPIO
import sys
import thread
import datetime
import argparse
import math

from threading import Thread
from new_mission import createFile as createMissionFile
from DroneData import *
from CmdExecutor import *
from TimeStampLib import *
from Tasks import *
from MavLinkSerialComm import *
from dronekit import connetc, Command, LocationGlobal, VehicleMode
from pymavlink import mavutil

global vehicle
global cycle

MAV_MODE_AUTO = 4
home_position_set = False

appcmd = 'doNothing'

timestamp = TimeStamp()
dronedata = DroneData()
commandexecutor = CmdExecutor()

class WebSocketHandler(tornado.websocket.WebSocketHandler):
    def check_origin(self, origin):
        return True
    
    def open(self):
        pass
 
    def on_message(self, message):
        print("received = " + message)
        
        reply = "Sorry, I didn't understand " + message
        
        if(message == "arm" or message == "start"):
            reply = "arm completed"
        
        if(message == "disarm" or message == "stop"):
            reply = "disarm completed"
        
        if(message == "altitude"):
            reply = "Currently auto drone altitude is at 10m above sea level"
            
        if(message == "hello"):
            reply = "hello from the other side"
        
        self.write_message(reply)
 
    def on_close(self):
        pass
 
 
class IndexPageHandler(tornado.web.RequestHandler):

    def get(self):
        #self.render("index.html")
        self.write("REPLY":"SmartDrone")
    
    def post(self):
        data = {"ACTION": ""}
        commands = ['up', 'down', 'left', 'right', 'arm', 'disarm', 'video', 'picture',
                    'forward', 'reverse', 'connect', 'autoTakeoff', 'autoLand',
                    'goWayPoint', 'getLocation', 'sensor']
        if self.request.body:
            print "Got JSON data:", self.request.body
            data = json.loads(self.request.body)
        if data["ACTION"] in commands:
            if data["ACTION"] == "goWayPoint":  # waypoint setting
                relAltList = ast.literal_eval(data["DRONE_RELATIVE_ALT"])
                mapList = ast.literal_eval(data["DRONE_MARKERS"])
                absAltList = ast.literal_eval(data["DRONE_MARKERS_ALT"])
                createMissionFile(mapList, relAltList, absAltList) #creating mission.txt
                reply = {"REPLY": "Coordinates sent"}
            elif data["ACTION"] == "getLocation": #drone location simulated as of now
                gpsData1 = {"location": ["1.2897150957619739", "103.77706065773963"],
                            "altitude": "0", "humidity": str(droneData.getHumidity()),
                            "temperature":str(droneData.getTemperature()),
                            "datetime": str(timestamp.getTimeStamp())}
                gpsData2 = {"location": ["1.289584036003337", "103.77684272825718"],
                            "altitude": "0", "humidity": str(droneData.getHumidity()),
                            "temperature":str(droneData.getTemperature()),
                            "datetime": str(timestamp.getTimeStamp())}
                gpsList = [gpsData1, gpsData2]
                reply = {"DRONE_GPS" : random.choice(gpsList)}
            elif data["ACION"] == "sensor":
                sensor1 = {'sonar-1': '50.9138436873', 'sonar-2': '51.3761760834' , 'sonar-3': '155.490976816',
                           'sonar-4': '32.89898989', 'temperature': str(droneData.getTemperature()),
                           'humidity': str(droneData.getHumidity())}
                sensor1 = {'sonar-1': '100.9138436873', 'sonar-2': '151.3761760834' , 'sonar-3': '145.490976816',
                           'sonar-4': '35.89898989', 'temperature': str(droneData.getTemperature()),
                           'humidity': str(droneData.getHumidity())}
            else:
                CmdExecutor.cmd = data["ACTION"]
                reply = {"REPLY" : "Command received"}
        else:
            reply = {"REPLY" : "command not recognized"}
        reply = json.dumps(reply)
        print reply
        self.write(reply)
        self.finish()
 
class Application(tornado.web.Application):
    def __init__(self):
        handlers = [
            (r'/', IndexPageHandler),
            (r'/websocket', WebSocketHandler)
        ]
 
        settings = {
            'template_path': 'templates'
        }
        tornado.web.Application.__init__(self, handlers, **settings)
 
 
if __name__ == '__main__':
    mavlinkcmd = MavLinkCmd()
    sonar1 = UltrasonicSensor(4,17)
    sonar2 = UltrasonicSensor(23,24)
    sonar3 = UltrasonicSensor(27,22)
    sonar4 = UltrasonicSensor(12,16)
    HuTempSensor = Adafruit_DHT.AM230
    port = 8080
    ws_app = Application()
    server = tornado.httpserver.HTTPServer(ws_app)
    server.listen(port)
    print("droneserver started on " + str(port))
    tornado.ioloop.IOLoop.instance().start()
    try:
        try:
            #thread.start_new_thread(SocketServeContinuousThread, (webserver, dronedata, timestamp ,commandexecutor, 2, ))
            thread.start_new_thread(performOperationThread, (commandexecutor, )) 
            thread.start_new_thread(HumidityAM2302Thread, (HudTempSensor, )) 
            thread.start_new_thread(Sonar1Thread, (sonar1, ))
            thread.start_new_thread(Sonar2Thread, (sonar2, ))
            thread.start_new_thread(Sonar3Thread, (sonar3, ))
            thread.start_new_thread(Sonar4Thread, (sonar4, ))
            #thread.start_new_thread(GetAllSensorDataThread, (sonar1,sonar2,sonar3,sonar4,HudTempSensor, ))       
            #thread.start_new_thread(SocketServerThread, (webserver, dronedata, timestamp ,5, ))
            #thread.start_new_thread(MavLinkSerialCommThread, (mavlinkdata, ))
        except:
            print "Error: unable to start thread"

    except KeyboardInterrupt: # If CTRL+C is pressed, exit cleanly:
        mavlinkdata.commClose()
        #webserver.closeConnection()
        GPIO.cleanup() # cleanup all GPIO
