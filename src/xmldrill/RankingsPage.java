package xmldrill;

import java.io.Serializable;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.apache.wicket.AttributeModifier;
import org.apache.wicket.ajax.AbstractAjaxTimerBehavior;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.AjaxSelfUpdatingTimerBehavior;
import org.apache.wicket.ajax.form.AjaxFormComponentUpdatingBehavior;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.repeater.Item;
import org.apache.wicket.markup.repeater.data.DataView;
import org.apache.wicket.markup.repeater.data.IDataProvider;
import org.apache.wicket.model.AbstractReadOnlyModel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.util.time.Duration;

import xmldrill.Rankings.Entry;


/**
 * @author Ilya Boyandin
 */
public class RankingsPage extends BasePage implements Serializable {

//    private final static Logger logger = Logger.getLogger(RankingsPage.class);

    public RankingsPage() {
//        System.out.println("========");
//        System.out.println("Rankings");
//        System.out.println("========");
//        for (Entry entry : rankings) {
//            System.out.println(entry);
//        }
//        ListDataProvider dataProvider = new ListDataProvider(rankings);


        add(createForm());
    }

    private Form<Void> createForm() {
        final Form<Void> form = new Form<Void>("rankingForm");

        final DropDownChoice<String> exercise = new DropDownChoice<String>("exercise", new Model<String>(), Arrays.asList(
                XmlTreePage.EXERCISE_NAME, XmlSchemaPage.EXERCISE_NAME));
        exercise.setNullValid(true);
        exercise.add(new AjaxFormComponentUpdatingBehavior("onchange") {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onUpdate(AjaxRequestTarget target) {
                target.addComponent(form);
            }
        });
        form.add(exercise);

        final IDataProvider<Rankings.Entry> dataProvider = new IDataProvider<Rankings.Entry>() {
            private static final long serialVersionUID = 1L;

            List<Entry> rankings = null;

            private List<Entry> getRankings() {
                if (rankings == null) {
                    Rankings r = getApp().getRankings();
                    String ex = exercise.getModelObject();
                    if (ex == null) {
                        rankings = r.getSnapshot();
                    } else {
                        rankings = r.getSnapshotFor(ex);
                    }
                }
                return rankings;
            }

            @Override
            public Iterator<? extends Entry> iterator(int first, int count) {
                return getRankings().iterator();
            }

            @Override
            public IModel<Entry> model(Entry object) {
                return new Model(object);
            }

            @Override
            public int size() {
                return getRankings().size();
            }

            @Override
            public void detach() {
                rankings = null;
            }

        };
        final DataView<Rankings.Entry> dataView = new DataView<Rankings.Entry>("rows", dataProvider) {
            private static final long serialVersionUID = 1L;
            @Override
            protected void onBeforeRender() {
                super.onBeforeRender();
                dataProvider.detach();
            }
            @Override
            protected void populateItem(final Item<Rankings.Entry> item) {
                final Rankings.Entry ranking = item.getModelObject();
                item.add(new Label("exercise", ranking.getExercise()));
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
        };
        dataView.setOutputMarkupId(true);

//        final CheckBox autoUpdateChk = new CheckBox("autoUpdateChk", new Model<Boolean>(false));
//        form.add(autoUpdateChk);

        form.add(new AjaxSelfUpdatingTimerBehavior(Duration.seconds(5)));
        form.add(new AbstractAjaxTimerBehavior(Duration.seconds(5)) {
            @Override
            protected void onTimer(AjaxRequestTarget target) {
                dataProvider.detach();
//                get("rankingForm").replaceWith(createForm());
            }
        });

        form.add(dataView);

        return form;
    }
}
