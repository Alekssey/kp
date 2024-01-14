package ru.mpei.brics.behaviours.activePowerImbalanceFSMSubbehaviours;

import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationContext;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.extention.ApplicationContextHolder;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;

@Slf4j
public class SendFitnessValue extends OneShotBehaviour {
    private ApplicationContext context = ApplicationContextHolder.getContext();
    private NetworkElementConfiguration cfg;

    public SendFitnessValue(NetworkElementAgent a) {
        super(a);
        this.cfg = a.getCfg();
    }

    @Override
    public void action() {
//        if (myAgent.getLocalName().equals("station2")) return;

        ACLMessage msg = new ACLMessage();

        msg.setPerformative(ACLMessage.REQUEST);
        msg.setProtocol("fitness values exchange");

        double fitnessVal = requestFitnessFromDrools();
        msg.setContent(Double.toString(fitnessVal));

        ((NetworkElementAgent) myAgent).getADetector().getActiveAgents().forEach(msg::addReceiver);

        myAgent.send(msg);
        log.info("{} send fitness val: {};", myAgent.getLocalName(), msg.getContent());
    }

    private double requestFitnessFromDrools() {
//        KieContainer kieContainer = (KieContainer) context.getBean("kieContainer");
//        KieSession kieSession = kieContainer.newKieSession();
//        KieSession kieSession = null;
//        if (((NetworkElementAgent) myAgent).getKieSession() == null) {
//            try {
//                kieSession = context.getBean(KieConfigurator.class).getKieSession((String) myAgent.getArguments()[1]);
//                ((NetworkElementAgent) myAgent).setKieSession(kieSession);
//            } catch (IOException e) {
//                throw new RuntimeException(e);
//            }
//        }
//        if(((NetworkElementAgent) myAgent).getKieSession() == null) {
//            ((NetworkElementAgent) myAgent).setKieSession(kieSession);
//        }
//        KnowledgeBaseFitnessDto dto = new KnowledgeBaseFitnessDto(myAgent.getLocalName(), cfg.getMaxP(), cfg.getMinP(), cfg.getCurrentP(), cfg.getF());
        double fitnessVal = cfg.getKnowledgeBase().getFitnessValue(this.cfg)  + Math.random() * 0.00001;

//        double fitnessVal = dto.getFitnessVal() + Math.random() * 0.00001;
        this.cfg.getFitnessValues().add(fitnessVal);
        this.cfg.getAgentsQueue().put(fitnessVal, myAgent.getAID());

        return fitnessVal;
    }
}
