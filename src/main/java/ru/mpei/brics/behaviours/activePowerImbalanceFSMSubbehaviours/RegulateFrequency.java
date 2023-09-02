package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.context.ApplicationContext;
import org.springframework.http.ResponseEntity;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.ApplicationContextHolder;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;
import ru.mpei.brics.extention.dto.CommandTO;
import ru.mpei.brics.extention.dto.DroolsFrequencyAllowDto;
import ru.mpei.brics.extention.dto.TSDBResponse;
import ru.mpei.brics.extention.helpers.HttpRequestsBuilder;
import ru.mpei.brics.extention.helpers.JacksonHelper;
import ru.mpei.brics.extention.regulator.PiRegulator;
import ru.mpei.brics.extention.regulator.Regulator;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class RegulateFrequency extends TickerBehaviour {

    private NetworkElementConfiguration cfg = ((NetworkElementAgent) myAgent).getCfg();
    private Regulator regulator = new PiRegulator(cfg.getKp(), cfg.getKi());
    private HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
    private String url;
    private int behaviourResult;

    public RegulateFrequency(Agent a, long period) {
        super(a, period);
        System.err.println(myAgent.getLocalName() + " regulate frequency behaviour created");
    }

    @Override
    public void onStart() {
        System.err.println(myAgent.getLocalName() + " regulate frequency behaviour start");
        this.url = "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/iec104/send/command";

        if(((NetworkElementAgent) myAgent).getKieSession() == null) {
            ApplicationContext context = ApplicationContextHolder.getContext();
            KieContainer kieContainer = (KieContainer) context.getBean("kieContainer");
            KieSession kieSession = kieContainer.newKieSession();
            ((NetworkElementAgent) myAgent).setKieSession(kieSession);
        }
    }

    @Override
    protected void onTick() {
        System.err.println(myAgent.getLocalName() + " regulate frequency behaviour act");

        DroolsFrequencyAllowDto dto = new DroolsFrequencyAllowDto(
                myAgent.getLocalName(), cfg.getMaxP(), cfg.getCurrentP(), cfg.getF());
        ((NetworkElementAgent) myAgent).getKieSession().insert(dto);
        ((NetworkElementAgent) myAgent).getKieSession().fireAllRules();
//        log.error("Allow signal: {}", dto.isAllow());
        if(dto.isAllow()) {
            double supplement = regulator.getSupplement(cfg.getTargetFreq(), cfg.getF());
            if(cfg.getCurrentP() + supplement > cfg.getMaxP()) {
                cfg.setCurrentP(cfg.getMaxP());
                this.behaviourResult = 2;
                this.stop();
            } else if (cfg.getCurrentP() + supplement <= 0) {
                cfg.setCurrentP(0);
                this.behaviourResult = 2;
                this.stop();
            } else {
                cfg.setCurrentP(cfg.getCurrentP() + supplement);
            }
            if(cfg.getF() >= cfg.getTargetFreq() - cfg.getDeltaFreq()
                    && cfg.getF() <= cfg.getTargetFreq() + cfg.getDeltaFreq()) {
                this.behaviourResult = 1;
                this.stop();
            }
            sendRequest();
        } else {
            this.behaviourResult = 2;
            this.stop();
        }
    }

    private void sendRequest() {

        ResponseEntity response = requestsBuilder.sendPostRequest(
                this.url,
                new HashMap<>(),
                new CommandTO("TestGen", Double.toString(cfg.getCurrentP())));
    }

    @Override
    public int onEnd() {
        System.err.println(myAgent.getLocalName() + " regulate frequency behaviour end, result: " + behaviourResult);
        return behaviourResult;
    }
}
