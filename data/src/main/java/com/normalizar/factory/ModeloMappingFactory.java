package com.normalizar.factory;

import java.util.Map;

import com.normalizar.strategy.MappingStrategyModel;
import com.normalizar.utility.impl.ImplInterruptoresMap;
import com.normalizar.utility.impl.ImplMappingCt;

public class ModeloMappingFactory {
    // dependiento de la prueba que nos llegue retornara una instancia del modelo

    private static final Map<String, MappingStrategyModel> estrategias = Map.of(
            "CT", new ImplMappingCt(),
            "INTER", new ImplInterruptoresMap());

    public MappingStrategyModel obtenerModeloToMapping(String tipoModelo) {
        MappingStrategyModel estrategia = estrategias.get(tipoModelo);
        
        if (estrategia == null) {
            throw new IllegalArgumentException("Tipo de modelo no soportado: " + tipoModelo);
            
        }
        return estrategia;

    }

}
