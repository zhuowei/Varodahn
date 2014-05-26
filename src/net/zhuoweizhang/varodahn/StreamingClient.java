package net.zhuoweizhang.varodahn;

import java.io.*;
import java.net.*;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;

import org.spongycastle.util.encoders.Hex;

import net.zhuoweizhang.varodahn.proto.*;
import static net.zhuoweizhang.varodahn.proto.StreamingClientMsg.*;

public class StreamingClient implements Runnable {

	private static final int PACKET_TYPE_CONNECT = 1;
	private static final int PACKET_TYPE_CONNECT_RESPONSE = 2;
	private static final int PACKET_TYPE_CONTROL = 5;
	private static final int PACKET_TYPE_DATA = 6;
	private static final int PACKET_TYPE_CONTROL_ACKNOWLEDGE = 7;
	private static final int PACKET_TYPE_DISCONNECT = 9;

	private String serverAddr;
	private int port;
	private ByteString authToken;
	private Thread networkThread;
	private DatagramSocket socket;
	private int senderId = 0x80;
	private int receiverId = 0;
	private short[] packetSequenceIds = new short[10];
	private OutputStream loggingOut;
	private boolean connected = false;

	public StreamingClient(String serverAddr, int port, ByteString authToken) {
		this.serverAddr = serverAddr;
		this.port = port;
		this.authToken = authToken;
	}

	public void start() {
		networkThread = new Thread(this);
		networkThread.start();
	}

	public void run() {
		try {
			setupLogging();
			connect();
			processLoop();
		} catch (Exception e) {
			throw e instanceof RuntimeException? (RuntimeException) e: new RuntimeException(e);
		}
	}

	private void connect() throws Exception {
		InetSocketAddress socketAddress = new InetSocketAddress(serverAddr, port);
		socket = new DatagramSocket();
		socket.connect(socketAddress);
		connected = true;
		// do you wanna send a packet?
		writeOpenConnectionMessage();
	}

	/* packet format: probably roughly based on the Steam(or Source?) UDP protocol.
	 * all values are little endian.
	 * byte: type of packet:
	 *	1 = open connection, 2 = open connection response, 3 = ???, 
	 * 	5 = control channel, 6 = data channel, 7 = control channel acknowledge, 9 = disconnect
	 * byte: unknown: repeat count?
	 * byte: sender ID
	 * byte: receiver ID (0 when sending open connection packet)
	 * byte: dunno: 0, 1, 2 sighted
	 * byte: dunno, always zero
	 * byte: dunno, always zero
	 * short: sequence ID for that packet type
	 * int: probably a timestamp: constantly increasing. The two sides start with different values for this field.
		Used for acknowledging packets in type 2 and 5.
	 * payload begins here. All packet types with the exception of 1 (open connection) and the acknowledge packets begins with:
	 * byte: packet subtype: unique for that channel.
	 * 	For control channel (5): represented by EStreamControlMessage on the Protobuf side
	 * the acknowledge packets (2, 7) instead just have the timestamp of the packet they are acknowledging.
	 */

	private void writeOpenConnectionMessage() throws IOException {
		writeRawMessage(1, 0, new byte[]{});
	}

