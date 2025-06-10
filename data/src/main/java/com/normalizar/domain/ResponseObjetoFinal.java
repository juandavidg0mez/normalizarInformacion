package com.normalizar.domain;

public class ResponseObjetoFinal {

    private String archivoPDF;
    private String message;

    public ResponseObjetoFinal(String archivoPDF, String message) {
        this.archivoPDF = archivoPDF;
        this.message = message;
    }

    public ResponseObjetoFinal() {
    }

    public String getArchivoPDF() {
        return archivoPDF;
    }

    public void setArchivoPDF(String archivoPDF) {
        this.archivoPDF = archivoPDF;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
