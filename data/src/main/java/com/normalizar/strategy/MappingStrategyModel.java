package com.normalizar.strategy;

import java.io.IOException;
import java.util.Map;

public interface MappingStrategyModel {
    Map<String, Object> mapJsonToThymeleafModel(String jsonString) throws IOException;
}
