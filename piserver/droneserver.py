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
#from PixhawkCmd import PixhawkCmd
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
        #self.render("index.html")
        self.write({"REPLY":"SmartDrone"})
    
    def post(self):
        data = {"ACTION": ""}
        commands = ['up', 'down', 'left', 'right', 'arm', 'disarm', 'video', 'picture',
                    'forward', 'reverse', 'connect', 'autoTakeoff', 'autoLand',
                    'goWayPoint', 'getLocation', 'getSensor', 'tiltOn', 'tiltOff', 'tiltValue',
                    'take off', 'land', 'tilt on', 'tilt off', 'rollLeft', 'rollRight',
                    'roll left', 'roll right']
        if self.request.body:
            print "Got JSON data:", self.request.body
            data = json.loads(self.request.body)
        if data["ACTION"] in commands:
            if data["ACTION"] == "goWayPoint":  # waypoint setting
                relAltList = ast.literal_eval(data["DRONE_RELATIVE_ALT"])
                mapList = ast.literal_eval(data["DRONE_MARKERS"])
                absAlt = data["DRONE_MARKERS_ALT"]
                createMissionFile(mapList, relAltList, absAlt) #creating mission.txt
                reply = {"REPLY": "Coordinates sent"}
            elif data["ACTION"] == "getLocation": #drone location simulated as of now
                gpsData1 = {"location": ["1.2897150957619739", "103.77706065773963"],
                            "altitude": "0", "humidity": "71.99995665",
                            "temperature":"31.84384783742",
                            "datetime": "2017-20-22 22:32:32"}
                gpsData2 = {"location": ["1.289584036003337", "103.77684272825718"],
                            "altitude": "0", "humidity": "65.83887371",
                            "temperature": "30.5364635",
                            "datetime": "2017-20-22 22:32:32"}
                gpsList = [gpsData1, gpsData2]
                reply = {"DRONE_GPS" : random.choice(gpsList)}
            elif data["ACTION"] == "getSensor":
                sensor1 = {"sonar-1": "50.9138436873", "sonar-2": "51.3761760834" , "sonar-3": "155.490976816",
                           "sonar-4": "32.89898989", "temperature": "30.764623",
                           "humidity": "65.33862786"}
                sensor2 = {"sonar-1": "100.9138436873", "sonar-2": "151.3761760834" , "sonar-3": "145.490976816",
                           "sonar-4": "35.89898989", "temperature": "32.92837826876",
                           "humidity": "34.656726326"}
                sensorList = [sensor1, sensor2]
                reply = {"SENSOR" : random.choice(sensorList)}
            elif data["ACTION"] == "take off" or data["ACTION"] == "land" or data["ACTION"] == "roll left" or data["ACTION"] == "roll right":
                voiceDict = {"take off":"autoTakeoff", "land":"autoLand", "roll left":"rollLeft", "roll right":"rollRight"}
                reply = {"REPLY" : "Command received: " + voiceDict[data["ACTION"]]}
            elif data["ACTION"] == "tiltValue":
                reply = {"REPLY" : "Command received"}
            elif data["ACTION"] == "connect":
                reply = {"REPLY" : "Command received: " + data["ACTION"]}
                #pixcmd.connect('/dev/ttyAMA0', baud=57600, wait_ready=True)
                print "drone connected"
            else:
                reply = {"REPLY" : "Command received: " + data["ACTION"]}
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
    port = 8080
    #pixcmd = PixhawkCmd()
    ws_app = Application()
    server = tornado.httpserver.HTTPServer(ws_app)
    server.listen(port)
    print("droneserver started on " + str(port))
    tornado.ioloop.IOLoop.instance().start()
