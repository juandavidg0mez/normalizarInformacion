package com.normalizar.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Report {
    private String norma;
    private String activo;
    private String application;
    private String tenant;
    private String poolUserId;
    private String archivoToFront;

}
