#!/usr/bin/python
import sys
import csv
from socket import *

def create_port(arg):
	global Port
	global serverSocket
	Port = arg
	if(not Port):
		return -1
	else:
		serverSocket = socket(AF_INET, SOCK_DGRAM)
		if (not serverSocket):
			sys.exit('create_socket() failed')
		return 1
		
def load_csv():
	global ID
	global first_name
	global last_name
	global quality_points
	global gpa_hours
	global gpa
	ID, first_name, last_name, quality_points, gpa_hours, gpa = [], [] ,[] , [] , [] , [] 
	with open('data.csv') as csvfile:
		reader = csv.DictReader(csvfile)
		for row in reader:
			ID.append(row['ID'])
			first_name.append(row['first_name'])
			last_name.append(row['last_name'])
			quality_points.append(row['quality_points'])
			gpa_hours.append(row['gpa_hours'])
			gpa.append(row['gpa'])
	return
	
def query_data(arg):
	global ID
	global first_name
	global last_name
	global quality_points
	global gpa_hours
	global gpa
	parsed_arg = arg.split(',')
	message = 'From server: ' 
	if(len(parsed_arg)==1):
		return 'You did not query any data'
	else:
		ID_arg = parsed_arg[0]
		try:
			ID_index = ID.index(ID_arg)
		except ValueError:
			return 'No ID found'
		for i in range(1,len(parsed_arg)):
			message = message + parsed_arg[i] + ': '
			if(parsed_arg[i]=='first_name'):
				parsed_arg[i] = first_name[ID_index]
			elif(parsed_arg[i]=='last_name'):
				parsed_arg[i] = last_name[ID_index]
			elif(parsed_arg[i]=='quality_points'):
				parsed_arg[i] = quality_points[ID_index]
			elif(parsed_arg[i]=='gpa_hours'):
				parsed_arg[i] = gpa_hours[ID_index]
			elif(parsed_arg[i]=='gpa'):
				parsed_arg[i] = gpa[ID_index]
			else:
				parsed_arg[i] = 'invalid'
			message = message + parsed_arg[i] + ', '
	return message[0:-2]
	
	
load_csv()	
if (len(sys.argv)==1):
	sys.exit('You do not specify a PORT number')
elif (len(sys.argv)==2):
	if(create_port(sys.argv[1])==-1):
		print 'Wrong formant, please follow PORT'
	serverSocket.bind(('', int(Port)))
	print 'The server is ready to receive'
	while 1:
		message, clientAddress = serverSocket.recvfrom(2048)
		message = message
		message = query_data(message)
		sent_bytes = serverSocket.sendto(message, clientAddress)
		if(sent_bytes<0):
			sys.exit('send failed')
else:
		sys.exit('Too many arguments')