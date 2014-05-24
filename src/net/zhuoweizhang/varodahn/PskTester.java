package net.zhuoweizhang.varodahn;

import java.io.*;
import java.net.*;
import java.nio.charset.Charset;

import org.spongycastle.crypto.tls.*;
import org.spongycastle.util.encoders.*;

public class PskTester {
	public static void main(String[] args) throws Exception {
		Socket sock = new Socket(args[0], Integer.parseInt(args[1]));
		TlsClientProtocol tlsClientProtocol = new TlsClientProtocol(sock.getInputStream(), sock.getOutputStream());
		TlsPSKIdentity identity = new SimpleTlsPSKIdentity(
			"steam".getBytes(Charset.forName("UTF-8")), Hex.decode(args[2]));
		tlsClientProtocol.connect(new SimplePskTlsClient(identity));
		copyStreams(tlsClientProtocol.getInputStream(), System.out);
	}

	private static void copyStreams(InputStream in, OutputStream out) throws IOException {
		byte[] buffer = new byte[1024];
		int count;
		while ((count = in.read(buffer)) != -1) {
			out.write(buffer, 0, count);
		}
	}
}
