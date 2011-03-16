package ch.unifr.mme;


import java.io.Serializable;

import org.apache.wicket.markup.html.WebPage;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.model.Model;

/**
 * NOTE: The pages should be marked serializable. This way they can be saved in the page store
 * and processed without "Page expired" even when the "Back" button is pressed.
 * See
 * http://apache-wicket.1842946.n4.nabble.com/Support-for-back-button-and-new-windows-td1894153.html
 *
 * And
 * https://cwiki.apache.org/WICKET/page-maps.html
 *
 * @author Ilya Boyandin
 */
public abstract class BasePage extends WebPage implements Serializable {

    public BasePage() {
//        add(new NavomaticBorder("navomaticBorder"));
        Label greeting = new Label("greeting", new Model<String>());
        Link changeNick = new Link("changeNick") {
            @Override
            public void onClick() {
                setResponsePage(new UserNamePage(BasePage.this));
            }
        };
        Link homeLink = new Link("homeLink") {
          @Override
          public void onClick() {
            setResponsePage(new HomePage());
          }
        };
        add(homeLink);
        homeLink.setVisible(getClass() != HomePage.class);
        if (MySession.get().isAuthenticated()) {
            greeting.setModelObject("Your nickname is '" + MySession.get().getNickName() + "'.");
        } else {
            greeting.setVisible(false);
            changeNick.setVisible(false);
        }
        add(greeting);
        add(changeNick);
    }

    public TMExercisesApplication getApp() {
        return (TMExercisesApplication)getApplication();
    }

}
