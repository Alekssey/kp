package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.Agent;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieContainer;
import org.kie.api.runtime.KieSession;
import org.springframework.context.ApplicationContext;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.ApplicationContextHolder;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;
import ru.mpei.brics.extention.dto.DroolsFrequencyFitnessDto;

@Slf4j
public class SendFitnessValue extends OneShotBehaviour {
    ApplicationContext context = ApplicationContextHolder.getContext();
    private NetworkElementConfiguration cfg = ((NetworkElementAgent) myAgent).getCfg();

    public SendFitnessValue(Agent a) {
        super(a);
        System.err.println(myAgent.getLocalName() + " send fitness value behaviour created");
    }

    @Override
    public void onStart() {
        System.err.println(myAgent.getLocalName() + " send fitness value behaviour start");
    }

    @Override
    public void action() {
        System.err.println(myAgent.getLocalName() + " send fitness value behaviour act");
        ACLMessage msg = new ACLMessage();

        msg.setPerformative(ACLMessage.REQUEST);
        msg.setProtocol("initiatePowerTrade");

        double fitnessVal = doRequestFitnessFromDrools();
        msg.setContent(Double.toString(fitnessVal));

//        List<AID> activeAgents = DFHelper.findAgents(myAgent, "networkUnit");
//        List<AID> activeAgents = ((NetworkElementAgent) myAgent).getADetector().getActiveAgents();
//        cfg.setNumberOfActiveAgents(activeAgents.size() - 1);

//        for(AID aid : ((NetworkElementAgent) myAgent).getADetector().getActiveAgents()) {
//            msg.addReceiver(aid);
//        }

        ((NetworkElementAgent) myAgent).getADetector().getActiveAgents().forEach(msg::addReceiver);

        myAgent.send(msg);
        log.info("{} send fitness val: {};", myAgent.getLocalName(), msg.getContent());
    }

    private double doRequestFitnessFromDrools() {
        KieContainer kieContainer = (KieContainer) context.getBean("kieContainer");
        KieSession kieSession = kieContainer.newKieSession();
        if(((NetworkElementAgent) myAgent).getKieSession() == null) {
            ((NetworkElementAgent) myAgent).setKieSession(kieSession);
        }
        DroolsFrequencyFitnessDto dto = new DroolsFrequencyFitnessDto(myAgent.getLocalName(), cfg.getMaxP(), cfg.getCurrentP(), cfg.getF());
        kieSession.insert(dto);
        kieSession.fireAllRules();

        double fitnessVal = dto.getFitnessVal() + Math.random() * 0.00001;
        this.cfg.getFitnessValues().add(fitnessVal);
        this.cfg.getAgentsQueue().put(fitnessVal, myAgent.getAID());

        return fitnessVal;
    }

    @Override
    public int onEnd() {
        System.err.println(myAgent.getLocalName() + " send fitness value behaviour end");
        return 0;
    }
}
