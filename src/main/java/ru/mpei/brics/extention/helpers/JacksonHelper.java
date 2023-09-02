package ru.mpei.brics.extention.helpers;

import lombok.SneakyThrows;
import org.codehaus.jackson.map.ObjectMapper;

public class JacksonHelper {
    public JacksonHelper() {
    }

    private static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public static <T> String toJackson(T value){
        return objectMapper.writeValueAsString(value);
    }

    @SneakyThrows
    public static <T> T fromJackson(String str, Class<T> valueType){
        return objectMapper.readValue(str, valueType);
    }

}
