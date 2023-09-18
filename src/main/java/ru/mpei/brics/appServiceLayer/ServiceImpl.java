package ru.mpei.brics.appServiceLayer;

import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.ApplicationContextHolder;

import java.lang.reflect.Field;
import java.util.List;


@Slf4j
@Service("service")
public class ServiceImpl implements ServiceInterface {
    private int lastIndex = 0;
    private double startTime = System.currentTimeMillis();

    @Override
    public String setPowerToLoad(String loadName, double p, double q) {
        NetworkElementAgent loadAgent = (NetworkElementAgent) getAgentFromContext(loadName);
        loadAgent.getCfg().setCurrentP(p);
        loadAgent.getCfg().setCurrentQ(q);
        return "Values successfully changed: "
                + "Current P: " + loadAgent.getCfg().getCurrentP()
                + "; Current Q: " + loadAgent.getCfg().getCurrentQ();
    }

    @Override
    public void unlockAgents(List<String> agentsNames) {
        for (String agentName : agentsNames) {
            getAgentFromContext(agentName);
        }
    }

    @Override
    public Agent getAgentFromContext(String agentName) {
        ApplicationContext context = ApplicationContextHolder.getContext();
        AgentContainer mainContainer = (AgentContainer) context.getBean("jadeMainContainer");

        Agent agentInstance = null;

        try {
            AgentController agentController = mainContainer.getAgent(agentName);

            Field agentAidField = agentController.getClass().getDeclaredField("agentID");
            agentAidField.setAccessible(true);
            Field agentContainerField = agentController.getClass().getDeclaredField("myContainer");
            agentContainerField.setAccessible(true);

            AID agentAID = (AID) agentAidField.get(agentController);
            jade.core.AgentContainer agentContainer = (jade.core.AgentContainer) agentContainerField.get(agentController);

            agentInstance = agentContainer.acquireLocalAgent(agentAID);
            agentContainer.releaseLocalAgent(agentAID);

        } catch (ControllerException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return agentInstance;
    }

}
