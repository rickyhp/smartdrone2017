#####################################
#   File Name : Server.py   
#   Author      : NayLA  
#   Date         : 15/03/2017
#####################################

import socket               # Import socket module
import json
from TimeStampLib import *
import urllib2
import re
import requests
import webbrowser
from flask import Flask
from flask import Flask , render_template 
import cgi
import cgitb
from DroneData import *



class Webserver:

    #soc = socket.socket()         # Create a socket object
    soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = socket.gethostname() # Get local machine name
    port = 8081              # Reserve a port for your service.
  
    def __init__(self):
        self.soc.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR , 1)# For reusage of port
        #self.soc.bind((self.host, self.port))           # Bind to localhost and port
        self.soc.bind(('169.254.138.189',self.port))# Bind to IP & port

    def waitForConnection(self):
        self.soc.listen(5)                 # Now wait for client connection (5 connections).
        print "\r\n Server started and listening to port.... \r\n\n"

    def  serviceToClient(self, dronedata,timestamp):      
        while True:
            client, address = self.soc.accept()     # Establish connection with client.
            print 'Got connection from', address ,'\r\n\n'          
            client.sendall('Thank you for connecting... \r\n\n')
            client.sendall('Webserver for drone... \r\n\n')
            
            datetime = timestamp.getTimeStamp()
            temperature = dronedata.getTemperature()
            humidity = dronedata.getHumidity()
            altitude = dronedata.getAltitude()
            latitude = dronedata.getLatitude()
            longitude = dronedata.getLongitude()

            dicData = {"datetime": str(datetime) , "temperature":  str(temperature) ,
                       "humidity": str(humidity),"altitude": str(altitude) , "latitude": str(latitude),"longitude":str(longitude)}


            #print (dicData['temperature'])
            #parsed_json = dicData
            #print (parsed_json["temperature"])

            #json_string ='{"humidity": "23" , "altitude": "10"}'
            #parsed_json = json.loads(json_string)
            #print (parsed_json['humidity'])

            ########################
            #  Data read here!
            ########################

            client.sendall(json.dumps(dicData))# Send data  out in json format
            print(json.dumps(dicData))

            req = client.recv(1024)
            print req
            match = re.match('GET /move\?a=(\d+)\sHTTP/1.1',req)
            #angle = match.group(1)
            #print "ANGLE: " + angle + "\n"

##            client.sendall( """
##            HTTP/1.1  200 OK
##            Content-Type: text/html
##            <!DOCTYPE html>
##            <html>
##            <head>
##            <title>Page Title</title>
##            </head>
##            <body>
##
##            <h1>This is a Heading</h1>
##            <p>This is a paragraph.</p>
##
##            </body>
##            </html>
##            """)
            
            #template = Template('index.html')
            #template.render(name = name)                            
            client.close()  # Close the connection

    def closeConnection(self):
        print '\r\nSocket connection closed!\r\n\n'
        self.soc.close
        self.soc.shutdown(socket.SHUT_RDWR)
        
