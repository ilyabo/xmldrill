package xmldrill;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;

import org.apache.wicket.WicketRuntimeException;

import at.fhj.utils.xml.LightXMLWriter;

public class XmlGenerator2 {
    private final int numTags;
    private int numOfElements;
    private int maxNesting;
    private static final Random rnd = new Random();
    private final Set<String> usedWords = new HashSet<String>();

    public XmlGenerator2(int minTagsNum, int maxTagsNum) {
        this.numTags = minTagsNum + (maxTagsNum - minTagsNum > 0 ? rnd.nextInt(maxTagsNum - minTagsNum) : 0);
    }

    public String rndWordFromList(boolean unique) {
      if (!unique  ||  usedWords.size() >= WORDS.length) {
        return WORDS[rnd.nextInt(WORDS.length)];
      } else {
        String word;
        do {
          word = WORDS[rnd.nextInt(WORDS.length)];
        } while (usedWords.contains(word));
        usedWords.add(word);
        return word;
      }
    }

    public static String rndWord(int len) {
        char[] buf = new char[len];
        for (int i = 0; i < len; i++) {
            buf[i] = (char)('a' + rnd.nextInt(26));
        }
        return new String(buf);
    }

    public String generate() {
      usedWords.clear();
      maxNesting = 0;
      numOfElements = 0;
      StringWriter sw = new StringWriter();
      LightXMLWriter xml = new LightXMLWriter(sw, "utf-8");
      try {
          xml.startDocument();
          xml.tagOpen(rndWordFromList(false));
          numOfElements++;
          int nesting = 0;
          for (int i = 0; i < numTags; i++) {
              numOfElements++;
              nesting++;
              if (nesting > maxNesting) maxNesting = nesting;
              xml.tagOpen(rndWordFromList(false));
              if (rnd.nextInt() % 2 == 0)
              {
                  numOfElements++;
                  if (nesting + 1 > maxNesting) maxNesting = nesting + 1;
                  xml.attr(rndWord(1), rndWordFromList(false));
              }
              if (nesting > 0  &&  rnd.nextInt() % 2 == 0) {
                  numOfElements++;
                  if (nesting + 1 > maxNesting) maxNesting = nesting + 1;
                  xml.text(rndWordFromList(false).toUpperCase());
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

    public String generateSchema() {
      usedWords.clear();
        maxNesting = 0;
        numOfElements = 0;
        StringWriter sw = new StringWriter();
        LightXMLWriter xml = new LightXMLWriter(sw, "utf-8");
        try {
            xml.startDocument();
            xml.tagOpen("xs:schema");
            xml.attr("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
//            xml.attr("targetNamespace", "http://unifr.ch");

            xml.tagOpen("xs:element");
            xml.attr("name", rndWordFromList(true));
            xml.tagOpen("xs:complexType");
            xml.tagOpen("xs:sequence");

            numOfElements++;
            int nesting = 0;
            for (int i = 0; i < numTags; i++) {
                numOfElements++;
                nesting++;
                if (nesting > maxNesting) maxNesting = nesting;

                xml.tagOpen("xs:element");
                xml.attr("name", rndWordFromList(true));

                boolean complex = i == 0 || (i < numTags - 1  &&  rnd.nextInt(3) == 0);
                if (!complex) {
                    switch (rnd.nextInt(4)) {
                    case 0:
                    case 1:
                        xml.attr("type", SCHEMA_TYPES[rnd.nextInt(SCHEMA_TYPES.length)]);
                        break;
                    case 2:
                        xml.tagOpen("xs:simpleType");
                        xml.tagOpen("xs:restriction");
                        xml.attr("base", "xs:integer");
                        xml.tagOpen("xs:minInclusive");
                        xml.attr("value", Integer.toString(rnd.nextInt(50)));
                        xml.tagClose();
                        xml.tagOpen("xs:maxInclusive");
                        xml.attr("value", Integer.toString(rnd.nextInt(50) + 50));
                        xml.tagClose();
                        xml.tagClose();
                        xml.tagClose();
                        numOfElements+=2;
                        break;
                    case 3:
                        addEnumerationType(xml);
                        break;
                    }
                }

                if (complex) {
                    xml.tagOpen("xs:complexType");
                    if (rnd.nextInt(2) == 0) {
                        for (int k = 0, numAttrs = rnd.nextInt(4)+1; k < numAttrs; k++) {
                            numOfElements++;
                            xml.tagOpen("xs:attribute");
                            xml.attr("name", rndWordFromList(true));
                            if (rnd.nextInt(3) < 2) xml.attr("use", "required");
                            if (rnd.nextInt(2) == 0) {
                                xml.attr("type", SCHEMA_TYPES[rnd.nextInt(SCHEMA_TYPES.length)]);
                            } else {
                                addEnumerationType(xml);
                            }
                            xml.tagClose();
                        }
                        xml.tagClose();
                        xml.tagClose();
                    } else {
                        xml.tagOpen(SCHEMA_INDICATORS[rnd.nextInt(SCHEMA_INDICATORS.length)]);
                    }
                } else {
                    xml.tagClose();
                }
            }
            xml.endDocument();
        } catch (IOException e) {
            throw new WicketRuntimeException(e);
        }
        return sw.getBuffer().toString();
    }

    private void addEnumerationType(LightXMLWriter xml) throws IOException {
        xml.tagOpen("xs:simpleType");
        xml.tagOpen("xs:restriction");
        xml.attr("base", "xs:string");

        for (int k = 0, numAttrs = rnd.nextInt(3)+2; k < numAttrs; k++) {
            xml.tagOpen("xs:enumeration");
            xml.attr("value", rndWordFromList(true));
            xml.tagClose();
            numOfElements++;
        }
        numOfElements++;

        xml.tagClose();
        xml.tagClose();
    }

    public int getTreeHeight() {
        return maxNesting + 1;
    }

    public int getNumTags() {
        return numTags;
    }

    public int getNumOfElements() {
        return numOfElements;
    }

    public int getCost() {
        return (int)Math.round(numOfElements * 1.2);
    }

    private static final String[] SCHEMA_INDICATORS = {
        "xs:sequence", "xs:choice", "xs:all"
    };
    private static final String[] SCHEMA_TYPES = {
        "xs:string",
        "xs:decimal",
        "xs:integer",
        "xs:boolean",
        "xs:date",
        "xs:time"
    };

    public static final String[] WORDS = {
        "aah",  "aal",  "aas",  "aba",  "abe",  "abo",  "abs",  "abu",  "aby",  "ace",  "ack",  "act",
        "ada",  "add",  "ade",  "ado",  "ads",  "adz",  "aff",  "aft",  "aga",  "age",  "ago",  "aha",
        "aid",  "ail",  "aim",  "ain",  "air",  "ais",  "ait",  "aja",  "ala",  "alb",  "ale",  "all",
        "alm",  "alp",  "als",      "alt",  "ama",  "ami",  "amp",  "amu",  "amy",  "ana",  "and",
        "ane",  "ang",  "ani",  "ann",  "ant",  "any",  "ape",  "apt",      "arb",  "arc",  "are",
        "arf",  "arg",  "ark",  "arm",  "ars",  "art",  "ash",  "ask",  "asp",  "ass",  "ate",  "aud",
        "auk",  "ava",  "ave",  "avo",  "awa",  "awe",  "awl",  "awn",  "axe",  "aye",  "ays",  "azo",

        "baa",  "bad",  "bag",  "bah",  "bam",  "ban",  "bar",  "bas",  "bat",  "bay",      "bed",
        "bee",  "beg",  "bel",  "ben",  "bet",  "bey",  "bib",  "bic",  "bid",  "big",  "bin",  "bio",
        "bis",  "bit",  "biz",  "boa",  "bob",  "bod",  "bog",  "bon",  "boo",  "bop",  "bow",  "box",
        "boy",  "boz",  "bra",  "bro",  "brr",  "bub",  "bud",  "bug",  "bum",  "bun",  "bur",  "bus",
        "but",  "buy",  "bye",

        "cab",  "cad",  "cam",  "can",  "cap",  "car",  "cat",  "caw",  "cay",  "cee",  "cel",
        "cha",  "chi",  "cob",  "cod",  "cog",      "com",  "con",  "coo",  "cop",  "cos",  "cot",
        "cow",  "cox",  "coy",      "cry",  "cub",  "cud",  "cum",  "cup",  "cur",  "cut",
        "cyn",

        "dab",  "dad",  "dag",  "dah",  "dak",  "dal",  "dam",  "dan",  "dao",  "dap",  "dar",  "dat",
        "daw",  "day",  "deb",  "dee",  "def",  "dei",  "del",  "den",  "des",  "dev",  "dew",  "dex",
        "dey",  "dib",  "did",  "die",  "dig",  "dim",  "din",  "dio",  "dip",  "dis",  "dit",  "diz",
        "doc",  "doe",  "dog",  "dol",  "dom",  "don",  "doo",  "dor",      "dot",  "dow",  "dry",
        "dub",  "dud",  "due",  "dug",  "duh",  "dui",  "duo",  "dup",  "dye",  "dzo",

        "ear",  "eat",  "eau",  "ebb",  "ecu",  "edh",  "eds",          "eek",  "eel",  "eff",
        "efs",  "eft",  "egg",  "ego",  "eke",  "elf",  "elk",  "eli",  "ell",  "elm",  "els",  "ems",
        "emu",  "end",  "eng",  "ens",  "ent",  "eon",  "era",  "erg",  "ern",  "err",  "ers",  "ess",
        "eta",      "eth",  "eva",  "eve",  "ewe",  "eye",

        "fab",  "fad",  "fag",  "fan",  "far",  "fat",  "fax",  "fay",  "fed",  "fee",  "feh",  "fem",
        "fen",  "fer",  "fet",  "feu",  "few",  "fey",  "fez",  "fib",  "fid",  "fie",  "fig",  "fin",
        "fir",  "fit",  "fix",  "fiz",  "flo",  "flu",  "fly",  "fob",  "foe",  "fog",  "foh",  "fon",
        "fop",  "for",  "fou",  "fox",  "foy",  "fro",  "fry",  "fub",  "fud",  "fug",  "fum",  "fun",
        "fur",

        "gab",  "gad",  "gae",  "gag",  "gak",  "gal",  "gam",  "gan",  "gap",  "gar",  "gas",  "gay",
        "ged",  "gee",  "gel",  "gem",  "geo",  "get",  "gey",  "ghi",  "gib",  "gie",  "gig",  "gin",
        "gip",  "git",  "gnu",  "goa",  "gob",  "god",  "goo",  "gor",  "got",  "gov",  "gox",  "goy",
        "gul",  "gum",  "gun",  "gus",  "gut",  "guv",  "guy",  "gym",  "gyp",

        "hab",  "had",  "hae",  "hag",  "hah",  "haj",  "hal",  "ham",  "han",  "hap",  "har",  "has",
        "hat",  "haw",  "hay",      "hee",  "hef",  "heh",  "hem",  "hen",  "hep",  "her",
        "het",  "hew",  "hex",  "hey",  "hic",  "hid",  "hie",  "him",  "hin",  "hip",  "his",
        "hit",  "hmm",  "hob",  "hoc",  "hod",  "hoe",  "hog",  "hoi",  "hon",  "hop",      "hot",
        "how",  "hoy",  "hub",  "hue",  "hug",  "huh",  "hum",  "hun",  "hup",  "hut",  "hyp",

        "ian",  "ice",  "ich",  "ick",  "icy",  "ida",  "ids",  "iff",  "ifs",  "ike",  "ilk",  "ill",
            "imp",  "ink",  "inn",  "ins",  "ion",  "ira",  "ire",  "irk",  "irv",  "ism",  "its",
                "ivy",

        "jab",  "jag",  "jai",  "jam",  "jan",  "jap",  "jar",  "jav",  "jaw",  "jax",  "jay",  "jeb",
        "jed",  "jee",  "jen",  "jet",  "jeu",  "jew",  "jib",  "jig",  "jim",  "jin",  "job",  "joe",
        "jog",  "jon",  "jot",  "joy",  "jud",  "jug",  "jul",  "jut",

        "kab",  "kae",  "kaf",  "kas",  "kat",  "kay",  "kea",  "kef",  "keg",  "kei",  "ken",  "kep",
        "kev",  "kex",  "key",  "khi",  "kid",  "kif",  "kim",  "kin",  "kip",  "kir",  "kit",  "koa",
        "kob",  "kop",  "kor",  "kos",  "kue",

        "lab",  "lac",  "lad",  "lag",  "lam",  "lao",  "lap",  "lar",  "las",  "lat",  "lav",  "law",
        "lax",  "lay",  "lea",  "led",  "lee",  "leg",  "lei",  "lek",  "leo",  "les",  "let",  "leu",
        "lev",  "lew",  "lex",  "ley",  "lez",  "lib",  "lid",  "lie",  "lil",      "lin",  "lip",
        "lis",  "lit",  "liz",  "loa",  "lob",  "log",  "loo",  "lop",  "los",  "lot",  "lou",  "low",
        "lox",      "lub",  "luc",  "lug",  "lum",  "luv",  "lye",

        "mac",  "mad",  "mae",  "mag",  "mah",  "mai",  "mal",  "man",  "mao",  "map",  "mar",  "mas",
            "mat",  "maw",  "max",  "may",  "med",  "meg",  "mel",  "mem",  "men",  "met",  "mew",
        "mex",  "mho",  "mia",  "mib",  "mic",  "mid",  "mig",  "mil",  "mim",  "min",  "mir",  "mis",
        "mix",  "moa",  "mob",  "mod",  "moe",  "mog",  "moi",  "mol",  "mom",  "mon",  "moo",  "mop",
        "mor",  "mos",  "mot",  "mow",  "mud",  "mug",  "mum",  "mun",  "mus",  "mut",

        "nab",  "nae",  "nag",  "nah",  "nam",  "nap",  "nat",  "naw",  "nay",  "neb",  "ned",  "nee",
        "neo",  "net",  "new",  "nib",      "nil",  "nim",  "nip",  "nix",  "nob",  "nod",  "nog",
        "noh",  "nom",  "non",  "noo",  "nor",  "nos",  "not",  "now",  "nth",  "nub",  "nun",  "nus",
        "nut",

        "oaf",  "oak",  "oar",  "oat",  "obe",  "obi",  "occ",  "odd",  "ode",  "ods",  "oes",  "off",
        "oft",  "ohm",  "oho",  "ohs",  "oil",  "oka",  "oke",  "old",  "ole",  "oms",  "one",  "ons",
        "oof",  "ooh",  "oom",  "opt",  "ora",  "orb",  "orc",  "ore",  "ork",  "ors",  "ort",  "ose",
        "oud",  "our",  "out",  "ova",  "owe",  "owl",  "own",  "oxo",  "oxy",

        "pac",  "pad",  "pah",  "pal",  "pam",  "pan",  "pap",  "par",  "pas",      "pat",  "paw",
        "pax",  "pay",  "pea",  "ped",  "pee",  "peg",  "peh",  "pen",  "pep",  "per",  "pes",  "pet",
        "pew",  "pez",  "phi",  "pho",  "pia",  "pic",  "pie",  "pig",  "pin",  "pip",  "pis",  "pit",
        "piu",  "pix",  "ply",  "pod",  "poe",  "poh",  "poi",  "pol",  "pom",  "poo",  "pop",  "pot",
        "pow",  "pox",  "pre",  "pro",  "pry",  "psi",  "pub",  "pud",  "pug",  "pul",  "pun",  "pup",
        "pur",  "pus",  "put",  "pya",  "pye",  "pyx",

        "qat",  "qua",  "que",  "quo",

        "rad",  "rae",  "rag",  "rah",  "raj",  "ram",  "ran",  "rap",  "ras",  "rat",  "raw",  "rax",
        "ray",  "reb",  "rec",  "red",  "ree",  "ref",  "reg",  "rei",  "rem",  "ren",  "rep",  "res",
        "ret",  "rev",  "rex",  "rho",  "ria",  "rib",  "rid",  "rif",  "rig",  "rim",  "rin",  "rio",
        "rip",  "rob",  "roc",  "rod",  "roe",  "rog",  "rom",  "ron",  "rot",  "row",  "rox",  "roy",
        "roz",  "rub",  "rue",  "rug",  "rum",  "run",  "rut",  "rya",  "rye",

        "sab",  "sac",  "sad",  "sae",  "sag",  "sal",  "sam",  "san",  "sap",  "sat",  "saw",  "sax",
        "say",  "sea",  "sec",  "see",  "seg",  "sei",  "sel",  "sen",  "ser",  "set",  "sew",  "sex",
        "sez",  "sha",  "she",  "shh",  "shy",  "sib",  "sic",  "sid",  "sim",  "sin",  "sip",  "sir",
        "sis",  "sit",  "six",  "ska",  "ski",  "sky",  "slo",  "sly",  "sob",  "sod",  "sog",  "sol",
        "son",  "sop",  "sos",  "sot",  "sou",  "sow",  "sox",  "soy",  "spa",  "spy",  "sri",  "stu",
        "sty",  "sub",  "suc",  "sud",  "sue",  "sum",  "sun",  "sup",  "sur",  "sus",  "syd",  "syn",

        "tab",  "tac",  "tad",  "tae",  "tag",  "tai",  "taj",  "tam",  "tan",  "tao",  "tap",  "tar",
        "tas",  "tat",  "tau",  "tav",  "taw",  "tax",  "taz",  "tea",  "ted",  "tee",  "teg",  "tel",
        "ten",  "teo",  "tet",  "tew",  "tex",  "the",  "tho",  "thy",  "tic",  "tie",      "tim",
        "tin",  "tip",      "tit",  "toe",  "tog",  "tom",  "ton",  "too",  "top",  "tor",  "tot",
        "tow",  "toy",  "try",  "tse",  "tsk",  "tub",  "tug",  "tui",  "tum",  "tun",  "tut",  "tux",
        "twa",  "two",  "tye",

        "udo",  "ugh",  "uke",  "ula",  "uma",  "umm",  "ump",  "una",  "uno",  "uns",  "upo",  "ups",
        "urb",  "urd",  "urn",  "urp",  "use",  "ush",  "uta",  "uts",  "uzi",

        "vac",  "val",  "van",  "var",  "vas",  "vat",  "vau",  "vaw",  "vaz",  "vee",  "veg",  "vet",
        "vex",  "via",  "vic",  "vie",  "vig",  "vim",  "vin",  "vis",  "viv",  "viz",  "voe",  "von",
        "vow",  "vox",  "vug",

        "wab",  "wad",  "wae",  "wag",  "wah",  "wan",  "wap",  "war",  "was",  "wat",  "waw",  "wax",
        "way",  "web",  "wed",      "wee",  "wet",  "wha",  "who",  "why",  "wig",  "wil",
        "win",  "wit",  "wiz",  "woa",  "woe",  "wog",  "wok",  "won",  "woo",  "wop",  "wos",  "wot",
        "wow",  "wry",  "wud",  "wye",  "wyn",

        "xis",  "xor",  "xox",

        "yah",  "yak",  "yam",  "yap",  "yar",  "yaw",  "yay",  "yaz",  "yea",  "yeh",  "yen",  "yep",
        "yer",  "yes",  "yet",  "yew",  "yid",  "yin",  "yip",  "yom",  "yon",  "you",  "yow",  "yoy",
        "yuh",  "yuk",  "yum",  "yup",

        "zag",  "zak",  "zap",  "zax",  "zeb",  "zed",  "zee",  "zen",  "zig",  "zil",  "zip",  "zit",
        "zoa",  "zoo",  "zow"
        };
}