import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;

public class ftaclient {

	private static int timeout = 2000;
	private static int seq = 0;
	public static void main(String[] args) throws Exception {
		Scanner reader = new Scanner(System.in);  // Reading from System.in
		System.out.println("Type your command, pls follow format H:P W other arguments ");
		String in = reader.nextLine(); // Scans the next token of the input as an int.
		//String in = "localhost:8222 5000";
		//String in = "130.207.107.13:8222 5000";
		//String in = "130.207.107.17:8222 5000";
		String[] arguments = in.split(" ");					
		//arguments = new String[]{"localhost:8190", "5000", "903076259", "first_name", "last_name"};
		if(arguments.length == 0){
			System.err.println("You do not specify a HOST:PORT number");
			System.exit(1);
		} else if(arguments.length > 1) {
			String[] parsedArgument = arguments[0].split(":");
			if(parsedArgument.length!=2){
				System.err.println("Wrong format, please follow HOST:PORT");
				System.exit(1);
			}
			String Host = parsedArgument[0];
			String Port = parsedArgument[1];
			if(Host.isEmpty() || Port.isEmpty()){
				System.err.println("Wrong formant, please follow HOST:PORT");
				System.exit(1);
			} 
			InetAddress destIPaddress = InetAddress.getByName(Host);
			Integer destinationPort = Integer.parseInt(Port);
			InetSocketAddress destsocketAddress = new InetSocketAddress(destIPaddress, destinationPort);
			int rcvWindow = Integer.parseInt(arguments[1]);	
			
			
			
			
			RTP rtp = new RTP(timeout, rcvWindow, -1, destinationPort, null, false);
			System.out.println("soucePort:" + rtp.getsourcePort()+"\ndestIPaddress:" + Host + "\ndestinationPort:" + Port + "\nrcvWindow:" + rcvWindow);
			ArrayBlockingQueue<ArrayList<Object>> output = rtp.getoutPut();
			ConcurrentHashMap<InetSocketAddress, ArrayList<Object>> connections = rtp.getConnections();
			//ConcurrentHashMap<InetSocketAddress, ArrayList<ArrayList<String>>> log = rtp.getLog();
			rtp.startReceive();
			rtp.connectionSetup(destinationPort, destIPaddress);				
			while(!connections.containsKey(destsocketAddress)){
				
			}
			System.out.println("Connection Complete");
			while(true) {
				Scanner r = new Scanner(System.in);  // Reading from System.in
				System.out.println("Type your command, pls follow format get F or post G or get-post F G or disconnect");
				in = r.nextLine(); // Scans the next token of the input as an int.				
				//in = "get-post 123.zip 1234.zip";
				arguments = in.split(" ");	
				String message = String.join(" ", arguments);
				String getfilename = null;
				String postfilename = null;
				if(arguments[0].equals("disconnect") && arguments.length == 1){
					System.exit(0);
					} else {
						if(arguments[0].equals("get") && arguments.length == 2){
							getfilename = arguments[1];
						} else if(arguments[0].equals("post") && arguments.length == 2){
							postfilename = arguments[1];
						} else if(arguments[0].equals("get-post") && arguments.length == 3) {
							getfilename = arguments[1];
							postfilename = arguments[2];
						} else {
							System.err.println("Invalid arguments");
							System.exit(1);
						}		
						byte[] message_byte = message.getBytes();
						rtp.pushToQueue(message_byte, destinationPort, destIPaddress, seq++, 1);
		        		System.out.println(message);
		    			boolean flag =true;
		    			while(flag){
		        		while(output.isEmpty() && flag){
						}
						while(!output.isEmpty()){
							ArrayList<Object> output_info = output.poll();
							ArrayBlockingQueue<DatagramPacket> data_pkt = (ArrayBlockingQueue<DatagramPacket>) output_info.get(1);							
							DatagramPacket initial = data_pkt.peek();
							RTPPacket initial_rtp = rtp.UDP2RTP(initial);
							if(initial_rtp.getData() == null && initial_rtp.getHeader().isFIN()){
								System.err.println("File not found");
								break;
							} else if(new String(initial_rtp.getData()).equals("pass")&& initial_rtp.getHeader().isFIN()){
								System.out.println("pass");
								seq = rtp.pushFiletoQueue(postfilename, destinationPort, destIPaddress, seq);						
	
								break;
							} else {
								System.out.println(data_pkt.size());
								FileOutputStream fos = new FileOutputStream("get_" + getfilename);
								for(DatagramPacket pkt : data_pkt) {
									System.out.println(rtp.UDP2RTP(pkt).getHeader().getSequenceNumber());
									RTPPacket rtppacket = rtp.UDP2RTP(pkt);
									fos.write(rtppacket.getData());
								}
								fos.close();
								flag = false;
							}
						}
		    			}
					}
			}
	
		}

	}
	
}
