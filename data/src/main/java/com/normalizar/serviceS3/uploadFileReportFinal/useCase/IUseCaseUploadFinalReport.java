package com.normalizar.serviceS3.uploadFileReportFinal.useCase;

import java.io.IOException;
import java.io.InputStream;

public interface IUseCaseUploadFinalReport {
    String uploadFileReportFinal(String s3key, InputStream archivoHtml) throws IOException;
}   
