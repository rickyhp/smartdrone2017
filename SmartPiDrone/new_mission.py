######################################################################################################
## File created on 31-03-2016 to be used in creating waypoint mission file for Pixhawk FC			##
## By Naitik Shukla fir ISS-Drone																	##
## Sample file taken from http://qgroundcontrol.org/mavlink/waypoint_protocol#waypoint_file_format	##
## MAV_CMD_NAV_WAYPOINT																				##
######################################################################################################
from os.path import abspath, dirname, join

def createFile(maps,altitudeRel, altitudeAbs):
	version = '110'# version of firmware currently in use
	print join(dirname(abspath(__file__)), 'mission.txt')
	missionFile = join(dirname(abspath(__file__)), 'mission.txt')
	#missionFile = 'C:\\Users\\naiti\\Desktop\\mission.txt'	# name of mission file and path which will be sent to pixhawk
	f = open(missionFile,"w")						#open a file in write mode
	f.write('QGC\tWPL\t%s\n' % version)				# write first line for waypoint file
	listLen = len(maps)							# getting length of waypoints gather in list
	
	# Loop for all point which will be print in each line
	for i in range(listLen):
		#pdb.set_trace()
		lat, lng = maps[i]
		if i == 0:																		# First line should be start position hence param 1 should be 1 (arm)
			f.write('%d\t1\t0\t16\t0.14999999999999994\t0\t0\t0\t%.17f\t%.17f\t%d\t1\n' % (i, lng, lat, int(round(float(altitudeAbs)))))
		elif i == 1:
			f.write('%d\t0\t0\t16\t0.14999999999999994\t0\t0\t0\t%.17f\t%.17f\t%d\t1\n' % (i,lng, lat, int(round(float(altitudeAbs)))+int(altitudeRel[i-1])))		# Takeoff
		else:
			f.write('%d\t0\t0\t16\t0.14999999999999994\t0\t0\t0\t%.17f\t%.17f\t%d\t1\n' % (i,lng, lat, int(altitudeRel[i-1])))					# Fly
	f.close()
	# close the file after write completes

if __name__ == "__main__":
	try:
		maps =[(123.12222,234.2323232),(123.12222,234.2323232),(23423.231212,1213.1213432),(54545.23434234,23423.23432424)] #[dronepoisiton, droneposition, markerpoints...]
		altitudeRel = ["10", "15", "20"]
		#altitudeAbs = ["30.323232","30.21212121","24.12312123","40.1212121"]
		altitudeAbs = "30.323232"
		createFile(maps,altitudeRel, altitudeAbs)
	except KeyboardInterrupt:
		pass
