package ru.mpei.brics.extention.knowledgeBase;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class KnowledgeBaseFitnessDto {
//    private String agentName;
    private double maxP;
    private double minP;
    private double currentP;
    private double currentFreq;
    private double fitnessVal;

    public KnowledgeBaseFitnessDto(double maxP, double minP, double currentP, double frequency) {
//        this.agentName = agent;
        this.maxP = maxP;
        this.minP = minP;
        this.currentP = currentP;
        this.currentFreq = frequency;
    }
}
