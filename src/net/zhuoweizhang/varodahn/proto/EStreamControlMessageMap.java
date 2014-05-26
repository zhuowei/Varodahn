package net.zhuoweizhang.varodahn.proto;

import java.util.*;

import com.google.protobuf.GeneratedMessage;

import static net.zhuoweizhang.varodahn.proto.StreamingClientMsg.*;
import static net.zhuoweizhang.varodahn.proto.StreamingClientMsg.EStreamControlMessage.*;
public final class EStreamControlMessageMap {

	private EStreamControlMessageMap() {}

	private static Map<Integer, Class<? extends GeneratedMessage>> idToClass = new HashMap<Integer, Class<? extends GeneratedMessage>>();

	private static Map<Class<? extends GeneratedMessage>, Integer> classToId = new HashMap<Class<? extends GeneratedMessage>, Integer>();

	static {
		map(k_EStreamControlAuthenticationRequest_VALUE, CAuthenticationRequestMsg.class);
		map(k_EStreamControlAuthenticationResponse_VALUE, CAuthenticationResponseMsg.class);
		map(k_EStreamControlNegotiationInit_VALUE, CNegotiationInitMsg.class);
		map(k_EStreamControlNegotiationSetConfig_VALUE, CNegotiationSetConfigMsg.class);
		map(k_EStreamControlNegotiationComplete_VALUE, CNegotiationCompleteMsg.class);
		map(k_EStreamControlStartAudioData_VALUE, CStartAudioDataMsg.class);
		map(k_EStreamControlStopAudioData_VALUE, CStopAudioDataMsg.class);
		map(k_EStreamControlStartVideoData_VALUE, CStartVideoDataMsg.class);
		map(k_EStreamControlStopVideoData_VALUE, CStopVideoDataMsg.class);
		map(k_EStreamControlInputMouseMotion_VALUE, CInputMouseMotionMsg.class);
		map(k_EStreamControlInputMouseWheel_VALUE, CInputMouseWheelMsg.class);
		map(k_EStreamControlInputMouseDown_VALUE, CInputMouseDownMsg.class);
		map(k_EStreamControlInputMouseUp_VALUE, CInputMouseUpMsg.class);
		map(k_EStreamControlInputKeyDown_VALUE, CInputKeyDownMsg.class);
		map(k_EStreamControlInputKeyUp_VALUE, CInputKeyUpMsg.class);
		map(k_EStreamControlInputGamepadAttached_VALUE, CInputGamepadAttachedMsg.class);
		map(k_EStreamControlInputGamepadEvent_VALUE, CInputGamepadEventMsg.class);
		map(k_EStreamControlInputGamepadDetached_VALUE, CInputGamepadDetachedMsg.class);
		map(k_EStreamControlShowCursor_VALUE, CShowCursorMsg.class);
		map(k_EStreamControlHideCursor_VALUE, CHideCursorMsg.class);
		map(k_EStreamControlSetCursor_VALUE, CSetCursorMsg.class);
		map(k_EStreamControlGetCursorImage_VALUE, CGetCursorImageMsg.class);
		map(k_EStreamControlSetCursorImage_VALUE, CSetCursorImageMsg.class);
		map(k_EStreamControlDeleteCursor_VALUE, CDeleteCursorMsg.class);
		map(k_EStreamControlSetTargetFramerate_VALUE, CSetTargetFramerateMsg.class);
		map(k_EStreamControlInputLatencyTest_VALUE, CInputLatencyTestMsg.class);
		map(k_EStreamControlGamepadRumble_VALUE, CGamepadRumbleMsg.class);
		map(k_EStreamControlSetMaximumFramerate_VALUE, CSetMaximumFramerateMsg.class);
		map(k_EStreamControlSetMaximumBitrate_VALUE, CSetMaximumBitrateMsg.class);
		map(k_EStreamControlOverlayEnabled_VALUE, COverlayEnabledMsg.class);
		map(k_EStreamControlInputControllerAttached_VALUE, CInputControllerAttachedMsg.class);
		map(k_EStreamControlInputControllerState_VALUE, CInputControllerStateMsg.class);
		map(k_EStreamControlTriggerHapticPulse_VALUE, CTriggerHapticPulseMsg.class);
		map(k_EStreamControlInputControllerDetached_VALUE, CInputControllerDetachedMsg.class);
		map(k_EStreamControlSystemInfo_VALUE, CSystemInfoMsg.class);
		map(k_EStreamControlVideoDecoderInfo_VALUE, CVideoDecoderInfoMsg.class);
		map(k_EStreamControlSetTitle_VALUE, CSetTitleMsg.class);
		map(k_EStreamControlSetIcon_VALUE, CSetIconMsg.class);
		map(k_EStreamControlQuitRequest_VALUE, CQuitRequest.class);
		map(k_EStreamControlSetOverrideMode_VALUE, CSetOverrideModeMsg.class);
		map(k_EStreamControlSetMaximumResolution_VALUE, CSetMaximumResolutionMsg.class);
		map(k_EStreamControlSetQualityPreference_VALUE, CSetQualityPreferenceMsg.class);
		map(k_EStreamControlSetQoS_VALUE, CSetQoSMsg.class);

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
