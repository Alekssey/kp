package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.AID;
import jade.core.Agent;
import jade.lang.acl.ACLMessage;
import jade.proto.AchieveREInitiator;
import lombok.extern.slf4j.Slf4j;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;
import ru.mpei.brics.extention.dto.TransferDutyStatus;
import ru.mpei.brics.extention.helpers.JacksonHelper;

import java.util.Date;
import java.util.Vector;

@Slf4j
public class SendFailMsg extends AchieveREInitiator {
    private NetworkElementConfiguration cfg = ((NetworkElementAgent) myAgent).getCfg();
    private AID receiverAgent = null;
    private int behaviourResult;

    public SendFailMsg(Agent a, ACLMessage msg) {
        super(a, msg);
    }

    @Override
    public void onStart() {
        if ((this.receiverAgent = findNexInQueue(myAgent.getAID())) == null) {
            this.behaviourResult = 2;
        }

        super.onStart();
    }

    @Override
    protected Vector prepareRequests(ACLMessage request) {
        request.setPerformative(ACLMessage.REQUEST);
        request.setProtocol("transfer duty");
        request.addReceiver(this.receiverAgent);
        request.setContent(JacksonHelper.toJackson(TransferDutyStatus.FAIL));
        request.setReplyByDate(new Date(System.currentTimeMillis() + 1000));
        return super.prepareRequests(request);
    }

    @Override
    protected void handleInform(ACLMessage inform) {
        TransferDutyStatus status = JacksonHelper.fromJackson(inform.getContent(), TransferDutyStatus.class);

        if (status.equals(TransferDutyStatus.CONFIRM)) {
            this.behaviourResult = 1;
        } else if (status.equals(TransferDutyStatus.DISCONFIRM)
                && (this.receiverAgent = findNexInQueue(this.receiverAgent)) != null) {
            reset(new ACLMessage());
        } else if (this.receiverAgent == null) {
            log.error("It is impossible to eliminate the violation");
            this.behaviourResult = 2;
        }
    }

    @Override
    protected void handleAllResultNotifications(Vector resultNotifications) {
        log.error(" handle all info ");
        if (resultNotifications.isEmpty() && (this.receiverAgent = findNexInQueue(this.receiverAgent)) != null) {
            reset(new ACLMessage());
        } else if (resultNotifications.isEmpty() && this.receiverAgent == null) {
            log.error("It is impossible to eliminate the violation");
            this.behaviourResult = 2;
        }
    }

    @Override
    public int onEnd() {
        log.error(" on end : {} ", this.behaviourResult);
        sendBlockMsg();
        return this.behaviourResult;
    }

    private AID findNexInQueue(AID aid) {
        AID nextAgent = null;
        for (int i = 0; i < this.cfg.getFitnessValues().size(); i++) {
            double fitness = this.cfg.getFitnessValues().get(i);
            if(this.cfg.getAgentsQueue().get(fitness).equals(aid)
                    && i + 1 < this.cfg.getFitnessValues().size()) {
                nextAgent = this.cfg.getAgentsQueue().get(this.cfg.getFitnessValues().get(i + 1));
                break;
            }
        }
        return nextAgent;
    }

    private void sendBlockMsg() {
        if (!((NetworkElementAgent) myAgent).getADetector().getActiveAgents().isEmpty()) {
            ACLMessage blockMsg = new ACLMessage();
            blockMsg.setPerformative(ACLMessage.REQUEST);
            blockMsg.setProtocol("transfer duty");
            blockMsg.setContent(JacksonHelper.toJackson(TransferDutyStatus.BLOCK));
            for (AID receiver : ((NetworkElementAgent) myAgent).getADetector().getActiveAgents()) {
                blockMsg.addReceiver(receiver);
            }
            myAgent.send(blockMsg);
        }
    }
}
