#!/usr/bin/python
import sys
import select
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
		clientSocket = socket(AF_INET, SOCK_DGRAM)
		clientSocket.setblocking(0)
		if (not clientSocket):
			sys.exit('create_socket() failed')
		return 1

def send_message(arg):
	global Host
	global Port
	global clientSocket
	
	timeout_in_seconds = 2
	timeout_trial = 4
	message = ','.join(arg)
	print "sent message:", message
	for i in range(timeout_trial):
		sent_bytes = clientSocket.sendto(message,(Host, int(Port)))
		if(sent_bytes<0):
			sys.exit('send failed')
		ready = select.select([clientSocket], [], [], timeout_in_seconds)
		if ready[0]:
			try:
				modifiedMessage, serverAddress = clientSocket.recvfrom(2048)
			except error as e:
				if e.errno == errno.ECONNRESET:
					sys.exit('Connection Reset')	
			break
		else:
			if(i == timeout_trial-1):
				sys.exit('Timeout after 4 trials')
			print 'The server has not answered in the last two seconds.'
			print 'retrying...'
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



