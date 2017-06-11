#!/usr/bin/env python3

import RPi.GPIO as GPIO
import tornado.web
import tornado.websocket
import tornado.httpserver
import tornado.ioloop
import json
import ast
import random
import sys
import thread
import datetime
import base64
import picamera
import Adafruit_DHT
from threading import Thread

from MavLinkCmd import *
from DroneData import *
from CmdExecutor import *
from new_mission import createFile

global droneData
global commandexecutior

droneData = DroneData()
commandexecutor = CmdExecutor()
mavlinkcmd = MavLinkCmd()
sonar1 = UltrasonicSensor(4,17)
sonar2 = UltrasonicSensor(23,24)
sonar3 = UltrasonicSensor(27,22)
sonar4 = UltrasonicSensor(12,16)
HudTempSensor = Adafruit_DHT.AM2302
 
class IndexPageHandler(tornado.web.RequestHandler):

    def get(self):
        self.write("Welcome to Smart Drone")
    
    def post(self):
        data = {'ACTION': ''}
        #Overall drone commands list
        commands = ['up', 'down', 'left', 'right', 'arm', 'disarm', 'video', 'picture',
                    'forward', 'reverse', 'connect', 'autoTakeoff', 'autoLand',
                    'goWayPoint', 'getLocation', 'getSensor', 'tiltOn', 'tiltOff', 'tiltValue',
                    'take off', 'land', 'rotate left', 'rotate right', 'Stabilize', '3D Scan',
                    'Circle', 'Loiter', 'Altitude Hold', 'return home']
        #Implemented drone commands
        cmdDict = {'connect': commandexecutor.connect(), 'arm': commandexecutor.arm(), 'disarm': commandexecutor.disarm(),
                   'forward': commandexecutor.forward(), 'reverse': commandexecutor.reverse(), 'left': commandexecutor.left(),
                   'right': commandexecutor.right(), 'up': commandexecutor.up(), 'down': commandexecutor.down(),
                   'rotate left': commandexecutor.rotateLeft(), 'rotate right': commandexecutor.rotateRight(),
                   'take off', commandexecutor.takeoff()}
        if self.request.body:
            print "Got JSON data:", self.request.body
            data = json.loads(self.request.body)
        if data['ACTION'] in commands:
            print "Command recognised: ", data['ACTION']
            if data['ACTION'] == 'goWayPoint':
                relAltList = ast.literal_eval(data['DRONE_RELATIVE_ALT'])
                mapList = ast.literal_eval(data['DRONE_MARKERS'])
                absAlt = data['DRONE_MARKERS_ALT']
                createFile(mapList, relAltList, absAlt)
            elif data['ACTION'] == 'getLocation': #not implemented so simulated data
                gpsData1 = {'location': ['1.2897150957619739', '103.77706065773963'],
                            'altitude': '0', 'humidity': '71.99995665',
                            'temperature':'31.84384783742',
                            'datetime': str(datetime.now())}
                gpsData2 = {'location': ['1.289584036003337', '103.77684272825718'],
                            'altitude': '0', 'humidity': '65.83887371',
                            'temperature': '30.5364635',
                            'datetime': str(datetime.now())}
                self.write({"DRONE_GPS" : random.choice([gpsData1, gpsData2])})
            elif data['ACTION'] == 'getSensor':
                sensor = {'SENSOR1': str(dronedata.getSonar1_ObsDistance()), 'SENSOR2': str(dronedata.getSonar2_ObsDistance()),
                          'SENSOR3': str(dronedata.getSonar3_ObsDistance()), 'SENSOR4': str(dronedata.getSonar4_ObsDistance()),
                          'TEMPERATURE': str(droneData.getTemperature()), 'HUMIDITY': str(droneData.getHumidity())}
                self.write({'SENSOR': sensor})
            elif data['ACTION'] == 'picture':
                camera = picamera.PiCamera()
                camera.resolution = (1920, 1080)
                camera.capture(r'/home/pi/image.jpg')
                time.sleep(2.0)
                camera.close()
                with open('image.jpg', 'rb') as imageFile:
                    string = base64.b64encode(imageFile.read())
                    self.write(string)
            elif data['ACTION'] in cmdDict.keys():
                if data['ACTION'] == 'connect':
                    self.write("Connection Established")
                cmdDict[data['ACTION']]
        self.finish()
 
class Application(tornado.web.Application):
    def __init__(self):
        handlers = [(r'/', IndexPageHandler)] 
        settings = {'template_path': 'templates'}
        tornado.web.Application.__init__(self, handlers, **settings)
 
if __name__ == '__main__':
    try:
        try:
            thread.start_new_thread(performOperationThread, (commandexecutor, dronedata, ))  
            thread.start_new_thread(Sonar1Thread, (sonar1, dronedata, ))
            thread.start_new_thread(Sonar2Thread, (sonar2, dronedata, ))
            thread.start_new_thread(Sonar3Thread, (sonar3, dronedata, ))
            thread.start_new_thread(Sonar4Thread, (sonar4, dronedata, ))
            thread.start_new_thread(HumidityAM2302Thread, (HudTempSensor, dronedata, ))
        except:
            print "Error: unable to start thread"
        port = 8080
        ws_app = Application()
        server = tornado.httpserver.HTTPServer(ws_app)
        server.listen(port)
        print("droneserver started on " + str(port))
        tornado.ioloop.IOLoop.instance().start()
    except KeyboardInterrupt:
        mavlinkcmd.close()
        GPIO.cleanup()

