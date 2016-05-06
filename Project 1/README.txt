Name: Enmao Diao
GTid: ediao3
Email emdiao@gatech.edu
CS3251 Fall 2016
Programming Assignment 1 Description Database Query
Feb 11th

1. My code is written in Python and Java and is only compatible with Python 2 and Java 8.
2. To run the code, simply open a terminal and cd to the folder
3. To run python code, type in the command following this format
python dbengineTCP.py [PORT]
python dbclientTCP.py [HOST:PORT] [ID] [first_name] ...

To run java code, type in the command following this format
Compile:
javac dbengineTCP.java
javac dbclientTCP.java
Run:
java dbengineTCP [PORT]
java dbclientTCP [HOST:PORT] [ID] [first_name] ...


I did not figure out to connect server and client programmed with both Java and Python together.
At first I use Python 3 and this causes me a lot of trouble because socket.send() in python need to encode
str into utf-8 format since in Python 3 str is not considered as low-level data types.
The problem is Java write a different kind of UTF format called modified UTF which has two binary at the beginning
indicating the length of array. So this is not compatible with Python or standard UTF-8 format.
When I finally gave up and switch to Python 2 it is a little late.
I still do not figure out a way to connect readBytes() and writeBytes() in java with send() and recv() in python.


A sample of running the code is shown in sample.txt

Mostly input cases are handled.
If server is not up, when the client send messages, it will handle as Connection Rest or Connection Refused Error.
To test the timeout scheme of UDP simply comment out these few lines at the end of dbengineUDP.py

		sent_bytes = serverSocket.sendto(message, clientAddress)
		if(sent_bytes<0):
			sys.exit('send failed')

A sample output for timeout is also shown in sample.txt
A known bug is also included in sample.txt under TCP Java section.
The bug is when I do not specify any arguments after the HOST:PORT number,
not only the clien shut down but also my server.
This is because I have to handle a Connection Rest Exception at server side
and this causes the server to shut down.


FILES INCLUDED

ediao3/
	dbclientTCP.py
	dbengineTCP.py
	dbclientUDP.py
	dbengineUDP.py
	dbclientTCP.java
	dbengineTCP.java
	README.txt
	sample.txt
	data.csv
	
	
	
	