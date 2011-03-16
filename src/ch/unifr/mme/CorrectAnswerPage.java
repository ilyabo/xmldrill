package ch.unifr.mme;

import java.io.Serializable;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.ListDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;

import ch.unifr.mme.Rankings.Entry;

public class CorrectAnswerPage extends BasePage implements Serializable {

    public CorrectAnswerPage(String exercise, int earnedPoints, Form<Void> generateForm) {
        final int total = MySession.get().getPoints(exercise);
        add(new Label("title", exercise));
        add(new Label("message",
                "You just earned "+ earnedPoints + " points and your total score is " +
                        (total == earnedPoints ? " also " : "") +
                		total + " points."));

        add(generateForm);

//        add(new Link<Void>("repeatLink") {
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            public void onClick() {
//                setResponsePage(new XmlTreePage());
//            }
//        });


//        add(new Link<Void>("rankingsLink") {
//            private static final long serialVersionUID = 1L;
//
//            @Override
//            public void onClick() {
//                setResponsePage(new RankingsPage());
//            }
//        });


        List<Entry> rankings = getApp().getRankings().getSnapshotFor(exercise);
        System.out.println("========");
        System.out.println("Rankings");
        System.out.println("========");
        for (Entry entry : rankings) {
            System.out.println(entry);
        }
        ListDataProvider dataProvider = new ListDataProvider(rankings);
        add(new DataView<Rankings.Entry>("rows", dataProvider) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void populateItem(final Item<Rankings.Entry> item) {
                final Rankings.Entry ranking = item.getModelObject();
                item.add(new Label("nick", ranking.getNick()));
                item.add(new Label("ip", ranking.getIp()));
                item.add(new Label("points", Integer.toString(ranking.getPoints())));
                item.add(new AttributeModifier("class", true, new AbstractReadOnlyModel<String>() {
                    private static final long serialVersionUID = 1L;
                    @Override
                    public String getObject() {
                        return (item.getIndex() % 2 != 0) ? "even" : "odd";
                    }
                }));
            }
        });

    }

}
