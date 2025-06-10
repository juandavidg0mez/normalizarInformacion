package com.normalizar.templateMemori;

import java.io.ByteArrayInputStream;
import java.io.IOException;

public interface ItemplateCase {
    String selectTemplate(String norma);
    ByteArrayInputStream generatePdfromHtml (String HtmlContent) throws IOException;
}
