package net.zhuoweizhang.varodahn.net;

import java.io.*;
import java.net.*;
import java.util.*;

import net.zhuoweizhang.varodahn.proto.*;
import static net.zhuoweizhang.varodahn.proto.SteamMsgRemoteClient.*;

public class DiscoveryClient implements Runnable {
	public static final int DISCOVERY_PORT = 27036;
	private long clientId;
	private ResponseListener listener;
	private Thread thread;
	private boolean isRunning = false;
	private DatagramSocket socket;
	private int seqNum;
	private static final byte[] PACKET_PREHEADER = {(byte) 0xff, (byte) 0xff, (byte) 0xff, (byte) 0xff, 0x21, 0x4c, 0x5f, (byte) 0xa0};
	private static int nextSeqNum = 0;

	public static interface ResponseListener {
		public void onResponse(SocketAddress address, CMsgRemoteClientBroadcastHeader header, CMsgRemoteClientBroadcastStatus status);
		public void onError(Exception e);
	}

	public DiscoveryClient(long clientId, ResponseListener listener) {
		this.clientId = clientId;
		this.listener = listener;
		this.seqNum = nextSeqNum++;
	}

	public void start() {
		isRunning = true;
		thread = new Thread(this);
		thread.start();
	}

	public void stop() {
		try {
			isRunning = false;
			socket.close();
			thread.join();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void run() {
		try {
			setupSocket();
			sendDiscoveryPacket();
			byte[] buf = new byte[8192];
			DatagramPacket packet = new DatagramPacket(buf, buf.length);
			while(isRunning) {
				socket.receive(packet);
				handlePacket(packet);
			}
		} catch (Exception e) {
			if (isRunning) {
				e.printStackTrace();
				listener.onError(e);
			}
		}
	}

	private void setupSocket() throws IOException {
		socket = new DatagramSocket(DISCOVERY_PORT);
	}

	private void sendDiscoveryPacket() throws IOException {
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		DataOutputStream dos = new DataOutputStream(bos);
		dos.write(PACKET_PREHEADER);
		CMsgRemoteClientBroadcastHeader header = CMsgRemoteClientBroadcastHeader.newBuilder().
			setClientId(this.clientId).
			setMsgType(ERemoteClientBroadcastMsg.k_ERemoteClientBroadcastMsgDiscovery).
			build();
		byte[] headerBytes = header.toByteArray();
		dos.writeInt(Integer.reverseBytes(headerBytes.length));
		dos.write(headerBytes);
		CMsgRemoteClientBroadcastDiscovery message = CMsgRemoteClientBroadcastDiscovery.newBuilder().
			setSeqNum(0).
			build();
		byte[] messageBytes = message.toByteArray();
		dos.writeInt(Integer.reverseBytes(messageBytes.length));
		dos.write(messageBytes);
		byte[] buf = bos.toByteArray();

		DatagramPacket packet = new DatagramPacket(buf, buf.length, new InetSocketAddress("255.255.255.255", DISCOVERY_PORT));
		socket.send(packet);
	}

	private void handlePacket(DatagramPacket packet) throws IOException {
		ByteArrayInputStream bis = new ByteArrayInputStream(packet.getData());
		DataInputStream dis = new DataInputStream(bis);
		byte[] preheaderBytes = new byte[PACKET_PREHEADER.length];
		dis.read(preheaderBytes);
		if (!Arrays.equals(PACKET_PREHEADER, preheaderBytes)) {
			return;
		}
		int headerLength = Integer.reverseBytes(dis.readInt());
		byte[] headerBytes = new byte[headerLength];
		dis.read(headerBytes);
		CMsgRemoteClientBroadcastHeader header = CMsgRemoteClientBroadcastHeader.parseFrom(headerBytes);
		if (header.getMsgType() != ERemoteClientBroadcastMsg.k_ERemoteClientBroadcastMsgStatus) return;
		int messageLength = Integer.reverseBytes(dis.readInt());
		byte[] messageBytes = new byte[messageLength];
		dis.read(messageBytes);
		CMsgRemoteClientBroadcastStatus message = CMsgRemoteClientBroadcastStatus.parseFrom(messageBytes);
		listener.onResponse(packet.getSocketAddress(), header, message);
	}

	public static void main(String[] args) {
		DiscoveryClient client = new DiscoveryClient(12345678, new ResponseListener() {
			public void onResponse(SocketAddress address, CMsgRemoteClientBroadcastHeader header,
				CMsgRemoteClientBroadcastStatus status) {
				System.out.println("Response from " + address);
				System.out.println(header);
				System.out.println(status);
			}
			public void onError(Exception e) {
			}
		});
		client.start();
	}

}
