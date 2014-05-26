== Usage ==

`./run.sh 192.168.1.1 12345 KEY`

replace with your own IP address, your own Steam control port (use Process Explorer's TCP panel to see the ports that Steam listens on: it's the second one) and your own key (which you can find with the instructions on http://codingrange.com/blog/steam-in-home-streaming-control-protocol)

== Configuration ==

Configuration is done through environmental variables.

TCP control protocol configs:

- VARODAHN_APPID: app ID of the game to launch. Defaults to 400 (portal)
- VARODAHN_DONTSTART: don't start any game; just connect to the control port
- VARODAHN_ECHO: echo all packets received on the control protocol

UDP streaming protocol configs:

- VARODAHN_STREAMNOPARSE: do not try to parse Protocol Buffer messages

== Thanks ==

This project uses third party libraries, including
- the Spongycastle encryption libraries: http://rtyley.github.io/spongycastle/
- Google Protocol Buffers library: https://developers.google.com/protocol-buffers/docs/overview
- the Protocol Buffer descriptors extracted from Steam by the SteamKit project: https://github.com/SteamRE/SteamKit
- the work done by http://codingrange.com/blog/steam-in-home-streaming-control-protocol

Thanks!
