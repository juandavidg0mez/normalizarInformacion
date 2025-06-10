package com.normalizar.domain;



public class Report {
    private String norma;
    private String activo;
    private String application;
    private String tenant;
    private String poolUserId;
    private String archivoToFront;

    public Report() {
    }

    public Report(String norma, String activo, String application, String tenant, String poolUserId,
            String archivoToFront) {
        this.norma = norma;
        this.activo = activo;
        this.application = application;
        this.tenant = tenant;
        this.poolUserId = poolUserId;
        this.archivoToFront = archivoToFront;
    }

    public String getNorma() {
        return norma;
    }

    public void setNorma(String norma) {
        this.norma = norma;
    }

    public String getActivo() {
        return activo;
    }

    public void setActivo(String activo) {
        this.activo = activo;
    }

    public String getApplication() {
        return application;
    }

    public void setApplication(String application) {
        this.application = application;
    }

    public String getTenant() {
        return tenant;
    }

    public void setTenant(String tenant) {
        this.tenant = tenant;
    }

    public String getPoolUserId() {
        return poolUserId;
    }

    public void setPoolUserId(String poolUserId) {
        this.poolUserId = poolUserId;
    }

    public String getArchivoToFront() {
        return archivoToFront;
    }

    public void setArchivoToFront(String archivoToFront) {
        this.archivoToFront = archivoToFront;
    }
}
