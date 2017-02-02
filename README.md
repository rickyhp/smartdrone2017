# smartdrone2017

This is repository for our Independent Work - Autonomous Drone, 2017

1) spacyprj, spacyapp, db.sqlite3, manage.py are files created using django framework (python3 based).
You can setup python3 virtual environment in your system and install python3 + django web framework + spacy for text processing

What is spacyapp ?

The idea is to build web services for drone to understand natural English language.
Our smartphone apps receive user instructions from microphone, convert it to text via google cloud speech api and pass this text
to spacyapp. Spacyapp will process and return exact command text (pre-defined in our system e.g. stop).

For example:
User says "Drone, please stop now"
Spacy returns "stop" and send to our Rasp PI server side code and call respective multiwii codes to stop the rotors.

How to run spacyapp ?

execute: python manage.py runserver

call HTTP GET http://localhost:8000/spacyapp/similarity/?s=drone%20stop%20now

return stop


The key here is user must says at least one VERB that is having 'highest similarity or equals to' pre-defined commands in system.

stop VERB 0.649615358913 = start

stop VERB 1.00000000366 = stop

stop VERB 0.410976274631 = left

stop VERB 0.610306594989 = right

stop VERB 0.56040019562 = up

stop VERB 0.574057983567 = down