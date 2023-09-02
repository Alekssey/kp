package ru.mpei.brics.test;

import jade.core.behaviours.OneShotBehaviour;

public class TestFirstBehaviour extends OneShotBehaviour {
    @Override
    public void action() {
        System.err.println("first");
    }
}
