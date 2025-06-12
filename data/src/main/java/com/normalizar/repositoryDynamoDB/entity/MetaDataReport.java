package com.normalizar.repositoryDynamoDB.entity;

public class MetaDataReport {
    private String tenantId;
    private String reporteId;
    private String norma; // Estandar con el que se elaboro el teamplate del reporte
    private String activo;
    private String application;
    private String poolUserId;
    private String s3JsonPath;
    private String s3PdfPath;
    private String timestamp;
    private String estado;

    public MetaDataReport() {
    }

    public MetaDataReport(String tenantId, String reporteId, String norma, String activo, String application,
            String poolUserId, String s3JsonPath, String s3PdfPath, String timestamp, String estado) {
        this.tenantId = tenantId;
        this.reporteId = reporteId;
        this.norma = norma;
        this.activo = activo;
        this.application = application;
        this.poolUserId = poolUserId;
        this.s3JsonPath = s3JsonPath;
        this.s3PdfPath = s3PdfPath;
        this.timestamp = timestamp;
        this.estado = estado;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getReporteId() {
        return reporteId;
    }

    public void setReporteId(String reporteId) {
        this.reporteId = reporteId;
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

    public String getPoolUserId() {
        return poolUserId;
    }

    public void setPoolUserId(String poolUserId) {
        this.poolUserId = poolUserId;
    }

    public String getS3JsonPath() {
        return s3JsonPath;
    }

    public void setS3JsonPath(String s3JsonPath) {
        this.s3JsonPath = s3JsonPath;
    }

    public String getS3PdfPath() {
        return s3PdfPath;
    }

    public void setS3PdfPath(String s3PdfPath) {
        this.s3PdfPath = s3PdfPath;
    }

    public String getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

}
