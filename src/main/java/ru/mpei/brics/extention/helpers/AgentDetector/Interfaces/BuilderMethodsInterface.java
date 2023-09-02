package ru.mpei.brics.extention.helpers.AgentDetector.Interfaces;

//import AgentDetector.Helpers.PacketBuilder;

import ru.mpei.brics.extention.helpers.AgentDetector.Helpers.PacketBuilderInterface;

public interface BuilderMethodsInterface {
    PacketBuilderInterface addHeader(String data);
    PacketBuilderInterface addUdpPart(int portToSend);
    byte[] addPayload();
}
