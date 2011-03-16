package xmldrill;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Random;

import org.apache.wicket.WicketRuntimeException;

import at.fhj.utils.xml.LightXMLWriter;

/**
 * @author Ilya Boyandin
 */
public class XmlGenerator {

    private XmlGenerator() {
    }

    private static final Random rnd = new Random();

    public static String generateXMLForEasyXslt() {
        StringWriter sw = new StringWriter();
        LightXMLWriter xml = new LightXMLWriter(sw, "utf-8");
        try {
            xml.startDocument();
            xml.tagOpen("doc");
            int nesting = 0;
            for (int i = 0; i < 3; i++) {
                nesting++;
                if (rnd.nextInt() % 2 == 0) {
                    xml.tagOpen("data");
                    xml.attr("name", TITLES[rnd.nextInt(TITLES.length)]);
                    for (int k = 0; k < 3; k++) {
                        xml.tagOpen("item");
                        xml.attr("name", rndWord());
                        xml.text(Integer.toString(rnd.nextInt(100)));
                        xml.tagClose();
                    }
                    xml.tagClose();
                } else {
                    xml.tagOpen("section");
                    xml.attr("title", TITLES[rnd.nextInt(TITLES.length)]);
                    for (int k = 0, n = rnd.nextInt(1) + 2; k < n; k++) {
                        xml.tagOpen("paragraph");
                        xml.text(SENTENCES[rnd.nextInt(SENTENCES.length)]);
                        xml.tagClose();
                    }
                    xml.tagClose();
//                } else {
//                    xml.tagOpen(rndLetter());
//                    if (nesting > 1  &&  rnd.nextInt() % 2 == 0) {
//                        xml.text(rndLetter());
//                        xml.tagClose();
//                        nesting--;
//                    }
                }
            }
            xml.endDocument();
        } catch (IOException e) {
            throw new WicketRuntimeException(e);
        }
        return sw.getBuffer().toString();
    }

    private static final String[] SENTENCES = new String [] {
    "Lorem ipsum dolor sit amet, consectetur adipiscing elit.",
    "Donec et augue a neque varius pretium.",
    "Praesent nec arcu condimentum est blandit feugiat at in felis.",
    "Praesent posuere libero vel nunc ultricies nec congue urna posuere.",
    "Aliquam posuere diam at sapien venenatis quis varius risus varius.",
    "Pellentesque elementum erat vitae neque tincidunt vitae viverra diam laoreet.",
    "Maecenas non mauris eleifend est placerat fringilla et vitae risus.",

    "Pellentesque rhoncus purus in turpis volutpat vulputate.",
    "Etiam dictum pharetra dolor, nec consequat sem cursus et.",
    "Nam ac arcu eu urna ultricies vehicula vitae nec nibh.",
    "Nullam vestibulum urna eget risus congue euismod.",

    "Nunc et nunc tellus, in bibendum lacus.",
    "Fusce quis orci elementum velit tempor tincidunt.",
    "Ut vel purus erat, in dapibus augue.",
    "Phasellus pharetra massa vel lectus dictum suscipit.",
    "Aenean suscipit nulla et ligula venenatis vel pulvinar nisi interdum.",

    "Nullam cursus condimentum justo, quis venenatis magna accumsan sit amet.",
    "Nunc tincidunt augue nec turpis porttitor blandit.",
    "Praesent semper eros scelerisque ligula bibendum sodales.",

    "Praesent commodo odio ut justo pulvinar semper.",
    "Nulla sit amet libero in tortor facilisis lacinia.",
    "Vivamus ultricies vestibulum metus, vel interdum arcu semper ut.",
    "Morbi gravida molestie justo, eu lacinia augue ullamcorper sit amet.",
    "Curabitur placerat est ut eros dignissim scelerisque.",
    "Phasellus dictum justo nec quam vestibulum sed luctus purus vestibulum.",
    };

    private static final String[] TITLES = new String [] {
        "Lorem ipsum dolor sit amet",  "Consectetur adipiscing elit",
        "Donec et augue",
        "Praesent nec arcu condimentum",
        "Praesent posuere libero",
        "Aliquam posuere diam",
        "Pellentesque elementum",
        "Maecenas non mauris",

        "Pellentesque rhoncus",
        "Etiam dictum pharetra dolor",
        "Nam ac arcu eu urna",
        "Nullam vestibulum urna",

        "Nunc et nunc tellus",
        "Fusce quis orci",
        "Ut vel purus erat",
        "Phasellus pharetra massa vel",
        "Aenean suscipit nulla et ligula",

        "Nullam cursus condimentum justo",
        "Nunc tincidunt augue nec",
        "Praesent semper eros",

        "Praesent commodo odio",
        "Nulla sit amet libero",
        "Vivamus ultricies vestibulum metus",
        "Morbi gravida molestie justo",
        "Curabitur placerat",
        "Phasellus dictum justo nec quam vestibulum",
        };
    private static String rndWord() {
        return XmlGenerator2.WORDS[rnd.nextInt(XmlGenerator2.WORDS.length)];
    }

    private static String rndText(int minLen, int maxLen) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0, num = rnd.nextInt(maxLen - minLen) + minLen; i < num; i++) {
            sb.append(rndWord()).append(" ");
        }
        return sb.toString();
    }

    public static String generateXmlForHardXslt() {
        StringWriter sw = new StringWriter();
        LightXMLWriter xml = new LightXMLWriter(sw, "utf-8");
        try {
            xml.startDocument();
            xml.tagOpen("R");
            int nesting = 0;
            for (int i = 0; i < 4; i++) {
                nesting++;
                xml.tagOpen(rndLetter());
                if (rnd.nextInt() % 2 == 0) {
                    xml.attr(rndLetter(), rndLetter());
                }
                if (nesting > 1  &&  rnd.nextInt() % 2 == 0) {
                    xml.text(rndLetter());
                    xml.tagClose();
                    nesting--;
                }
            }
            xml.endDocument();
        } catch (IOException e) {
            throw new WicketRuntimeException(e);
        }
        return sw.getBuffer().toString();
    }

    public static String rndLetter() {
        return Character.toString((char)('A' + rnd.nextInt(4)));
    }

}
