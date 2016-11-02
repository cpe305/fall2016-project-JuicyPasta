package io.github.honeypot.service;

import org.apache.commons.codec.binary.Hex;

import java.util.Arrays;

/**
 * Created by jackson on 10/19/16.
 * Class to represent an SSH Service.
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
        if ("SSH-2.0-OpenSSH_7.2".equals(input)) {
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

        /**
         * class to represent an SSH packet.
         *
         * @param pack - bytes for the package
         */
        public BinaryPacket(byte[] pack) {
            int idx = 0;

            //SSHv2
            packetLength = unpackInt(pack, idx); idx += 4;
            paddingLength = pack[idx++];
            System.out.println(packetLength);
            System.out.println(paddingLength);

            //Key Exchange
            int messageCode = pack[idx++];
            System.out.println(messageCode);

            //Algorithms
            byte[] cookie = Arrays.copyOfRange(pack, idx, idx + 16); idx += 16;
            System.out.println(printBytes(cookie));

            int kexAlgorithmsLength = unpackInt(pack, idx); idx += 4;
            System.out.println(pack[idx]);
            System.out.println(pack[idx+1]);
            System.out.println(pack[idx+2]);
            System.out.println(pack[idx+3]);
            System.out.println(kexAlgorithmsLength);
            String kexAlgorithmsStr = new String(Arrays.copyOfRange(pack, idx, idx + kexAlgorithmsLength));
            System.out.println(kexAlgorithmsStr);
            int serverHostKeyAlgorithmsLength = unpackInt(pack, idx); idx += 4;
            String serverHostKeyAlgorithms = new String(Arrays.copyOfRange(pack, idx, idx + serverHostKeyAlgorithmsLength));
            System.out.println(serverHostKeyAlgorithms);
            int encryptionClientToServerLength = unpackInt(pack, idx); idx += 4;
            String encryptionClientToServer = new String(Arrays.copyOfRange(pack, idx, idx + encryptionClientToServerLength));
            System.out.println(encryptionClientToServer);
            int encryptionServerToClientLength = unpackInt(pack, idx); idx += 4;
            String encryptionServerToClient = new String(Arrays.copyOfRange(pack, idx, idx + encryptionServerToClientLength));
            System.out.println(encryptionServerToClient);

            int macCliToServLen = unpackInt(pack, idx); idx += 4;
            String macCliToServ = new String(Arrays.copyOfRange(pack, idx, idx + macCliToServLen));
            System.out.println(macCliToServ);
            int macServToCliLen = unpackInt(pack, idx); idx += 4;
            String macServToCli = new String(Arrays.copyOfRange(pack, idx, idx + macServToCliLen));
            System.out.println(macServToCli);



            int payloadLength = packetLength - paddingLength - 1;
            System.out.println(packetLength + idx);
            payload = Arrays.copyOfRange(pack, idx, idx + packetLength-1);

            idx += payloadLength;

            padding = new byte[] {'a'};//Arrays.copyOfRange(pack, idx, idx + paddingLength);
            idx += paddingLength;

            System.out.println("=============");

        }

        public String printBytes(byte[] bytes) {
            return Hex.encodeHexString(bytes);
        }
        public int unpackInt(byte[] bytes, int startidx) {
            int toPack = 0;

            if (true) { // big endian
                toPack += bytes[startidx] << 24;
                toPack += bytes[startidx+1] << 16;
                toPack += bytes[startidx+2] << 8;
                toPack += bytes[startidx+3];
            } else {
                toPack += bytes[startidx];
                toPack += bytes[startidx+1] << 8;
                toPack += bytes[startidx+2] << 16;
                toPack += bytes[startidx+3] << 24;
            }
            return toPack;
        }

        public byte[] pack(String msg) {
            return new byte[0];
        }

        @Override
        public String toString() {
            return String.format("Length: %s\n" + "PadLength: %s\n"
                            + "payload: %s\n" + "padding: %s\n", packetLength,
                    paddingLength, new String(payload), new String(padding));
        }
    }
}
