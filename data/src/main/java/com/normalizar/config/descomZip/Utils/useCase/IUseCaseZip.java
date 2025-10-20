package com.normalizar.config.descomZip.Utils.useCase;

import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;
import com.normalizar.config.descomZip.Utils.domain.ZipFileToLambda;
import com.amazonaws.services.lambda.runtime.Context;

public interface IUseCaseZip {
    void unZipAndUpload(ZipFileToLambda zipFileToLambda);
    void procesarRecord(S3EventNotificationRecord record, Context context);
}
