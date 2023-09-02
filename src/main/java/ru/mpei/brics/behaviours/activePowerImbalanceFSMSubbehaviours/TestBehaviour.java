package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class TestBehaviour extends TickerBehaviour {
    public TestBehaviour(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        System.err.println("test behaviour act");
    }
}
