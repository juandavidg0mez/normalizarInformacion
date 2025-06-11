package com.normalizar.templateMemori;


import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.bouncycastle.jcajce.provider.asymmetric.rsa.DigestSignatureSpi.noneRSA;

import com.openhtmltopdf.pdfboxout.PdfRendererBuilder;

public class ImpleCaseMemory implements ItemplateCase {

    @Override
    public String selectTemplate(String norma) {
        return switch (norma.toLowerCase()) {
            case "IEC 61869-2"  -> "IEC 61869-2";
            case "iso9001" -> "iso9001";
            case "gdpr" -> "gdpr";
            default -> "default";
        };
    }

    @Override
    public byte[] generatePdfromHtml(String HtmlContent) throws IOException {
        ByteArrayOutputStream pdfGenerate = new ByteArrayOutputStream();
        try{
           PdfRendererBuilder builder = new PdfRendererBuilder();
           builder.withHtmlContent(HtmlContent, null);
           builder.toStream(pdfGenerate);
           builder.run();

            
        } catch (Exception e) {
            throw new IOException("Error generating PDF from HTML: " + e.getMessage(), e);
        }
        return pdfGenerate.toByteArray();
        
    }

}
							
