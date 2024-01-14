package ru.mpei.brics.behaviours;

import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import jade.lang.acl.ACLMessage;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours.*;

public class ActivePowerImbalanceFSM extends FSMBehaviour {
    private static String
            SEND_FITNESS = "send_fitness",
            RECEIVE_FITNESS = "receive_fitness",
            REGULATE_FREQUENCY = "regulate_frequency",
            SEND_SUCCESS = "send_success",
            SEND_FAIL = "send_fail",
            NOTIFICATION_WAITING = "notification_waiting",
            WAIT_IF_QUE_END = "wait_if_que_end",
            END = "end";

    public ActivePowerImbalanceFSM(NetworkElementAgent a, long period) {
        super(a);
        a.setStartTime(System.currentTimeMillis());

        registerFirstState(new SendFitnessValue(a), SEND_FITNESS);
        registerState(new ReceiveFitnessWithTimeOut(a), RECEIVE_FITNESS);
        registerState(new RegulateFrequency(a, period), REGULATE_FREQUENCY);
        registerState(new SendSuccessMsg(a), SEND_SUCCESS);
        registerState(new SendFailMsg(a, new ACLMessage()), SEND_FAIL);
        registerState(new WaitForNotification(a), NOTIFICATION_WAITING);
        registerState(new WaitIfQueEnd(a, 60_000), WAIT_IF_QUE_END);
        registerLastState(new LastBehaviour(a), END);

        registerDefaultTransition(SEND_FITNESS, RECEIVE_FITNESS);

        registerTransition(RECEIVE_FITNESS, REGULATE_FREQUENCY, 1);
        registerTransition(REGULATE_FREQUENCY, SEND_SUCCESS, 1);
        registerTransition(REGULATE_FREQUENCY, SEND_FAIL, 2);
        registerTransition(SEND_FAIL, NOTIFICATION_WAITING, 1);
        registerTransition(SEND_FAIL, WAIT_IF_QUE_END, 2);
        registerDefaultTransition(SEND_SUCCESS, END);
        registerDefaultTransition(WAIT_IF_QUE_END, END);
        
        registerTransition(RECEIVE_FITNESS, NOTIFICATION_WAITING, 2);
        registerTransition(NOTIFICATION_WAITING, REGULATE_FREQUENCY, 1);
        registerTransition(NOTIFICATION_WAITING, END, 2);
        registerTransition(NOTIFICATION_WAITING, WAIT_IF_QUE_END, 3);
        registerDefaultTransition(REGULATE_FREQUENCY, END);

    }
}
