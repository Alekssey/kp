package ru.mpei.brics.extention.agentDescription;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

@Data
@Slf4j
@XmlRootElement (name="agentDescriptions")
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentDescriptionContainer {
    public AgentDescriptionContainer() {
        log.info("Agent Description container created");
    }

    @XmlElement(name="agentDescription")
    private List<AgentDescription> agentDescriptionList;
//    private List<Agent> agentsList;
}
