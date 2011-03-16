package ch.unifr.mme;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.io.StringWriter;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.wicket.WicketRuntimeException;

class XsltTransformer { 

    private TransformerFactory transformerFactory;
    
    private final String stylesheet;
    
    public XsltTransformer(String stylesheet) {
        this.stylesheet = stylesheet;
    }

    public String getStylesheetText() {
        InputStreamReader r = new InputStreamReader(getClass().getResourceAsStream(stylesheet));
        char[] chars = new char[1024];
        StringBuilder sb = new StringBuilder();
        try {
            int read;
            do {
                read = r.read(chars);
                if (read > 0) {
                    sb.append(chars, 0, read);
                }
            } while (read >= 0);
            r.close();
        } catch (IOException e) {
            throw new WicketRuntimeException(e);
        }
        return sb.toString();
    }
    
    public String transform(String input) {
        return transform(input, stylesheet);
    }
    
    public String transform(String input, String stylesheet) {
        StringWriter writer = new StringWriter();
        try {
            getTransformer(stylesheet).transform(
                    new StreamSource(new StringReader(input)),
                    new StreamResult(writer));
        } catch (TransformerException e) {
            throw new WicketRuntimeException(e);
        }
        return writer.getBuffer().toString();
    }

    public Transformer getTransformer(String stylesheet) throws TransformerConfigurationException {
        if (transformerFactory == null) {
            transformerFactory = TransformerFactory.newInstance();
        }
        return transformerFactory.newTransformer(new StreamSource(getClass().getResourceAsStream(stylesheet)));
    }

}
