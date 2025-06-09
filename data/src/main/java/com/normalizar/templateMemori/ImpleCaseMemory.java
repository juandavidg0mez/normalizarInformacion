package com.normalizar.templateMemori;

public class ImpleCaseMemory implements ItemplateCase {

    @Override
    public String selectTemplate(String norma) {
        return switch (norma.toLowerCase()) {
            case "iso27001"  -> "iso27001";
            case "iso9001" -> "iso9001";
            case "gdpr" -> "gdpr";
            default -> "default";
        };
    }

}
