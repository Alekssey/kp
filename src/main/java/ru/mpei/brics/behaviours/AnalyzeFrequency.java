package ru.mpei.brics.behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.ApplicationContextHolder;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;


@Slf4j
public class AnalyzeFrequency extends TickerBehaviour {

    ApplicationContext context = ApplicationContextHolder.getContext();
    private NetworkElementConfiguration cfg;
//    private ActivePowerImbalanceFSM regulateBehaviour;
    private NetworkElementAgent a;

    public AnalyzeFrequency(NetworkElementAgent a, long period) {
        super(a, period);
        this.a = a;
        this.cfg = a.getCfg();
//        this.regulateBehaviour = new ActivePowerImbalanceFSM(a, this.getPeriod());
    }

    @Override
    protected void onTick() {
        if (cfg.getF() >= cfg.getTargetFreq() + cfg.getDeltaFreq()
                || cfg.getF() <= cfg.getTargetFreq() - cfg.getDeltaFreq()) {
            this.stop();
        }
    }

    @Override
    public int onEnd() {
//        a.setStartTime(System.currentTimeMillis());
        myAgent.addBehaviour(new ActivePowerImbalanceFSM(a, this.getPeriod()));
        return 1;
    }
}

