package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.AgentContainer;
import jade.core.behaviours.OneShotBehaviour;
import jade.core.behaviours.TickerBehaviour;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import ru.mpei.brics.extention.ApplicationContextHolder;

import java.lang.reflect.Field;

@Slf4j
public class BlockBehaviour extends TickerBehaviour {
    public BlockBehaviour(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        log.error("Agent {} blocked", myAgent.getLocalName());
    }


//    @Override
//    public void action() {
//        ApplicationContext context = ApplicationContextHolder.getContext();
//        jade.wrapper.AgentContainer mainContainer = (jade.wrapper.AgentContainer) context.getBean("jadeMainContainer");
//
//        try {
//            AgentController agentController = mainContainer.getAgent(myAgent.getLocalName());
//
//            Field agentAidField = agentController.getClass().getDeclaredField("agentID");
//            agentAidField.setAccessible(true);
//            Field agentContainerField = agentController.getClass().getDeclaredField("myContainer");
//            agentContainerField.setAccessible(true);
//
//            AID agentAID = (AID) agentAidField.get(agentController);
//            jade.core.AgentContainer agentContainer = (jade.core.AgentContainer) agentContainerField.get(agentController);
//
//            agentContainer.acquireLocalAgent(agentAID);
//
//
//        } catch (ControllerException | NoSuchFieldException | IllegalAccessException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
//        }
//    }
}
