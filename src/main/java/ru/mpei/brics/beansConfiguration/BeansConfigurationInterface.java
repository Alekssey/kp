package ru.mpei.brics.beansConfiguration;

import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import org.kie.api.runtime.KieContainer;
import ru.mpei.brics.extention.agentDescription.AgentDescription;
import ru.mpei.brics.extention.agentDescription.AgentDescriptionContainer;

public interface BeansConfigurationInterface {
    AgentDescription createAgentDescription();
    AgentDescriptionContainer unmarshallConfig();
    AgentController createAgent (AgentDescription agentDescription, AgentContainer mainContainer);
    AgentContainer startJadeMainContainer();
    KieContainer createKieContainer();

}








//    AgentController createAgent ();
//    AgentController createAgent (AgentDescription agentDescription);
