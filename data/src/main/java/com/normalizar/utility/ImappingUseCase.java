package com.normalizar.utility;

import java.io.IOException;
import java.util.Map;



public interface ImappingUseCase {
    Map<String, Object> mapJsonToThymeleafModel(String jsonString) throws IOException;
}
