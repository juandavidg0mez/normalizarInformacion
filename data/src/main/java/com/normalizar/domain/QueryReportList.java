package com.normalizar.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class QueryReportList {
    private String tenantName;
    private String userPoolId;
    private String status;
}
