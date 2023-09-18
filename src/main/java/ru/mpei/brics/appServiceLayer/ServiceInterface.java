package ru.mpei.brics.appServiceLayer;

import jade.core.Agent;

import java.util.List;

public interface ServiceInterface {
    String setPowerToLoad(String loadName, double p, double q);
    void unlockAgents(List<String> agentsNames);
    Agent getAgentFromContext(String agentName);
}
