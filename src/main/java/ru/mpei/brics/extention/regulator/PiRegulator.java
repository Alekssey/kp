package ru.mpei.brics.extention.regulator;

public class PiRegulator implements Regulator {
    private double kp = 1;
    private double ki = 0.5;
    double proportionalAdd = 0;
    double integralAdd = 0;

    public PiRegulator(double kp, double ki) {
        this.kp = kp;
        this.ki = ki;
    }

    @Override
    public double getSupplement(double targetValue, double currentValue) {
        double delta = targetValue - currentValue;
        this.proportionalAdd = kp * delta;
        this.integralAdd += ki * delta;
        return this.proportionalAdd + this.integralAdd;
    }
}
