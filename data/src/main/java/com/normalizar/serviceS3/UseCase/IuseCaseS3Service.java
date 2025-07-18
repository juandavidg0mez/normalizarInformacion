package com.normalizar.serviceS3.UseCase;

import java.io.IOException;
import java.io.InputStream;

public interface IuseCaseS3Service {
          // Caso de uso para Subir un Archivo al bucket
    // Boolean uploadFile(String bucketName, String key, Path fileLocation);
    String uploaFile(String userPoolId, String tennatName, String activo,InputStream file, String nameFile , String contenType) throws IOException;
}   
