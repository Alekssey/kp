package ru.mpei.brics.test;

import jade.core.Agent;
import jade.core.behaviours.FSMBehaviour;

public class TestFSM extends FSMBehaviour {
    public TestFSM(Agent a) {
        super(a);
        registerFirstState(new TestFirstBehaviour(), "1");
        registerState(new TestSecondBehaviour(), "2");
        registerState(new TestThirdBehaviour(myAgent, 1000), "3");
        registerState(new TestFourthBehaviour(myAgent, 1000), "4");
        registerLastState(new TestFifthBehaviour(), "5");

        registerDefaultTransition("1", "2");
        registerTransition("2", "3", 1);
        registerTransition("2", "4", 2);
        registerDefaultTransition("3", "5");
        registerDefaultTransition("4", "5");
    }
}
