package ru.mpei.brics;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.ResponseEntity;
import ru.mpei.brics.extention.dto.CommandTO;
import ru.mpei.brics.extention.dto.TSDBResponse;
import ru.mpei.brics.extention.helpers.HttpRequestsBuilder;
import ru.mpei.brics.extention.helpers.JacksonHelper;

import java.util.HashMap;
import java.util.List;

@SpringBootTest
class SpringBootApplicationTests {

    @Test
    void askMeasurementFromTSDB() {
        HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
        ResponseEntity response = requestsBuilder.sendPostRequest(
                "http://10.8.8.103:9001/request/measurements/last",
                new HashMap<>(),
                List.of("TestFreq"));
        System.out.println(response.getBody());

        TSDBResponse responseObject = JacksonHelper.fromJackson(response.getBody().toString(), TSDBResponse.class);
        System.out.println(responseObject.getResponses().get(0).getValues().get(0));
    }

    @Test
    void sendCommandToTSDB() {
        HttpRequestsBuilder requestsBuilder = new HttpRequestsBuilder();
        ResponseEntity response = requestsBuilder.sendPostRequest(
                "http://10.8.8.103:9001/iec104/send/command",
                new HashMap<>(),
                new CommandTO("TestGen", "90"));
        System.out.println(response.getBody());
    }

}
