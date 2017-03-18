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
 
 
class WebSocketHandler(tornado.websocket.WebSocketHandler):
    def check_origin(self, origin):
        return True
    
    def open(self):
        pass
 
    def on_message(self, message):
        if(message == "arm"):
            print("armed")
        if(message == "disarm"):
            print("disarmed")
            
        self.write_message(u"Your message was: " + message)
 
    def on_close(self):
        pass
 
 
class IndexPageHandler(tornado.web.RequestHandler):
    def get(self):
        self.render("index.html")
    
    def post(self):
        commands = ['up', 'down', 'turn left', 'turn right', 'roll left',
                    'roll right', 'arm', 'disarm', 'video', 'picture',
                    'forward', 'reverse', 'return home']
        data = json.loads(self.request.body)
        print "Got JSON data:", data["ACTION"]
        if data["ACTION"] not in commands:
            reply = {"REPLY" : "Command not recognized"}
        else:
            reply = {"REPLY" : "Command recognized"}
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
    ws_app = Application()
    server = tornado.httpserver.HTTPServer(ws_app)
    server.listen(8080)
    tornado.ioloop.IOLoop.instance().start()
