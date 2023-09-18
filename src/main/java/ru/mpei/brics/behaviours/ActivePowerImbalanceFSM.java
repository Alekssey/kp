package ru.mpei.brics.behaviours;

import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;
import ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours.*;

public class ActivePowerImbalanceFSM extends FSMBehaviour {
    private static String SEND_FITNESS, RECEIVE_FITNESS, REGULATE_FREQUENCY, SEND_SUCCESS,
    SEND_FAIL, NOTIFICATION_WAITING, BLOCK, END;

    public ActivePowerImbalanceFSM(Agent a, long period) {
        super(a);

        registerFirstState(new SendFitnessValue(myAgent), "SEND_FITNESS");
        registerState(new ReceiveFitnessValues(myAgent), "RECEIVE_FITNESS");
        registerState(new RegulateFrequency(myAgent, period), "REGULATE_FREQUENCY");
        registerState(new SendSuccessMsg(myAgent), "SEND_SUCCESS");
        registerState(new AchieveSendFailMsg(myAgent, new ACLMessage()), "SEND_FAIL");
        registerState(new WaitForNotification(myAgent), "NOTIFICATION_WAITING");
        registerState(new BlockBehaviour(myAgent), "BLOCK");
        registerLastState(new LastBehaviour(myAgent), "END");

        registerDefaultTransition("SEND_FITNESS", "RECEIVE_FITNESS");

        registerTransition("RECEIVE_FITNESS", "REGULATE_FREQUENCY", 1);
        registerTransition("REGULATE_FREQUENCY", "SEND_SUCCESS", 1);
        registerTransition("REGULATE_FREQUENCY", "SEND_FAIL", 2);
        registerTransition("SEND_FAIL", "NOTIFICATION_WAITING", 1);
        registerTransition("SEND_FAIL", "BLOCK", 2);
        registerDefaultTransition("SEND_SUCCESS", "END");
        registerDefaultTransition("BLOCK", "END");
        
        registerTransition("RECEIVE_FITNESS", "NOTIFICATION_WAITING", 2);
        registerTransition("NOTIFICATION_WAITING", "REGULATE_FREQUENCY", 1);
        registerTransition("NOTIFICATION_WAITING", "END", 2);
        registerDefaultTransition("REGULATE_FREQUENCY", "END");

    }
}
