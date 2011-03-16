package ch.unifr.mme;

import java.io.Serializable;
import java.util.regex.Pattern;

import org.apache.wicket.Page;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Button;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.FeedbackPanel;
import org.apache.wicket.model.Model;
import org.apache.wicket.validation.IValidatable;
import org.apache.wicket.validation.ValidationError;
import org.apache.wicket.validation.validator.AbstractValidator;
import org.apache.wicket.validation.validator.StringValidator;

/**
 * @author Ilya Boyandin
 */
public class UserNamePage extends BasePage implements Serializable {

    private final Page pageToReturnTo;
    private final Class<? extends BasePage> pageClassToReturnTo;

    public UserNamePage() {
        this(null, null);
    }

    public UserNamePage(Page pageToReturnTo) {
        this(pageToReturnTo, null);
    }

    public UserNamePage(Class<? extends BasePage> pageClassToReturnTo) {
        this(null, pageClassToReturnTo);
    }

    public UserNamePage(Page _pageToReturnTo, Class<? extends BasePage> _pageClassToReturnTo) {
        this.pageToReturnTo = _pageToReturnTo;
        this.pageClassToReturnTo = _pageClassToReturnTo;


        Form<Void> form = new Form<Void>("nicknameForm");
        add(form);

        final MySession mySession = MySession.get();
        String oldNickname = mySession.isAuthenticated() ? mySession.getNickName() : "";
        final TextField<String> nickname = new TextField<String>("nickname",
                new Model<String>(oldNickname));
        nickname.setRequired(true)
                .add(new StringValidator.LengthBetweenValidator(3, 64))
                .add(new AbstractValidator() {
                    Pattern p = Pattern.compile("^[\\p{L}0-9\\-\\.\\_]+$");
                    private static final long serialVersionUID = 1L;
                    @Override
                    protected void onValidate(IValidatable v) {
                        String nick = (String)v.getValue();
                        if (!p.matcher(nick).matches())
                            v.error(new ValidationError().setMessage("Nickname must consist of letters and digits."));
                    }
                });
        Label warning = new Label("warning", "NOTE: If you change your nickname the POINTS IN THE RANKINGS WILL START FROM ZERO.");
        if (!mySession.isAuthenticated()) {
            warning.setVisible(false);
        }
        form.add(warning);
        form.add(new FeedbackPanel("feedback"));
        form.add(nickname);

        form.add(new Button<Void>("okBut") {
            private static final long serialVersionUID = 1L;
            @Override
            public void onSubmit() {
                mySession.setNickName(nickname.getModelObject().trim());
                goBack();
            }
        });

        final Button<Void> cancelButton = new Button<Void>("cancelBut") {
            private static final long serialVersionUID = 1L;
            @Override
            public void onSubmit() {
                goBack();
            }
        };
        form.add(cancelButton.setDefaultFormProcessing(false));
        if (!mySession.isAuthenticated()) {
            cancelButton.setVisible(false);
        }


    }

    private void goBack() {
        if (pageToReturnTo != null) {
            setResponsePage(pageToReturnTo);
        } else if (pageClassToReturnTo != null) {
            setResponsePage(pageClassToReturnTo);
        } else {
            setResponsePage(UserNamePage.class);
        }
    }

}
