import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class dbclientRTP {
	
	private static int timeout = 2000;

	public static void main(String[] args) throws Exception {

		while(true){
			Scanner reader = new Scanner(System.in);  // Reading from System.in
			System.out.println("Type your command, pls follow format H:P W other arguments ");
			String in = reader.nextLine(); // Scans the next token of the input as an int.
			//String in = "localhost:8222 5000 903076259 first_name last_name";
			String[] arguments = in.split(" ");						
			//arguments = new String[]{"localhost:8190", "5000", "903076259", "first_name", "last_name"};
			if(arguments.length == 0){
				System.err.println("You do not specify a HOST:PORT number");
				System.exit(1);
			} else if(arguments.length > 3) {
				String[] parsedArgument = arguments[0].split(":");
				if(parsedArgument.length!=2){
					System.err.println("Wrong format, please follow HOST:PORT");
					System.exit(1);
				}
				String Host = parsedArgument[0];
				String Port = parsedArgument[1];
				if(Host.isEmpty() || Port.isEmpty()){
					System.err.println("Wrong format, please follow HOST:PORT");
					System.exit(1);
				} 
				InetAddress destIPaddress = InetAddress.getByName(Host);
				Integer destinationPort = Integer.parseInt(Port);
				InetSocketAddress destsocketAddress = new InetSocketAddress(destIPaddress, destinationPort);
				int rcvWindow = Integer.parseInt(arguments[1]);	
			
				
				String[] argument = Arrays.copyOfRange(arguments, 2, arguments.length);
				String message = String.join(" ", argument);
				
				
				RTP rtp = new RTP(timeout, rcvWindow, -1, destinationPort, null, false);
				System.out.println("soucePort:" + rtp.getsourcePort()+"\ndestIPaddress:" + Host + "\ndestinationPort:" + Port + "\nrcvWindow:" + rcvWindow);
				
				ArrayBlockingQueue<ArrayList<Object>> output = rtp.getoutPut();
				ConcurrentHashMap<InetSocketAddress, ArrayList<Object>> connections = rtp.getConnections();
				rtp.startReceive();
			
				rtp.connectionSetup(destinationPort, destIPaddress);
				while(!connections.containsKey(destsocketAddress)){
					
				}
				byte[] message_byte = message.getBytes();
				rtp.pushToQueue(message_byte, destinationPort, destIPaddress, 0, 1);
        		System.out.println(message);
					while(output.isEmpty()){
					}
					while(!output.isEmpty()){
						ArrayList<Object> output_info = output.poll();
						ArrayBlockingQueue<DatagramPacket> data_pkt = (ArrayBlockingQueue<DatagramPacket>) output_info.get(1);
						String data = "";
						for(DatagramPacket pkt : data_pkt) {
							RTPPacket rtppacket = rtp.UDP2RTP(pkt);
							data += new String(rtppacket.getData());
						}
						data = data.trim();
						System.out.println(data);
					}
			} else {
				if(arguments[0].equals("disconnect") && arguments.length == 1){
					System.exit(0);
				}
				System.err.println("Not enough arguments");
				System.exit(1);
			}
		}
		
	}
	
}
