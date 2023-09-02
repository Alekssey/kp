package ru.mpei.brics.behaviours;

import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;
import ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours.*;
import ru.mpei.brics.test.TestSecondBehaviour;

public class ActivePowerImbalanceFSM extends FSMBehaviour {

    public ActivePowerImbalanceFSM(Agent a, long period) {
        super(a);
        registerFirstState(new SendFitnessValue(myAgent), "sendFitness");
        registerState(new ReceiveFitnessValues(myAgent), "receiveFitness");
//        registerState(new TestSecondBehaviour(), "receiveFitness");
        registerState(new RegulateFrequency(myAgent, period), "regulateFrequency");
        registerState(new SendSuccessMsg(myAgent), "sendSuccess");
        registerState(new SendFailMsg(myAgent), "sendFail");
        registerState(new WaitForNotification(myAgent), "notificationWaiting");
        registerLastState(new LastBehaviour(myAgent), "end");

        registerDefaultTransition("sendFitness", "receiveFitness");

        registerTransition("receiveFitness", "regulateFrequency", 1);
        registerTransition("regulateFrequency", "sendSuccess", 1);
        registerTransition("regulateFrequency", "sendFail", 2);
        registerDefaultTransition("sendFail", "notificationWaiting");
        registerDefaultTransition("sendSuccess", "end");
        
        registerTransition("receiveFitness", "notificationWaiting", 2);
        registerTransition("notificationWaiting", "regulateFrequency", 1);
        registerTransition("notificationWaiting", "end", 2);
        registerDefaultTransition("regulateFrequency", "end");

//        registerDefaultTransition("sendFitness", "receiveFitness");
//        registerDefaultTransition("receiveFitness", "end");

    }
}
