import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;


	public class RTPHeader {
		private int sourcePort;
		private int destinationPort;
		private int sequenceNumber;
		private int data_length;
		private int rcvWindow;
		private int checksum;
		

		private boolean ACK;
		private boolean SYN;
		private boolean FIN;
		
		public RTPHeader() {
			this.data_length = 0;
			this.checksum = 0;
			this.ACK = false;
			this.SYN = false;
			this.FIN = false;
		}
		
		public RTPHeader(int sourcePort, int destinationPort, int sequenceNumber, int rcvWindow) {			
			this();			
			this.sourcePort = sourcePort;
			this.destinationPort = destinationPort;
			this.sequenceNumber = sequenceNumber;
			this.rcvWindow = rcvWindow;
			

		}
			
		/*
		 * Constructor for an RTP Header from a passed in byte array.
		 */
		public RTPHeader(byte[] headerByteArray) {
			ByteBuffer byteBuffer = ByteBuffer.wrap(headerByteArray);
			IntBuffer intBuffer = byteBuffer.asIntBuffer();
			this.sourcePort = intBuffer.get(0);
			this.destinationPort = intBuffer.get(1);
			this.sequenceNumber = intBuffer.get(2);
			this.data_length = intBuffer.get(3);
			this.checksum = intBuffer.get(4);
			int flagsCombined = intBuffer.get(5);
			this.rcvWindow = intBuffer.get(6);
			

			int ackInt = (flagsCombined >>> 31) & 0x1;
			int synInt = (flagsCombined >>> 30) & 0x1;
			int finInt = (flagsCombined >>> 29) & 0x1;
			
			this.ACK = (ackInt != 0);
			this.SYN = (synInt != 0);
			this.FIN = (finInt != 0);
			
			
			/* For Testing
			System.out.println("sourcePort" + this.sourcePort);
			System.out.println("destinationPort" + this.destinationPort);
			System.out.println("windowSizeOffset" + this.windowSizeOffset);
			System.out.println("checksum" + this.checksum);
			System.out.println("timestamp" + this.timestamp);
			System.out.println("ackInt" + ackInt);
			System.out.println("nackInt" + nackInt);
			System.out.println("synInt" + synInt);
			System.out.println("finInt" + finInt);
			System.out.println("begInt" + begInt);
			*/
		}
		
		public byte[] getHeaderByteArray() {
			//Initializes the byte array to return
			byte[] headerByteArray;
			
			//Allocates enough space for 7 32-bit words in a bytebuffer to return the fields in
			ByteBuffer byteBuffer = ByteBuffer.allocate(Integer.BYTES * 7);
			byteBuffer.order(ByteOrder.BIG_ENDIAN);
			
			//Places the following fields in the bytebuffer in the order they appear in the header.
			byteBuffer.putInt(sourcePort);
			byteBuffer.putInt(destinationPort);
			byteBuffer.putInt(sequenceNumber);
			byteBuffer.putInt(data_length);
			byteBuffer.putInt(checksum);
			
			//Converts the flags to ints and then utilizes bitshifting and masking to create a binary string for the flag field row in the header.
			int ackByte = (ACK ? 1 : 0) << 31;
			int synByte = (SYN ? 1 : 0) << 30;
			int finByte = (FIN ? 1 : 0) << 29;
			

			int flagsCombined = ackByte |  synByte | finByte;
			
			//System.out.println("flagsCombined " + flagsCombined);
			
		
			//Places the finished flag byte array in the byte buffer.
			byteBuffer.putInt(flagsCombined);
			
			//Places the last header, the rcvWindow in the byte buffer.
			byteBuffer.putInt(rcvWindow);
			//Converts the byte buffer into a byte array.
			headerByteArray = byteBuffer.array();
			
			//System.out.println(Arrays.toString(headerByteArray));
			
			//Returns the completed byte buffer.
			return headerByteArray;
		}
		
		public int getSourcePort() {
			return sourcePort;
		}

		public void setSourcePort(int sourcePort) {
			this.sourcePort = sourcePort;
		}

		public int getDestinationPort() {
			return destinationPort;
		}

		public void setDestinationPort(int destinationPort) {
			this.destinationPort = destinationPort;
		}
		
		public void setSequenceNumber(int sequenceNumber) {
			this.sequenceNumber = sequenceNumber;
		}
		
		public int getSequenceNumber() {
			return sequenceNumber;
		}
		
		public void setDataLength(int data_length) {
			this.data_length = data_length;
		}
		
		public int getDataLength() {
			return data_length;
		}

		public void setRcvWindow(int rcvWindow) {
			this.rcvWindow = rcvWindow;
		}
		
		public int getRcvWindow() {
			return rcvWindow;
		}
		
		public void setChecksum(int checksum) {
			this.checksum = checksum;
		}
		
		public int getChecksum() {
			return checksum;
		}
		
		public boolean isACK() {
			return ACK;
		}

		public void setACK(boolean ACK) {
			this.ACK = ACK;
		}

		public boolean isSYN() {
			return SYN;
		}

		public void setSYN(boolean SYN) {
			this.SYN = SYN;
		}

		public boolean isFIN() {
			return FIN;
		}

		public void setFIN(boolean FIN) {
			this.FIN = FIN;
		}
		
		public String toString(){
			return "\nSource Port: " + this.getSourcePort()
					+ "\nDestination Port: " + this.getDestinationPort()
					+ "\nSequence number: " + this.getSequenceNumber()
					+ "\nWindow Size offset: " + this.getDataLength()
					+ "\nReceiver Window: " + this.getRcvWindow()
					+ "\nChecksum: " + this.getChecksum()
					+ "\nACK: " + this.isACK()
					+ "\nSYN: " + this.isSYN()
					+ "\nFIN: " + this.isFIN();
			
		}
	}
