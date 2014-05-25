package net.zhuoweizhang.varodahn.proto;

import java.util.*;
import com.google.protobuf.GeneratedMessage;

import static net.zhuoweizhang.varodahn.proto.SteamMsgRemoteClient.*;

public class EMsgRemoteClient {

	private static Map<Integer, Class<? extends GeneratedMessage>> idToClass = new HashMap<Integer, Class<? extends GeneratedMessage>>();

	private static Map<Class<? extends GeneratedMessage>, Integer> classToId = new HashMap<Class<? extends GeneratedMessage>, Integer>();

	static {
		map(9500, CMsgRemoteClientAuth.class);
		map(9501, CMsgRemoteClientAuthResponse.class);
		map(9502, CMsgRemoteClientAppStatus.class);
		map(9503, CMsgRemoteClientStartStream.class);
		map(9504, CMsgRemoteClientStartStreamResponse.class);
		map(9505, CMsgRemoteClientPing.class);
		map(9506, CMsgRemoteClientPingResponse.class);
	}

	public static Class<? extends GeneratedMessage> getById(int id) {
		return idToClass.get(id);
	}

	public static int getByClass(Class<? extends GeneratedMessage> clazz) {
		return classToId.get(clazz);
	}

	private static void map(int id, Class<? extends GeneratedMessage> clazz) {
		idToClass.put(id, clazz);
		classToId.put(clazz, id);
	}

}

