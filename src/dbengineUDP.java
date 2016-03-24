import java.io.*; 
import java.net.*;

public class dbengineUDP {
	public static void main(String arg[]) throws Exception
	{
		DatagramSocket serverSocket = new DatagramSocket(8591);
		byte[] receiveData = new byte[256];
		byte[] sendData = new byte[256];
		
		//receiving packets from client
		DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);


		
		//Receiving a packet from the client
		serverSocket.receive(receivePacket);
		
		//get the packets and putting it in a string 
		String message = new String(receivePacket.getData());
		message = message.trim();
		//System.out.println("Received: " + message);
		
		//splitting the message and put them into arrays
		String [] array = message.split(" ");
		
		//Retrieving the ID of the student
		Students student = retrieveID(array[0]);
		
		//see which column name that needs to be sent back to the client
		String backToClient = "";
		
		//retrieving the relevent data from the columns queried
		for (int i = 1; i < array.length; i++)
		{
			String findColumn = columnName(array[i].trim(), student);
			backToClient = backToClient + array[i] + ": " + findColumn + " ";
		}
		
		//the data that is going to be sent back to the client
		sendData = backToClient.getBytes();
		
		//getting the IP address from Client
		InetAddress IPAddress = receivePacket.getAddress();
		
		//getting the port from the Client
		int port = receivePacket.getPort();
		
		//Creating a DatagramPacket object destined for the client's IP address and port number
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, port);
		
		//sending back to the client
		serverSocket.send(sendPacket);
	}
	
	//database of students, returning the entire array of which
	////it contains the information of the particular student
	public static Students retrieveID(String ID)
	{
		Students [] anArray = new Students[7];
		
		anArray[0] = new Students("903076259", "Anthony", "Peterson", "231", "63", "3.666667");
		anArray[1] = new Students("903084074", "Richard", "Harris", "236", "66", "3.575758");
		anArray[2] = new Students("903077650", "Joe", "Miller", "224", "65", "3.446154");
		anArray[3] = new Students("903083691", "Todd", "Collins", "218", "56", "3.892857");
		anArray[4] = new Students("903082265", "Laura", "Stewart", "207", "64", "3.234375");
		anArray[5] = new Students("903075951", "Marie", "Cox", "246", "63", "3.904762");
		anArray[6] = new Students("903084336", "Stephen", "Baker", "234", "66", "3.545455");
		
		for (int i = 0; i < 7; i++)
		{
			if (anArray[i].getID().equals(ID))
			{
				return anArray[i];
			}
		}
		System.out.println("Error! This specific student cannot be found!");
		return null;
	}
	
	//comparing the query to the column names and return what is queried
	public static String columnName(String message, Students student)
	{
		String returnToClient = "";
		
		if (message.equals("first_name"))
			returnToClient += student.getFirst_name();
		else if (message.equals("last_name"))
			returnToClient += student.getLast_name();
		else if (message.equals("gpa"))
			returnToClient += student.getGpa();
		else if (message.equals("gpa_hours"))
			returnToClient += student.getGpa_hours();
		else if (message.equals("quality_points"))
			returnToClient += student.getQuality_points();
		else
			returnToClient = "No such column! Please try again!";
		
		return returnToClient;
	}
}
