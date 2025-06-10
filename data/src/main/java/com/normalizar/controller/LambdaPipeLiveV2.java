package com.normalizar.controller;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestStreamHandler;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.normalizar.templateMemori.ImpleCaseMemory;
import com.normalizar.templateMemori.ItemplateCase;

public class LambdaPipeLiveV2 implements  RequestStreamHandler{
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private ItemplateCase itemplateCase;
    public LambdaPipeLiveV2(){
        this.itemplateCase = new ImpleCaseMemory();
    }
    @Override
    public void handleRequest(InputStream input, OutputStream output, Context context) throws IOException {
        try {
            
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

}
