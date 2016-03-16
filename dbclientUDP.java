import java.io.*; 
import java.net.*;

public class dbclientUDP {  
		 
	public static void main(String arg[]) throws Exception    
	{       
		//Creating a DatagramSocket
		DatagramSocket clientSocket = new DatagramSocket();
			
			
		byte[] sendData = new byte[1024];
		byte[] receiveData = new byte[1024]; 

		String q = "";
		for(int x = 1; x < arg.length; x++)
		{
			q = q + arg[x] + " ";
				
		}
			
		//Used to create IP address to the server
		InetAddress IPAddress = InetAddress.getByName("localhost");            
			      
		//what to send to the server for query
		sendData = q.getBytes();
			
		//used to send packets to the server
		DatagramPacket sendPacket = new DatagramPacket(sendData, sendData.length, IPAddress, 8591);
		
		//sending packets to the server
		clientSocket.send(sendPacket);
		
		//Seting a waiting time limit
		clientSocket.setSoTimeout(2000);
			
		int counter = 0;
		boolean received = false;
		
		System.out.println("From server: ");
			
		while(!received)
		{	
				
			try{
				
				//Using this to receive reply from the server
				DatagramPacket receivePacket = new DatagramPacket(receiveData, receiveData.length);
					
				//receiving packets from the server
				clientSocket.receive(receivePacket);
					
				//System.out.println(receivePacket);
				String receive = new String(receivePacket.getData());
				//trim away the excess bytes
				receive = receive.trim();
				//final result
				System.out.println(receive);
				
				
				// close the connection
				clientSocket.close();
				received = true;
			
			} catch (SocketTimeoutException e) {
				counter++;
				System.out.println("The server has not answered in the last two seconds.");
				System.out.println(" retrying...");
				if (counter == 3)
				{
					System.out.println("Closing Program...");
					clientSocket.close();
				} 
			}
		
		}
	}
}
