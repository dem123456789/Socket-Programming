#!/usr/bin/python
import sys
import errno
from socket import *



def create_port(arg):
	global Host
	global Port
	global clientSocket
	parsed_arg = arg.split(':')
	if(len(parsed_arg)<2):
		return -1
	Host = parsed_arg[0]
	Port = parsed_arg[1]
	if(not Host or not Port):
		return -1
	else:
		clientSocket = socket(AF_INET, SOCK_STREAM)
		if (not clientSocket):
			sys.exit('create_socket() failed')
		try:
			clientSocket.connect((Host, int(Port)))
		except error as e:
			if e.errno == errno.ECONNREFUSED:
				sys.exit('Connection Refused')
		return 1
	


def send_message(arg):
	global Host
	global Port
	global clientSocket
	
	message = ','.join(arg)
	print "sent message:", message
	sent_bytes = clientSocket.send(message)
	if(sent_bytes<0):
		sys.exit('send failed')
	modifiedMessage = clientSocket.recv(2048)
	return modifiedMessage

if (len(sys.argv)==1):
	sys.exit('You do not specify a HOST:PORT number')
else:
	if(create_port(sys.argv[1])==-1):
		print 'Wrong formant, please follow HOST:PORT'
	else:
		print 'Host name:',Host,'\nPort number:',Port
	if(len(sys.argv)==2):
		sys.exit('No other arguments, only socket created')
	else:	
		argument = sys.argv[2:]
		return_message = send_message(argument)
		print(return_message)
		clientSocket.close()



