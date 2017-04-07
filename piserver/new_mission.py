######################################################################################################
## File created on 31-03-2016 to be used in creating waypoint mission file for Pixhawk FC			##
## By Naitik Shukla fir ISS-Drone																	##
## Sample file taken from http://qgroundcontrol.org/mavlink/waypoint_protocol#waypoint_file_format	##
## MAV_CMD_NAV_WAYPOINT																				##
######################################################################################################
from os.path import abspath
import pdb

def createFile(maps,altitudeRel, altitudeAbs):
	version = '110'									# version of firmware currently in use
	missionFile = abspath("mission.txt")
	#missionFile = 'C:\\Users\\naiti\\Desktop\\mission.txt'	# name of mission file and path which will be sent to pixhawk
	f = open(missionFile,"w")						#open a file in write mode
	f.write('QGC\tWPL\t%s\n' % version)				# write first line for waypoint file
	listLen = len(maps)							# getting length of waypoints gather in list
	
	# Loop for all point which will be print in each line
	for i in range(listLen):
		#pdb.set_trace()
		mapi = maps[i]				#take 1st string from List input
		coma = mapi.find(',')		# Find separator , between lat and long
		totlen = len(mapi)-1
		lat = float(mapi[1:coma])	# lat is between 1st and coma position
		lng = float(mapi[coma+1:totlen])	# long is between coma+1 and last-1 position
		if i == 0:																		# First line should be start position hence param 1 should be 1 (arm)
			f.write('%d\t1\t0\t16\t0.14999999999999994\t0\t0\t0\t%.17f\t%.17f\t%d\t1\n' % (i, lng, lat, altitudeAbs))
		elif i == 1:
			f.write('%d\t0\t0\t16\t0.14999999999999994\t0\t0\t0\t%.17f\t%.17f\t%d\t1\n' % (i,lng, lat, (altitudeAbs+altitudeRel)))		# Takeoff
		else:
			f.write('%d\t0\t0\t16\t0.14999999999999994\t0\t0\t0\t%.17f\t%.17f\t%d\t1\n' % (i,lng, lat, altitudeRel))					# Fly
	f.close()										# close the file after write completes

if __name__ == "__main__":
	try:
		map =["(123.12222,234.2323232)","(23423.231212,1213.1213432)","(54545.23434234,23423.23432424)"]
		altitudeRel = 10
		altitudeAbs = 30
		createFile(map,altitudeRel, altitudeAbs)
	except KeyboardInterrupt:
		pass
