package ru.mpei.brics;

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
    void sendCommandToTSDB() {
        HttpRequestsBuilder requestsBuilder1 = new HttpRequestsBuilder();
        ResponseEntity response = requestsBuilder1.sendPostRequest(
                "http://10.8.8.171:9001/iec104/send/command",
                new HashMap<>(),
                new CommandTO("TestGen", "60"));

        HttpRequestsBuilder requestsBuilder2 = new HttpRequestsBuilder();
        ResponseEntity response2 = requestsBuilder2.sendPostRequest(
                "http://10.8.8.171:9001/iec104/send/command",
                new HashMap<>(),
                new CommandTO("TestGen2", "40"));
        System.out.println(response.getBody());
    }






    @Test
    void askMeasurementFromTSDB() {
        HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
        ResponseEntity response = requestsBuilder.sendPostRequest(
                "http://10.8.8.183:9001/request/measurements/last",
                new HashMap<>(),
                List.of("TestFreq", "TestGeneration"));
        System.out.println(response.getBody());

        TSDBResponse responseObject = JacksonHelper.fromJackson(response.getBody().toString(), TSDBResponse.class);
        System.out.println(responseObject.getResponses().get(0).getValues().get(0));
    }


    @Test
    void unlockAgents() {
        HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
        ResponseEntity response = requestsBuilder.sendPostRequest(
                "http://127.0.0.1:9005/agents/unlock",
                new HashMap<>(),
                List.of("station1")
        );
    }

    @Test
    void check() {
//        double[] dsf = new double[]{-1.0, -1.0};
//        System.out.println();


    }


}
