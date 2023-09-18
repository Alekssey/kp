package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;
import ru.mpei.brics.extention.dto.TransferDutyStatus;
import ru.mpei.brics.extention.helpers.JacksonHelper;

@Slf4j
public class SendFailMsg extends OneShotBehaviour {

    private NetworkElementConfiguration cfg = ((NetworkElementAgent) myAgent).getCfg();
    private int behaviourResult;
    public SendFailMsg(Agent a) {
        super(a);
//        System.err.println(myAgent.getLocalName() + " send fail msg behaviour created");
    }

//    @Override
//    public void onStart() {
//        System.err.println(myAgent.getLocalName() + " send fail msg behaviour start");
//    }

    @Override
    public void action() {
//        System.err.println(myAgent.getLocalName() + " send fail msg behaviour act");

        AID receiverAgent = findNexInQueue();
        if (receiverAgent != null) {
            ACLMessage msg = new ACLMessage();
            msg.setPerformative(ACLMessage.REQUEST);
            msg.setProtocol("unsuccessful regulating");
            msg.setContent(JacksonHelper.toJackson(TransferDutyStatus.FAIL));
            msg.addReceiver(receiverAgent);
            myAgent.send(msg);
            this.behaviourResult = 1;
        } else {
            this.behaviourResult = 2;
        }

    }

    private AID findNexInQueue() {
        AID nextAgent = null;

        for (int i = 0; i < this.cfg.getFitnessValues().size(); i++) {
            double fitness = this.cfg.getFitnessValues().get(i);
            if(this.cfg.getAgentsQueue().get(fitness).equals(myAgent.getAID())
                    && i + 1 < this.cfg.getFitnessValues().size()) {
                nextAgent = this.cfg.getAgentsQueue().get(this.cfg.getFitnessValues().get(i + 1));
                break;
            }
        }
        return nextAgent;
    }

    @Override
    public int onEnd() {
        return this.behaviourResult;
    }

    //    @Override
//    public int onEnd() {
//        System.err.println(myAgent.getLocalName() + " send fail msg behaviour end");
//        return 0;
//    }

}
