package com.normalizar.templateMemori;


public class ImpleCaseMemory implements ItemplateCase {

    @Override
    public String selectTemplate(String activo) {
        return switch (activo.toLowerCase()) {
            case "ALT"  -> "ALT";
            case "CT" -> "CT";
            case "DTEST" -> "DTEST";
            case "ENG" -> "ENG";
            case "ETEST" -> "ETEST";
            case "IB" -> "IB";
            case "IM" -> "IM";
            case "PT" -> "PT";
            case "RL" -> "RL";
            default -> "default";
        };
    }

}
							
