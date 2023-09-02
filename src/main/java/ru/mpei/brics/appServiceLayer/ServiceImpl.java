package ru.mpei.brics.appServiceLayer;

import jade.core.AID;
import jade.core.Agent;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.ControllerException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.stereotype.Service;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.appRepositoryLayer.RepositoryInterface;
import ru.mpei.brics.extention.ApplicationContextHolder;

import java.lang.reflect.Field;


@Slf4j
@Service("service")
public class ServiceImpl implements ServiceInterface {

    @Autowired
    private RepositoryInterface repository;
    private int lastIndex = 0;
//    private GridAgent gridAgent = null;
    private double startTime = System.currentTimeMillis();

//    @Override
//    public String setPowerToGrid(double p, double q) {
//        if (this.gridAgent == null) {
//            this.gridAgent = (GridAgent) getAgentFromContext("grid");
//        }
//        gridAgent.cfg.setNecessaryP(p);
//        gridAgent.cfg.setNecessaryQ(q);
//        return "Values successfully changed";
//    }

    @Override
    public String setPowerToLoad(String loadName, double p, double q) {
        NetworkElementAgent loadAgent = (NetworkElementAgent) getAgentFromContext(loadName);
        loadAgent.getCfg().setCurrentP(p);
        loadAgent.getCfg().setCurrentQ(q);
        return "Current P: " + loadAgent.getCfg().getCurrentP() + "; Max P: " + loadAgent.getCfg().getMaxP();
//        return "Values successfully changed";
    }



//    @Override
//    public void saveMeasurementInDB(Measurement m) {
//        this.lastIndex = (int) m.getId();
//        repository.save(m);
//    }

//    @Override
//    public Measurement getLastMeasurement() {
//        List<Measurement> mList = repository.getMeasurements(lastIndex, lastIndex);
//        if (mList.size() != 0) {
//            return mList.get(0);
//        }
//        return null;
//    }

    @Override
    public Agent getAgentFromContext(String agentName) {
        ApplicationContext context = ApplicationContextHolder.getContext();
        AgentContainer mainContainer = (AgentContainer) context.getBean("jadeMainContainer");

        Agent agentInstance = null;

        try {
            AgentController agentController = mainContainer.getAgent(agentName);

            Field agentAidField = agentController.getClass().getDeclaredField("agentID");
            agentAidField.setAccessible(true);
            Field agentContainerField = agentController.getClass().getDeclaredField("myContainer");
            agentContainerField.setAccessible(true);

            AID agentAID = (AID) agentAidField.get(agentController);
            jade.core.AgentContainer agentContainer = (jade.core.AgentContainer) agentContainerField.get(agentController);

            agentInstance = agentContainer.acquireLocalAgent(agentAID);
            agentContainer.releaseLocalAgent(agentAID);

        } catch (ControllerException | NoSuchFieldException | IllegalAccessException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }

        return agentInstance;
    }

//    @Override
//    public TSDBResponse getMeasurementsByParameters(List<String> paramNames, int collectBeforeSec) {
//        Measurement m = this.getLastMeasurement();
//        if(m == null) {
//            return null;
//        }
//
//        Response r = new Response(
//                "frequency",
//                List.of(Double.toString(m.getFrequency())),
//                List.of(Double.toString((System.currentTimeMillis() - this.startTime) / 1000)));
//        return new TSDBResponse(List.of(r));
//    }


}
