package com.normalizar.templateMemori;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;

import javax.imageio.ImageIO;

import org.w3c.dom.Element;
import org.xhtmlrenderer.extend.FSImage;
import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.extend.ReplacedElementFactory;
import org.xhtmlrenderer.extend.UserAgentCallback;
import org.xhtmlrenderer.layout.LayoutContext;
import org.xhtmlrenderer.pdf.ITextImageElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.simple.extend.FormSubmissionListener;
import org.xhtmlrenderer.swing.AWTFSImage;

public class B64ImageReplacedElementFactory implements ReplacedElementFactory {

    @Override
    public ReplacedElement createReplacedElement(LayoutContext c, BlockBox box, UserAgentCallback uac, int cssWidth,
            int cssHeight) {
       try {
         String url = box.getElement().getAttribute("src");
            if (url != null && url.startsWith("data:image")) {

                String base64Data = url.substring(url.indexOf(",") + 1);
                byte[] imageBytes = Base64.getDecoder().decode(base64Data);

                // Crear un ByteArrayInputStream para la imagen
                ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
                 BufferedImage bufferedImage = ImageIO.read(inputStream);

                if (bufferedImage == null) {
                    throw new IllegalArgumentException("No se pudo leer la imagen desde los datos Base64.");
                }
                FSImage fsImage = AWTFSImage.createImage(bufferedImage);

                // Crear un elemento reemplazado para la imagen
                 return new ITextImageElement(fsImage);
            }
       } catch (Exception e) {
         e.printStackTrace();
       }
         return null;
    }

    @Override
    public void reset() {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'reset'");
    }

    @Override
    public void remove(Element e) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }

    @Override
    public void setFormSubmissionListener(FormSubmissionListener listener) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'setFormSubmissionListener'");
    }

}
