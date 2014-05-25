package net.zhuoweizhang.varodahn;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

import org.spongycastle.crypto.tls.*;
import org.spongycastle.util.encoders.*;

import com.google.protobuf.GeneratedMessage;

import net.zhuoweizhang.varodahn.proto.*;

public class PskTester {

	private static byte[] magicBytes = "VT01".getBytes(Charset.forName("UTF-8"));

	public static GeneratedMessage readMessage(DataInputStream in) throws Exception {
		int length = Integer.reverseBytes(in.readInt());
		int magic = Integer.reverseBytes(in.readInt());
		int emsg = Integer.reverseBytes(in.readInt());
		int empty = Integer.reverseBytes(in.readInt()); // according to SteamKit this is the length of legacy header. Always 0.
		System.out.println(emsg);
		Class<? extends GeneratedMessage> clazz = EMsgRemoteClient.getById(emsg & 0xffff);
		byte[] messageBytes = new byte[length - 8];
		in.read(messageBytes);
		return (GeneratedMessage) clazz.getMethod("parseFrom", byte[].class).invoke(null, messageBytes);
	}

	public static void writeMessage(DataOutputStream out, GeneratedMessage msg) throws Exception {
		byte[] serialized = msg.toByteArray();
		int length = serialized.length + 8; // size of message + size of emsg field + size of length of legacy header
		out.writeInt(Integer.reverseBytes(length));
		out.write(magicBytes);
		int emsg = EMsgRemoteClient.getByClass(msg.getClass()) | 0x80000000; // SteamKit says 0x80000000 is used to flag ProtoBuf messages
		out.writeInt(Integer.reverseBytes(emsg));
		out.writeInt(0); // SteamKit says this is the length of the legacy header. Always 0.
		out.write(serialized);
	}

	public static void processMessage(GeneratedMessage msg) {
		System.out.println(msg);
	}

	public static void processLoop(DataInputStream in, DataOutputStream out) throws Exception {
		SteamMsgRemoteClient.CMsgRemoteClientAuthResponse outmsg = SteamMsgRemoteClient.CMsgRemoteClientAuthResponse.newBuilder().
			build();
		writeMessage(out, outmsg);
		SteamMsgRemoteClient.CMsgRemoteClientStartStream outmsg2 = SteamMsgRemoteClient.CMsgRemoteClientStartStream.newBuilder().
			setAppId(400).
			build();
		writeMessage(out, outmsg2);

		for(;;) {
			GeneratedMessage msg = readMessage(in);
			processMessage(msg);
		}
	}

	public static void main(String[] args) throws Exception {
		Socket sock = new Socket(args[0], Integer.parseInt(args[1]));
		TlsClientProtocol tlsClientProtocol = new TlsClientProtocol(sock.getInputStream(), sock.getOutputStream());
		TlsPSKIdentity identity = new SimpleTlsPSKIdentity(
			"steam".getBytes(Charset.forName("UTF-8")), Hex.decode(args[2]));
		tlsClientProtocol.connect(new SimplePskTlsClient(identity));
		//copyStreams2(tlsClientProtocol.getInputStream(), System.out);
		processLoop(new DataInputStream(tlsClientProtocol.getInputStream()), new DataOutputStream(tlsClientProtocol.getOutputStream()));
	}

	private static void copyStreams(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int count;
		while ((count = in.read(buffer)) != -1) {
			out.write(buffer, 0, count);
		}
	}

	private static void copyStreams2(InputStream in, OutputStream out) throws IOException {
		int buffer;
		int count;
		while ((buffer = in.read()) != -1) {
			out.write(buffer);
			out.flush();
		}
	}
}
