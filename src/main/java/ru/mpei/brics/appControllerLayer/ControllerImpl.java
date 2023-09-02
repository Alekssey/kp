package ru.mpei.brics.appControllerLayer;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.mpei.brics.appServiceLayer.ServiceInterface;

@Slf4j
@RestController("controller")
public class ControllerImpl implements ControllerInterface {

    @Autowired
    ServiceInterface service;

    @Override
    @PostMapping("/load/set/power")
    public String changeLoadPower(@RequestParam String loadName, @RequestParam double p, @RequestParam double q){
        return service.setPowerToLoad(loadName, p, q);
    }


}
