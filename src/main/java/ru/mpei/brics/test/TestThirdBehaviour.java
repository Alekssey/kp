package ru.mpei.brics.test;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class TestThirdBehaviour extends TickerBehaviour {
    private int counter;
    public TestThirdBehaviour(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        System.err.println("third act");
        counter++;
        if(counter > 3) {
            this.stop();
        }
    }
}
