var SpeechRecognition = SpeechRecognition || webkitSpeechRecognition
var SpeechGrammarList = SpeechGrammarList || webkitSpeechGrammarList
var SpeechRecognitionEvent = SpeechRecognitionEvent || webkitSpeechRecognitionEvent

var validcommands = [ 'arm' , 'disarm', 'start' , 'stop', 'land', 'hold', 'roll left' , 'roll right', 'turn left' ,'turn right', 'up' , 'down' ,'temperature', 'altitude', 'gps', 'thermal', 'picture' , 'video'];
var grammar = '#JSGF V1.0; grammar commands; public <commands> = ' + validcommands.join(' | ') + ' ;'

var recognition = new SpeechRecognition();
var speechRecognitionList = new SpeechGrammarList();
speechRecognitionList.addFromString(grammar, 1);
recognition.grammars = speechRecognitionList;
//recognition.continuous = false;
recognition.lang = 'en-US';
recognition.interimResults = false;
recognition.maxAlternatives = 1;

var diagnostic = document.querySelector('.output');
var response = document.querySelector('.response');
var bg = document.querySelector('html');
var hints = document.querySelector('.hints');

hints.innerHTML = 'Tap/click then talk to auto drone. Try altitude.';

function reqListener () {
  console.log("Your extracted voice command : " + this.responseText);
  var extractedCommand = JSON.parse(this.responseText);
  diagnostic.textContent = "Your extracted voice command : " + extractedCommand.command;
  callDrone(extractedCommand.command);
}


document.body.onclick = function() {
  recognition.start();
  console.log('Ready to receive command.');
}

// droneserver communication
var ws;
 
function initDroneSocket() {
    ws = new WebSocket("ws://localhost:8080/websocket");
    
    ws.onmessage = function(e) {
        response.textContent = e.data;
    };
}

function callDrone(msg) {
    ws.send(msg);
}

function extractCommand(msg)
{
  var oReq = new XMLHttpRequest();
  oReq.addEventListener("load", reqListener);
  oReq.open("GET", "http://localhost:8000/spacyapp/similarity/?s=" + msg);
  oReq.setRequestHeader("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8");
  oReq.send();
}

recognition.onresult = function(event) {
  // The SpeechRecognitionEvent results property returns a SpeechRecognitionResultList object
  // The SpeechRecognitionResultList object contains SpeechRecognitionResult objects.
  // It has a getter so it can be accessed like an array
  // The [last] returns the SpeechRecognitionResult at the last position.
  // Each SpeechRecognitionResult object contains SpeechRecognitionAlternative objects that contain individual results.
  // These also have getters so they can be accessed like arrays.
  // The [0] returns the SpeechRecognitionAlternative at position 0.
  // We then return the transcript property of the SpeechRecognitionAlternative object

  var last = event.results.length - 1;
  var voicecommand = event.results[last][0].transcript; 
  console.log("Complete voice command : " + voicecommand);
  extractCommand(voicecommand);
  
  console.log('Confidence: ' + event.results[0][0].confidence);
}

recognition.onspeechend = function() {
  recognition.stop();
}

recognition.onnomatch = function(event) {
  diagnostic.textContent = "I didn't recognise that command.";
}

recognition.onerror = function(event) {
  diagnostic.textContent = 'Error occurred in recognition: ' + event.error;
}