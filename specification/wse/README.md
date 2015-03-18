## WebSocket Emulation Protocol Specification

This project uses [K3PO](http://github.com/k3po/k3po) to help WebSocket Emulation Protocol ([WSE](SPEC.md)) implementations automate their verification of the necessary requirements.

The integration tests fall into the following categories:
 * Opening Handshake
 * Downstream
 * Upstream
 * Data Frame (Binary)
 * Command Frames (NOP, RECONNECT, CLOSE)
 * Control Frames (Ping, Pong)
 * Closing Handshake
 * Browser Considerations
 * Proxy Considerations
