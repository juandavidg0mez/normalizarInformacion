package com.normalizar.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ItemQuery {
    private String tenant_name;
    private String job_id;
    private String estado;
    private String type;
}
