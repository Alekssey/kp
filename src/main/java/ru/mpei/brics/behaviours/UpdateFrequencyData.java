package ru.mpei.brics.behaviours;

import jade.core.Agent;
import jade.core.behaviours.TickerBehaviour;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import ru.mpei.brics.agents.NetworkElementAgent;
import ru.mpei.brics.appServiceLayer.ServiceInterface;
import ru.mpei.brics.extention.ApplicationContextHolder;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;
import ru.mpei.brics.extention.dto.TSDBResponse;
import ru.mpei.brics.extention.helpers.HttpRequestsBuilder;
import ru.mpei.brics.extention.helpers.JacksonHelper;

import java.util.HashMap;
import java.util.List;

@Slf4j
public class UpdateFrequencyData extends TickerBehaviour {
    private ServiceInterface service = ApplicationContextHolder.getContext().getBean(ServiceInterface.class);
    private NetworkElementConfiguration cfg = ((NetworkElementAgent) myAgent).getCfg();
    private String url;


    public UpdateFrequencyData(Agent a, long period) {
        super(a, period);
    }

    @Override
    public void onStart() {
        this.url = "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/request/measurements/last";
    }

    @Override
    protected void onTick() {

        HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
        ResponseEntity response = requestsBuilder.sendPostRequest(
                this.url,
                new HashMap<>(),
                List.of("TestFreq"));
        System.out.println(response.getBody());

        TSDBResponse responseObject = JacksonHelper.fromJackson(response.getBody().toString(), TSDBResponse.class);
        double frequency = Double.parseDouble(responseObject.getResponses().get(0).getValues().get(0));

        this.cfg.setF(frequency);
//        TSDBResponse response = service.getMeasurementsByParameters(List.of("frequency"), 0);
//        if(response != null) {
//            this.cfg.setF(Double.parseDouble(
//                    response.getResponses().get(0).getValues().get(0)
//            ));
//        }
    }

}
