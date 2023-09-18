package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.behaviours.OneShotBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import org.springframework.context.ApplicationContext;
import ru.mpei.brics.extention.ApplicationContextHolder;

import java.lang.reflect.Field;

public class BlockBehaviour extends OneShotBehaviour {

    public BlockBehaviour(Agent a) {
        super(a);
    }

    @Override
    public void action() {
        ApplicationContext context = ApplicationContextHolder.getContext();
        jade.wrapper.AgentContainer mainContainer = (jade.wrapper.AgentContainer) context.getBean("jadeMainContainer");

        try {
            AgentController agentController = mainContainer.getAgent(myAgent.getLocalName());

            Field agentAidField = agentController.getClass().getDeclaredField("agentID");
            agentAidField.setAccessible(true);
            Field agentContainerField = agentController.getClass().getDeclaredField("myContainer");
            agentContainerField.setAccessible(true);

            AID agentAID = (AID) agentAidField.get(agentController);
            jade.core.AgentContainer agentContainer = (jade.core.AgentContainer) agentContainerField.get(agentController);

            agentContainer.acquireLocalAgent(agentAID);


        } catch (ControllerException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }
}
