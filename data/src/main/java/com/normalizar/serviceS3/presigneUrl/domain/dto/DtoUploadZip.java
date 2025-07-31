package com.normalizar.serviceS3.presigneUrl.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DtoUploadZip {
    private String tenantName;
    private String userPoolId;
    private String fileName;
    private String file;
    private String activo;
}
