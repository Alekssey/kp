package ru.mpei.brics.extention.agentDescription;

import lombok.Data;
import lombok.extern.slf4j.Slf4j;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@Data @Slf4j
@XmlAccessorType(XmlAccessType.FIELD)
public class AgentDescription {
    public AgentDescription() {
        log.info("Agent Description created");
    }

    @XmlElement(name="name")
    private String agentName;
    @XmlElement(name="class")
    private Class agentClass;
    @XmlElement(name="arg")

    private Object[] args;
}
