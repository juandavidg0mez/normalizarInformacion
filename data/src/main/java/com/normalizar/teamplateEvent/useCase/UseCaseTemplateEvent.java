package com.normalizar.teamplateEvent.useCase;


import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.events.models.s3.S3EventNotification.S3EventNotificationRecord;

public interface UseCaseTemplateEvent {
    void teamplate_created(S3EventNotificationRecord record, Context context);
      

}
