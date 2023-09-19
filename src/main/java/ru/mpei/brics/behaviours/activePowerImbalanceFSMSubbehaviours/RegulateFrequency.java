package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.context.ApplicationContext;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.ApplicationContextHolder;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;
import ru.mpei.brics.extention.dto.CommandTO;
import ru.mpei.brics.extention.dto.DroolsFrequencyAllowDto;
import ru.mpei.brics.extention.helpers.HttpRequestsBuilder;
import ru.mpei.brics.extention.regulator.PiRegulator;
import ru.mpei.brics.extention.regulator.Regulator;

import java.util.HashMap;

@Slf4j
public class RegulateFrequency extends TickerBehaviour {

    private NetworkElementConfiguration cfg = ((NetworkElementAgent) myAgent).getCfg();
    private final Regulator regulator = new PiRegulator(cfg.getKp(), cfg.getKi());
    private final HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
    private final double[] previousFrequencyValue = new double[]{-1.0, -1.0};
    private double powerBeforeRegulating;
    private String url;
    private int behaviourResult;

    public RegulateFrequency(Agent a, long period) {
        super(a, period);
//        System.err.println(myAgent.getLocalName() + " regulate frequency behaviour created");
    }

    @Override
    public void onStart() {
//        System.err.println(myAgent.getLocalName() + " regulate frequency behaviour start");
        this.url = "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/iec104/send/command";
        this.powerBeforeRegulating = this.cfg.getCurrentP();
        System.err.println("my start power " + this.powerBeforeRegulating);

        if(((NetworkElementAgent) myAgent).getKieSession() == null) {
            ApplicationContext context = ApplicationContextHolder.getContext();
            KieContainer kieContainer = (KieContainer) context.getBean("kieContainer");
            KieSession kieSession = kieContainer.newKieSession();
            ((NetworkElementAgent) myAgent).setKieSession(kieSession);
        }
    }

    @Override
    protected void onTick() {
        DroolsFrequencyAllowDto dto = new DroolsFrequencyAllowDto(
                myAgent.getLocalName(), cfg.getMaxP(), cfg.getCurrentP(), cfg.getF());
        ((NetworkElementAgent) myAgent).getKieSession().insert(dto);
        ((NetworkElementAgent) myAgent).getKieSession().fireAllRules();

        if(dto.isAllow() && checkFrequencyChanging()) {
//            if (!checkFrequencyChanging()) {
//                behaviourResult = 2;
//                this.cfg.setCurrentP(this.powerBeforeRegulating);
//                sendCommandToModel();
//                this.stop();
//
//                System.out.println();
//            }

            if(cfg.getF() >= cfg.getTargetFreq() - cfg.getDeltaFreq()
                    && cfg.getF() <= cfg.getTargetFreq() + cfg.getDeltaFreq()) {
                this.behaviourResult = 1;
                this.stop();
            }

            double supplement = regulator.getSupplement(cfg.getTargetFreq(), cfg.getF());

            if(cfg.getCurrentP() + supplement >= cfg.getMaxP()) {
                cfg.setCurrentP(cfg.getMaxP());
                this.behaviourResult = 2;
                this.stop();
            } else if (cfg.getCurrentP() + supplement <= cfg.getMinP()) {
                cfg.setCurrentP(cfg.getMinP());
                this.behaviourResult = 2;
                this.stop();
            } else {
                cfg.setCurrentP(cfg.getCurrentP() + supplement);
            }

            sendCommandToModel();
        } else if (!dto.isAllow()) {
            this.behaviourResult = 2;
            this.stop();
        } else if (!checkFrequencyChanging()) {
            behaviourResult = 2;
            this.cfg.setCurrentP(this.powerBeforeRegulating);
            sendCommandToModel();
            this.stop();
        }
    }
    
    private boolean checkFrequencyChanging() {
        if (this.previousFrequencyValue[0] != -1 && this.previousFrequencyValue[1] != -1 
                && (this.cfg.getF() != this.previousFrequencyValue[0] || this.cfg.getF() != this.previousFrequencyValue[1])) {
            this.previousFrequencyValue[0] = this.previousFrequencyValue[1];
            this.previousFrequencyValue[1] = this.cfg.getF();
            return true;
        } else if (this.previousFrequencyValue[0] == -1) {
            this.previousFrequencyValue[0] = this.cfg.getF();
            return true;
        } else if (this.previousFrequencyValue[1] == -1) {
            this.previousFrequencyValue[1] = this.cfg.getF();
            return true;
        }
        return false;
    }

    private void sendCommandToModel() {
        requestsBuilder.sendPostRequest(
                this.url,
                new HashMap<>(),
                new CommandTO(this.cfg.getPCommandName(), Double.toString(cfg.getCurrentP())));
    }

    @Override
    public int onEnd() {
        return behaviourResult;
    }
}
