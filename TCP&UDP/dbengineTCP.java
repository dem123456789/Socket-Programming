import java.io.*;
import java.net.*;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;
import java.util.ArrayList;

class dbengineTCP {
	
	private static final int BUFSIZE = 2048; // Size of receive buffer	
	private static ServerSocket servSock; // socket
	private static ArrayList<String> ID = new ArrayList<String>();
	private static ArrayList<String> FN = new ArrayList<String>();
	private static ArrayList<String> LN = new ArrayList<String>();
	private static ArrayList<String> QP = new ArrayList<String>();
	private static ArrayList<String> GH = new ArrayList<String>();
	private static ArrayList<String> GPA = new ArrayList<String>();
	
	public static void main(String args[])throws Exception {
		
        String line = "";
        //Create the file reader
        BufferedReader fileReader = new BufferedReader(new FileReader("./data.csv"));
        line = fileReader.readLine();
        //Read the file line by line
        while ((line = fileReader.readLine()) != null) 
        {
            //Get all tokens available in line
            String[] tokens = line.split(",");
            ID.add(tokens[0]);
            FN.add(tokens[1]);
            LN.add(tokens[2]);
            QP.add(tokens[3]);
            GH.add(tokens[4]);
            GPA.add(tokens[5]);
        }
		if(args.length == 0){
			System.err.println("You do not specify a PORT number");
			System.exit(1);
		} else if (args.length == 1){
			String Port = args[0];
			if(Port.isEmpty()){
				System.err.println("Wrong formant, please follow HOST:PORT");
				System.exit(1);
			} else {
				servSock = new ServerSocket(Integer.parseInt(Port)) ;
				if(servSock == null){
					System.err.println("Create socket failed");
					System.exit(1);
				}
			}
			System.out.println("The server is ready to receive");
	        String clientSentence = "";
	        String capitalizedSentence;			
	        while(true) {
	        	String message = "From server: ";
	            Socket connectionSocket = servSock.accept();
	            BufferedReader inFromClient =
	                    new BufferedReader(new InputStreamReader(connectionSocket.getInputStream()));	            
	            DataOutputStream outToClient = new DataOutputStream(connectionSocket.getOutputStream());
	            try {
	            	clientSentence = inFromClient.readLine();
	            } catch (Exception e) {
					System.err.println("Connection Reset");
					System.exit(1);
	            }
	        	String[] parsed_arg = clientSentence.split(",");
    			if(parsed_arg.length==1){
    				message = "You did not query any data";
    			} else {
    				String ID_arg = parsed_arg[0];
    				int	ID_index = ID.indexOf(ID_arg);
    				if(ID_index == -1){
    					message = "No ID found";
    				} else {
    					for(int i=1;i<parsed_arg.length;i++){
							message = message + parsed_arg[i] + ": ";
							if(parsed_arg[i].equals("first_name")){
								parsed_arg[i] = FN.get(ID_index);
							} else if(parsed_arg[i].equals("last_name")){
								parsed_arg[i] = LN.get(ID_index);
							} else if(parsed_arg[i].equals("quality_points")){
								parsed_arg[i] = QP.get(ID_index);
							} else if(parsed_arg[i].equals("gpa_hours")){
								parsed_arg[i] = GH.get(ID_index);
							} else if(parsed_arg[i].equals("gpa")){
								parsed_arg[i] = GPA.get(ID_index);
							} else {
								parsed_arg[i] = "invalid" ;
							}
							message = message + parsed_arg[i] + ", ";
							}
    				message = message.substring(0,message.length()-2);
    				}
    			}
				System.out.println(message);
    			//message = message  + "\n";
	            //capitalizedSentence = clientSentence.toUpperCase() + '\n';
	            outToClient.writeBytes(message+"\n");
	            connectionSocket.close();
	        }
		}
	}
}
