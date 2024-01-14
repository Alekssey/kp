package ru.mpei.brics.agents;

import jade.core.Agent;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import ru.mpei.brics.behaviours.AnalyzeFrequency;
import ru.mpei.brics.behaviours.UpdateFrequencyData;
import ru.mpei.brics.extention.ApplicationContextHolder;
import ru.mpei.brics.extention.dto.Response;
import ru.mpei.brics.extention.knowledgeBase.drools.DroolsCommunicator;
import ru.mpei.brics.extention.configirationClasses.NetworkElementConfiguration;
import ru.mpei.brics.extention.dto.CommandTO;
import ru.mpei.brics.extention.dto.TSDBResponse;
import ru.mpei.brics.extention.helpers.AgentDetector.AgentDetector;
import ru.mpei.brics.extention.helpers.HttpRequestsBuilder;
import ru.mpei.brics.extention.helpers.JacksonHelper;
import ru.mpei.brics.extention.knowledgeBase.jena.JenaCommunicator;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Slf4j
public class NetworkElementAgent extends Agent {
    @Getter
    private NetworkElementConfiguration cfg = null;
//    @Getter @Setter
//    private KieSession kieSession = null;
    @Getter
    private AgentDetector aDetector = null;
    @Getter @Setter
    private long startTime;

//    File file = new File("C:\\Users\\Aleksey\\Desktop\\Table\\Work\\BricsProject\\AgentBRICS_Clean\\src\\main\\resources\\oscillograms\\Oscillogram20.txt");
//    FileWriter out = null;
//    long writeStartTime;

    @Override
    protected void setup() {
        log.info("{} was born", this.getLocalName());
        String[] arguments = (String[]) this.getArguments();
        for (String s : arguments) {
            System.out.println(s);
        }
        String configFileName = (String) this.getArguments()[0];
        try{
            JAXBContext context = JAXBContext.newInstance(NetworkElementConfiguration.class);
            Unmarshaller jaxbUnmarshaller = context.createUnmarshaller();
            cfg = (NetworkElementConfiguration) jaxbUnmarshaller.unmarshal(new File("src/main/resources/agentsConfigs/" + configFileName));
        } catch (JAXBException e){
            e.printStackTrace();
        }

        this.cfg.setKnowledgeBase(new JenaCommunicator(String.valueOf(this.getArguments()[1])));
        log.info("rule set file was read successfully");

/*        try {
            this.cfg.setKnowledgeBase(new DroolsCommunicator(String.valueOf(this.getArguments()[1])));
            log.info("rule set file was read successfully");
        } catch (FileNotFoundException e) {
            log.error("file path to rule set is specified incorrectly, agent has been stopped");
            doDelete();
            return;
        }*/

        this.aDetector = new AgentDetector(this.getAID(), this.cfg.getIFace(), this.cfg.getUdpMonitoringPeriod(), this.cfg.getUdpMonitoringPort());
        requestInitialDataFromModel();
        identifyConnectingOfAgent();
        if(this.getLocalName().equals("wes")) startWritingThread();

        this.addBehaviour(new UpdateFrequencyData(this, this.cfg.getUdpMonitoringPeriod()));
        this.addBehaviour(new AnalyzeFrequency(this, this.cfg.getUdpMonitoringPeriod()));
    }

    void requestInitialDataFromModel() {

//        HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
//        ResponseEntity response = requestsBuilder.sendPostRequest(
//                "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/request/measurements/last",
//                new HashMap<>(),
//                List.of(cfg.getPMeasurementName()));
//        System.err.println(response);
//        TSDBResponse responseObject = JacksonHelper.fromJackson(response.getBody().toString(), TSDBResponse.class);
//        double activePower = Double.parseDouble(responseObject.getResponses().get(0).getValues().get(0));
//        this.cfg.setCurrentP(activePower);
        HttpRequestsBuilder requestsBuilder = ApplicationContextHolder.getContext().getBean(HttpRequestsBuilder.class);
        ResponseEntity response = requestsBuilder.sendPostRequest(
                "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/request/measurements/last",
                new HashMap<>(),
                List.of(cfg.getPMeasurementName()));
        System.err.println(response);
        TSDBResponse responseObject = JacksonHelper.fromJackson(response.getBody().toString(), TSDBResponse.class);
        double activePower = Double.parseDouble(responseObject.getResponses().get(0).getValues().get(0));
        this.cfg.setCurrentP(activePower);
    }

