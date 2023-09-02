package ru.mpei.brics.beansConfiguration;

import jade.core.ProfileImpl;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import jade.wrapper.StaleProxyException;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.runtime.KieContainer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.ConfigurableBeanFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Scope;
import ru.mpei.brics.extention.agentDescription.AgentDescription;
import ru.mpei.brics.extention.agentDescription.AgentDescriptionContainer;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;

@Configuration("agentConfiguration")
@Slf4j
public class BeansConfigurationImpl implements BeansConfigurationInterface {
    @Value("${agents.config.file.path}")
    private String configFilePath;

    @Override
    @Bean("agentDescription")
    public AgentDescription createAgentDescription() {
        return new AgentDescription();
    }

    @Override
    @Bean("agentDescriptionsContainer")
    public AgentDescriptionContainer unmarshallConfig() {
        AgentDescriptionContainer adContainer = null;
        try{
            JAXBContext context = JAXBContext.newInstance(AgentDescriptionContainer.class);
            Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
            adContainer = (AgentDescriptionContainer) jaxbUnmarshaller.unmarshal(new File(configFilePath));
        } catch (JAXBException e){
            e.printStackTrace();
        }
        return adContainer;
    }

    @Override
    @Scope(ConfigurableBeanFactory.SCOPE_PROTOTYPE)
    @Bean("agentCreator")
    public AgentController createAgent(AgentDescription agentDescription, AgentContainer mainContainer) {

        AgentController newAgent = null;

        try {
            newAgent = mainContainer.createNewAgent(
                    agentDescription.getAgentName(),
                    agentDescription.getAgentClass().getName(),
                    agentDescription.getArgs());
            newAgent.start();
        } catch (StaleProxyException e) {
            e.printStackTrace();
        }
        return newAgent;
    }


    @Override
    @Bean("jadeMainContainer")
    public AgentContainer startJadeMainContainer() {
        ProfileImpl profile = new ProfileImpl();
//        profile.setParameter("gui", "true");
        jade.core.Runtime.instance().setCloseVM(true);
        AgentContainer mainContainer = jade.core.Runtime.instance().createMainContainer(profile);

        try {
            mainContainer.start();
        } catch (ControllerException e) {
            e.printStackTrace();
        }

        return mainContainer;
    }

    @Override
    @Bean("kieContainer")
    public KieContainer createKieContainer() {
        KieServices ks = KieServices.Factory.get();
        KieContainer kContainer = ks.getKieClasspathContainer();
        return kContainer;
    }
}
