package ru.mpei.brics.appServiceLayer;

import jade.core.Agent;

public interface ServiceInterface {
    Agent getAgentFromContext(String agentName);
    String setPowerToLoad(String loadName, double p, double q);


//    String setPowerToGrid(double p, double q);
//    void saveMeasurementInDB(Measurement m);
//    Measurement getLastMeasurement();
//    TSDBResponse getMeasurementsByParameters(List<String> paramNames, int collectBeforeSec);

//    AID getAidFromContext(String agentName);
//    void saveFile(MultipartFile file);
//    String checkKZ(int begin, int end);
//    void setTriggerValue(double trigVal);

}
