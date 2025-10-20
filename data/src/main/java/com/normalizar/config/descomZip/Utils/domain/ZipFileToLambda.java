package com.normalizar.config.descomZip.Utils.domain;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ZipFileToLambda {
    private InputStream zipStream;
    private String tenantName;
    private String userPoolId;
    private String jobId;
    private String activo;
}
