package xmldrill;

import java.io.ByteArrayOutputStream;
import java.io.Serializable;
import java.io.StringReader;

import org.apache.batik.transcoder.TranscoderException;
import org.apache.batik.transcoder.TranscoderInput;
import org.apache.batik.transcoder.TranscoderOutput;
import org.apache.batik.transcoder.image.PNGTranscoder;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.form.AjaxButton;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.image.Image;
import org.apache.wicket.markup.html.image.resource.DynamicImageResource;

/**
 * @author Ilya Boyandin
 */
public class SvgGeneratorPage extends BasePage implements Serializable {

    private final SvgGenerator svgGenerator = new SvgGenerator();

    public SvgGeneratorPage() {
        final Label<String> generatedSvgLabel =
            new Label<String>("generatedSvg", svgGenerator.generate());
        generatedSvgLabel.setOutputMarkupId(true);
        add(generatedSvgLabel);

        final DynamicImageResource svgImageRes = new DynamicImageResource("png") {
            private static final long serialVersionUID = 1L;

            @Override
            protected byte[] getImageData() {
                PNGTranscoder t = new PNGTranscoder();

                TranscoderInput input = new TranscoderInput(
                        new StringReader(generatedSvgLabel.getModelObject()));

                ByteArrayOutputStream os = new ByteArrayOutputStream();
                TranscoderOutput output = new TranscoderOutput(os);

                try {
                    t.transcode(input, output);
                } catch (TranscoderException e) {
                    throw new RuntimeException(e);
                }

                return os.toByteArray();
            }

        };
        svgImageRes.setCacheable(false);
//        final Image<Void> svgImage = new Image<Void>("svgImage",
//                new DynamicImageResource("svg+xml") {
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            protected byte[] getImageData() {
//                return generatedSvgLabel.getModelObject().getBytes();
//            }
//        }
//        );
        final Image<Void> svgImage = new Image<Void>("svgImage",  svgImageRes);
        svgImage.setVisible(false);
        svgImage.setOutputMarkupId(true);

        final Form<Void> form = new Form<Void>("showImageForm");
        add(form);
        form.add(svgImage);

        final AjaxButton<Void> showImageBut = new AjaxButton<Void>("showImage") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
                setEnabled(false);
                svgImage.setVisible(true);
                target.addComponent(form);
            }
        };
        form.add(showImageBut);

//        form.add(new AjaxButton<Void>("newImage") {
//            private static final long serialVersionUID = 1L;
//            @Override
//            protected void onSubmit(AjaxRequestTarget target, Form<?> form) {
//                showImageBut.setEnabled(true);
//                svgImage.setVisible(false);
//                generatedSvgLabel.setModelObject(svgGenerator.generate());
//                target.addComponent(generatedSvgLabel);
//                target.addComponent(form);
//            }
//        });
        form.add(new Button<Void>("newImage") {
            private static final long serialVersionUID = 1L;
            @Override
            public void onSubmit() {
                setResponsePage(new SvgGeneratorPage());
            }
        });

    }
}
