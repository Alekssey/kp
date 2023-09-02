package ru.mpei.brics.test;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;

public class TestFourthBehaviour extends TickerBehaviour {
    private int counter;
    public TestFourthBehaviour(Agent a, long period) {
        super(a, period);
    }

    @Override
    protected void onTick() {
        System.err.println("fourth act");
        counter++;
        if(counter > 3) {
            this.stop();
        }
    }
}
