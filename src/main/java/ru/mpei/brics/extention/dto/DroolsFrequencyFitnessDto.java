package ru.mpei.brics.extention.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DroolsFrequencyFitnessDto {
    private String agentName;
    private double maxP;
    private double currentP;
    private double currentFreq;
    private double fitnessVal;

    public DroolsFrequencyFitnessDto(String agent, double maxP, double currentP, double frequency) {
        this.agentName = agent;
        this.maxP = maxP;
        this.currentP = currentP;
        this.currentFreq = frequency;
    }
}
