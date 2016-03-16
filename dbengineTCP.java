import java.io.*;
import java.net.*;

public class dbengineTCP {
	
	public static void main(String arg[]) throws Exception 
	{
		
		ServerSocket welcomeSocket = new ServerSocket(8591);
	
		while(true)
		{
			//waiting for client to connect to server. Returns a pointer that points to the connectionSocket object
			Socket connectionSocket = welcomeSocket.accept();
			
			//Getting input from client
			InputStreamReader IR = new InputStreamReader(connectionSocket.getInputStream());
			//Reading the input
			BufferedReader inFromClient = new BufferedReader(IR);
			//Sending the info back to client
			DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
			
			String message = inFromClient.readLine();
			
			//Splitting the ID and the other queries
			String [] array = message.split(" ");
			
			//Retrieving the ID of the student
			Students student = retrieveID(array[0]);
			
			//see which column name that needs to be sent back to the client
			String backToClient = "";
			
			//retrieving the relevent data from the columns queried
			for (int i = 1; i < array.length; i++)
			{
				String findColumn = columnName(array[i], student);
				backToClient = backToClient + array[i] + ": " + findColumn + " ";
			}
			
			//send back to client
			outToClient.writeBytes(backToClient + "\n");
		}
	}
	
	//database of students, returning the entire array of which 
	//it contains the information of the particular student
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
