package com.normalizar.serviceS3.abstracObject.domian;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RequestObject {
    private String s3Key;
}
