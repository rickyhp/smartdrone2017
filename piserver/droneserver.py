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
        commands = ['up', 'down', 'turn left', 'turn right', 'roll left',
                    'roll right', 'arm', 'disarm', 'video', 'picture',
                    'forward', 'reverse', 'return home', 'connect',
                    'autoTakeOff', 'autoLand', 'drone position']
        if self.request.body:
            print "Got JSON data:", self.request.body
            data = json.loads(self.request.body)
        if data["ACTION"] == "sensor":
            reply = {"SENSOR":"132.0"}
        elif data["ACTION"] == "map":
            count = 1
            for latLng in ast.literal_eval(data["MAP"]):
                lat, lng = ast.literal_eval(latLng)
                print count, lat,  lng
                print type(lat), type(lng)
                count += 1
            reply = {"REPLY": "Coordinates sent"}
        elif data["ACTION"] == "drone position":
            latLngList = ["1.2897150957619739,103.77706065773963", "1.289584036003337,103.77684272825718"]
            reply = {"POSITION" : random.choice(latLngList)}
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
    ws_app = Application()
    server = tornado.httpserver.HTTPServer(ws_app)
    server.listen(port)
    print("droneserver started on " + str(port))
    tornado.ioloop.IOLoop.instance().start()
