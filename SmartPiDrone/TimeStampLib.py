#####################################
#   File Name : TimeStamp.py   
#   Author      : NayLA  
#   Date         : 02/06/2016
#####################################

import datetime

class  TimeStamp:

    DateTime = 0
    year = 0
    month = 0
    date = 0
    hr = 0
    mm = 0
    sec = 0


    def __init__(self):
        self.DateTime = datetime.datetime.strftime(datetime.datetime.now(),' %Y-%m-%d %H:%M:%S')
        year = (datetime.datetime.strftime(datetime.datetime.now(),'%Y'))
        month = (datetime.datetime.strftime(datetime.datetime.now(),'%M'))
        date = (datetime.datetime.strftime(datetime.datetime.now(),'%D'))
        hr = (datetime.datetime.strftime(datetime.datetime.now(),'%H'))
        mm = (datetime.datetime.strftime(datetime.datetime.now(),'%M'))
        sec = (datetime.datetime.strftime(datetime.datetime.now(),'%S'))

    def getTimeStamp(self):
        self.DateTime = datetime.datetime.strftime(datetime.datetime.now(),'%Y-%m-%d %H:%M:%S')
        return self.DateTime

    def  getYear(self):
        self.year = (datetime.datetime.strftime(datetime.datetime.now(),'%Y'))
        return self.year

    def  getMonth(self):
        self.month = (datetime.datetime.strftime(datetime.datetime.now(),'%M'))
        return self.month

    def  getDate(self):
        self.date = (datetime.datetime.strftime(datetime.datetime.now(),'%D'))
        return self.date

    def  getHour(self):
        self.hr = (datetime.datetime.strftime(datetime.datetime.now(),'%H'))
        return self.hr

    def  getMinute(self):
        self.mm = (datetime.datetime.strftime(datetime.datetime.now(),'%M'))
        return self.mm

    def  getSecond(self):
        self.sec = (datetime.datetime.strftime(datetime.datetime.now(),'%S'))
        return self.sec
