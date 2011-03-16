package xmldrill;

import java.io.IOException;
import java.util.Properties;
import java.util.Random;

/**
 * @author Ilya Boyandin
 */
public class SvgGenerator {

    private static final long serialVersionUID = -1611847045868578734L;
    
    private static final Random rnd = new Random();

    private static final Properties props;

    private static final String template;
    private static final Integer numObjectsToGenerate;

    static {
        props = new Properties();
        try {
            props.load(SvgGenerator.class.getResourceAsStream("svg-gen.properties"));
        } catch (IOException ioe) {
            throw new RuntimeException(ioe);
        }

        template = (String)props.get("svg.template");
        numObjectsToGenerate = Integer.valueOf((String)props.get("svg.numObjectsToGenerate"));
    }
    
    public String generate() {
        StringBuilder generated = new StringBuilder();
        for (int i = 0; i < numObjectsToGenerate; i++) {
            generated.append(ObjType.random().generate()).append("\n");
        }
        
        StringBuilder sb = new StringBuilder();
        sb.append(
                template.replace("#generated#", generated.toString())
        );
        return sb.toString();
    }

    enum ObjType {
        CIRCLE {
            @Override
            public String generate() {
                String color = color();
                return 
                "  <circle cx=\"" + circlePos() + "\" cy=\"" + circlePos() + "\" r=\"" + smnum() + "\" fill=\""  + color +
                    "\" stroke=\"" + ncolor(color) + "\"/>";
            }
        },
        POLYGON {
            @Override
            public String generate() {
                String color = color();
                String ncolor = ncolor(color);
                StringBuilder sb = new StringBuilder();
                sb.append("  <polygon points=\"");
                int repeatCnt = 3 + rnd.nextInt(1);
                for (int k = 0; k < repeatCnt; k++)
                    sb.append(num()).append(",").append(num()).append(" "); 
                sb.append("\" fill=\"").append(color).append("\" stroke=\"").append(ncolor).append("\" />");
                return sb.toString();
            }
        },
//        BEZIER {
//            @Override
//            public String generate() {
//                String stroke = "black";
//                String fill = "none";
//                StringBuilder sb = new StringBuilder();
//                sb.append("  <path d=\"M").append(num()).append(",").append(num()).append(" C");
//                for (int k = 0; k < 3; k++)
//                    sb.append(num()).append(",").append(num()).append(" "); 
//                sb.append("\" fill=\"").append(fill).append("\" stroke=\"").append(stroke).append("\" />");
//                return sb.toString();
//            }
//        },
        BEZIER {
            @Override
            public String generate() {
                String stroke = "black";
                String fill = "none";
                StringBuilder sb = new StringBuilder();
                sb.append("  <path d=\"").append(rnd())
                  .append("\" fill=\"").append(fill).append("\" stroke=\"").append(stroke).append("\" />");
                return sb.toString();
            }

            String rnd() {
                switch (rnd.nextInt(7)) {
                case 0: return "M25,100 C50,50 75,50 100,100";
                case 1: return "M0,75 C25,25 50,25 75,75";
                case 2: return "M75,0 C50,50 25,50 0,0";
                case 3: return "M100,25 C50,50 50,75 100,100";
                case 4: return "M0,75 C50,50 50,25 0,0";
                case 5: return "M0,100 C100,100 0,0 100,0";
                case 6: return "M0,100 C0,0 100,100 100,0";
                default: return null;
                }
            }
        },
        RECT {
            @Override
            public String generate() {
                String stroke = color();
                String fill = ncolor(stroke);
                int x=num(), y=num();
                int w=num(), h=num();
                if (x+w > 100) x=100-w;
                if (y+h > 100) y=100-h;
                if (w>50 || h>50) { fill = "white"; stroke = "black"; }
                return
                "  <rect x=\""+x+"\" y=\""+y+"\" width=\""+w+"\" height=\""+h+"\" stroke=\""+
                stroke+"\" fill=\""+fill+"\"/>";
            }
        },
        ;
        
        public abstract String generate();

        int circlePos() {
            switch (rnd.nextInt(3)) {
            case 0: return 25;
            case 1: return 50;
            case 2: return 75;
            default: return 0;
            }
        }
        
        public static ObjType random() {
            int ri = rnd.nextInt(values().length);
            for (ObjType type : values()) {
                if (type.ordinal() == ri) return type;
            }
            return null;
        }
    }


    static int smnum() {
        switch (rnd.nextInt(3)) {
        case 0: return 5;
        case 1: return 10;
        case 2: return 15;
        default: return 0;
        }
    }


    static int num() {
        switch (rnd.nextInt(4)) {
        case 0: return 5;
        case 1: return 25;
        case 2: return 50;
        case 3: return 100;
        default: return 0;
        }
    }


    static String color() {
        switch (rnd.nextInt(3)) {
        case 1: return "white";
        case 2: return "none";
        case 0: return "black";
        default: return "";
        }
    }

    static String ncolor(String color) {
        if (color.equals("white") || color.equals("none"))
            return "black";
        else
            return "white";
    }

}
