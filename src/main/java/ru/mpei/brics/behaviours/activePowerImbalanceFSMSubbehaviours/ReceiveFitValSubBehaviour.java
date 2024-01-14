package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;

import java.util.Collections;

@Slf4j
public class ReceiveFitValSubBehaviour extends Behaviour {
    private MessageTemplate mt = null;
    private NetworkElementConfiguration cfg;
    private int msgCounter = 0;

    public ReceiveFitValSubBehaviour(NetworkElementAgent a) {
        super(a);
        this.cfg = a.getCfg();
//        System.err.println(myAgent.getLocalName() + " receive fitness behaviour created");
    }

    @Override
    public void onStart() {
        this.mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.MatchProtocol("fitness values exchange")
        );
//                MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
//        System.err.println(myAgent.getLocalName() + " receive fitness behaviour start");
    }

    @Override
    public void action() {
//        log.error("Receive Fitness behaviour act; number of active agents = {}", ((NetworkElementAgent) myAgent).getADetector().getActiveAgents().size());

        if(((NetworkElementAgent) myAgent).getADetector().getActiveAgents().isEmpty()) return;

        ACLMessage msg = myAgent.receive(mt);
        if(msg != null) {
            msgCounter ++;
//            log.info("{} receive fitness val: {} from {} ", myAgent.getLocalName(), msg.getContent(), msg.getSender().getLocalName());
            double fitnessVal = Double.parseDouble(msg.getContent());
            this.cfg.setPTradeIsOpen(true);
            this.cfg.getAgentsQueue().put(fitnessVal, msg.getSender());
            this.cfg.getFitnessValues().add(fitnessVal);
            this.cfg.getFitnessValues().sort(Collections.reverseOrder());
        } else {
            block();
        }
    }

    @Override
    public int onEnd() {
        System.err.println(myAgent.getLocalName() + " hash map size: " + this.cfg.getAgentsQueue().size());
//        System.err.println(myAgent.getLocalName() + " receive fitness behaviour end");
        if(this.cfg.getAgentsQueue().get(this.cfg.getFitnessValues().get(0)).equals(myAgent.getAID())) {
//            System.err.println(myAgent.getLocalName() + " receive fitness behaviour return 1");
            return 1;
        } else {
//            System.err.println(myAgent.getLocalName() + " receive fitness behaviour return 2");
            return 2;
        }
    }

    @Override
    public boolean done() {
//        System.err.println("done method act");
        return msgCounter == ((NetworkElementAgent) myAgent).getADetector().getActiveAgents().size();
    }

}
