package com.normalizar.domain;


import java.util.Map;

public class ConclusionesObjetoFinal {
    private Map<Integer, String> conclucion;
    private String template;
    private String userPoolId;
    private String tenantName;

    public ConclusionesObjetoFinal() {
    }

    public ConclusionesObjetoFinal(Map<Integer, String> conclucion, String template, String userPoolId,
            String tenantName) {
        this.conclucion = conclucion;
        this.template = template;
        this.userPoolId = userPoolId;
        this.tenantName = tenantName;
    }

    public Map<Integer, String> getConclucion() {
        return conclucion;
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

    public String getUserPoolId() {
        return userPoolId;
    }

    public void setUserPoolId(String userPoolId) {
        this.userPoolId = userPoolId;
    }

    public String getTenantName() {
        return tenantName;
    }

    public void setTenantName(String tenantName) {
        this.tenantName = tenantName;
    }
}
