package ru.mpei.brics.test;

import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;

public class TestSecondBehaviour extends Behaviour {
    private double val;
    @Override
    public void action() {
        System.err.println("second");
        this.val = Math.random();
    }

    @Override
    public boolean done() {
        return true;
    }

    @Override
    public int onEnd() {
        if(this.val < 0.5) {
            System.err.println("return 1");
            return 1;
        } else {
            System.err.println("return 2");
            return 2;
        }
    }
}
