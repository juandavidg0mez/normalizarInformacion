package com.normalizar.serviceS3.presigneUrl.domain;

import java.io.InputStream;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

// Clase para gesion de Url Prefirmada

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PresignedUrl {
    private String tenantName;
    private String userPoolId;
    private String activo;
    private String fileName;
    private InputStream fileZip;
    private long fileSize;
}
