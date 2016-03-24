import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ConcurrentHashMap;


public class Server {
	
	public static void main(String[] args) throws Exception {
		RTP rtp = new RTP(2000, 5000, 8190, 0, null, true);
		rtp.run();
		InetAddress destIPaddress = InetAddress.getByName("localhost");
		InetSocketAddress socketAddress = new InetSocketAddress(destIPaddress, 8888);
		ConcurrentHashMap<InetSocketAddress, ArrayList<Object>> connections = rtp.getConnections();
		ArrayList<Object> connection_info = connections.get(socketAddress);
		ArrayBlockingQueue<DatagramPacket> data_pkt = (ArrayBlockingQueue<DatagramPacket>) connection_info.get(2);
		String data = "";
		for(DatagramPacket pkt : data_pkt) {
			RTPPacket rtppacket = rtp.UDP2RTP(pkt);
			data += new String(rtppacket.getData());
		}
		System.out.print(data); 
		
		
		
		
		ConcurrentHashMap<InetSocketAddress, ArrayList<ArrayList<String>>> log = rtp.getLog();
		ArrayList<ArrayList<String>> sequence = log.get(new InetSocketAddress(destIPaddress, 8190));
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
