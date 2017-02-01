from django.http import HttpResponse
import spacy

en_nlp = spacy.load('en')
commands = "start"

def postagger(request):
    message = request.GET.get('s')
    en_doc = en_nlp(message)
    resp=""
    for token in en_doc:
        resp = resp + token.text + ' ' + token.pos_ + '<br>'
    
    return HttpResponse(resp)

def similarity(request):
    message = request.GET.get('s')
    en_doc = en_nlp(message) # text from client
    cmd = en_nlp(commands)   # list of pre-defined commands
    resp=""
    for token in en_doc:
        if(token.pos_ == 'VERB'):
            resp = resp + token.text + ' ' + token.pos_ + ' ' + str(token.similarity(cmd[0])) + '<br>'
            
    if(resp==""):
        resp = "Sorry, I dont understand it"
    
    return HttpResponse(resp)