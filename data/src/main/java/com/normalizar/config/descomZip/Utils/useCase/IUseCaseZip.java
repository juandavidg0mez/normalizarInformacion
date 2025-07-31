package com.normalizar.config.descomZip.Utils.useCase;

import com.normalizar.config.descomZip.Utils.domain.ZipFileToLambda;

public interface IUseCaseZip {
    void unZipAndUpload(ZipFileToLambda zipFileToLambda);
}
