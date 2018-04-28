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
 * @Date:Created in 14:57 2018/4/28
 * @Modified By:
 */
public class ActionSerializer extends JsonSerializer<String> {

    private static Map<String, String> map = new HashMap<>(4);

    static {
        map.put("SELL", "买入");
        map.put("BUY", "卖出");
        map.put("SHORT", "做空");
        map.put("COVER", "平仓");
    }

    @Override
    public void serialize(String value, JsonGenerator jsonGenerator, SerializerProvider serializerProvider) throws IOException, JsonProcessingException {
        String result = Objects.isNull(map.get(value)) ? value : map.get(value);
        jsonGenerator.writeNumber("\"" + result + "\"");
    }
}
