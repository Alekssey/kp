package ru.mpei.brics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.SpringApplication;
import org.springframework.context.ApplicationContext;
import ru.mpei.brics.extention.ApplicationContextHolder;
import ru.mpei.brics.extention.agentDescription.AgentDescription;
import ru.mpei.brics.extention.agentDescription.AgentDescriptionContainer;

@Slf4j
@org.springframework.boot.autoconfigure.SpringBootApplication
public class SpringBootApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpringBootApplication.class, args);

        createAgents();
    }

    private static void createAgents() {
        ApplicationContext springContext = ApplicationContextHolder.getContext();
        AgentDescriptionContainer agentDescriptionsContainer = (AgentDescriptionContainer) springContext.getBean("agentDescriptionsContainer");
        AgentDescription agentDescription = (AgentDescription) springContext.getBean("agentDescription");

        if (agentDescriptionsContainer.getAgentDescriptionsList() != null
                && !agentDescriptionsContainer.getAgentDescriptionsList().isEmpty()) {
            for (AgentDescription ad : agentDescriptionsContainer.getAgentDescriptionsList()) {
                agentDescription.setAgentName(ad.getAgentName());
                agentDescription.setAgentClass(ad.getAgentClass());
                agentDescription.setArgs(ad.getArgs());

                springContext.getBean("agentCreator");
            }
        }
    }

}
