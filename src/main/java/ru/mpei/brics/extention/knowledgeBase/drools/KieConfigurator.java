package ru.mpei.brics.extention.knowledgeBase.drools;

import lombok.extern.slf4j.Slf4j;
import org.kie.api.KieServices;
import org.kie.api.builder.*;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.kie.internal.io.ResourceFactory;
import org.springframework.stereotype.Component;

import java.io.FileNotFoundException;
import java.io.IOException;

@Slf4j
@Component
public class KieConfigurator {
    private KieServices kieServices = KieServices.Factory.get();
    private String rulesetPath;

    private KieFileSystem getKieFileSystem() throws FileNotFoundException {
        KieFileSystem kieFileSystem = kieServices.newKieFileSystem();
        try {
        kieFileSystem.write(ResourceFactory.newClassPathResource(this.rulesetPath));

        } catch (RuntimeException e) {
            throw new FileNotFoundException();
        }
        return kieFileSystem;
    }

    private KieContainer getKieContainer() throws FileNotFoundException {
        System.out.println("Container created");
        getKieRepository();
        KieBuilder kb = kieServices.newKieBuilder(getKieFileSystem());
        kb.buildAll();
        KieModule kieModule = kb.getKieModule();
        KieContainer kContainer = kieServices.newKieContainer(kieModule.getReleaseId());
        return kContainer;
    }

    private void getKieRepository() {
        final KieRepository kieRepository = kieServices.getRepository();
        kieRepository.addKieModule(new KieModule() {
            @Override
            public ReleaseId getReleaseId() {
                return kieRepository.getDefaultReleaseId();
            }
        });
    }

    public synchronized KieSession getKieSession(String rulesetPath) throws FileNotFoundException {
        this.rulesetPath = rulesetPath;
        KieSession ks = getKieContainer().newKieSession();
        System.out.println("Session created");

        return ks;
    }
}
