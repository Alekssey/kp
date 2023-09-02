package ru.mpei.brics.extention.helpers.AgentDetector.Interfaces;

import jade.core.AID;

import java.util.List;

public interface DetectorMethodsInterface {
    void startDiscovering();
    void startSending();
    List<AID> getActiveAgents();
//    void subscribeOnChange(MyAgentListener l);
    void stopSending();

}

