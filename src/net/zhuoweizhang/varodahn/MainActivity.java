package net.zhuoweizhang.varodahn;

import java.net.*;

import android.app.*;
import android.os.*;
import android.widget.*;

import net.zhuoweizhang.varodahn.net.*;
import static net.zhuoweizhang.varodahn.proto.SteamMsgRemoteClient.*;

public class MainActivity extends ListActivity {
	private static final int MSG_RECEIVE_SERVER = 10;
	private DiscoveryClient discoveryClient;
	private ArrayAdapter<ServerListItem> adapter;
	private Handler handler = new Handler() {
		public void handleMessage(Message msg) {
			switch(msg.what) {
				case MSG_RECEIVE_SERVER:
					serverReceived((ServerListItem) msg.obj);
					break;
			}
		}
	};

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)	{
		super.onCreate(savedInstanceState);
		//setContentView(R.layout.main);
		adapter = new ArrayAdapter<ServerListItem>(this, R.layout.server_list_item);
		setListAdapter(adapter);
	}

	@Override
	protected void onResume() {
		super.onResume();
		refreshList();
	}

	@Override
	protected void onPause() {
		super.onPause();
		discoveryClient.stop();
		discoveryClient = null;
	}

	private void refreshList() {
		adapter.clear();
		System.out.println("Refreshing list");
		discoveryClient = new DiscoveryClient(Utils.getClientId(this), new DiscoveryListener());;
		discoveryClient.start();
	}

	private void serverReceived(ServerListItem item) {
		for (int i = adapter.getCount() - 1; i >= 0; i--) {
			if (adapter.getItem(i).clientId == item.clientId) adapter.remove(adapter.getItem(i));
		}
		adapter.add(item);
		adapter.notifyDataSetChanged();
		System.out.println(item);
	}

	private class DiscoveryListener implements DiscoveryClient.ResponseListener {
		public void onResponse(SocketAddress sockAddr, CMsgRemoteClientBroadcastHeader header, CMsgRemoteClientBroadcastStatus status) {
			ServerListItem item = new ServerListItem(sockAddr, header.getClientId(), status);
			handler.sendMessage(handler.obtainMessage(MSG_RECEIVE_SERVER, item));
		}
		public void onError(Exception e) {}
	}
}
