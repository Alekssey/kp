package ru.mpei.brics.appControllerLayer;

import org.springframework.web.bind.annotation.RequestBody;

import java.util.List;

public interface ControllerInterface {
    String changeLoadPower(String loadName, double p, double q);
    void unlockAgents(List<String> agentsNames);
}
