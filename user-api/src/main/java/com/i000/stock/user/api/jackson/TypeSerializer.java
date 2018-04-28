package com.i000.stock.user.api.jackson;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @Author:qmfang
 * @Description:
 * @Date:Created in 15:03 2018/4/28
 * @Modified By:
 */
public class TypeSerializer extends JsonSerializer<String> {

    private static Map<String, String> map = new HashMap<>(3);

    static {
        map.put("LONG1", "稳定型");
        map.put("LONG2", "波动强");
        map.put("SHORT", "做空(稳定型)");
    }

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        String result = Objects.isNull(map.get(value)) ? value : map.get(value);
        jsonGenerator.writeNumber("\"" + result + "\"");
    }
}
