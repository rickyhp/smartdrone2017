#!/usr/bin/env python3

"""droneserver.py: Handles incoming socket request and send command to FC using Mavlink."""

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
from PixhawkCmd import PixhawkCmd
from new_mission import createFile as createMissionFile

pixcmd = None

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
        self.render("index.html")
    
    def post(self):
        data = {"ACTION": ""}
        commands = ['up', 'down', 'left', 'right', 'arm', 'disarm', 'video', 'picture',
                    'forward', 'reverse', 'connect', 'autoTakeoff', 'autoLand',
                    'goWayPoint', 'getLocation', 'sensor']
        if self.request.body:
            print "Got JSON data:", self.request.body
            data = json.loads(self.request.body)
        
        if data["ACTION"] == "sensor":
            reply = {"SENSOR":"132.0"}
        elif data["ACTION"] == "goWayPoint":
            relAltList = ast.literal_eval(data["DRONE_RELATIVE_ALT"])
            mapList = ast.literal_eval(data["DRONE_MARKERS"])
            absAltList = ast.literal_eval(data["DRONE_MARKERS_ALT"])
            createMissionFile(mapList, relAltList, absAltList)
            reply = {"REPLY": "Coordinates sent"}
        elif data["ACTION"] == "getLocation":
            gpsData1 = {"location": ["1.2897150957619739", "103.77706065773963"],
                         "altitude": "0", "humidity":"79.09999", "temperature":"30.39999996185",
                         "datetime": "2017-04-06 22:32:32"}
            gpsData2 = {"location": ["1.289584036003337", "103.77684272825718"],
                         "altitude": "0", "humidity":"89.03333", "temperature":"31.59",
                         "datetime": "2017-04-06 22:32:32"}
            gpsList = [gpsData1, gpsData2]
            reply = {"DRONE_GPS" : random.choice(gpsList)} 
        elif data["ACTION"] == "connect":
            pixcmd.connect('/dev/ttyAMA0', baud=57600, wait_ready=True)
            print "drone connected"
        else:
            reply = {"REPLY" : "Command received"}
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
    port = 8080
    pixcmd = PixhawkCmd()
    ws_app = Application()
    server = tornado.httpserver.HTTPServer(ws_app)
    server.listen(port)
    print("droneserver started on " + str(port))
    tornado.ioloop.IOLoop.instance().start()
