import java.io.*;
import java.net.*;
import java.util.Arrays;
class dbclientTCP {
	
	private static Socket clientSocket; // socket
	
	public static void main(String args[])throws Exception {
		if(args.length == 0){
			System.err.println("You do not specify a HOST:PORT number");
			System.exit(1);
		} else {
			String[] parsedArgument = args[0].split(":");
			if(parsedArgument.length<2){
				System.err.println("Wrong formant, please follow HOST:PORT");
				System.exit(1);
			}
			String Host = parsedArgument[0];
			String Port = parsedArgument[1];
			if(Host.isEmpty() || Port.isEmpty()){
				System.err.println("Wrong formant, please follow HOST:PORT");
				System.exit(1);
			} else {
				try {
					clientSocket = new Socket(Host, Integer.parseInt(Port));
				} catch (Exception e) {
					System.err.println("Connection Reset");
					System.exit(1);
				}
				if(clientSocket == null){
					System.err.println("Create socket failed");
					System.exit(1);
				}
			}
			System.out.println("Host name:" + Host + "\nPort number:" + Port);
			if(args.length == 1) {
				System.err.println("No other arguments, only socket created");
				System.exit(1);
			} else {
				String[] argument = Arrays.copyOfRange(args, 1, args.length);
				String message = String.join(",", argument);
				  String sentence;
				  String modifiedSentence;
				  DataOutputStream outToServer = new DataOutputStream(clientSocket.getOutputStream());
				  BufferedReader inFromServer = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
				  sentence = message;
				  outToServer.writeBytes(sentence+"\n");
				  modifiedSentence = inFromServer.readLine();
				  System.out.println(modifiedSentence);
				  clientSocket.close();
			}
		}
	}
}
