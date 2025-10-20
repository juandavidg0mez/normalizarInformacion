package com.normalizar.serviceS3.uploadFileReportFinal.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RequestUploadFile {
    private String s3key;
    private String archivoHtml; // Base64
}


