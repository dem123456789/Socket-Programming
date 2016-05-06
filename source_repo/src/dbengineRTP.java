import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class dbengineRTP {
	
	private static int timeout = 2000;
	
	public static void main(String args[])throws Exception {
			Scanner reader = new Scanner(System.in);  // Reading from System.in
			System.out.println("Type your command, pls follow format P W other arguments ");
			String in = reader.nextLine(); // Scans the next token of the input as an int.	
			//String in = "8222 5000";
			String[] arguments = in.split(" ");
			//arguments = new String[]{"8190", "5000"};
		       if(arguments.length == 0){
					System.err.println("You do not specify a PORT number");
					System.exit(1);
				} else if (arguments.length == 2){
					String Port = arguments[0];
					int sourcePort = Integer.parseInt(Port);
					int rcvWindow = Integer.parseInt(arguments[1]);
					System.out.println("sourcePort:" + sourcePort + "\nrcvWindow:" + rcvWindow);
					if(Port.isEmpty()){
						System.err.println("Wrong format, please follow P W ");
						System.exit(1);
					}
					RTP rtp = new RTP(timeout, rcvWindow, sourcePort, 0, null, true);
					ArrayBlockingQueue<ArrayList<Object>> output = rtp.getoutPut();
					ConcurrentHashMap<InetSocketAddress, ArrayList<ArrayList<String>>> log = rtp.getLog();
					rtp.startReceive();
					while(true){
							while(output.isEmpty()){
							}
							while(!output.isEmpty()){
								System.out.println("sent high");
								ArrayList<Object> output_info = output.poll();
								InetSocketAddress socketAddress = (InetSocketAddress) output_info.get(0);
								InetAddress destIPaddress = socketAddress.getAddress();
								int destinationPort = socketAddress.getPort();
								ArrayBlockingQueue<DatagramPacket> data_pkt = (ArrayBlockingQueue<DatagramPacket>) output_info.get(1);
								String data = "";
								for(DatagramPacket pkt : data_pkt) {
									RTPPacket rtppacket = rtp.UDP2RTP(pkt);
									data += new String(rtppacket.getData());
								}
								data = data.trim();
								String [] array = data.split(" ");
								System.out.println(data); 
								Students student = retrieveID(array[0]);
								String backToClient = "";
								for (int i = 1; i < array.length; i++) {
									String findColumn = columnName(array[i].trim(), student);
									backToClient = backToClient + array[i] + ": " + findColumn + " ";
								}
								byte[] sendData = backToClient.getBytes();
								rtp.pushToQueue(sendData, destinationPort, destIPaddress, 0, 1);
								
								ArrayList<ArrayList<String>> sequence = log.get(new InetSocketAddress(destIPaddress, destinationPort));
								if(sequence != null) {
									for(int i = 0; i < sequence.size(); i++) { 
										ArrayList<String> msgsequence = sequence.get(i);
										for(int j = 0; j < msgsequence.size(); j++) { 
											System.out.println(msgsequence.get(j));
										}
									} 
								}
							}
							
					}
				} else {
					System.err.println("Wrong formant, please follow P W");
					System.exit(1);
				}
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
