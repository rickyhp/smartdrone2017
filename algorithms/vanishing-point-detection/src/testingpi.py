import os
import time
from picamera.array import PiRGBArray
from picamera import PiCamera
from VanishingPoint import *

# initialize the camera and grab a reference to the raw camera capture
camera = PiCamera()
camera.resolution = (640, 480)
camera.framerate = 32
rawCapture = PiRGBArray(camera, size=(640, 480))

# allow the camera to warmup
time.sleep(0.1)

kernel = np.ones((15, 15), np.uint8)

for frame in camera.capture_continuous(rawCapture, format="bgr", use_video_port=True):
    image = frame.array

    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)
    opening = cv2.morphologyEx(gray, cv2.MORPH_OPEN, kernel)  # Open (erode, then dilate)
    edges = cv2.Canny(opening, 50, 150, apertureSize=3)  # Canny edge detection
    
    cv2.imshow("Frame", edges)
                
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
    
    # clear the stream in preparation for the next frame
    rawCapture.truncate(0)

# When everything is done, release the capture
cv2.destroyAllWindows()
