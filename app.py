from flask import Flask
from flask import render_template,request, jsonify
from tornado.wsgi import WSGIContainer
from tornado.httpserver import HTTPServer
from tornado.ioloop import IOLoop
import io
import trainer
import os

app = Flask(__name__)

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
		trainer.raw_text = text
		execfile("trainer.py")
		return jsonify({})
	else:
		LSTM.raw_text = text
		execfile("LSTM.py")
		recc = "Hi, My name is Michael"

		return jsonify({'recc': recc})





if __name__ == '__main__':
    port = int(os.environ.get("PORT", 5000))
    http_server = HTTPServer(WSGIContainer(app))
    http_server.listen(port)
    IOLoop.instance().start()
