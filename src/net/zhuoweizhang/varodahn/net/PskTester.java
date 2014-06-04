package net.zhuoweizhang.varodahn.net;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

import org.spongycastle.crypto.tls.*;
import org.spongycastle.util.encoders.*;

import com.google.protobuf.ByteString;
import com.google.protobuf.GeneratedMessage;

import net.zhuoweizhang.varodahn.proto.*;
import static net.zhuoweizhang.varodahn.proto.SteamMsgRemoteClient.*;

public class PskTester {

	private static String serverAddr;

	private static byte[] magicBytes = "VT01".getBytes(Charset.forName("UTF-8"));

	public static GeneratedMessage readMessage(DataInputStream in) throws Exception {
		int length = Integer.reverseBytes(in.readInt());
		int magic = Integer.reverseBytes(in.readInt());
		int emsg = Integer.reverseBytes(in.readInt());
		int empty = Integer.reverseBytes(in.readInt()); // according to SteamKit this is the length of legacy header. Always 0.
		System.out.println(emsg);
		Class<? extends GeneratedMessage> clazz = EMsgRemoteClient.getById(emsg & 0x7fffffff);
		byte[] messageBytes = new byte[length - 8];
		in.read(messageBytes);
		return (GeneratedMessage) clazz.getMethod("parseFrom", byte[].class).invoke(null, messageBytes);
	}

	public static void writeMessage(DataOutputStream out, GeneratedMessage msg) throws Exception {
		byte[] serialized = msg.toByteArray();
		int length = serialized.length + 8; // size of message + size of emsg field + size of length of legacy header
		out.writeInt(Integer.reverseBytes(length));
		out.write(magicBytes);
		int emsg = EMsgRemoteClient.getByClass(msg.getClass()) | 0x80000000;
		// SteamKit says 0x80000000 is used to flag ProtoBuf messages
		out.writeInt(Integer.reverseBytes(emsg));
		out.writeInt(0); // SteamKit says this is the length of the legacy header. Always 0.
		out.write(serialized);
	}

	public static void processMessage(GeneratedMessage msg, DataInputStream in, DataOutputStream out) throws Exception {
		System.out.println(msg.getClass());
		System.out.println(msg);
		if ("1".equals(System.getenv("VARODAHN_ECHO"))) {
			writeMessage(out, msg);
			return;
		}
		String appIdStr = System.getenv("VARODAHN_APPID");
		CMsgRemoteClientStartStream startStreamMessage = CMsgRemoteClientStartStream.newBuilder().
			setAppId(appIdStr == null? 400: (int) Long.parseLong(appIdStr)).
			build();
		CMsgRemoteClientAuthResponse authResponseMessage = CMsgRemoteClientAuthResponse.newBuilder().
			setEresult(1). // success
			build();
		if (msg instanceof CMsgRemoteClientAuth) {
			// echo back the auth so that we can log in
			writeMessage(out, msg);
		} else if (msg instanceof CMsgRemoteClientAuthResponse) {
			// same to you, sir. Allow the other side to log in too.
			writeMessage(out, authResponseMessage);
			// we've got a response; send the start game message
			if (System.getenv("VARODAHN_DONTSTART") == null) {
				writeMessage(out, startStreamMessage);
			}
		} else if (msg instanceof CMsgRemoteClientPing) {
			// ping; we send a response
			writeMessage(out, CMsgRemoteClientPingResponse.getDefaultInstance());
		} else if (msg instanceof CMsgRemoteClientStartStreamResponse) {
			// we're streaming! whoo!
			CMsgRemoteClientStartStreamResponse resp = (CMsgRemoteClientStartStreamResponse) msg;
			if (resp.getELaunchResult() == 1) { // huge success
				int streamPort = resp.hasStreamPort()? resp.getStreamPort(): 27031;
				ByteString authToken = resp.getAuthToken();
				StreamingClient streamingClient = new StreamingClient(serverAddr, streamPort, authToken);
				streamingClient.start();
			}
		}
	}

	public static void processLoop(DataInputStream in, DataOutputStream out) throws Exception {
		for(;;) {
			GeneratedMessage msg = readMessage(in);
			processMessage(msg, in, out);
		}
	}

	public static void main(String[] args) throws Exception {
		serverAddr = args[0];
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