    private void identifyConnectingOfAgent() {
//            HttpRequestsBuilder requestsBuilder2 = new HttpRequestsBuilder();
//            ResponseEntity response2 = requestsBuilder2.sendPostRequest(
//                    "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/iec104/send/command",
//                    new HashMap<>(),
//                    new CommandTO(cfg.getPCommandName(), String.valueOf(cfg.getCurrentP())));
//        HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
//        ResponseEntity response = requestsBuilder.sendPostRequest(
//                "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/iec104/send/command",
//                new HashMap<>(),
//                new CommandTO(cfg.getIdentifyingCommand(), "1"));

        HttpRequestsBuilder rb = ApplicationContextHolder.getContext().getBean(HttpRequestsBuilder.class);
        ResponseEntity re = rb.sendPostRequest(
                "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/iec104/send/command",
                new HashMap<>(),
                new CommandTO(cfg.getPCommandName(), String.valueOf(cfg.getCurrentP())));
        re = rb.sendPostRequest(
                "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/iec104/send/command",
                new HashMap<>(),
                new CommandTO(cfg.getIdentifyingCommand(), "1"));
    }

    private void startWritingThread() {
        File file = new File("C:\\Users\\Aleksey\\Desktop\\Table\\Study\\3_semestr\\kProject\\AgentBRICS_Clean" +
                "\\src\\main\\resources\\oscillograms\\Oscillogram39.txt");
        long writingStartTime = System.currentTimeMillis();
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
        ses.scheduleAtFixedRate(() -> {
                    ResponseEntity response = requestsBuilder.sendPostRequest(
                            "http://" + cfg.getModelIp() + ":" + cfg.getModelPort() + "/request/measurements/last",
                            new HashMap<>(),
                            List.of("WES", "DGU", "AKB", "Load"));
//            System.err.println(response);
                    TSDBResponse responseObject = JacksonHelper.fromJackson(response.getBody().toString(), TSDBResponse.class);
//            System.err.println("1" + responseObject.getResponses().get(0));
//            System.err.println("2" + responseObject.getResponses().get(1));
//            System.err.println("3" + responseObject.getResponses().get(2));
//            System.err.println("4" + responseObject.getResponses().get(3));
                    String data = "frequency : " + this.cfg.getF() + "\n"
                            + "time : " + (System.currentTimeMillis() - writingStartTime) / 1000.0;
                    for (Response r : responseObject.getResponses()) {
                        data += "\n" + r.getParamName() + " : " + r.getValues().get(0);
                    }
//            System.err.println(data);
                    try {
                        FileWriter writer = new FileWriter(file, true);
                        writer.append(data + "\n");
                        writer.close();
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }

                }, 100, 100, TimeUnit.MILLISECONDS
        );
    }

/*    private void startWritingThread() {
        ScheduledExecutorService ses = Executors.newScheduledThreadPool(1);
        ses.scheduleAtFixedRate(() -> {
            try {
            AtomicReference<FileWriter> atomicWriter = new AtomicReference<>();
            atomicWriter.set(new FileWriter(file, true));
            String data;
            if (this.getLocalName().equals("station1")) {
                data = this.getLocalName() + " : " + this.getCfg().getCurrentP() + "\n"
                        + "frequency : " + this.cfg.getF() + "\n"
                        + "time : " + (System.currentTimeMillis() - this.writeStartTime) / 1000.0;
            } else {
                data = this.getLocalName() + " : " + this.getCfg().getCurrentP();
            }
            out = atomicWriter.get();
            out.append(data + "\n");
            out.close();

            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }, 100, 100, TimeUnit.MILLISECONDS);
    }*/
}
