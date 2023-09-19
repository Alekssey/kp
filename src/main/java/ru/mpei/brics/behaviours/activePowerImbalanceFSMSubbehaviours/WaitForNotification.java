package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import lombok.extern.slf4j.Slf4j;
import ru.mpei.brics.extention.dto.TransferDutyStatus;
import ru.mpei.brics.extention.helpers.JacksonHelper;

@Slf4j
public class WaitForNotification extends Behaviour {

    private boolean doneFlg = false;
    private int behaviourResult;
    private MessageTemplate mt;

    public WaitForNotification(Agent a) {
        super(a);
        this.mt = MessageTemplate.and(
                MessageTemplate.MatchPerformative(ACLMessage.REQUEST),
                MessageTemplate.MatchProtocol("transfer duty")
        );
    }

    @Override
    public void action() {
        ACLMessage request = myAgent.receive();
        if(request != null) {
            TransferDutyStatus status = JacksonHelper.fromJackson(request.getContent(), TransferDutyStatus.class);
            switch (status) {
                case FAIL:
                    log.error(" Fail message received by {}", myAgent.getLocalName());
                    this.behaviourResult = 1;
                    handleRequest(request);
                    break;
                case SUCCESS:
                    this.behaviourResult = 2;
                    break;
                case BLOCK:
                    this.behaviourResult = 3;
                    break;
            }
            this.doneFlg = true;
        } else {
            block();
        }
    }

    @Override
    public int onEnd() {
        return this.behaviourResult;
    }

    @Override
    public boolean done() {
        return this.doneFlg;
    }

    private void handleRequest(ACLMessage request) {
        ACLMessage response = request.createReply();
        response.setPerformative(ACLMessage.INFORM);
        response.setProtocol("transfer duty");
        response.setContent(JacksonHelper.toJackson(TransferDutyStatus.CONFIRM));
        myAgent.send(response);
    }
}
