package com.normalizar.serviceS3.presigneUrl.useCase;

import com.normalizar.serviceS3.presigneUrl.domain.PresignedUrl;

public interface IUseCasePresignedUrl {
 
    String upLoadFileForUrl (PresignedUrl presignedUrl);
    
}
