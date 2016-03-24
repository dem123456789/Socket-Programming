import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.concurrent.ConcurrentHashMap;

public class Client {

	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		RTP rtp = new RTP(2000, 5000, 8888, 8190, null,false);
		InetAddress destIPaddress = InetAddress.getByName("localhost");
		InetSocketAddress socketAddress = new InetSocketAddress(destIPaddress, 8190);
		rtp.connectionSetup(8190, destIPaddress);
		String test = "Hello World";
		byte[] test_byte = test.getBytes();
		rtp.pushToQueue(test_byte, 8190, destIPaddress, 0, 1);
		rtp.run();
		ConcurrentHashMap<InetSocketAddress, ArrayList<Object>> connections = rtp.getConnections();
		ArrayList<Object> data = connections.get(socketAddress);
		
		
		
		
		
		
		
		
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
