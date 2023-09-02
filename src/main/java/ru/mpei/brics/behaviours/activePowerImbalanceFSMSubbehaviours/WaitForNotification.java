package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import ru.mpei.brics.extention.dto.AgentToAgentDto;
import ru.mpei.brics.extention.helpers.JacksonHelper;

@Slf4j
public class WaitForNotification extends Behaviour {

    private boolean doneFlg = false;
    private int behaviourResult;

    public WaitForNotification(Agent a) {
        super(a);
        System.err.println(myAgent.getLocalName() + " wait for notification behaviour created");
    }

    @Override
    public void onStart() {
        System.err.println(myAgent.getLocalName() + " wait for notification behaviour start");
    }

    @Override
    public void action() {
        System.err.println(myAgent.getLocalName() + " wait for notification behaviour act");
        ACLMessage msg = myAgent.receive(MessageTemplate.MatchPerformative(ACLMessage.REQUEST));
        if(msg != null) {
            AgentToAgentDto dto = JacksonHelper.fromJackson(msg.getContent(), AgentToAgentDto.class);
            switch (dto.getStatus()) {
                case FAIL:
                    this.behaviourResult = 1;
//                    sendConfirmAnswer(msg);
                    break;
                case SUCCESS:
                    this.behaviourResult = 2;
                    break;
            }
            this.doneFlg = true;
        } else {
            block();
        }
    }

    @Override
    public int onEnd() {
        System.err.println(myAgent.getLocalName() + " wait for notification behaviour end");
        return this.behaviourResult;
    }

    @Override
    public boolean done() {
        return this.doneFlg;
    }

    private void sendConfirmAnswer(ACLMessage msg) {
        ACLMessage answer = new ACLMessage();
        answer.setPerformative(ACLMessage.CONFIRM);
        answer.addReceiver(msg.getSender());
        myAgent.send(answer);
    }
}
