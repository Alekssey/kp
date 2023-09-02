package ru.mpei.brics.extention.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class DroolsFrequencyAllowDto {
    private String agentName;
    private double maxP;
    private double currentP;
    private double currentFreq;
    private boolean allow;

    public DroolsFrequencyAllowDto(String agentName, double maxP, double currentP, double currentFreq) {
        this.agentName = agentName;
        this.maxP = maxP;
        this.currentP = currentP;
        this.currentFreq = currentFreq;
        this.allow = false;
    }

}
