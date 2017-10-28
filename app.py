# -*- coding: utf-8 -*-

from flask import Flask
from flask import render_template,request, jsonify
from tornado.wsgi import WSGIContainer
from tornado.httpserver import HTTPServer
from tornado.ioloop import IOLoop

#from chatterbot import ChatBot
import chatbot

#import trainer

import os
from json import dumps




app = Flask(__name__)

chatbot.train()



@app.route("/test")
def main():
	return "test complete!"

@app.route("/speech", methods = ['POST'])
def handleSpeech():

	
	json = request.json
	text = json.get('speech')
	isUser = json.get('isUser')
	

	#print(p);
	if isUser == "true":
		print "user response: " + text
		
		#trainer.main(text)
		return jsonify({})
	else:
		
		print "friend said: " + text
		# Get a response to the input text 
		response = chatbot.getResponse(text)
		
		#trainer.raw_text = response
		#execfile("trainer.py")
		# LSTM.raw_text = text // LSTM.main()<< could do this to use the neural net in the future

		recc = response.serialize()

		print "reccomend: " + recc['text']
		return jsonify({'recc': recc['text']})



'''
def set_interval(func,sec):
	print 'start interval'

	def wrapper_func():
		set_interval(func, sec)
		func()
	t = threading.Timer(sec, wrapper_func)
	t.start()
	return t

def keep_app_alive():
	requests.get('http://tipofthetongue.herokuapp.com/')

'''


if __name__ == '__main__':
	port = int(os.environ.get("PORT", 5000))
	http_server = HTTPServer(WSGIContainer(app))
	print "listening on port " + str(port)
	http_server.listen(port)
	IOLoop.instance().start()

	