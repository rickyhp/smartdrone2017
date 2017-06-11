#####################################
#   File Name : Server.py   
#   Author      : NayLA  
#   Date         : 15/03/2017
#####################################
import sys
import socket               # Import socket module
import json
from TimeStampLib import *
import urllib2
import re
import requests
import webbrowser
#from flask import Flask
#from flask import Flask , render_template 
import cgi
import cgitb
from DroneData import *
from CmdExecutor import *
import ast
from new_mission import createFile


class Webserver:

    
    #soc = socket.socket()         # Create a socket object
    soc = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
    host = socket.gethostname() # Get local machine name
    port = 8081              # Reserve a port for your service.

    #client = None
    #address = None
     
  
    def __init__(self):
        self.soc.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR , 1)# For reusage of port
        #self.soc.bind((self.host, self.port))           # Bind to localhost and port
        #self.soc.bind(('169.254.138.189',self.port))# Bind to IP & port , (eth0)
        #self.soc.bind(('192.168.0.147',self.port))# Bind to IP & port , (wlan0)
        self.soc.bind(('192.168.43.148',self.port))# Bind to IP & port , (wlan0)
        #self.soc.bind(('192.168.1.1',self.port))# Bind to IP & port , (wlan0)


    def waitForConnection(self):
        self.soc.listen(5)                 # Now wait for client connection (5 connections).
        print "\r\n Server started and listening to port and waiting for client connection.... \r\n\n"


    def WaitForConnectionEstablishment(self):
        Connection = True
        while  Connection:
            self.waitForConnection()
            client, address = self.soc.accept()
            Connection = False          
        print 'Got connection from client : ', address ,'\r\n\n' 


    def ServeContinuously(self, dronedata,timestamp,commandexecutor):
        while True:
          
##            datetime = timestamp.getTimeStamp()
##            temperature = dronedata.getTemperature()
##            humidity = dronedata.getHumidity()
##            altitude = dronedata.getAltitude()
##            latitude = dronedata.getLatitude()
##            longitude = dronedata.getLongitude()
##
##            dicData = {"datetime": str(datetime) , "temperature":  str(temperature) ,
##                                   "humidity": str(humidity),"altitude": str(altitude) , "location": [str(latitude),str(longitude)]}
           

            client, address = self.soc.accept()
##            client.sendall('Sending out drone data... \r\n\n')
##            client.sendall(json.dumps(dicData))# Send data  out in json format
##            print(json.dumps(dicData))

            req = client.recv(4096)
            print req

            location = {}

            # Split header and payload body
            body = req.split('\r\n\r\n',1)[1]


            #Convert body into json object 
            parsed_json = json.loads(body)#

            #import pdb; pdb.set_trace()

            print "Size of received Jason array : ", len(parsed_json),"\r\n"

            #Set received  command and way point locations
            if len(parsed_json) ==1:
                commandexecutor.cmd = parsed_json["ACTION"] 
                print "Parsed JSON CMD : ",commandexecutor.cmd
                            
            elif len(parsed_json) > 1:
                commandexecutor.cmd = parsed_json["ACTION"]
                print "Parsed WapPoint CMD : ",commandexecutor.cmd
                #commandexecutor.locMap = parsed_json["DRONE_MARKERS"]#["MAP"]
                
                relAltList = ast.literal_eval(parsed_json["DRONE_RELATIVE_ALT"])
                mapList = ast.literal_eval(parsed_json["DRONE_MARKERS"])
                absAltList = ast.literal_eval(parsed_json["DRONE_MARKERS_ALT"])
                
                createFile(mapList, relAltList, absAltList)
		print mapList, relAltList, absAltList		
                #for item in range(len(commandexecutor.locMap)):
                #    location[item] = commandexecutor.locMap[item]
                #    print "latitude and longitude : ", location[item]

            if commandexecutor.cmd =='getSensor':
                temperature = dronedata.getTemperature()
                humidity = dronedata.getHumidity()
                sensor1 = dronedata.getSonar1_ObsDistance()
                sensor2 = dronedata.getSonar2_ObsDistance()
                sensor3 = dronedata.getSonar3_ObsDistance()
                sensor4 = dronedata.getSonar4_ObsDistance()
                dicData = ["SENSOR": {"SENSOR1": str(sensor1), "SENSOR2": str(sensor2),
                                      "SENSOR3": str(sensor3), "SENSOR4": str(sensor4),
                                      "HUMIDITY": str((humidity), "TEMPERATURE": str(temperature)}]
                print "Sending sensor data....\r\n"
                client.sendall(json.dumps(dicData))# Send data  out in json format
                print(json.dumps(dicData))
                print "Size of transmitted Jason array : ", len(dicData),"\r\n"

            if commandexecutor.cmd == 'connect':
                dicData = {"REPLY": "Connection established"}
                client.sendall(json.dumps(dicData))# Send data  out in json format
                print(json.dumps(dicData))
                print "Size of transmitted Jason array : ", len(dicData),"\r\n"

            #take picture and send it back to app    
            if commandexecutor.cmd == 'picture':
                camera = picamera.PiCamera()
                camera.resolution = (1920, 1080)
                camera.capture(r'/home/pi/image.jpg')
                time.sleep(2.0)
                camera.close()
                with open(r"/home/pi/image.jpg", "rb") as imageFile:
                    string = base64.b64encode(imageFile.read())
                    client.sendall(string)

            #Send drone data out when 'getLocation' command is received
            if commandexecutor.cmd =='getLocation':
                
                datetime = timestamp.getTimeStamp()
                temperature = dronedata.getTemperature()
                humidity = dronedata.getHumidity()
                altitude = 5 # dronedata.getAltitude()
                latitude = 1.289584036003337 #dronedata.getLatitude()
                longitude = 103.77684272825718 #dronedata.getLongitude()
                
                dicData = {"DRONE_GPS":{"datetime": str(datetime) , "temperature":  str(temperature) ,
                                   "humidity": str(humidity),"altitude": str(altitude) , "location": [str(latitude),str(longitude)]}}

                print "Sending location data....\r\n"
                client.sendall(json.dumps(dicData))# Send data  out in json format
                print(json.dumps(dicData))
                print "Size of transmitted Jason array : ", len(dicData),"\r\n"



##            if commandexecutor.cmd =='goWayPoint':
##                commandexecutor.cmd = parsed_json["ACTION"]
##                print "Parsed WapPoint CMD : ",commandexecutor.cmd
##                commandexecutor.locMap = parsed_json["DRONE_MARKERS"]
##                for item in range(len(commandexecutor.locMap)):
##                    location[item] = commandexecutor.locMap[item]
##                    print "Way Points (latitude and longitude) : ", location[item]   
                
            
            client.close()  # Close the connection

        
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
            
            #json_string ={"humidity": "23" , "altitude": "10"}
            #parsed_json = json.loads(json_string)
            #print (parsed_json['humidity'])

            #parsed_json = json.loads('[{"Front": 1, "Right": 4, "Rear": 2, "Left": 3}]')
            #print (parsed_json)
            ########################
            #  Data read here!
            ########################


            client.sendall(json.dumps(dicData))# Send data  out in json format
            print(json.dumps(dicData))

            req = client.recv(1024)
            print req

            body = req.split('\r\n\r\n',1)[1]
            parsed_json = json.loads(body)
            print "Size of received Jason  array : ", len(parsed_json)
            print "Parsed JSON : " ,parsed_json["ACTION"]
            
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
        client.close()
        self.soc.close
        self.soc.shutdown(socket.SHUT_RDWR)
        
