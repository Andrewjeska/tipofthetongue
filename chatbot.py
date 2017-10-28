# -*- coding: utf-8 -*-

from chatterbot import ChatBot
from chatterbot.trainers import ChatterBotCorpusTrainer
import os

chatbot = ChatBot('Charlie',

	storage_adapter='chatterbot.storage.MongoDatabaseAdapter',
  database='heroku_0zzgv0cs',
  database_uri=os.environ['MONGODB_URI'])

chatbot.set_trainer(ChatterBotCorpusTrainer)



# Train based on the english corpus
	
def train():
	print "training!"

	chatbot.train(
    "chatterbot.corpus.english.greetings",
    "chatterbot.corpus.english.conversations"
	)

	print "done training!"


# Get a response to an input statement
#print(chatbot.get_response("Hey, I hate you!"))

def getResponse(text):
	return chatbot.get_response(text)

