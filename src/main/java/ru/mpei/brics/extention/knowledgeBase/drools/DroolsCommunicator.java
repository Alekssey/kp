package ru.mpei.brics.extention.knowledgeBase.drools;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.ApplicationContextHolder;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;
import ru.mpei.brics.extention.knowledgeBase.KnowledgeBaseAllowDto;
import ru.mpei.brics.extention.knowledgeBase.KnowledgeBaseCommunicator;

import ru.mpei.brics.extention.knowledgeBase.KnowledgeBaseFitnessDto;

import java.io.FileNotFoundException;


@Slf4j
public class DroolsCommunicator extends KnowledgeBaseCommunicator {
    private KieSession kieSession = null;

    public DroolsCommunicator(String rulesetPath) throws FileNotFoundException {
            this.kieSession = ApplicationContextHolder.getContext().getBean(KieConfigurator.class).getKieSession(rulesetPath);
    }

    @Override
    public double getFitnessValue(NetworkElementConfiguration cfg) {
        KnowledgeBaseFitnessDto dto = new KnowledgeBaseFitnessDto(
                cfg.getMaxP(), cfg.getMinP(), cfg.getCurrentP(), cfg.getF());
        this.kieSession.insert(dto);
        this.kieSession.fireAllRules();
        return dto.getFitnessVal();
    }

    @Override
    public boolean getAllowSignal(NetworkElementConfiguration cfg) {
        KnowledgeBaseAllowDto dto = new KnowledgeBaseAllowDto(
                cfg.getMaxP(), cfg.getMinP(), cfg.getCurrentP(), cfg.getF());
        this.kieSession.insert(dto);
        this.kieSession.fireAllRules();
        return dto.isAllow();
    }
}
