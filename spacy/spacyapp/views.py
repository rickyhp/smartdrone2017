from django.http import HttpResponse
from django.http import JsonResponse
from django.views.decorators.csrf import csrf_exempt
import spacy
import json

en_nlp = spacy.load('en')
commands = "start stop left right up down"

@csrf_exempt
def postagger(request):
    if request.method == 'POST':
        json_data = json.loads(request.body) #{"speech" : "text"}
        print('Raw Data: ', request.body) 
        try:
            message = json_data['speech']
            print('message : ', message)
        except KeyError:
            HttpResponseServerError("Malformed data")
    
    en_doc = en_nlp(message)
    
    resp={}

    for token in en_doc:
        resp[token.text] = token.pos_
           
    return JsonResponse(resp) #{"drone" : "NOUN"}

@csrf_exempt
def similarity(request):
    if request.method == 'POST':
        json_data = json.loads(request.body)
        print('Raw Data: ', request.body) 
        try:
            message = json_data['speech']
        except KeyError:
            HttpResponseServerError("Malformed data")
    elif request.method == 'GET':
        message = request.GET.get("s")
        
    en_doc = en_nlp(message) # text from client
    cmd = en_nlp(commands)   # list of pre-defined commands
    resp={}
    prob=[]
    
    for token in en_doc:
        if(token.pos_ == 'VERB'):
                for cmdtoken in cmd:
                        prob.append(token.similarity(cmdtoken))
    
    listCommands = commands.split( )
    bubbleSort(prob, listCommands)
    
    print("prob : ", prob)
    print("cmd : ", listCommands[len(listCommands)-1])
    
    resp["command"] = listCommands[len(listCommands)-1]
    resp["p"] = prob[len(prob)-1]
    
    if(len(resp)==0):
        resp = {"result" : "Sorry, I dont understand it"}
    
    return JsonResponse(resp)

def bubbleSort(alist,cmd):
    for passnum in range(len(alist)-1,0,-1):
        for i in range(passnum):
            if alist[i]>alist[i+1]:
                temp = alist[i]
                tempcmd = cmd[i]
                alist[i] = alist[i+1]
                cmd[i] = cmd[i+1]
                alist[i+1] = temp
                cmd[i+1] = tempcmd