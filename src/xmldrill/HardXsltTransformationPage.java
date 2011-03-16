package xmldrill;

import java.io.Serializable;


/**
 * @author Ilya Boyandin
 */
public class HardXsltTransformationPage extends XsltTransformationPage implements Serializable {

    public HardXsltTransformationPage() {
        super("xslt-stylesheet-hard.xsl");
    }

    @Override
    public void onSubmit() {
        setResponsePage(new HardXsltTransformationPage());
    }

    @Override
    public String generateInput() {
        return XmlGenerator.generateXmlForHardXslt();
    }

}
