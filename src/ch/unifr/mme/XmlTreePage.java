package ch.unifr.mme;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.wicket.WicketRuntimeException;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.custommonkey.xmlunit.DetailedDiff;
import org.custommonkey.xmlunit.Diff;
import org.custommonkey.xmlunit.Difference;
import org.custommonkey.xmlunit.XMLUnit;


/**
 * @author Ilya Boyandin
 */
public class XmlTreePage extends BasePage implements Serializable {

    public static final String EXERCISE_NAME = "XML Tree";

    private final static XsltTransformer xt =
        new XsltTransformer("xml2svgtree.xsl");
//        new XsltTransformer("xml2svgtree2.xsl");

    private final Form<Void> xmlTreeForm;
    private final XmlGenerator2 xmlGen;
    private final TextArea<String> answerTextArea;

    public XmlTreePage() {
        this(2, 5);
    }

    public XmlTreePage(int generateTreeOfSize) {
        this(generateTreeOfSize, generateTreeOfSize);
    }

    public XmlTreePage(int minTreeSize, int maxTreeSize) {
        if (!MySession.get().isAuthenticated()) {
            setResponsePage(new UserNamePage(getClass()));
        }

        final GenerateForm generateForm = new GenerateForm(
                "generateForm",
                minTreeSize == maxTreeSize ? minTreeSize : null);
        add(generateForm);

        xmlTreeForm = new Form<Void>("xmlTreeForm");
        xmlGen =
//          new XmlGenerator2(10, 20);
          new XmlGenerator2(minTreeSize, maxTreeSize);
        answerTextArea = new TextArea<String>("answerXml", new Model<String>());
        final String randomXml = xmlGen.generate();

        add(new Label("cost", Integer.toString(xmlGen.getCost())));

        answerTextArea.setRequired(true);
        answerTextArea.add(new AbstractValidator() {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onValidate(IValidatable validatable) {
                String answerXml = (String)validatable.getValue();
                DetailedDiff myDiff;
                try {
                    XMLUnit.setIgnoreWhitespace(true);
                    myDiff = new DetailedDiff(new Diff(randomXml, answerXml));
                    List<Difference> allDifferences = myDiff.getAllDifferences();

                    for (Difference diff : allDifferences) {
                        errorMessage(validatable, diff.toString());
                    }

                } catch (Exception e) {
                    errorMessage(validatable, e.getMessage());
                }
            }
        });


//        System.out.println(randomXml);
//        System.out.println("TREE HEIGHT: " + xmlGen.getTreeHeight());

        add(xmlTreeForm);


        xmlTreeForm.add(new FeedbackPanel("feedback"));
        xmlTreeForm.add(answerTextArea);

        DynamicImageResource svgImageRes = new DynamicImageResource("png") {
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getImageData() {
                PNGTranscoder t = new PNGTranscoder();

                try {
                    String svg = xt.transform(randomXml);
//                    System.out.println(svg);
                    TranscoderInput input = new TranscoderInput(new StringReader(svg));
                    input.setURI("http://diuf.unifr.ch/my.svg");

                    ByteArrayOutputStream os = new ByteArrayOutputStream();
                    TranscoderOutput output = new TranscoderOutput(os);

                    t.addTranscodingHint(PNGTranscoder.KEY_WIDTH, (float)40*xmlGen.getNumOfElements() );
                    t.addTranscodingHint(PNGTranscoder.KEY_HEIGHT, (float)xmlGen.getTreeHeight() * 30);
                    t.addTranscodingHint(PNGTranscoder.KEY_EXECUTE_ONLOAD, Boolean.TRUE);
                    t.transcode(input, output);

                    return os.toByteArray();
                } catch (Exception e) {
                    throw new WicketRuntimeException(e);
                }
            }

        };
        svgImageRes.setCacheable(false);

        add(new Image<Void>("svgImage", svgImageRes));


        xmlTreeForm.add(new Button<Void>("okBut") {
            private static final long serialVersionUID = 1L;
            @Override
            public void onSubmit() {
                final int earnedPoints = xmlGen.getCost();
                MySession.get().addPoints(EXERCISE_NAME, earnedPoints);
                setRedirect(true);
                setResponsePage(new CorrectAnswerPage(
                        EXERCISE_NAME, earnedPoints,
                        new GenerateForm("generateForm", generateForm.getTreeSize())));
            }
        });


        Button<Void> cancelBut = new Button<Void>("cancelBut") {
            private static final long serialVersionUID = 1L;
            @Override
            public void onSubmit() {
                setRedirect(true);
                setResponsePage(new HomePage());
            }
        };
        cancelBut.setDefaultFormProcessing(false);
        xmlTreeForm.add(cancelBut);


        add(xmlTreeForm);
    }

    public static class GenerateForm extends Form<Void> {
        private final DropDownChoice<Integer> numOfTagsDDC;
        public GenerateForm(String id, Integer prevTreeSize) {
            super(id);
            final List<Integer> values = Arrays.asList(2, 5, 10, 15);
            numOfTagsDDC = new DropDownChoice<Integer>("numOfTags", new Model<Integer>(), values);
            if (prevTreeSize != null  &&  values.contains(prevTreeSize)) {
                numOfTagsDDC.getModel().setObject(prevTreeSize);
            }

            add(new Button<Void>("newInput") {
                private static final long serialVersionUID = 1L;
                @Override
                public void onSubmit() {
                    setRedirect(true);
                    Integer treeSize = numOfTagsDDC.getModelObject();
                    XmlTreePage page;
                    if (treeSize == null)
                        page = new XmlTreePage();
                    else
                        page = new XmlTreePage(treeSize);
                    setResponsePage(page);
                }
            });

            add(numOfTagsDDC);
        }
        public Integer getTreeSize() {
            return numOfTagsDDC.getModelObject();
        }
    }

    protected void errorMessage(IValidatable validatable, String message) {
        validatable.error(new ValidationError().setMessage(message));
    }


}
