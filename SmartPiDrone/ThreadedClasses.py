#####################################
#   File Name : Server.py   
#   Author      : NayLA  
#   Date         : 11/03/2017
#####################################


from threading import Thread
import time



global cycle
cycle = 0.0

############################################
class DummyThread:  
    def __init__(self):
        self._running = True

    def terminate(self):  
        self._running = False  

    def run(self):
        global cycle
        while self._running:
            print("Dummy Thread running....")
            time.sleep(param)

############################################

class ReceiveControlCmdThread: 
    def __init__(self):
        self._running = True

    def terminate(self):  
        self._running = False  

    def run(self):
        global cycle
        while self._running:
           print("Receiving Control command from App....")

############################################

class SensorThread: 
    def __init__(self):
        self._running = True

    def terminate(self):  
        self._running = False  

    def run(self):
        global cycle
        while self._running:
            print("Getting sensors' data....")
            print("Left Sonar Sensor :  %s" %sensor_data.getLeftObstacleDistance())
