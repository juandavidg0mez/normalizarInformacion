package com.normalizar.utility.impl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.strategy.MappingStrategyModel;


public class ImplEstaticasMotores implements MappingStrategyModel {
    private static final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Map<String, Object> mapJsonToThymeleafModel(String jsonString) throws IOException {
        Map<String, Object> fullNormalizedData = objectMapper.readValue(jsonString,
                new TypeReference<Map<String, Object>>() {
                });

        // Este será el modelo que se pasará al template Thymeleaf
        Map<String, Object> modelo = new HashMap<>();

        /*
         * ────────────────────────────────
         * 1. BLOQUE: Información General
         * ────────────────────────────────
         */
        Map<String, Object> infoGeneral = (Map<String, Object>) fullNormalizedData.get("InfoGeneral");
        if (infoGeneral != null) {
            Map<String, Object> informacionGeneral = new HashMap<>();
            informacionGeneral.put("tipo_motor", infoGeneral.get("tipo_motor"));
            informacionGeneral.put("numero_de_serie", infoGeneral.get("numero_de_serie"));
            informacionGeneral.put("frecuencia_nominal", infoGeneral.get("frecuencia_nominal"));
            informacionGeneral.put("tension_nom_v", infoGeneral.get("tension_nom_v"));
            informacionGeneral.put("corriente_nom_A", infoGeneral.get("corriente_nom_A"));
            informacionGeneral.put("potencia_kW", infoGeneral.get("potencia_kW"));
            informacionGeneral.put("potencia_HP", infoGeneral.get("potencia_HP"));
            informacionGeneral.put("velocidad_nominal", infoGeneral.get("velocidad_nominal"));
            informacionGeneral.put("velocidad_operacion", infoGeneral.get("velocidad_operacion"));
            informacionGeneral.put("fecha_prueba", infoGeneral.get("fecha_prueba"));
            informacionGeneral.put("punto_de_medida", infoGeneral.get("punto_de_medida"));
            informacionGeneral.put("usuario", infoGeneral.get("usuario"));
            informacionGeneral.put("serial_probador", infoGeneral.get("serial_probador"));
            informacionGeneral.put("fabricante", infoGeneral.get("fabricante"));
            informacionGeneral.put("ubicacion", infoGeneral.get("ubicacion"));
            informacionGeneral.put("nombre", infoGeneral.get("nombre"));
            informacionGeneral.put("voltaje_alternativo", infoGeneral.get("voltaje_alternativo"));
            informacionGeneral.put("temperatura_referencia", infoGeneral.get("temperatura_referencia"));
            modelo.put("InformacionGeneral", informacionGeneral);
        }

        /*
         * ────────────────────────────────
         * 2. BLOQUE: Parámetros de Prueba
         * ────────────────────────────────
         */
        Map<String, Object> parametrosPrueba = (Map<String, Object>) fullNormalizedData.get("ParametrosPrueba");
        if (parametrosPrueba != null) {
            Map<String, Object> parametros = new HashMap<>();
            parametros.put("voltaje_prueba", parametrosPrueba.get("voltaje_prueba"));
            parametros.put("temperatura_prueba", parametrosPrueba.get("temperatura_prueba"));
            parametros.put("resistencia_aislamiento_60seg", parametrosPrueba.get("resistencia_aislamiento_60seg"));
            parametros.put("resistencia_aislamiento_600seg", parametrosPrueba.get("resistencia_aislamiento_600seg"));
            parametros.put("capacitancia", parametrosPrueba.get("capacitancia"));
            modelo.put("ParametrosPrueba", parametros);
        }

        /*
         * ────────────────────────────────
         * 3. BLOQUE: Resistencia Óhmica
         * ────────────────────────────────
         */
        Map<String, Object> resistenciaOhmica = (Map<String, Object>) fullNormalizedData.get("ResistenciaOhmica");
        if (resistenciaOhmica != null) {
            modelo.put("ResistenciaOhmica", resistenciaOhmica);
        }

        /*
         * ────────────────────────────────
         * 4. BLOQUE: Inductancia
         * ────────────────────────────────
         */
        Map<String, Object> inductancia = (Map<String, Object>) fullNormalizedData.get("Inductancia");
        if (inductancia != null) {
            modelo.put("Inductancia", inductancia);
        }

        /*
         * ────────────────────────────────
         * 5. BLOQUE: Índice de Polarización
         * ────────────────────────────────
         */
        Map<String, Object> indicePolarizacion = (Map<String, Object>) fullNormalizedData.get("IndicePolarizacion");
        if (indicePolarizacion != null) {
            modelo.put("IndicePolarizacion", indicePolarizacion);
        }

        /*
         * ────────────────────────────────
         * 6. BLOQUE: Gráfica Índice de Polarización
         * ────────────────────────────────
         */
        Map<String, Object> graficaIndice = (Map<String, Object>) fullNormalizedData.get("grafica_indice_polarizacion");
        if (graficaIndice != null) {
            modelo.put("grafica_indice_polarizacion", graficaIndice);
        }

        /*
         * ────────────────────────────────
         * 7. BLOQUE: Datos de Referencia
         * ────────────────────────────────
         */
        Map<String, Object> datosReferencia = (Map<String, Object>) fullNormalizedData.get("DatosReferencia");
        if (datosReferencia != null) {
            modelo.put("DatosReferencia", datosReferencia);
        }

        /*
         * ────────────────────────────────
         * 8. Campo de conclusiones (vacío para plantilla)
         * ────────────────────────────────
         */

        return modelo;
    }

}