	private void writeRawMessage(int packetType, int flags, byte[] payload) throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.write(packetType);
		dos.write(0); //repeat count: todo
		dos.write(senderId);
		dos.write(receiverId);
		dos.write(flags); //dunno
		dos.writeShort(0); //dunno
		dos.writeShort(Short.reverseBytes(packetSequenceIds[packetType]));
		dos.writeInt(Integer.reverseBytes(getTimestamp()));
		dos.write(payload);
		byte[] output = bos.toByteArray();
		DatagramPacket packet = new DatagramPacket(output, output.length);
		socket.send(packet);
		packetSequenceIds[packetType]++;
	}

	private void processLoop() throws IOException {
		byte[] byteBuf = new byte[8192];
		DatagramPacket packet = new DatagramPacket(byteBuf, byteBuf.length);
		while(connected) {
			socket.receive(packet);
			loggingOut.write(byteBuf, 0, packet.getLength());
			loggingOut.flush();
			System.out.println(Hex.toHexString(byteBuf, 0, packet.getLength()));
			int packetType = byteBuf[0];
			switch(packetType) {
				case PACKET_TYPE_CONNECT_RESPONSE:
					receiverId = byteBuf[2] & 0xff;
					sendAuthMessage();
					break;
				case PACKET_TYPE_CONTROL:
					processControlMessage(byteBuf, 0, packet.getLength());
					break;
				case PACKET_TYPE_DISCONNECT:
					connected = false;
					break;
				default:
					break;
			}
		}
	}

	/**
	 * Get a timestamp, which is used as an autoincrementing packet ID.
	 */
	private int getTimestamp() {
		return (int) System.currentTimeMillis();
	}

	private void setupLogging() throws IOException {
		loggingOut = new FileOutputStream(new File("streaminglog.dat"));
	}

	private void processControlMessage(byte[] buffer, int begin, int length) throws IOException {
		sendControlAckMessage(buffer, begin);
		GeneratedMessage msg = readControlMessage(buffer, begin, length);
		System.out.println(msg);
		if (msg instanceof CNegotiationInitMsg) {
			CNegotiationInitMsg initMsg = (CNegotiationInitMsg) msg;
			CNegotiationSetConfigMsg setconfigMsg = CNegotiationSetConfigMsg.newBuilder().
				setConfig(CNegotiatedConfig.newBuilder().
					setReliableData(false).
					setSelectedAudioCodec(EStreamAudioCodec.k_EStreamAudioCodecVorbis).
					setSelectedVideoCodec(EStreamVideoCodec.k_EStreamVideoCodecH264)
					// todo: un-hardcode
				).
				build();
			writeControlMessage(setconfigMsg);
		} else if (msg instanceof CNegotiationSetConfigMsg) {
			writeControlMessage(CNegotiationCompleteMsg.getDefaultInstance());
		}
	}
	private GeneratedMessage readControlMessage(byte[] buffer, int beginOfBuffer, int lengthOfBuffer) {
		int begin = beginOfBuffer + 13;
		int length = lengthOfBuffer - 13;
		int escmsg = buffer[begin] & 0xff;
		Class<? extends GeneratedMessage> clazz = EStreamControlMessageMap.getById(escmsg);
		System.out.println(Integer.toString(escmsg, 16) + ":" + clazz);
		if (System.getenv("VARODAHN_STREAMNOPARSE") != null) return null;
		byte[] messageBytes = new byte[length - 1];
		System.arraycopy(buffer, begin + 1, messageBytes, 0, length - 1);
		try {
			return (GeneratedMessage) clazz.getMethod("parseFrom", byte[].class).invoke(null, messageBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}

	}

	private void sendAuthMessage() throws IOException {
		CAuthenticationRequestMsg authMsg = CAuthenticationRequestMsg.newBuilder().
			setVersion(EStreamVersion.k_EStreamVersionCurrent).
			setToken(authToken).
			build();
		writeControlMessage(authMsg);
	}

	private void writeControlMessage(GeneratedMessage msg) throws IOException {
		int escmsg = EStreamControlMessageMap.getByClass(msg.getClass());
		byte[] serialized = msg.toByteArray();
		byte[] withId = new byte[serialized.length + 1];
		withId[0] = (byte) escmsg;
		System.arraycopy(serialized, 0, withId, 1, serialized.length);
		writeRawMessage(PACKET_TYPE_CONTROL, 1, withId);
	}

	private void sendControlAckMessage(byte[] originalMessage, int offset) throws IOException {
		byte[] outmsg = new byte[4];
		System.arraycopy(originalMessage, offset + 9, outmsg, 0, 4);
		writeRawMessage(PACKET_TYPE_CONTROL_ACKNOWLEDGE, 1, outmsg);
	}

	private void writeAckMessageRaw(int timestamp) throws IOException {
		byte[] data = new byte[4];
		writeIntToByteArray(data, 0, timestamp);
		writeRawMessage(PACKET_TYPE_CONTROL_ACKNOWLEDGE, 1, data);
	}
	private static void writeIntToByteArray(byte[] buffer, int offset, int value) {
		buffer[offset++] = (byte) value;
		buffer[offset++] = (byte) (value >> 8);
		buffer[offset++] = (byte) (value >> 16);
		buffer[offset++] = (byte) (value >> 24);
	}
}
