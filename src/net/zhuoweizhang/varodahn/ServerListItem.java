package net.zhuoweizhang.varodahn;
import java.net.SocketAddress;

import static net.zhuoweizhang.varodahn.proto.SteamMsgRemoteClient.*;

public class ServerListItem {
	public SocketAddress address;
	public long clientId;
	public CMsgRemoteClientBroadcastStatus status;
	public ServerListItem(SocketAddress address, long clientId, CMsgRemoteClientBroadcastStatus status) {
		this.address = address;
		this.clientId = clientId;
		this.status = status;
	}

	@Override
	public String toString() {
		return this.address.toString();
	}
}
