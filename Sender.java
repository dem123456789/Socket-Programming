import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;

/**
 * The rdt sender class.
 *
 * Name: Chenguang He
 * Email: readman@iastate.edu
 * Created by chenguanghe on 9/17/14.
 */
public class Sender{
    private int windowSize;  // real windows size
    private int timeOut; // the time of timeout
    private int seq = 0; // the seq number of packet
    public boolean isBlock; // if the current packet is sending
    private ArrayBlockingQueue<RTPPacket> queue = new ArrayBlockingQueue<>(99999); // the large queue for message
    private LinkedList<RTPPacket> WindowsList; // the window
    public HashMap<Integer, ArrayList<String>> log = new HashMap<Integer, ArrayList<String>>();//the queue have all packet with different state
    private DatagramSocket socket; // the socket
    private Timer timeoutTimer; // the timer to schedule timeout
    private final int RTP_PACKET_SIZE = 1000; // the size of packet
    private final int UDP_PACKET_SIZE = 2000; // the size of packet
    private int windows[]; // the windows to get the feedback from client
    private final int ACK = 1; // ack
    private final int NAK = 0; // nak
    private int numberOfTimeouts; // the number of timeouts
    private int rcvWindow;  // receiver windows size
    private int maxWindowsSize; // the limit of windows size
    private int destinationPort; // the receive port
    private int sourcePort; // the send port
    private InetAddress IPaddress; // the IP address
    /**
     * no default public constructor
     */
    private Sender() {
    }

    /**
     * the public constructor to build the sender
     * @param windowsSize the limit of windows
     * @param timeout the time of timeout
     * @param recPort the receive port
     * @param sourcePort the send port
     * @throws SocketException // socket exception
     */
    public Sender(int windowsSize, int timeout, int rcvWindow, int destinationPort, int sourcePort, InetAddress IPaddress) throws SocketException {
        this.maxWindowsSize = windowsSize;
        this.timeOut = timeout;
        windows = new int[maxWindowsSize];
        socket = new DatagramSocket(destinationPort);
        WindowsList = new LinkedList<RTPPacket>();
        this.rcvWindow = rcvWindow;
        this.destinationPort = destinationPort;
        this.sourcePort = sourcePort;
        this.IPaddress = IPaddress;
    }

