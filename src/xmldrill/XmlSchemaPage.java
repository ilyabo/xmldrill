package xmldrill;

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextArea;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

/**
 * @author Ilya Boyandin
 */
public class XmlSchemaPage extends BasePage implements Serializable {

    public static final String EXERCISE_NAME = "XML Schema";

    static final String JAXP_SCHEMA_SOURCE =
        "http://java.sun.com/xml/jaxp/properties/schemaSource";

    static final String JAXP_SCHEMA_LANGUAGE =
        "http://java.sun.com/xml/jaxp/properties/schemaLanguage";

    static final String W3C_XML_SCHEMA =
        "http://www.w3.org/2001/XMLSchema";

    private final Form<Void> xmlTreeForm;
    private final XmlGenerator2 schemaGen;
    private final TextArea<String> answerTextArea;

    public XmlSchemaPage() {
        this(2, 5);
    }

    public XmlSchemaPage(int generateTreeOfSize) {
        this(generateTreeOfSize, generateTreeOfSize);
    }

    public XmlSchemaPage(int minTreeSize, int maxTreeSize) {
        if (!MySession.get().isAuthenticated()) {
            setResponsePage(new UserNamePage(getClass()));
        }

        final GenerateForm generateForm = new GenerateForm(
                "generateForm",
                minTreeSize == maxTreeSize ? minTreeSize : null);
        add(generateForm);

        xmlTreeForm = new Form<Void>("xmlTreeForm");
        schemaGen =
//          new XmlGenerator2(10, 20);
          new XmlGenerator2(minTreeSize, maxTreeSize);
        answerTextArea = new TextArea<String>("answerXml", new Model<String>());

        final String schema = schemaGen.generateSchema();

        add(new Label("cost", Integer.toString(schemaGen.getCost())));


        add(new Label("schema", schema));


        answerTextArea.setRequired(true);
        answerTextArea.add(new DocValidator(schema));


//        System.out.println(randomXml);
//        System.out.println("TREE HEIGHT: " + xmlGen.getTreeHeight());

        add(xmlTreeForm);


        xmlTreeForm.add(new FeedbackPanel("feedback"));
        xmlTreeForm.add(answerTextArea);



        xmlTreeForm.add(new Button<Void>("okBut") {
            private static final long serialVersionUID = 1L;
            @Override
            public void onSubmit() {
                final int earnedPoints = schemaGen.getCost();
                MySession.get().addPoints(EXERCISE_NAME, earnedPoints);
                setRedirect(true);
                setResponsePage(new CorrectAnswerPage(
                        EXERCISE_NAME, earnedPoints,
                        new GenerateForm("generateForm", generateForm.getTreeSize())
                        ));
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


    class DocValidator extends AbstractValidator {
        public DocValidator(String schema) {
            this.schema = schema;
        }
        private static final long serialVersionUID = 1L;

        private final String schema;


        @Override
        protected void onValidate(final IValidatable validatable) {

            String answerXml = (String)validatable.getValue();

            DocumentBuilderFactory factory =
                DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            factory.setValidating(true);

          try {
              Schema sc = SchemaFactory.newInstance(W3C_XML_SCHEMA).newSchema(new StreamSource(
                      new ByteArrayInputStream(schema.getBytes("utf-8")),
                      "myschema.xsd"
              ));
            factory.setSchema(sc);
//              factory.setAttribute(JAXP_SCHEMA_LANGUAGE, W3C_XML_SCHEMA);
//              factory.setAttribute(JAXP_SCHEMA_SOURCE, schema);
            DocumentBuilder builder = factory.newDocumentBuilder();
            builder.setErrorHandler(new ErrorHandler() {

                @Override
                public void error(SAXParseException ex)
                        throws SAXException {
                    String msg = ex.getMessage();
                    if (msg != null) {
                        if (msg.equals("Document is invalid: no grammar found.") ||
                            msg.endsWith("must match DOCTYPE root \"null\".")) {
                            return;
                        }
                    }
                    errorMessage(validatable, msg != null ? msg : ex.getClass().getSimpleName());
                }

                @Override
                public void fatalError(SAXParseException exception)
                        throws SAXException {
                    //error(exception);
                }

                @Override
                public void warning(SAXParseException exception)
                        throws SAXException {
                }

            });
            builder.parse(new ByteArrayInputStream(answerXml.getBytes("utf-8")));
          }
          catch (Exception ex) {
              String msg = ex.getMessage();
              errorMessage(validatable, msg != null ? msg : ex.getClass().getSimpleName());
          }


//            String answerXml = (String)validatable.getValue();
//            DetailedDiff myDiff;
//            try {
//                XMLUnit.setIgnoreWhitespace(true);
//                myDiff = new DetailedDiff(new Diff(randomXml, answerXml));
//                List<Difference> allDifferences = myDiff.getAllDifferences();
//
//                for (Difference diff : allDifferences) {
//                    errorMessage(validatable, diff.toString());
//                }
//
//            } catch (Exception e) {
//                errorMessage(validatable, e.getMessage());
//            }
        }
    }

    public static class GenerateForm extends Form<Void> {
        private final DropDownChoice<Integer> numOfTagsDDC;
        public GenerateForm(String id, Integer prevTreeSize) {
            super(id);
            final List<Integer> values = Arrays.asList(2, 5, 7, 10);
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
                    XmlSchemaPage page;
                    if (treeSize == null)
                        page = new XmlSchemaPage();
                    else
                        page = new XmlSchemaPage(treeSize);
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
