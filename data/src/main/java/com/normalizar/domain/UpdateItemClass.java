package com.normalizar.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateItemClass {
    private String tenantName;
    private String sortKey;
    private String statusValue;
}
