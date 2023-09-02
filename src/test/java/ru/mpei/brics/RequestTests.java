package ru.mpei.brics;

import jade.lang.acl.ACLMessage;
import org.junit.jupiter.api.Test;
import org.springframework.http.ResponseEntity;
import ru.mpei.brics.extention.dto.CommandTO;
import ru.mpei.brics.extention.dto.TSDBResponse;
import ru.mpei.brics.extention.helpers.HttpRequestsBuilder;
import ru.mpei.brics.extention.helpers.JacksonHelper;

import java.util.HashMap;
import java.util.List;

public class RequestTests {
    @Test
    void askMeasurementFromTSDB() {
        HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
        ResponseEntity response = requestsBuilder.sendPostRequest(
                "http://10.8.8.117:9001/request/measurements/last",
                new HashMap<>(),
                List.of("TestFreq", "TestGeneration"));
        System.out.println(response.getBody());

        TSDBResponse responseObject = JacksonHelper.fromJackson(response.getBody().toString(), TSDBResponse.class);
        System.out.println(responseObject.getResponses().get(0).getValues().get(0));
    }

    @Test
    void sendCommandToTSDB() {
        HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
        ResponseEntity response = requestsBuilder.sendPostRequest(
                "http://192.168.1.231:9001/iec104/send/command",
                new HashMap<>(),
                new CommandTO("TestGen", "100"));
        System.out.println(response.getBody());
    }

    @Test
    void seePerformatives() {
        List<String> list = List.of(ACLMessage.getAllPerformativeNames());

    }
}
