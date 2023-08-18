package com.architecture.springboot.response;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import lombok.Data;
import lombok.extern.log4j.Log4j2;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * REST API Response에 담을 JSON 객체 생성기
 * **/
@Data
@JsonAutoDetect(fieldVisibility = JsonAutoDetect.Visibility.ANY)
@Log4j2
public class ResMessage {
    private Map<String, Object> map;

    public ResMessage() {
        this.map = new HashMap<>();
    }

    public <T> ResMessage put(String key, T object) {
        map.put(key, object);
        return this;
    }

    public boolean pop(String key) {
        if(map.containsKey(key)) {
            map.remove(key);
            return true;
        } else {
            return false;
        }
    }

    public Map<String, Object> getHashMap(boolean isLog) throws JSONException {
        if (isLog) {
            for (String key : map.keySet()) {
                String value = map.get(key).toString();
                log.info("Message Map Value -> {},{}", key, value);
            }
        }
        return map;
    }

    public Map<String, Object> getHashMap() throws JSONException {
        return map;
    }
}
