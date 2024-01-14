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

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class UpdateFrequencyData extends TickerBehaviour {
//    private ServiceInterface service = ApplicationContextHolder.getContext().getBean(ServiceInterface.class);
    private NetworkElementConfiguration cfg;
    private String url;
    HttpRequestsBuilder requestsBuilder = ApplicationContextHolder.getContext().getBean(HttpRequestsBuilder.class);
//    int i = 0;
    long startTime;

//    File file = new File("C:\\Users\\Aleksey\\Desktop\\Table\\Work\\BricsProject\\AgentBRICS_Clean\\src\\main\\resources\\oscillograms\\Oscillogram16.txt");
//    FileWriter out;

    public UpdateFrequencyData(NetworkElementAgent a, long period) {
        super(a, period);
        this.cfg = a.getCfg();
    }

    @Override
    public void onStart() {
        System.err.println("STARTED");
//        this.startTime = System.currentTimeMillis();
        this.url = "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/request/measurements/last";
    }

    @Override
    protected void onTick() {

        ResponseEntity response = requestsBuilder.sendPostRequest(
                this.url,
                new HashMap<>(),
                List.of("frequency"));
        System.out.println(response.getBody());

        TSDBResponse responseObject = JacksonHelper.fromJackson(response.getBody().toString(), TSDBResponse.class);
        double frequency = Double.parseDouble(responseObject.getResponses().get(0).getValues().get(0));

        if (Math.abs(frequency - cfg.getF()) < 5) {
            this.cfg.setF(frequency);

//            if (myAgent.getLocalName().equals("station1")) {
//                writeToFile(frequency);
//            }

        }


    }


/*    private void writeToFile(double data) {
        try {
            out = new FileWriter(file, true);
//            System.out.println(data);
            out.append(data + "\n");
//            out.append(((System.currentTimeMillis() - this.startTime) / 1000) + "\n");
            out.close();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

    }*/

}
