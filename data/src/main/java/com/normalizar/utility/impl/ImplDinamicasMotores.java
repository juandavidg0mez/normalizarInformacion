package com.normalizar.utility.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.strategy.MappingStrategyModel;

public class ImplDinamicasMotores implements MappingStrategyModel{

  
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> mapJsonToThymeleafModel(String jsonString) throws IOException {

        Map<String, Object> fullNormalizedData = objectMapper.readValue(
                jsonString, new TypeReference<Map<String, Object>>() {
                });

        Map<String, Object> modelo = new HashMap<>();

        // ----------- 1. Información General -----------
        // este es el fragmento del objeto que proviene del json
        Map<String, Object> informacionGeneral = (Map<String, Object>) fullNormalizedData.get("InfoGeneral");

        if (informacionGeneral != null) {
            Map<String, Object> informacionGeneralMap = new HashMap<>();
           for (Map.Entry<String, Object> entry : informacionGeneral.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                informacionGeneralMap.put(key, value);

            }

            modelo.put("InformacionGeneral", informacionGeneralMap);
        }

        // ----------- 2. Tensiones Línea -----------
        Map<String, Object> tensionesLinea = (Map<String, Object>) fullNormalizedData.get("TensionesLinea");
        if (tensionesLinea != null) {
            Map<String, Object> tensionLineaMap = new HashMap<>();
            for (Map.Entry<String, Object> entry : tensionesLinea.entrySet()) {
                final String key = entry.getKey();
                final Object value = entry.getValue();
                tensionLineaMap.put(key, value);

            }
            modelo.put("TensionesLinea", tensionLineaMap);
        }

        // ----------- 3. Tensiones Fase -----------
        Map<String, Object> tensionesFase = (Map<String, Object>) fullNormalizedData.get("TensionesFase");
        if (tensionesFase != null) {
            modelo.put("TensionesFase", tensionesFase);
        }

        // ----------- 4. Corrientes Línea -----------
        Map<String, Object> corrientesLinea = (Map<String, Object>) fullNormalizedData.get("CorrientesLinea");
        if (corrientesLinea != null) {
            modelo.put("CorrientesLinea", corrientesLinea);
        }

        // ----------- 5. Potencias -----------
        Map<String, Object> potencias = (Map<String, Object>) fullNormalizedData.get("Potencias");
        if (potencias != null) {
            modelo.put("Potencias", potencias);
        }

        // ----------- 6. Componentes Simétricas -----------
        Map<String, Object> componentesSimetricas = (Map<String, Object>) fullNormalizedData.get("ComponentesSimetricas");
        if (componentesSimetricas != null) {
            modelo.put("ComponentesSimetricas", componentesSimetricas);
        }

        // ----------- 7. Evaluación Rotor -----------
        Map<String, Object> evaluacionRotor = (Map<String, Object>) fullNormalizedData.get("EvaluacionRotor");
        if (evaluacionRotor != null) {
            modelo.put("EvaluacionRotor", evaluacionRotor);
        }

        // ----------- 8. Evaluación Excentricidad -----------
        Map<String, Object> evaluacionExcentricidad = (Map<String, Object>) fullNormalizedData.get("EvaluacionExcentricidad");
        if (evaluacionExcentricidad != null) {
            modelo.put("EvaluacionExcentricidad", evaluacionExcentricidad);
        }

        // ----------- 9. Datos adicionales o placeholders -----------

        return modelo;
    }
}
