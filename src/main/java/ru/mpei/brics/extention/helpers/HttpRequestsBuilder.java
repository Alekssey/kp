package ru.mpei.brics.extention.helpers;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;

public class HttpRequestsBuilder<T> {
    public ResponseEntity sendGetRequest(String url, HashMap<?,?> parameters) {
        url = addParametersToUrl(url, parameters);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        return response;
    }

    public ResponseEntity sendPostRequest(String url, HashMap<?,?> parameters, T body) {
        url = addParametersToUrl(url, parameters);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<String> request = new HttpEntity<String>(JacksonHelper.toJackson(body), headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.postForEntity(url, request, String.class);
        return response;

    }

    private String addParametersToUrl(String url, HashMap<?,?> parameters) {
        if(!parameters.isEmpty()) {
            url += "?";
            for(Object o : parameters.keySet()) {
                url += o + "=" + parameters.get(o) + "&";
            }
            url = url.substring(0, url.length() - 1);
        }
        return url;
    }
}
