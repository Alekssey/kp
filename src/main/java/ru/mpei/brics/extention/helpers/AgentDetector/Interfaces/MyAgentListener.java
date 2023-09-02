package ru.mpei.brics.extention.helpers.AgentDetector.Interfaces;

import jade.core.AID;

import java.util.List;

@FunctionalInterface
public interface MyAgentListener {
    void listen(List<AID> aidList);
}
