package xmldrill;

import java.io.Serializable;

import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.Model;

/**
 * @author Ilya Boyandin
 */
public abstract class XsltTransformationPage extends BasePage implements Serializable {

    private final XsltTransformer xt;
    private final String stylesheet;

    public XsltTransformationPage(String stylesheet) {
        this.stylesheet = stylesheet;

        xt = new XsltTransformer(stylesheet);

        add(new Label<String>("stylesheet", xt.getStylesheetText()));

        final Label<String> generatedInputLabel =
            new Label<String>("generatedInput", generateInput());
        generatedInputLabel.setOutputMarkupId(true);
        add(generatedInputLabel);

        final Label<String> outputLabel = new Label<String>("output", new Model<String>() {
            @Override
            public String getObject() {
                return xt.transform(generatedInputLabel.getModelObject());
            }
        });
        outputLabel.setOutputMarkupId(true);
        outputLabel.setVisible(false);
        add(outputLabel);

        final Label<String> renderedOutputLabel = new Label<String>("outputRendered", new Model<String>() {
            @Override
            public String getObject() {
                return xt.transform(generatedInputLabel.getModelObject());
            }
        });
        renderedOutputLabel.setOutputMarkupId(true);
        renderedOutputLabel.setVisible(false);
        renderedOutputLabel.setEscapeModelStrings(false);


        final Form<Void> form = new Form<Void>("showOutputForm");
        add(form);
        form.add(outputLabel);
        form.add(renderedOutputLabel);

        final AjaxButton<Void> showHtmlBut = new AjaxButton<Void>("showHtmlOutput") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                outputLabel.setVisible(!outputLabel.isVisible());
                renderedOutputLabel.setVisible(!renderedOutputLabel.isVisible());
                target.addComponent(form);
            }
        };
        form.add(showHtmlBut);

        showHtmlBut.setVisible(false);

        final AjaxButton<Void> showOutputBut = new AjaxButton<Void>("showOutput") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setEnabled(false);
                outputLabel.setVisible(true);
                showHtmlBut.setVisible(true);
                target.addComponent(form);
            }
        };
        form.add(showOutputBut);


        form.add(new Button<Void>("newInput") {
            private static final long serialVersionUID = 1L;
            @Override
            public void onSubmit() {
                XsltTransformationPage.this.onSubmit();
            }
        });
    }

    public abstract void onSubmit();

    public String getStylesheet() {
        return stylesheet;
    }

    public abstract String generateInput();
}
