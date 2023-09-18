package ru.mpei.brics.agents;

import jade.core.Agent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.kie.api.runtime.KieSession;
import org.springframework.http.ResponseEntity;
import ru.mpei.brics.behaviours.AnalyzeFrequency;
import ru.mpei.brics.behaviours.UpdateFrequencyData;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;
import ru.mpei.brics.extention.dto.TSDBResponse;
import ru.mpei.brics.extention.helpers.AgentDetector.AgentDetector;
import ru.mpei.brics.extention.helpers.HttpRequestsBuilder;
import ru.mpei.brics.extention.helpers.JacksonHelper;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.util.HashMap;
import java.util.List;

@Slf4j
public class NetworkElementAgent extends Agent {
    @Getter
    private NetworkElementConfiguration cfg = null;
    @Getter @Setter
    private KieSession kieSession = null;
    @Getter
    private AgentDetector aDetector = null;
    @Getter @Setter
    private long startTime;

    @Override
    protected void setup() {
        log.info("{} was born", this.getLocalName());
//        String configFileName = this.getLocalName() + "Configuration.xml";
        String configFileName = (String) this.getArguments()[0];
        try{
            JAXBContext context = JAXBContext.newInstance(NetworkElementConfiguration.class);
            Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
            cfg = (NetworkElementConfiguration) jaxbUnmarshaller.unmarshal(new File("src/main/resources/agentsConfigs/" + configFileName));
        } catch (JAXBException e){
            e.printStackTrace();
        }

        requestInitialDataFromModel();

        this.aDetector = new AgentDetector(this.getAID(), this.cfg.getIFace(), this.cfg.getUdpMonitoringPeriod(), this.cfg.getUdpMonitoringPort());

        this.addBehaviour(new UpdateFrequencyData(this, this.cfg.getUdpMonitoringPeriod()));
        this.addBehaviour(new AnalyzeFrequency(this, this.cfg.getUdpMonitoringPeriod()));
    }

    void requestInitialDataFromModel() {

        HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
        ResponseEntity response = requestsBuilder.sendPostRequest(
                "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/request/measurements/last",
                new HashMap<>(),
                List.of(cfg.getPMeasurementName()));
        System.err.println(response);
        TSDBResponse responseObject = JacksonHelper.fromJackson(response.getBody().toString(), TSDBResponse.class);
        double activePower = Double.parseDouble(responseObject.getResponses().get(0).getValues().get(0));
        this.cfg.setCurrentP(activePower);
    }
}
