package ru.mpei.brics.extention.regulator;

public interface Regulator {
    double getSupplement(double targetValue, double currentValue);
}
