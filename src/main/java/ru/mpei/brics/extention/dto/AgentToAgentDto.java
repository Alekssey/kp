package ru.mpei.brics.extention.dto;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
//@AllArgsConstructor
@NoArgsConstructor
public class AgentToAgentDto {

    public AgentToAgentDto(ru.mpei.brics.extention.dto.TradeStatus status) {
        this.status = status;
    }

    private TradeStatus status;
}

