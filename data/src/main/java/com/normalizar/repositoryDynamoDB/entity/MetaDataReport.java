package com.normalizar.repositoryDynamoDB.entity;



import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MetaDataReport {
    private String tenantId;
    private String jobId;
    private String activo;
    private String poolUserId;
    private String s3JsonPath;
    private String s3PdfPath;
    private String s3ZipPath;
    private String s3ZipFileFinalReport;
    private String s3FinalPdfReport;
    private String timestamp;
    private String estado;
}
