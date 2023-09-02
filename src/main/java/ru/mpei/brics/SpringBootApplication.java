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

        ApplicationContext context = ApplicationContextHolder.getContext();
        String[] beanNames = context.getBeanDefinitionNames();
        log.info("Number of beans in context: {}", beanNames.length);
        createAgents();
    }

    private static void createAgents() {
        ApplicationContext context = ApplicationContextHolder.getContext();
        AgentDescriptionContainer agentDescriptionContainer = (AgentDescriptionContainer) context.getBean("agentDescriptionsContainer");
        AgentDescription adBean = (AgentDescription) context.getBean("agentDescription");

        for (AgentDescription ad : agentDescriptionContainer.getAgentDescriptionList()) {
            adBean.setAgentName(ad.getAgentName());
            adBean.setAgentClass(ad.getAgentClass());
            adBean.setArgs(ad.getArgs());

            context.getBean("agentCreator");
        }
    }

}
