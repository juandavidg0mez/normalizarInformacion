package com.normalizar.templateMemori;



import java.io.IOException;

public interface ItemplateCase {
    String selectTemplate(String norma);
    byte[] generatePdfromHtml (String HtmlContent) throws IOException;
}
