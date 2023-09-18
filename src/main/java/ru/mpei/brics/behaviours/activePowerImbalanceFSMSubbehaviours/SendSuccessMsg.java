package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.dto.TransferDutyStatus;
import ru.mpei.brics.extention.helpers.JacksonHelper;

@Slf4j
public class SendSuccessMsg extends OneShotBehaviour {

    public SendSuccessMsg(Agent a) {
        super(a);
//        System.err.println(myAgent.getLocalName() + " send success msg behaviour created");
    }

//    @Override
//    public void onStart() {
//        System.err.println(myAgent.getLocalName() + " send success msg behaviour start");
//    }

    @Override
    public void action() {
//        System.err.println(myAgent.getLocalName() + " send success msg behaviour act");
        ACLMessage msg = new ACLMessage();
        msg.setPerformative(ACLMessage.REQUEST);
        msg.setProtocol("transfer duty");
        msg.setContent(JacksonHelper.toJackson(TransferDutyStatus.SUCCESS));

//        for(AID aid : ((NetworkElementAgent) myAgent).getADetector().getActiveAgents()) {
//            msg.addReceiver(aid);
//        }

//        for(AID aid: DFHelper.findAgents(myAgent, "networkUnit")) {
//            if(!aid.equals(myAgent.getAID())) {
//                msg.addReceiver(aid);
//            }
//        }

        ((NetworkElementAgent) myAgent).getADetector().getActiveAgents().forEach(msg::addReceiver);
        myAgent.send(msg);
    }

//    @Override
//    public int onEnd() {
//        System.err.println(myAgent.getLocalName() + " send success msg behaviour end");
//        return 0;
//    }
}
