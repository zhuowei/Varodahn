package net.zhuoweizhang.varodahn;

import java.io.IOException;

import org.spongycastle.crypto.tls.*;

public class SimplePskTlsClient extends PSKTlsClient {
	public SimplePskTlsClient(TlsPSKIdentity pskIdentity) {
		super(pskIdentity);
	}

	public int[] getCipherSuites() {
		return new int[] {CipherSuite.TLS_PSK_WITH_AES_128_CBC_SHA};
	}

	public TlsAuthentication getAuthentication() throws IOException {
		return null;
	}
}
