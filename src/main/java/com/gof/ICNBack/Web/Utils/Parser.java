package com.gof.ICNBack.Web.Utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.HashMap;
import java.util.Map;

public class Parser {
    public static Map<String, String> parseFilterParameters(String filterParametersJson) throws JsonProcessingException {
        if (filterParametersJson == null || filterParametersJson.trim().isEmpty()) {
            return new HashMap<>();
        }

        ObjectMapper objectMapper = new ObjectMapper();
        return objectMapper.readValue(filterParametersJson, new TypeReference<Map<String, String>>() {
        });

    }
}
