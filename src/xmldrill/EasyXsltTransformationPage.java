package xmldrill;

import java.io.Serializable;


/**
 * @author Ilya Boyandin
 */
public class EasyXsltTransformationPage extends XsltTransformationPage implements Serializable {

    public EasyXsltTransformationPage() {
        super("xslt-stylesheet-easy.xsl");
    }

    @Override
    public void onSubmit() {
        setResponsePage(new EasyXsltTransformationPage());
    }

    @Override
    public String generateInput() {
        return XmlGenerator.generateXMLForEasyXslt();
    }


}
