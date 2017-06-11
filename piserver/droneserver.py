#!/usr/bin/env python3

import tornado.web
import tornado.websocket
import tornado.httpserver
import tornado.ioloop
import json
import ast
import random
import base64
from datetime import datetime
from new_mission import createFile as createMissionFile

class IndexPageHandler(tornado.web.RequestHandler):

    def get(self):
        self.write("Welcome to SmartDrone Server")
    
    def post(self):
        data = {'ACTION': ''}
        commands = ['up', 'down', 'left', 'right', 'arm', 'disarm', 'video', 'picture',
                    'forward', 'reverse', 'connect', 'autoTakeoff', 'autoLand',
                    'goWayPoint', 'getLocation', 'getSensor', 'tiltOn', 'tiltOff', 'tiltValue',
                    'take off', 'land', 'tilt on', 'tilt off', 'rollLeft', 'rollRight',
                    'roll left', 'roll right', 'Stabilize', '3D Scan', 'Circle', 'Loiter',
                    'Altitude Hold', 'returnHome', 'return home']
        if self.request.body:
            print "Got JSON data:", self.request.body
            data = json.loads(self.request.body)
        if data['ACTION'] in commands:
            print "Command recognised: ", data['ACTION']
            if data['ACTION'] == 'goWayPoint':
                relAltList = ast.literal_eval(data['DRONE_RELATIVE_ALT'])
                mapList = ast.literal_eval(data['DRONE_MARKERS'])
                absAlt = data['DRONE_MARKERS_ALT']
                createMissionFile(mapList, relAltList, absAlt)
            elif data['ACTION'] == 'getLocation':
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
                sensor1 = {'SENSOR1': '50.9138436873', 'SENSOR2': '51.3761760834' , 'SENSOR3': '155.490976816',
                           'SENSOR4': '32.89898989', 'TEMPERATURE': '30.764623',
                           'HUMIDITY': '65.33862786'}
                sensor2 = {'SENSOR1': '100.9138436873', 'SENSOR2': '151.3761760834' , 'SENSOR3': '145.490976816',
                           'SENSOR4': '35.89898989', 'TEMPERATURE': '32.92837826876',
                           'HUMIDITY': '34.656726326'}
                self.write({'SENSOR' : random.choice([sensor1, sensor2])})
            elif data['ACTION'] == 'picture':
                with open('image.jpg', 'rb') as imageFile:
                    string = base64.b64encode(imageFile.read())
                    self.write(string)
            elif data['ACTION'] == 'connect':
                self.write("Connection Established")
            self.finish()
 
class Application(tornado.web.Application):
    def __init__(self):
        handlers = [(r'/', IndexPageHandler)]
        settings = {'template_path': 'templates'}
        tornado.web.Application.__init__(self, handlers, **settings)
                
if __name__ == '__main__':
    port = 8080
    ws_app = Application()
    server = tornado.httpserver.HTTPServer(ws_app)
    server.listen(port)
    print("Smart Drone Server started on " + str(port))
    tornado.ioloop.IOLoop.instance().start()
