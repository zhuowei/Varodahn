mkdir gentemp
echo "option java_package = \"net.zhuoweizhang.varodahn.proto\";" >gentemp/StreamingClientMsg.proto
cat ../SteamKit/Resources/Protobufs/steamclient_streaming_client/stream.proto >>gentemp/StreamingClientMsg.proto
protoc --java_out=src --proto_path gentemp --proto_path ../SteamKit/Resources/Protobufs/steamclient_streaming_client/ gentemp/StreamingClientMsg.proto

echo "option java_package = \"net.zhuoweizhang.varodahn.proto\";" >gentemp/steammessages_base.proto
echo "option java_outer_classname = \"SteamMsgBase\";" >>gentemp/steammessages_base.proto
cat ../SteamKit/Resources/Protobufs/steamclient/steammessages_base.proto >>gentemp/steammessages_base.proto

echo "option java_package = \"net.zhuoweizhang.varodahn.proto\";" >gentemp/SteamMsgRemoteClient.proto
echo "option java_outer_classname = \"SteamMsgRemoteClient\";" >>gentemp/SteamMsgRemoteClient.proto
cat ../SteamKit/Resources/Protobufs/steamclient/steammessages_remoteclient.proto >>gentemp/SteamMsgRemoteClient.proto

protoc --java_out=src --proto_path gentemp --proto_path ../SteamKit/Resources/Protobufs/steamclient/ --proto_path /usr/include gentemp/steammessages_base.proto

protoc --java_out=src --proto_path gentemp --proto_path ../SteamKit/Resources/Protobufs/steamclient/ --proto_path /usr/include gentemp/SteamMsgRemoteClient.proto
