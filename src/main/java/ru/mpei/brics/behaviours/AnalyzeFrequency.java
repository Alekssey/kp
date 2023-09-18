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
    private NetworkElementConfiguration cfg = ((NetworkElementAgent) myAgent).getCfg();
    double startTime;

    public AnalyzeFrequency(Agent a, long period) {
        super(a, period);
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
        ((NetworkElementAgent) myAgent).setStartTime(System.currentTimeMillis());
        myAgent.addBehaviour(new ActivePowerImbalanceFSM(myAgent, this.getPeriod()));
        return 1;
    }
}

