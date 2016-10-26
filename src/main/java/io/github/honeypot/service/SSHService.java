package io.github.honeypot.service;

import java.util.Arrays;

/**
 * Created by jackson on 10/19/16.
 */
public class SSHService extends Service {

    public SSHService() {
        super.serviceName = "SSHService";
    }

    @Override
    public String getPreamble() {
        return null;
    }

    @Override
    public String feed(String input) {
        if (input.equals("SSH-2.0-OpenSSH_7.2")) {
            return "SSH-2.0-OpenSSH_6.7p1 Raspbian-5+deb8u2";
        }

        BinaryPacket packet = new BinaryPacket(input.getBytes());
        System.out.println(packet);

        return "hi";
    }


    public class BinaryPacket {
        int packetLength;
        byte paddingLength;
        byte[] payload;
        byte[] padding;
        byte[] messageAuthenticationCode;

        public BinaryPacket(byte[] pack) {
            int idx = 0;

            if (true) { // big endian
                packetLength += pack[idx++] << 24;
                packetLength += pack[idx++] << 16;
                packetLength += pack[idx++] << 8;
                packetLength += pack[idx++] << 0;
            } else {
                packetLength += pack[idx++] << 0;
                packetLength += pack[idx++] << 8;
                packetLength += pack[idx++] << 16;
                packetLength += pack[idx++] << 24;
            }

            paddingLength = pack[idx++];

            padding = Arrays.copyOfRange(pack, idx, idx + paddingLength);
            idx += paddingLength;

            int payloadLength = packetLength - paddingLength - 1 + 5;
            payload = Arrays.copyOfRange(pack, idx, idx + payloadLength);
            idx += payloadLength;


        }

        public byte[] pack(String msg) {
            return new byte[0];
        }

        @Override
        public String toString() {
            return String.format(
                    "Length: %s\n" +
                    "PadLength: %s\n" +
                    "payload: %s\n" +
                    "padding: %s\n",
                    packetLength, paddingLength, new String(payload), new String(padding));
        }
    }
}
