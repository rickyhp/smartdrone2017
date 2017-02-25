# AUTO-GENERATED CODE: DO NOT EDIT!!!


import struct
import sys

def _CRC8(data):

    crc = 0x00
   
    for c in data:

        crc ^= ord(c)

    return crc

def _bytes(s):

    return bytes(s) if sys.version[0] == '2' else bytes(s, "utf-8")

class MSP_Parser(object):

    def __init__(self):

        self.state = 0

    def parse(self, char):
        '''
        Parses one character, triggering pre-set handlers upon a successful parse.
        '''

        byte = ord(char)

        if self.state ==  0: # sync char 1
            if byte == 36: # $
                self.state += 1

        elif self.state ==  1: # sync char 2
            if byte == 77: # M
                self.state += 1
            else: # restart and try again
                self.state = 0

        elif self.state ==  2: # direction
            if byte == 62:  # >
                self.message_direction = 1
            else: # <
                self.message_direction = 0
            self.state += 1
            
        elif self.state ==  3:
            self.message_length_expected = byte
            self.message_checksum = byte
            # setup arraybuffer
            self.message_buffer = b''
            self.state += 1

        elif self.state ==  4:
            self.message_id = byte
            self.message_length_received  = 0
            self.message_checksum ^= byte
            if self.message_length_expected > 0:
                # process payload
                self.state += 1
            else:
                # no payload
                self.state += 2

        elif self.state ==  5: # payload
            self.message_buffer += char
            self.message_checksum ^= byte
            self.message_length_received += 1
            if self.message_length_received >= self.message_length_expected:
                self.state += 1

        elif self.state ==  6:
            if self.message_checksum == byte:
                # message received, process

                if self.message_id == 105:

                    if self.message_direction == 0:

                        if hasattr(self, 'RC_Request_Handler'):

                            self.RC_Request_Handler()

                    else:

                        if hasattr(self, 'RC_Handler'):

                            self.RC_Handler(*struct.unpack('=hhhhhhhh', self.message_buffer))

                if self.message_id == 108:

                    if self.message_direction == 0:

                        if hasattr(self, 'ATTITUDE_Request_Handler'):

                            self.ATTITUDE_Request_Handler()

                    else:

                        if hasattr(self, 'ATTITUDE_Handler'):

                            self.ATTITUDE_Handler(*struct.unpack('=hhh', self.message_buffer))

                if self.message_id == 109:

                    if self.message_direction == 0:

                        if hasattr(self, 'ALTITUDE_Request_Handler'):

                            self.ALTITUDE_Request_Handler()

                    else:

                        if hasattr(self, 'ALTITUDE_Handler'):

                            self.ALTITUDE_Handler(*struct.unpack('=ih', self.message_buffer))

                if self.message_id == 127:

                    if self.message_direction == 0:

                        if hasattr(self, 'SONARS_Request_Handler'):

                            self.SONARS_Request_Handler()

                    else:

                        if hasattr(self, 'SONARS_Handler'):

                            self.SONARS_Handler(*struct.unpack('=hhhh', self.message_buffer))

            else:
                print('code: ' + str(self.message_id) + ' - crc failed')
            # Reset variables
            self.message_length_received = 0
            self.state = 0

        else:
            print('Unknown state detected: %d' % self.state)



    def set_RC_Handler(self, handler):

        '''
        Sets the handler method for when a RC message is successfully parsed.
        You should declare this message with the following parameter(s):
            c1,c2,c3,c4,c5,c6,c7,c8
        '''
        self.RC_Handler = handler

    def set_ATTITUDE_Handler(self, handler):

        '''
        Sets the handler method for when a ATTITUDE message is successfully parsed.
        You should declare this message with the following parameter(s):
            roll,pitch,yaw
        '''
        self.ATTITUDE_Handler = handler

    def set_ALTITUDE_Handler(self, handler):

        '''
        Sets the handler method for when a ALTITUDE message is successfully parsed.
        You should declare this message with the following parameter(s):
            altitude,vario
        '''
        self.ALTITUDE_Handler = handler

    def set_SONARS_Handler(self, handler):

        '''
        Sets the handler method for when a SONARS message is successfully parsed.
        You should declare this message with the following parameter(s):
            back,front,left,right
        '''
        self.SONARS_Handler = handler

def serialize_RC(c1, c2, c3, c4, c5, c6, c7, c8):

    '''
    Serializes the contents of a message of type RC.
    '''
    message_buffer = struct.pack('hhhhhhhh', c1, c2, c3, c4, c5, c6, c7, c8)

    msg = chr(len(message_buffer)) + chr(105) + str(message_buffer)

    return _bytes('$M>' + msg + chr(_CRC8(msg)))

def serialize_RC_Request():

    '''
    Serializes a request for RC data.
    '''
    return _bytes('$M<' + chr(0) + chr(105) + chr(105))

def serialize_ATTITUDE(roll, pitch, yaw):

    '''
    Serializes the contents of a message of type ATTITUDE.
    '''
    message_buffer = struct.pack('hhh', roll, pitch, yaw)

    msg = chr(len(message_buffer)) + chr(108) + str(message_buffer)

    return _bytes('$M>' + msg + chr(_CRC8(msg)))

def serialize_ATTITUDE_Request():

    '''
    Serializes a request for ATTITUDE data.
    '''
    return _bytes('$M<' + chr(0) + chr(108) + chr(108))

def serialize_ALTITUDE(altitude, vario):

    '''
    Serializes the contents of a message of type ALTITUDE.
    '''
    message_buffer = struct.pack('ih', altitude, vario)

    msg = chr(len(message_buffer)) + chr(109) + str(message_buffer)

    return _bytes('$M>' + msg + chr(_CRC8(msg)))

def serialize_ALTITUDE_Request():

    '''
    Serializes a request for ALTITUDE data.
    '''
    return _bytes('$M<' + chr(0) + chr(109) + chr(109))

def serialize_SONARS(back, front, left, right):

    '''
    Serializes the contents of a message of type SONARS.
    '''
    message_buffer = struct.pack('hhhh', back, front, left, right)

    msg = chr(len(message_buffer)) + chr(127) + str(message_buffer)

    return _bytes('$M>' + msg + chr(_CRC8(msg)))

def serialize_SONARS_Request():

    '''
    Serializes a request for SONARS data.
    '''
    return _bytes('$M<' + chr(0) + chr(127) + chr(127))

def serialize_SET_RAW_RC(c1, c2, c3, c4, c5, c6, c7, c8):

    '''
    Serializes the contents of a message of type SET_RAW_RC.
    '''
    message_buffer = struct.pack('hhhhhhhh', c1, c2, c3, c4, c5, c6, c7, c8)

    msg = chr(len(message_buffer)) + chr(200) + str(message_buffer)

    return _bytes('$M<' + msg + chr(_CRC8(msg)))

def serialize_SET_HEAD(head):

    '''
    Serializes the contents of a message of type SET_HEAD.
    '''
    message_buffer = struct.pack('h', head)

    msg = chr(len(message_buffer)) + chr(205) + str(message_buffer)

    return _bytes('$M<' + msg + chr(_CRC8(msg)))

def serialize_SET_MOTOR(m1, m2, m3, m4):

    '''
    Serializes the contents of a message of type SET_MOTOR.
    '''
    message_buffer = struct.pack('hhhh', m1, m2, m3, m4)

    msg = chr(len(message_buffer)) + chr(214) + str(message_buffer)

    return _bytes('$M<' + msg + chr(_CRC8(msg)))

