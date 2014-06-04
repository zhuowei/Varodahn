package net.zhuoweizhang.varodahn.net;

import java.nio.charset.Charset;

import org.spongycastle.crypto.tls.*;

public class SimpleTlsPSKIdentity implements TlsPSKIdentity {
	private byte[] identity, psk;
	public SimpleTlsPSKIdentity(byte[] identity, byte[] psk) {
		this.identity = identity;
		this.psk = psk;
	}

	public byte[] getPSK() {
		return psk;
	}

	public byte[] getPSKIdentity() {
		return identity;
	}

	public void notifyIdentityHint(byte[] pskIdentityHint) {
		System.err.println("Identity notification: " + new String(pskIdentityHint, Charset.forName("UTF-8")));
	}

	public void skipIdentityHint() {
		System.err.println("Identity notification skipped");
	}
}
