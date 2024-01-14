package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.behaviours.TickerBehaviour;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.context.ApplicationContext;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.ApplicationContextHolder;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;
import ru.mpei.brics.extention.dto.CommandTO;
import ru.mpei.brics.extention.helpers.HttpRequestsBuilder;
import ru.mpei.brics.extention.regulator.PiRegulator;
import ru.mpei.brics.extention.regulator.Regulator;

import java.util.HashMap;

@Slf4j
public class RegulateFrequency extends TickerBehaviour {

    private NetworkElementConfiguration cfg;
    private final Regulator regulator;
    private final HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
    private final double[] previousFrequencyValue = new double[]{-1.0, -1.0, -1.0, -1.0};
    private double powerBeforeRegulating;
    private String url;
    private int behaviourResult;
    private long startTime;
    private KieSession kieSession;

    public RegulateFrequency(NetworkElementAgent a, long period) {
        super(a, period);
        this.cfg = a.getCfg();
        this.regulator = new PiRegulator(cfg.getKp(), cfg.getKi(), cfg.getCurrentP());
//        this.kieSession = a.getCfg().getKnowledgeBase()
//        System.err.println(myAgent.getLocalName() + " regulate frequency behaviour created");
    }

    @Override
    public void onStart() {
//        System.err.println(myAgent.getLocalName() + " regulate frequency behaviour start");
        this.url = "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/iec104/send/command";
        this.powerBeforeRegulating = this.cfg.getCurrentP();
        System.err.println("my start power " + this.powerBeforeRegulating);

//        if(((NetworkElementAgent) myAgent).getKieSession() == null) {
//            ApplicationContext context = ApplicationContextHolder.getContext();
//            KieContainer kieContainer = (KieContainer) context.getBean("kieContainer");
//            KieSession kieSession = kieContainer.newKieSession();
//            ((NetworkElementAgent) myAgent).setKieSession(kieSession);
//        }
        this.startTime = System.currentTimeMillis();
    }

    @Override
    protected void onTick() {
//        KnowledgeBaseAllowDto dto = new KnowledgeBaseAllowDto(
//                myAgent.getLocalName(), cfg.getMaxP(), cfg.getCurrentP(), cfg.getF());
//        ((NetworkElementAgent) myAgent).getKieSession().insert(dto);
//        ((NetworkElementAgent) myAgent).getKieSession().fireAllRules();

        if(this.cfg.getKnowledgeBase().getAllowSignal(this.cfg) && checkFrequencyChanging()) {

            if(isAllSamplesInNormalRange()) {
                this.behaviourResult = 1;
                System.err.println("ALL SAMPLES IN NORMAL RANGE");
//                Arrays.stream(previousFrequencyValue).forEach(el -> System.err.println(el));
                this.stop();
            }

            double supplement = regulator.getSupplement(cfg.getTargetFreq(), cfg.getF());

            if(supplement >= cfg.getMaxP()) {
                System.err.println(" СРАБОТАЛО УСЛОВИЕ IF УСТАВКА > MAX_P");
                cfg.setCurrentP(cfg.getMaxP());
                this.behaviourResult = 2;
                this.stop();
            } else if (supplement <= cfg.getMinP()) {
                System.err.println(" СРАБОТАЛО УСЛОВИЕ IF УСТАВКА < MIN_P");
                cfg.setCurrentP(cfg.getMinP());
                this.behaviourResult = 2;
                this.stop();
            } else {
                cfg.setCurrentP(supplement);
            }

//            System.err.println("CURRENT P : " + cfg.getCurrentP());
//            System.err.println("TIME: " + (System.currentTimeMillis() - this.startTime) / 1000.0);
            sendCommandToModel();
        } else if (!this.cfg.getKnowledgeBase().getAllowSignal(this.cfg)) {
            this.behaviourResult = 2;
            System.err.println("ЗАПРЕЩАЮЩИЙ СИГНАЛ ОТ DROOLS");
            this.stop();
        } else if (!checkFrequencyChanging()) {
            System.err.println("ЧАСТОТА НЕ МЕНЯЕТСЯ");
            behaviourResult = 2;
            this.cfg.setCurrentP(this.powerBeforeRegulating);
            sendCommandToModel();
            this.stop();
        } /*else if ((System.currentTimeMillis() - this.startTime) / 1000.0 > 30) {
            this.behaviourResult = 2;
            System.err.println("ПРЕВЫШЕНО ДОПУСТИМОЕ ВРЕМЯ РЕГУЛИРОВАНИЯ");
            this.stop();
        }*/
    }

//    /** РАСКОМЕНТИТЬ И УБРАТЬ АТИВНОЕ СЕЙЧАС RETURN TRUE !!!!!*/
    private boolean checkFrequencyChanging() {
        for (int i = 0; i < previousFrequencyValue.length; i++) {
            if (cfg.getF() != previousFrequencyValue[i]) {
                int j = 0;
                while (j <= previousFrequencyValue.length - 2) {
                    previousFrequencyValue[j] = previousFrequencyValue[j + 1];
                    j++;
//                    System.err.println("пересатвляю значения частот");
                }
                previousFrequencyValue[previousFrequencyValue.length - 1] = cfg.getF();
                return true;
            }
        }
        return false;
//        return true;
    }

    private boolean isAllSamplesInNormalRange() {
        for (double f : previousFrequencyValue) {
            if (f < cfg.getTargetFreq() - cfg.getDeltaFreq()
                    || f > cfg.getTargetFreq() + cfg.getDeltaFreq()) {
                return false;
            }
        }
        return true;
    }
/** предыдущая реализация метода checkFrequencyChanging */
/*        if (this.previousFrequencyValue[0] != -1 && this.previousFrequencyValue[1] != -1
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
        return false;*/

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
