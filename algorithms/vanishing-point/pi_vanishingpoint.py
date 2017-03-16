import os
import time
from picamera.array import PiRGBArray
from picamera import PiCamera
from VanishingPoint import *

# rasp pi
# initialize rasp pi camera and grab a reference to the raw camera capture
camera = PiCamera()
camera.resolution = (640, 480)
camera.framerate = 32
rawCapture = PiRGBArray(camera, size=(640, 480))

# laptop camera
#video_capture = cv2.VideoCapture(0)

# allow the camera to warmup
time.sleep(0.1)

kernel = np.ones((15, 15), np.uint8)

# rasp pi
for frame in camera.capture_continuous(rawCapture, format="bgr", use_video_port=True):
    img = frame.array
#while True:
#    if not video_capture.isOpened():
#        print('Unable to load camera.')
#        sleep(5)
#        pass
    
# Capture frame-by-frame
#    ret, img = video_capture.read()
    
    gray = cv2.cvtColor(img, cv2.COLOR_BGR2GRAY)  # Convert image to grayscale
    kernel = np.ones((15, 15), np.uint8)
    opening = cv2.morphologyEx(gray, cv2.MORPH_OPEN, kernel)  # Open (erode, then dilate)
    edges = cv2.Canny(opening, 50, 150, apertureSize=3)  # Canny edge detection
    lines = cv2.HoughLines(edges, 1, np.pi / 180, 200)  # Hough line detection
    hough_lines = []

    # Lines are represented by rho, theta; convert to endpoint notation
    if lines != None:
        for line in lines:
            for rho, theta in line:
                a = np.cos(theta)
                b = np.sin(theta)
                x0 = a * rho
                y0 = b * rho
                x1 = int(x0 + 1000 * (-b))
                y1 = int(y0 + 1000 * (a))
                x2 = int(x0 - 1000 * (-b))
                y2 = int(y0 - 1000 * (a))

                cv2.line(img, (x1, y1), (x2, y2), (0, 0, 255), 2)
                hough_lines.append(((x1, y1), (x2, y2)))

    cv2.imshow("Frame", img)
                
    if cv2.waitKey(1) & 0xFF == ord('q'):
        break
    
    # clear the stream in preparation for the next frame
    rawCapture.truncate(0)

#video_capture.release()
# When everything is done, release the capture
cv2.destroyAllWindows()
