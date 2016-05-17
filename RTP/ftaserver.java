import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

public class ftaserver {

	private static int timeout = 2000;
	private static ConcurrentHashMap<InetSocketAddress, String> postfilenames = new ConcurrentHashMap<InetSocketAddress, String>();
	
	public static void main(String[] args) throws IOException {
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
				ConcurrentHashMap<InetSocketAddress, ArrayList<Object>> connections = rtp.getConnections();
				//ConcurrentHashMap<InetSocketAddress, ArrayList<ArrayList<String>>> log = rtp.getLog();
				rtp.startReceive();
				while(true){
						while(output.isEmpty()){
						}
						while(!output.isEmpty()){
							ArrayList<Object> output_info = output.poll();
							InetSocketAddress socketAddress = (InetSocketAddress) output_info.get(0);
							InetAddress destIPaddress = socketAddress.getAddress();
							int destinationPort = socketAddress.getPort();
							ArrayList<Object> windowConnection = connections.get(socketAddress);
							AtomicInteger seq = (AtomicInteger) windowConnection.get(13);
							ArrayBlockingQueue<DatagramPacket> data_pkt = (ArrayBlockingQueue<DatagramPacket>) output_info.get(1);
							DatagramPacket initial = data_pkt.peek();
							RTPPacket initial_rtp = rtp.UDP2RTP(initial);
							String data = new String(initial_rtp.getData());
							String [] arr = data.split(" ");
							
							String getfilename = null;
							String postfilename_candidate = null;
							String command = arr[0];
							if(command.equals("get") && arr.length == 2){
								getfilename = arr[1];
							} else if(command.equals("post") && arr.length == 2){
								postfilename_candidate = arr[1];
							} else if(command.equals("get-post") && arr.length == 3) {
								getfilename = arr[1];
								postfilename_candidate = arr[2];
							} else {
								if(postfilenames.containsKey(socketAddress)){
									String postfilename = postfilenames.get(socketAddress);
									FileOutputStream fos = new FileOutputStream("post_" + postfilename);
									for(DatagramPacket pkt : data_pkt) {
										RTPPacket rtppacket = rtp.UDP2RTP(pkt);
										fos.write(rtppacket.getData());
									}
									fos.close();
								} else {
									System.out.println("Invalid Data Packet Received");
								}
							}
							if(command.equals("get")|| command.equals("get-post")){
								File f = new File(getfilename);
								if(command.equals("get-post")){
									postfilenames.put(socketAddress, postfilename_candidate);
									rtp.pushToQueue("pass".getBytes(), destinationPort, destIPaddress, seq.incrementAndGet(), 1);	
								}
								if(f.exists() && !f.isDirectory()) { 
									seq.set(rtp.pushFiletoQueue(getfilename, destinationPort, destIPaddress,seq.incrementAndGet()) - 1);
								} else {
									rtp.pushToQueue(null, destinationPort, destIPaddress, seq.incrementAndGet(), 1);	
								}
							} else if(command.equals("post")){
								postfilenames.put(socketAddress, postfilename_candidate);
								rtp.pushToQueue("pass".getBytes(), destinationPort, destIPaddress, seq.incrementAndGet(), 1);
							} else {
								//System.out.println("Wrong command: " + command);
							}
							windowConnection.set(13,seq);
						}
						
				}
			} else {
				System.err.println("Wrong formant, please follow P W");
				System.exit(1);
			}

	}
	
}
