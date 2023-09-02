package ru.mpei.brics.extention.helpers.AgentDetector.Helpers;

//import AgentDetector.Interfaces.IBuilderMethods;
import lombok.SneakyThrows;
import ru.mpei.brics.extention.helpers.AgentDetector.Interfaces.BuilderMethodsInterface;

import java.net.InetAddress;
import java.nio.ByteBuffer;

public class PacketBuilderInterface implements BuilderMethodsInterface {

    private String iface;
    int addCount = 0;
    private byte [] packet;
    private int totalLength;
    byte[] platformDescBytes;

    public PacketBuilderInterface(String interfaceName) {
        this.iface = interfaceName;
    }

    @Override
    @SneakyThrows
    public PacketBuilderInterface addHeader(String data){
        platformDescBytes = data.getBytes();
        int dataLength = platformDescBytes.length;
        totalLength = dataLength + 28;

        packet = new byte[totalLength + (iface.equals("\\Device\\NPF_Loopback") ? 4 : 14)];
        /* send to all*/
        byte[] ipDestinationBytes = InetAddress.getByName("255.255.255.255").getAddress();
        byte[] ipSourceBytes = InetAddress.getByName("127.0.0.1").getAddress();

        /* set NPF_Loopback as iface to use. WORKS ONLY FOR WINDOWS*/
        for (int i = 0, j = 7; i < 1; i++, j++) packet[i] = longToBytes(0x02)[j];

        //Header Length = 20 bytes
        for (int i = 4 + addCount, j = 7; i < 5 + addCount; i++, j++) packet[i] = longToBytes(69)[j];
        //Differentiated Services Field
        for (int i = 5 + addCount, j = 7; i < 6 + addCount; i++, j++) packet[i] = longToBytes(0x00)[j];
        //Total Length
        for (int i = 6 + addCount, j = 6; i < 8 + addCount; i++, j++) packet[i] = longToBytes(totalLength)[j];
        //Identification - for fragmented packages
        for (int i = 8 + addCount, j = 6; i < 10 + addCount; i++, j++) packet[i] = longToBytes(33500)[j];
        //Flag, Fragment Offset - for fragmented packages
        for (int i = 10 + addCount, j = 6; i < 12 + addCount; i++, j++) packet[i] = longToBytes(0x00)[j];
        //Time to Live - max limit for moving through the network
        for (int i = 12 + addCount, j = 7; i < 13 + addCount; i++, j++) packet[i] = longToBytes(128)[j];
        //Protocol - UDP
        for (int i = 13 + addCount, j = 7; i < 14 + addCount; i++, j++) packet[i] = longToBytes(17)[j];
        //Header Checksum, can be 0x00 if it is not calculated
        for (int i = 14 + addCount, j = 6; i < 16 + addCount; i++, j++) packet[i] = longToBytes(0x00)[j];

        for (int i = 16 + addCount, j = 0; i < 20 + addCount; i++, j++) packet[i] = ipSourceBytes[j];
        for (int i = 20 + addCount, j = 0; i < 24 + addCount; i++, j++) packet[i] = ipDestinationBytes[j];
        return this;
    }

    @Override
    public PacketBuilderInterface addUdpPart(int portToSend){
        //Source port
        for (int i = 24 + addCount, j = 6; i < 26 + addCount; i++, j++) packet[i] = longToBytes(portToSend)[j];
        //Destination port
        for (int i = 26 + addCount, j = 6; i < 28 + addCount; i++, j++) packet[i] = longToBytes(portToSend)[j];
        //Length
        int length = totalLength - 20;
        for (int i = 28 + addCount, j = 6; i < 30 + addCount; i++, j++) packet[i] = longToBytes(length)[j];
        //Checksum, can be 0x00 if it is not calculated
        for (int i = 30 + addCount, j = 6; i < 32 + addCount; i++, j++) packet[i] = longToBytes(0x0000)[j];


        return this;
    }

    @Override
    public byte[] addPayload(){
        //Data
        System.arraycopy(platformDescBytes, 0, packet, 32 + addCount, platformDescBytes.length);

        return packet;
    }

    public byte[] longToBytes(long x) {
        ByteBuffer buffer = ByteBuffer.allocate(Long.BYTES);
        buffer.putLong(x);
        return buffer.array();
    }

}
