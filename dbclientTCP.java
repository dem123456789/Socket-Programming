import java.io.*;
import java.net.*;

public class dbclientTCP {
	public static void main (String arg[]) throws Exception {
		
		//Socket constructor with arg (hostname, portnumber)
		Socket clientSocket = new Socket("localhost", 8591);			
		
		DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
		BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
		
		String q = "";
		for(int x = 1; x < arg.length; x++)
		{
			q = q + arg[x] + " ";
		}
		
		try
		{
			System.out.print("From server: ");
			outToServer.writeBytes(q + "\n");
			
			//Receiving data from the server  
			String sentence = inFromServer.readLine();
			System.out.println(sentence);
			
			//close the connection
			clientSocket.close();
				
			System.out.println(""); 
			
		} catch (UnknownHostException e) {
			System.err.println("Don't know about host: hostname");
		} catch (ConnectException e) {	
			System.err.println("Unable to connect to server. Please try again.");
		} catch (IOException e) {
			System.err.println("Couldn't get IO for the connection to: hostname");
		}
	}
}