    /**
     * put data to queue
     *
     * @param buf the data
     * @param len the length of data
     */
    public void pushToQueue(byte[] data, int rcvWindow) {
        try {
            RTPPacket rtppacket = new RTPPacket(this.sourcePort, this.destinationPort, data, rcvWindow);
            rtppacket.getHeader().setSequenceNumber(seq++);
            //state omitted
            queue.put(rtppacket);
            write(rtppacket.getHeader().getSequenceNumber());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    /**
     * write log
     * @param seq // the seq number
     * @param s the log
     */
    public void write(int seq) {
        if (log.containsKey(seq)){
           ArrayList<String> arrayList = log.get(seq);
            arrayList.add(state);
        }else {
            ArrayList<String> arrayList = new ArrayList<String>();
            arrayList.add(state);
            log.put(seq, arrayList);
        }
    }

    /**
     * Send method use selective repeat
     */
    public void SendData() throws Exception {
        isBlock = true; // in transmission, block all traffic
        numberOfTimeouts = 0; // times of timeouts
        timeoutTimer = new Timer(true); // sent timer
        windowSize = 0; //size of windows
        while (true) {
            while (queue.isEmpty()&&windowSize == 0) {
                isBlock = false;
            }
            if (windowSize == 0) { // if it is the first time to send
                isBlock = true;
                windowSize = Math.min(queue.size(), maxWindowsSize);
                windows = new int[windowSize];
                Arrays.fill(windows, NAK);
                for (int i = 0; i < windowSize; i++) {
                    RTPPacket rtppacket = queue.take();
                    rtppacket.state = State.Sent;
                    WindowsList.addLast(rtppacket);
                    SendPacket(rtppacket);
                    write(rtppacket.getHeader().getSequenceNumber(), State.Sent);
                }
            } else {
                isBlock = true;
                int emptySpace = adjustWindow();
                int[] newWindows = new int[windowSize];
                int ping = 0; // the variable to set windows
                //adjust list of sending windows
                for (int i = 0; i < emptySpace; i++) {
                    WindowsList.removeFirst();
                }
                // merge to new windows
                for (int i = emptySpace; i < windowSize; i++) {
                    newWindows[ping] = windows[i];
                    ping++;
                }
                // send new packet
                while (emptySpace-- != 0 && !queue.isEmpty()) {
                    RTPPacket rtppacket = queue.poll();
                    rtppacket.state = State.Sent;
                    WindowsList.addLast(rtppacket);
                    SendPacket(rtppacket);
                    write(rtppacket.getHeader().getSequenceNumber(), State.Sent);
                }
                // merge windows
                windows = newWindows;
                windowSize = WindowsList.size();
            }
            if (windowSize != 0) {
                isBlock = true;
                byte[] ackpacket = new byte[UDP_PACKET_SIZE];
                DatagramPacket getAck = new DatagramPacket(ackpacket, ackpacket.length);
                socket.receive(getAck);
                ack(getAck);
            } else {
                isBlock = false;
                windowSize = Math.min(queue.size(), maxWindowsSize);
            }
        }
    }

    /**
     * ack packet in datagram packet
     * @param packet the packet
     */
    private void ack(DatagramPacket ackpacket) {
        byte[] packetbyte = ackpacket.getData();
        RTPPacket rtppacket = new RTPPacket(packetbyte);       
        int seq = rtppacket.getHeader().getSequenceNumber();
        int ack = rtppacket.getHeader().isACK() ? 1 : 0;
        if (ack == ACK) { // if it acked
	        for (int i = 0; i < WindowsList.size(); i++) {
	        	RTPPacket p = WindowsList.get(i);
	        	if(p.getHeader().getSequenceNumber() == seq){
	                p.getHeader().setACK(true);
	                p.state = State.Acked;
	                windows[WindowsList.indexOf(p)] = ACK;
	                break;
	        	}
	        	// else then something wrong
	        }
            write(rtppacket.getHeader().getSequenceNumber(), State.Acked);
        }
    }

    /**
     * send a packet to client
     * @param packet the packet
     * @throws Exception the socket exception
     */
    private void SendPacket(RTPPacket rtppacket) throws Exception {
        byte[] packetData = rtppacket.getPacketByteArray();
        DatagramPacket pkt = new DatagramPacket(packetData, packetData.length, IPaddress, sourcePort);
        socket.send(pkt);
        timeoutTimer.schedule(new PacketTimeout(rtppacket), timeOut); // when send a packet, set a timer as well
    }

    /**
     * return if it is blocked
     * @return true it is in block, otherwise false;
     */
    public boolean isBlock() {
        return isBlock;
    }

    /**
     * the checksum for datagram
     * @param message the data
     * @return the checksum
     */
    private static int CheckSum(byte[] message) {
        int checksum = 0;
        for (int i = 0; i < message.length; i++) {
            checksum += message[i];
        }
        return checksum;
    }
    /**
     * the send method which in a new thread to put data into queue
     */
    private class Send extends Thread {
        Sender sender;

        public Send(Sender sender) {
            this.sender = sender;
        }

        public void run() {
            while (true) {
                try {
                    sender.SendData();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * the method will move the first nak in windows to the first position.
     * @return the number of shifts
     * @throws Exception the exception
     */
    private int adjustWindow() throws Exception {
        int windowMoved = 0;
        for (int i = 0; i < windowSize; i++) {
            if (windows[i] == ACK)
                windowMoved++;
            else
                break;
        }
        return windowMoved;
    }

    /**
     * the timer for packet use to set up the timeout.
     */
    private class PacketTimeout extends TimerTask {
        private RTPPacket p;

        public PacketTimeout(RTPPacket p) {
            this.p = p;
        }

        public void run() {
            //if packet has not been ACKed
            numberOfTimeouts++;
            try {
                if (!p.getHeader().isACK()) {
                    SendPacket(p);
                    p.reTransmits++;
                    p.state = State.Resent;
                    write(p.getHeader().getSequenceNumber(), State.Resent);
                }
            } catch (Exception e) {
            }
        }
    }
}