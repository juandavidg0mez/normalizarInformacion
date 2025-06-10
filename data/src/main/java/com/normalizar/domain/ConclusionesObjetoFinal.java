package com.normalizar.domain;

import java.util.List;
import java.util.Map;

public class ConclusionesObjetoFinal {
    private Map<Integer, String> conclucion;

    public Map<Integer, String> getConclucion() {
        return conclucion;
    }

    public ConclusionesObjetoFinal() {
    }

    public ConclusionesObjetoFinal(Map<Integer, String> conclucion, String template) {
        this.conclucion = conclucion;
        this.template = template;
    }

    public void setConclucion(Map<Integer, String> conclucion) {
        this.conclucion = conclucion;
    }

    public String getTemplate() {
        return template;
    }

    public void setTemplate(String template) {
        this.template = template;
    }

    private String template;
}
