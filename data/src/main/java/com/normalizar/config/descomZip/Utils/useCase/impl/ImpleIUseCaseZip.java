package com.normalizar.config.descomZip.Utils.useCase.impl;

import java.io.InputStream;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import com.normalizar.config.descomZip.Utils.domain.ZipFileToLambda;
import com.normalizar.config.descomZip.Utils.useCase.IUseCaseZip;

import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;

public class ImpleIUseCaseZip implements IUseCaseZip {
    private static final String BUCKET = "";
    private static final String UPLOADS_EXCEL_A_S3 = "uploadsExcels";
    private S3Client s3Client;

    public ImpleIUseCaseZip() {
        this.s3Client = S3Client.create();
    }

    @Override
    public void unZipAndUpload(ZipFileToLambda zipFileToLambda) {
        InputStream zipSQS = zipFileToLambda.getZipStream();
        try (ZipInputStream zis = new ZipInputStream(zipSQS)) {
            ZipEntry entry;
            while ((entry = zis.getNextEntry()) != null) {
                if (!entry.isDirectory()) {
                    // readAllBytes lee todos los archivos internos del archivo comprimido
                    byte[] bytes = zis.readAllBytes();

                    String outPutKey = UPLOADS_EXCEL_A_S3 + "/" +
                            zipFileToLambda.getJobId() + "/" +
                            zipFileToLambda.getTenantName() + "/" +
                            zipFileToLambda.getUserPoolId() + "/" +
                            zipFileToLambda.getActivo() + "/" +
                            entry.getName();

                    s3Client.putObject(PutObjectRequest.builder()
                            .bucket(BUCKET)
                            .key(outPutKey)
                            .metadata(Map.of("jobId", zipFileToLambda.getJobId()))
                            .build(), RequestBody.fromBytes(bytes));

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Archivo Zip descomprimido y listo para normalizar mensajes de SQS");
        }
    }

    // ZipInputStream zis = new ZipInputStream(inputStream);
    // ZipEntry entry;

    // while ((entry = zis.getNextEntry()) != null) {
    // System.out.println("Nombre: " + entry.getName());
    // System.out.println("¿Es directorio? " + entry.isDirectory());
    // }
    // Aunque S3 no tiene carpetas reales como un sistema de archivos, sí
    // interpreta / en las claves (keys) como si fueran "carpetas". Por eso,
    // si usas entry.getName() tal como viene, S3 mantendrá la estructura visual del
    // ZIP.

}
