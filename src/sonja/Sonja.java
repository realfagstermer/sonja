/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sonja;

import static sonja.RelationType.*;

import java.awt.Image;
import java.awt.event.InputEvent;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Properties;
import java.util.Set;

import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import javax.net.ssl.HttpsURLConnection;

/**
 *
 * @author knuthe
 */
public class Sonja {

    /**
     * @param args the command line arguments
     */
    final static String UBOGRUNNURL
            = "https://bibsys-primo.hosted.exlibrisgroup.com/primo_library/libweb/action/dlSearch.do?vid=UBO&institution=UBO";
    final static String UBBGRUNNURL
            = "https://bibsys-primo.hosted.exlibrisgroup.com/primo_library/libweb/action/dlSearch.do?vid=UBB&institution=UBB";

    public static Sonjavindu vindu = null;
    static HashMap<String, Term> id2term = new HashMap<>();
    static HashMap<String, Term> term2id = new HashMap<>();
    static int termid = 0;
    static int forsok = 0;
    static String idprefiks = null;
    static boolean datamodusID = true;
    static boolean gjortendringer = false;
    static String sokavd = "ureal?";
    static String sokbibl = "ubo";
    static String oppstartsstatus = null;
    // datastrukturene
    static ArrayList<Term> termliste = new ArrayList<Term>();
    static ArrayList<Streng> strengliste = new ArrayList<Streng>();
    static ArrayList<Streng> sandkasse = new ArrayList<Streng>();
    static ArrayList<Term> formliste = new ArrayList<Term>();
    static ArrayList<Term> tidsliste = new ArrayList<Term>();
    static ArrayList<Term> stedsliste = new ArrayList<Term>();
    static ArrayList<Term> latinliste = new ArrayList<Term>();
    static ArrayList<Term> forslagsliste = new ArrayList<Term>();
    static ArrayList<String> huskeliste = new ArrayList<String>();
    static ArrayList<String> bibsyshuskeliste = new ArrayList<String>();
    static int antallegne = 0;
    static int antallengelsk = 0;
    static String[] termtyper = {"Term", "Sted", "Tid", "Form"};
    static String trefflistetype = "";
    static String vokabularID = "";
    static String currentPath = null;
    static String xmlprefiks
            = "<?xml version=\"1.0\"  encoding=\"UTF-8\"?>\n\n"
            + "<!--\n"
            + "REALORD is made available under the Open Database License:\n"
            + "http://opendatacommons.org/licenses/odbl/1.0/. \n"
            + "Any rights in individual contents of the database are licensed\n"
            + "under the Database Contents License: \n"
            + "http://opendatacommons.org/licenses/dbcl/1.0/\n"
            + "-->\n\n"
            + "<rdf:RDF "
            + "     xmlns:rdf=\"http://www.w3.org/1999/02/22-rdf-syntax-ns#\"\n"
            + "     xmlns:skos=\"http://www.w3.org/2004/02/skos/core#\"\n"
            + "     xmlns:dcterms=\"http://purl.org/dc/terms/\">\n\n";
    static String xmlpostfiks = "</rdf:RDF>\n";
    static String latexprefiks01
            = "\\documentclass[10pt, a4paper,norsk]{article}\n"
            + "\\usepackage[utf8]{inputenc}\n"
            + "\\usepackage{babel, csquotes}\n"
            + "\\newcommand{\\startoppslag}{}\n"
            + "\\newcommand{\\oppslag}[1]{#1\\newline}\n"
            + "\\newcommand{\\engnor}[2]{#1 {\\footnotesize (#2)}\\newline}\n"
            + "\\newcommand{\\noreng}[2]{#1 {\\footnotesize (#2)}\\newline}\n"
            + "\\newcommand{\\norengkode}[3]{#1 {\\footnotesize (#2) - #3}\\newline}\n"
            + "\\newcommand{\\noruteneng}[1]{#1\\newline}\n"
            + "\\newcommand{\\henvisning}[2]{\\textit{#1}, se #2\\newline}\n";
    static String latexprefiks02
            = "\\begin{document}\n"
            + "\\maketitle\n"
            + "\\noindent{}1.kolonne er forkortelsen. Neste er bokmålsform, den siste er nynorskform. Foretrukne termer er fetet."
            + "\\newpage"
            + "\\noindent\\begin{tabular}{|p{1.5cm}|p{6cm}|p{6cm}|}\\hline\n";
    static String latextabellkutt
            = "\\end{tabular}\n\\newpage"
            + "\\noindent\\begin{tabular}{|p{1.5cm}|p{6cm}|p{6cm}|}\\hline\n";

    static String latexprefiks1
            = "\\documentclass[10pt, a4paper,norsk, twocolumn]{article}\n"
            + "\\usepackage[utf8]{inputenc}\n"
            + "\\usepackage{babel, csquotes}\n"
            + "\\newcommand{\\startoppslag}{}\n"
            + "\\newcommand{\\oppslag}[1]{#1\\newline}\n"
            + "\\newcommand{\\engnor}[2]{#1 {\\footnotesize (#2)}\\newline}\n"
            + "\\newcommand{\\noreng}[2]{#1 {\\footnotesize (#2)}\\newline}\n"
            + "\\newcommand{\\norengkode}[3]{#1 {\\footnotesize (#2) - #3}\\newline}\n"
            + "\\newcommand{\\noruteneng}[1]{#1\\newline}\n"
            + "\\newcommand{\\henvisning}[2]{\\textit{#1}, se #2\\newline}\n";
    static String latexprefiks2
            = "\\begin{document}\n"
            + "\\maketitle\n"
            + "\\newpage";
    static String latexpostfiks = "\\end{document}";
    static String latexpostfiks2 = "\\end{tabular}\n\\end{document}";
    static String[] users = {
        // Oslo
        "Mari", "Knut", "Dan",
        "Heidi", "Viola", "SMR"// Bergen
    };
    static String[] pwds = {
        // Oslo
        "iraM", "LvB", "naD",
        "nesrujS", "ibmeL", "rms"// Bergen
    };
    public static String currentuser = null;
    static boolean startup = false;
//    static String KATALOG = "M:/prosjekter/Sonja/input/";
//    static String RESULTAT = "M:/prosjekter/Sonja/output/";
    static String KATALOG = null;
    static String RESULTAT = null;
    static String REVISJON = null;
    static String ORIGINAL = null;
    static String DRIFT = null;
// disse katalognavnene brukes for testing på PC
//    static String BASEFOLDER = "Z:/test/ureal/rii/";
//    static String WEBDATA = "Z:/test/ureal/";
//    static String FORSLAG = "U:/Prosjekt687/roald2/test/data/";

    // disse katalognavnene brukes for drift
    static String BASEFOLDER = null;
    static String FORSLAG = null;
    static String WEBDATA = null;

    static String vokabular = null;
    static String LATEXFOLDER = "M:/emnedata/ordliste/";
    // disse katalognavnene bruker for testing på MAC
//    static String BASEFOLDER = "/Users/knutsen/Prosjekter/Sonja/data/";
//    static String FORSLAG = "/Users/knutsen/Prosjekter/Sonja/data/";
//    static String WEBDATA = "/Users/knutsen/Prosjekter/Sonja/data/";
    final static Properties config = new Properties();
    final static HashMap<Integer, Term> conceptsById = new HashMap<>();
    private static Locale defaultLanguage = Sonjavindu.nb;
    
    static {// initialize config
	try (InputStream in = Sonja.class.getResourceAsStream("/resources/config.properties")) {
	    config.load(in);
	} catch (IOException e) {
	    e.printStackTrace();
	}
    }

    public static void main(String... args) {
	if (args.length > 0) {
	    if (args[0] == "SMR" || args[0] == "REAL") {
		currentuser = args[0];
		vokabular = args[0];
	    }
	} else {
	    Vokabular vok = new Vokabular(null, true);
	    vok.setVisible(true);
	}
        if (vokabular == null) {
            System.exit(0);
        }
        if (vokabular.equals("SMR")) {
	    defaultLanguage = Sonjavindu.en;
            BASEFOLDER = "Z:/mr/";
            FORSLAG = "Z:/mr/";
            WEBDATA = "Z:/mr/";
            idprefiks = "SMR";
        } else if (vokabular.equals("REAL")) {
            BASEFOLDER = "Z:/ureal/rii/";
            FORSLAG = "U:/Prosjekt687/roald2/data/";
            WEBDATA = "Z:/ureal/";
            idprefiks = "REAL";
        }

        /*
        if (!sjekkopptattfil()) {
            Sonjavindu.melding("Filen er opptatt av "
                    + Sonjavindu.bruker, "Prøv igjen seinere!");
            System.exit(0);
        }
        */

        /*
        Passord pa = new Passord();
        pa.setVisible(true);
        while (true) {
            try {
                Thread.sleep(4000);
            } catch (Exception exp) {

            }
            if (pa.godkjent) {
                break;
            }
            if (Passord.forsok > 2) {
                Sonjavindu.melding("Ikke flere forsøk", "Spør Knut om brukernavn/passord");
                System.exit(0);
            }
        }
        if (vokabular.equals("SMR") && !currentuser.equals("SMR")) {
            System.exit(0);
        }
        if (vokabular.equals("REAL") && currentuser.equals("SMR")) {
            System.exit(0);
        }
        */
        settopptattfil("opptatt");
        if (BASEFOLDER.contains("test")) {
            Sonjavindu.melding("OBS", "Du kjører i testmodus");
        }
        //pa.dispose();
        vindu = new Sonjavindu();
        vindu.setVisible(true);
        startup = true;
        initFromPostgreSQL();
        //initierdatamedID();
        startup = false;
        lagrelogg("---------------------- " + currentuser);

        gjortendringer = false;

    }

    /**
     * setter stor forbokstav på oppgitt tekst, gjør ingen ting med resten
     *
     */
    public static String storforbokstav(String tekst) {
        String retval = null;
        if (tekst != null && tekst.length() > 0) {
            retval = tekst.substring(0, 1).toUpperCase() + tekst.substring(1);
        }
        return retval;
    }

    /**
     * spesialbehandling av steder: alle deler av ordet skal ha stor forbokstav
     * (del definert ved blank- eller bindestreksskille).
     */
    public static String storforbokstavisted(String s) {
        // først på Blank
        String[] deler = s.split(" ");
        for (int i = 0; i < deler.length; i++) {
            deler[i] = storforbokstav(deler[i]);
        }
        StringBuilder sb = new StringBuilder(deler[0]);
        for (int i = 1; i < deler.length; i++) {
            sb.append(" ").append(deler[i]);
        }
        s = sb.toString();

        // så på bindestrek
        deler = s.split("-");
        for (int i = 0; i < deler.length; i++) {
            deler[i] = storforbokstav(deler[i]);
        }
        sb = new StringBuilder(deler[0]);
        for (int i = 1; i < deler.length; i++) {
            sb.append("-").append(deler[i]);
        }
        s = sb.toString();
        return s;
    }

    /**
     * Metoden genererer ett nytt ID med prefiks avhengig av vokabular.
     * Utgangspunkt er forrige verdi av int-variabelen termid Metoden legger på
     * ledende nuller slik at lengden blir 6 (eller større - dersom id
     * overstiger 999999)
     */
    public static String nesteID() {
        StringBuilder nesteid = new StringBuilder(idprefiks);
        termid = nextidnr();
        if (termid < 10) {
            nesteid.append("00000");
            nesteid.append(termid);
        } else if (termid < 100) {
            nesteid.append("0000");
            nesteid.append(termid);
        } else if (termid < 1000) {
            nesteid.append("000");
            nesteid.append(termid);
        } else if (termid < 10000) {
            nesteid.append("00");
            nesteid.append(termid);
        } else if (termid < 100000) {
            nesteid.append("0");
            nesteid.append(termid);
        } else if (termid < 1000000) {
            nesteid.append(termid);
        }
        return nesteid.toString();
    }

    /**
     * Metoden genererer nytt id ett høyere enn det som er lagret i id-filen og
     * id-filen oppdateres med nytt id
     *
     */
    public static int nextidnr() {
        int idnr = -1;
        String katalog = BASEFOLDER;
        String filnavn = katalog + "admin/ID.txt";
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(filnavn)),
                            "ISO-8859-1"));
            String linje = br.readLine();
            if (linje != null) {
                idnr = Integer.parseInt(linje.trim());
                idnr++;
            }
            br.close();
            // oppdaterer idnr på fil
            PrintWriter pw = new PrintWriter(filnavn);
            pw.print(idnr);
            pw.close();

        } catch (IOException exp) {
            Sonjavindu.melding("IDNR-feil", "Feil ved lesing eller skriving av id-nr");
        }
        return idnr;
    }

    /**
     * Går gjennom de fire listetypene Finner id-en til en term ved eksakt match
     * Returnerer null dersom det er annet enn ett treff
     */
    public static String getID(String s) {

        Term[] termer = searchTermeksakt(s, "term");
        if (termer == null) {
            termer = searchTermeksakt(s, "form");
        }
        if (termer == null) {
            termer = searchTermeksakt(s, "tid");
        }
        if (termer == null) {
            termer = searchTermeksakt(s, "sted");
        }

        if (termer == null) {
            //System.out.println("Ikke funnet ID\t" + s);
            return null;
        } else if (termer.length != 1) {
            return null;
        } else {
            return termer[0].minID;
        }

    }

    /**
     * Bruker ID for å finne Term-objekt
     */
    public static Term getTerm(String id) {
        Term t = null;
        t = id2term.get(id);

        if (t == null) {
            Sonjavindu.melding("Null", "Ikke funnet term\n" + id);
            System.out.println("Ikke funnet term\t" + id);
        }

        return t;
    }

    /*
     * Søkemetoder
     */
 /*
     * Metoden gjennomløper aktuell termliste og sender
     * søkekriterie til hver enkelt term som svarer med treff eller ikke.
     */
    public static Term[] search(String s, String listetype) {
        Term[] retval = null;
        ArrayList<Term> treff = new ArrayList<Term>();
        ArrayList<Term> sokliste;
        // sjekker syntaks i strengene (testmodus)
        for (Streng str : strengliste) {
            if (!str.strengok()) {
                System.out.println("Search:\t" + str.toStringplain());
            }
        }
        // det kan være MSC-søk da skal vi bare ha høyst ett treff
        if (!listetype.equals("msc")) {
            // finner fram termer som oppfyller kriteriene
            // i relevant liste
            sokliste = velgliste(listetype);
            //System.out.println(listetype + " " + sokliste.size());
            for (int i = 0; i < sokliste.size(); i++) {
                Term t = sokliste.get(i);
                if (t != null) {
                    if (t.finn(s)) {
                        treff.add(t);
                    }
                }
            }
            // leter gjennom strengene
            //if (listetype.equals("term")) {
            if (Sonjavindu.strengesok) {
                for (int i = 0; i < strengliste.size(); i++) {
                    Streng str = strengliste.get(i);
                    if (str != null) {
                        if (str.finndel(s, listetype)) {
                            treff.add(str);
                        }
                    }
                }
            }

            //}
        } else {
            // går gjennom termlist og leter etter MSC-kode
            sokliste = velgliste("term");
            for (int i = 0; i < sokliste.size(); i++) {
                Term t = sokliste.get(i);
                if (t != null && t.finnmsc(s)) {
                    treff.add(t);
                }
            }
            // leter gjennom strengene
            for (Streng str : strengliste) {
                if (str.finnmsc(s)) {
                    // TODO trefflista må redefineres
//                    treff.add(str);
                }
            }

        }
        // lager en array av trefflist som returverdi

        if (treff.size() > 0) {
            retval = new Term[treff.size()];
            for (int j = 0; j < treff.size(); j++) {
                retval[j] = treff.get(j);
            }
        }
        return retval;

    }

    public static Term[] utenengelsk(String listetype) {
        Term[] retval = null;
        ArrayList<Term> treff = new ArrayList<Term>();
        ArrayList<Term> sokliste;
        // sjekker syntaks i strengene (testmodus)
        for (Streng str : strengliste) {
            if (!str.strengok()) {
                System.out.println("Search:\t" + str.toStringplain());
            }
        }

        // finner fram termer som oppfyller kriteriene
        // i relevant liste
        sokliste = velgliste(listetype);
        //System.out.println(listetype + " " + sokliste.size());
        for (int i = 0; i < sokliste.size(); i++) {
            Term t = sokliste.get(i);
            if (t != null) {
                if (!t.harengelsk()) {
                    treff.add(t);
                }
            }
        }

        // lager en array av trefflist som returverdi
        if (treff.size() > 0) {
            retval = new Term[treff.size()];
            for (int j = 0; j < treff.size(); j++) {
                retval[j] = treff.get(j);
            }
        }
        return retval;

    }

    public static void searchoria(Term t, boolean katalog) {
        String grunnurl = null;
        if (katalog) {
            grunnurl = UBOGRUNNURL;
        } else {
            grunnurl = UBBGRUNNURL;
        }

        if (t != null) {
            String searchstring = t.getoriasearchstring();
            searchstring = utf8oria(searchstring);
            String url = grunnurl + searchstring;
            BrowserLaunch.openURL(
                    url);

        }
    }

    /**
     * metode som gjør eksakt søk i oppgitt liste uten å se på strengene.
     *
     */
    public static Term[] searchTermeksakt(String s, String listetype) {

        ArrayList<Term> treff = new ArrayList<Term>();
        ArrayList<Term> sokliste = velgliste(listetype);

        for (int i = 0; i < sokliste.size(); i++) {
            Term t = sokliste.get(i);
            if (t != null && t.term != null && t.term.equalsIgnoreCase(s)) {
                treff.add(t);
            }
//            } else {
//                if (t != null) {
//                    if (t.sjekkBF(s)) {
//                        treff.add(t);
//                    }
//                }
//            }
        }

        if (treff.size() > 0) {
            Term[] liste = new Term[treff.size()];
            for (int j = 0; j < treff.size(); j++) {
                liste[j] = treff.get(j);
            }
            return liste;
        } else {
            return null;
        }
    }

    public static ArrayList<Term> velgliste(String listetype) {
        ArrayList<Term> liste = null;
        listetype = listetype.toLowerCase();

        if (listetype.equals("term")) {
            liste = termliste;
        }
        if (listetype.equals("form")) {
            liste = formliste;
        }
        if (listetype.equals("tid")) {
            liste = tidsliste;
        }
        if (listetype.equals("sted")) {
            liste = stedsliste;
        }
        return liste;
    }

    public static void leggnytermiliste(Term t) {
        ArrayList<Term> liste = velgliste(t.type);
        liste.add(t);
        id2term.put(t.minID, t);
        
	if (!startup) {
	    vindu.endringsrutiner("Lagt inn ny " + t.type + "-term " + t.term, t);
	}
    }

    public static void oppdatertermilister(Term t) {
        ArrayList<Term> liste = velgliste(t.type);
        for (int i = 0; i < liste.size(); i++) {
            Term t1 = liste.get(i);
            if (t1.term.equalsIgnoreCase(t.term)) {
                t.endretdato(fiksdato(new Date()));
                liste.remove(i);
                liste.add(t);
                gjortendringer = true;

                break;
            }
        }
        id2term.put(t.minID, t);
    }

    public static void sjekkid(String idline) {
        String tmp = idline.substring(3).trim();
        tmp = tmp.substring(4);
        int thisid = Integer.parseInt(tmp);
        if (thisid > termid) {
            termid = thisid;
        }
    }

    public static boolean sjekkterm(String t) {
        boolean retval = true;
        // utfører eksakt søk i alle listene på termens term-verdi
        // ved treff kan termen ikke bruke som ny term
        for (String type : termtyper) {
            Term[] treff = search("=" + t + "=", type);
            if (treff != null) {
                if (treff.length == 1) {
                    if (!vindu.likcurrentterm(treff[0])) {
                        // denne brukes av kollisjonsmetoden for å sette opp et søk
                        trefflistetype = type;
                        retval = false;
                        break;
                    }
                } else {
// denne brukes av kollisjonsmetoden for å sette opp et søk
                    trefflistetype = type;
                    retval = false;
                    break;
                }
            }
        }
        return retval;
    }
    //    public static boolean sjekknyhenvisning(String s) {
    //        boolean retval = true;
    //        // utfører eksakt søk i alle listene på termen term-verdi
    //        // ved treff kan termen ikke bruke som ny term
    //        for (String type : termtyper) {
    //            Term[] treff = search("=" + s + "=", type);
    //            if (treff != null && treff.length > 0) {
    //                trefflistetype = type;
    //                retval = false;
    //                break;
    //            }
    //        }
    //        return retval;
    //    }

    public static void importersteder() {
        String stedsfil = BASEFOLDER + "steder.txt";
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(stedsfil)),
                            "ISO-8859-1"));
            String linje = br.readLine();
            int antall = 0;
            Term term = null;

            while (linje != null) {
                String eksakt = "=" + linje.trim() + "=";
                Term[] t = search(eksakt, "term");
                if (t.length == 1) {
                    Term flytt = t[0];
                    termliste.remove(flytt);
                    stedsliste.add(flytt);
                    antall++;
                } else if (t.length == 0) {
                    vindu.melding("Ukjent term", "Fant ikke: " + linje);
                } else {
                    vindu.melding("Altfor kjent term", "Flere treff på: " + linje);
                }
                linje = br.readLine();
            }
            br.close();
            vindu.melding("Resultat", "Antall flyttet: " + antall);
        } catch (IOException exp) {
            vindu.melding("Fant ikke filen", stedsfil);
        }
    }

    public static void importerformer() {
        String stedsfil = BASEFOLDER + "former.txt";
        try {
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(stedsfil)),
                            "ISO-8859-1"));
            String linje = br.readLine();
            int antall = 0;
            Term term = null;

            while (linje != null) {
                String eksakt = "=" + linje.trim() + "=";
                Term[] t = search(eksakt, "term");
                if (t != null) {
                    if (t.length == 1) {
                        Term flytt = t[0];
                        termliste.remove(flytt);
                        formliste.add(flytt);
                        antall++;
                    } else if (t.length == 0) {
                        vindu.melding("Ukjent term", "Fant ikke: " + linje);
                    } else {
                        vindu.melding("Altfor kjent term", "Flere treff på: " + linje);
                    }
                } else {
                    vindu.melding("Ny form?", "Fant ikke: " + linje);
                }
                linje = br.readLine();
            }
            br.close();
            vindu.melding("Resultat", "Antall flyttet: " + antall);
        } catch (IOException exp) {
            vindu.melding("Fant ikke filen", stedsfil);
        }
    }

    public static void initierdatamedID() {
        String katalog = BASEFOLDER;
        termliste = new ArrayList<Term>();
        strengliste = new ArrayList<Streng>();
        formliste = new ArrayList<Term>();
        tidsliste = new ArrayList<Term>();
        stedsliste = new ArrayList<Term>();
        int antallbf = 0;
        int antallso = 0;
        int antallen = 0;
        int antallny = 0;
        int antallla = 0;
        // leser inn termene i en ArrayList
        String filnavn = katalog + "idtermer.txt";
        try {
            // termene
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(filnavn)),
                            "UTF-8"));
            String linje = br.readLine();
            Term term = null;

            while (linje != null) {
                // Starten på term i datafila er "te=" først på linja
                if (linje.startsWith("id=")) {
                    sjekkid(linje);
                    if (term != null) {
                        // tar vare på forrige term
                        if (term.introdato == null) {
                            term.nydato(fiksdato(new Date()));
                        }
                        termliste.add(term);

                        // samler opp statistiske data fra denne termen
                        antallbf = antallbf + term.getantallbf();
                        antallso = antallso + term.getantallso();
                        antallny = antallny + term.getantallny();
                        antallen = antallen + term.getantallengelsk();
                        antallla = antallla + term.getantalllatin();

                    }
                    // starter på ny term
                    term = new Term(linje.trim());
                    term.nytype("term");
                } else // legger inn et felt
                {
                    if (term != null) {
                        term.puttfelt(linje);
                    } else {
                        Sonjavindu.melding("Innlesing av data", "Termløse data\n" + linje);
                    }
                }

                linje = br.readLine();
            }
            br.close();

            if (term != null) {
                if (term.introdato == null) {
                    term.nydato(fiksdato(new Date()));
                }
                termliste.add(term);
            }

            // formene
            filnavn = katalog + "idformer.txt";
            term = null;
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(filnavn)),
                            "UTF-8"));
            linje = br.readLine();
            while (linje != null) {
                if (linje.startsWith("id=")) {
                    sjekkid(linje);
                    if (term != null) {
                        if (term.introdato == null) {
                            term.nydato(fiksdato(new Date()));
                        }
                        if (currentuser.equals("SMR")) {
                            if (term.engelsk.size() == 0) {
                                term.nyengelsk(term.term);
                            }
                        }
//                        if (currentuser.equals("SMR")) {
//                            if (term.engelsk.size() == 0) {
//                                term.nyengelsk(term.term);
//                            }
//                        }

                        formliste.add(term);
                    }
                    term = new Term(linje);
                    term.nytype("form");

                } else {
                    term.puttfelt(linje);
                }

                linje = br.readLine();
            }
            br.close();
            if (term != null) {
                if (term.introdato == null) {
                    term.nydato(fiksdato(new Date()));
                }
//                if (currentuser.equals("SMR")) {
//                    if (term.engelsk.size() == 0) {
//                        term.nyengelsk(term.term);
//                    }
//                }

                formliste.add(term);
            }
            // stedene
            filnavn = katalog + "idsteder.txt";
            br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(filnavn)),
                            "UTF-8"));
            linje = br.readLine();
            term = null;
            while (linje != null) {
                if (linje.startsWith("id=")) {
                    sjekkid(linje);
                    if (term != null) {
                        if (term.introdato == null) {
                            term.nydato(fiksdato(new Date()));
                        }
//                        if (currentuser.equals("SMR")) {
//                            if (term.engelsk.size() == 0) {
//                                term.nyengelsk(term.term);
//                            }
//                        }

                        stedsliste.add(term);
                    }
                    term = new Term(linje);
                    term.nytype("sted");
                } else {
                    if (linje.startsWith("te=")) {
                        String innhold = linje.substring(3).trim();
                        linje = "te= " + storforbokstavisted(innhold);
                    }
                    term.puttfelt(linje);
                }

                linje = br.readLine();
            }
            br.close();

            if (term != null) {
                if (term.introdato == null) {
                    term.nydato(fiksdato(new Date()));
                }
//                if (currentuser.equals("SMR")) {
//                    if (term.engelsk.size() == 0) {
//                        term.nyengelsk(term.term);
//                    }
//                }

                stedsliste.add(term);
            }

            if (vokabular.equals("REAL")) {
                // tidene
                filnavn = katalog + "idtider.txt";
                br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(
                                        new File(filnavn)),
                                "UTF-8"));
                linje = br.readLine();
                term = null;
                while (linje != null) {
                    if (linje.startsWith("id=")) {
                        sjekkid(linje);
                        if (term != null) {
                            if (term.introdato == null) {
                                term.nydato(fiksdato(new Date()));
                            }
                            tidsliste.add(term);
                        }
                        term = new Term(linje);
                        term.nytype("tid");
                    } else {
                        term.puttfelt(linje);
                    }

                    linje = br.readLine();
                }
                br.close();
                if (term != null) {
                    if (term.introdato == null) {
                        term.nydato(fiksdato(new Date()));
                    }
                    tidsliste.add(term);
                }

            }
            lagIDliste();
            // strenger
            if (vokabular.equals("REAL")) {
                filnavn = katalog + "idstrenger.txt";
                Streng str = null;
                br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(
                                        new File(filnavn)),
                                "UTF-8"));
                linje = br.readLine();
                while (linje != null) {
                    if (linje.startsWith("id= ")) {
                        sjekkid(linje);
                        if (str != null) {
                            if (str.introdato == null) {
                                str.nydato(fiksdato(new Date()));
                            }
                            // if (!strengfins(str)) {
                            nystreng(str);
                            // } else {
                            // System.out.println(str.toStringplain());
                            //}

                        }

                        str = new Streng();
                        str.addID(linje.substring(3).trim());
                        str.addtype("streng");
                    }
                    if (linje.startsWith("da= ")) {
                        str.addterm(linje.substring(3).trim());
                    }
                    if (linje.startsWith("db= ")) {
                        str.addunderterm(linje.substring(3).trim());
                    }
                    if (linje.startsWith("dc= ")) {
                        str.addkvalifikator(linje.substring(3).trim());
                    }
                    if (linje.startsWith("dx= ")) {
                        str.addform(linje.substring(3).trim());
                    }
                    if (linje.startsWith("dy= ")) {
                        str.addtid(linje.substring(3).trim());
                    }
                    if (linje.startsWith("dz= ")) {
                        // str.addsted(storforbokstavisted(linje.substring(3).trim()));
                        str.addsted(linje.substring(3).trim());
                    }
                    if (linje.startsWith("bf= ")) {
                        str.addbruktfor(linje.substring(3).trim());
                    }
                    if (linje.startsWith("ms= ")) {
                        str.addmsc(linje.substring(3).trim());
                    }
                    if (linje.startsWith("dw= ")) {
                        str.adddewey(linje.substring(3).trim());
                    }
                    if (linje.startsWith("tio= ")) {
                        str.introdato = linje.substring(4).trim();
                    }
                    if (linje.startsWith("tie= ")) {
                        str.endredato = linje.substring(4).trim();
                    }
                    if (linje.startsWith("tis= ")) {
                        str.slettdato = linje.substring(4).trim();
                    }
                    linje = br.readLine();
                }
                if (str != null) {
                    if (str.introdato == null) {
                        str.nydato(fiksdato(new Date()));
                    }
                    nystreng(str);
                }
            }
            StringBuilder sb = new StringBuilder("Oppstart:\n");
            sb.append("Antall termer:\t").append(termliste.size()).append("\n");
            sb.append("Antall strenger:\t").append(strengliste.size()).append("\n");
            sb.append("Antall former:\t").append(formliste.size()).append("\n");
            sb.append("Antall tider:\t").append(tidsliste.size()).append("\n");
            sb.append("Antall steder:\t").append(stedsliste.size()).append("\n");
            sb.append("Antall bf:\t").append(antallbf).append("\n");
            sb.append("Antall so:\t").append(antallso).append("\n");
            sb.append("Antall ny:\t").append(antallny).append("\n");
            sb.append("Antall en:\t").append(antallen).append("\n");
            sb.append("Antall la:\t").append(antallla).append("\n");
            sb.append("Maks id:\t").append(termid).append("\n");
            oppstartsstatus = sb.toString();
            vindu.addtologg(sb.toString(), false);
            vindu.klar();
            for (Streng str1 : strengliste) {
                if (!str1.strengok()) {
                    System.out.println(str1.toStringplain());
                }
            }

        } catch (IOException exp) {
            Sonjavindu.melding("Initiering av data",
                    "Data vil bli hentet fra WWW.\n"
                    + exp.toString());
            int termantall = termliste.size() - strengliste.size();
        }
    }
    
    public static void initFromPostgreSQL() {
	try {
	    Class.forName(config.getProperty("jdbc.driver"));
	} catch (ClassNotFoundException e) {
	    Sonjavindu.melding("Mangel", "Finner ikke driver: " + config.getProperty("jdbc.driver"));
	    System.exit(0);
	}

	final String query = "SELECT * FROM concepts WHERE vocab_id = '" + Sonja.vokabular + "';";
	final String queryTerms = "SELECT * FROM terms WHERE concept_id= ? ORDER BY status DESC"; // preferred before non_pref
	
	try (Database  db = new Database();
		Statement stmt = db.createStatement();
		ResultSet rs = stmt.executeQuery(query);
		PreparedStatement termsStmt= db.prepareStatement(queryTerms)) {

	    while (rs.next()) {
		db.getConcept(rs, termsStmt);
	    }

	    initRelationships(db);
	    initMappings(db);
	    initStringsFromSql(db);
	    
	    StringBuilder sb = new StringBuilder("Oppstart:\n");
	    sb.append("Antall termer:\t").append(termliste.size()).append("\n");
	    sb.append("Antall strenger:\t").append(strengliste.size()).append("\n");
	    sb.append("Antall former:\t").append(formliste.size()).append("\n");
	    sb.append("Antall tider:\t").append(tidsliste.size()).append("\n");
	    sb.append("Antall steder:\t").append(stedsliste.size()).append("\n");
	    // sb.append("Antall bf:\t").append(antallbf).append("\n");
	    // sb.append("Antall so:\t").append(antallso).append("\n");
	    // sb.append("Antall ny:\t").append(antallny).append("\n");
	    // sb.append("Antall en:\t").append(antallen).append("\n");
	    // sb.append("Antall la:\t").append(antallla).append("\n");
	    sb.append("Maks id:\t").append(termid).append("\n");
	    oppstartsstatus = sb.toString();
	    vindu.addtologg(sb.toString(), false);
	    vindu.klar();
	} catch (Exception e) {
//	    e.printStackTrace();
	    Sonjavindu.melding("Feil ved start:", e.getMessage());
	    System.exit(0);
	}
    }

    private static void initMappings(Database db) throws SQLException {
	try (PreparedStatement statement = db.prepareStatement("SELECT * FROM mappings;");
		ResultSet results = statement.executeQuery()) {
	    while (results.next()) {
		final ExternalVocabulary vocab = ExternalVocabulary.valueOf(results.getString("target_vocabulary_id"));
		final Term source = conceptsById.get(results.getInt("source_concept_id"));
		final String target = results.getString("target_concept_id");

		switch (vocab) {
		case ddc23:
		    source.dewey.add(target);
		    break;
		case msc1970:
		    source.msc.add(target);
		    break;
		default:
		    System.out.printf("Unknown vocabulary: %s\n", vocab);
		    break;
		}
	    }
	}
    }

    private static void initRelationships(Database db) throws SQLException {
	final String queryRel = "SELECT c1.external_id AS id1, c2.external_id AS id2, rel_type\n" +
		"  FROM relationships\n" +
		"  JOIN concepts AS c1 ON concept1 = c1.concept_id\n" +
		"  JOIN concepts AS c2 ON concept2 = c2.concept_id";

	try (Statement stmt = db.createStatement();
		ResultSet results = stmt.executeQuery(queryRel)) {
	    while (results.next()) {
		final String rel_type = results.getString("rel_type");
		final String concept1 = Term.makeId(results.getInt("id1"));
		final String concept2 = Term.makeId(results.getInt("id2"));

		switch (rel_type) {
		case "related":
		    getTerm(concept1).seog.add(concept2);
		    break;
		case "broader":
		    getTerm(concept1).nyoverordnet(concept2);
		    getTerm(concept2).nyunderordnet(concept1);
		    break;
		case "equivalent":
		    // todo
		    break;
		default:
		    System.out.printf("error: unknown relationship: %s\n", rel_type);
		}
	    }
	}
    }

    private static void initStringsFromSql(Database db) throws SQLException {
	String query = "SELECT * FROM strings WHERE vocab_id = '" + Sonja.vokabular + "'";
	try (Statement stmt = db.createStatement();
		ResultSet results = stmt.executeQuery(query)) {
	    while (results.next()) {
		Streng s = new Streng();
		s.addID(Streng.makeId(results.getInt("string_id")));
		s.addterm(getExternalId(results.getInt("topic")));
		s.addunderterm(getExternalId(results.getInt("subtopic")));
		s.addform(getExternalId(results.getInt("form")));
		s.addtid(getExternalId(results.getInt("temporal")));
		s.addsted(getExternalId(results.getInt("geographic")));
		s.introdato = results.getTimestamp("created").toString();
		Timestamp modified = results.getTimestamp("modified");
		Timestamp deprecated = results.getTimestamp("deprecated");

		if (modified != null) {
		    s.endredato = modified.toString();
		}

		if (deprecated != null) {
		    s.slettdato = deprecated.toString();
		}

		nystreng(s);
	    }
	}
    }

    private static String getExternalId(int conceptId) {
	return conceptId == 0 ? null : conceptsById.get(conceptId).minID;
    }

    public static void lagIDliste() {
        id2term = new HashMap<String, Term>();
        for (Term t : termliste) {
            id2term.put(t.minID, t);
        }
        /*
         for (Streng t : strengliste) {
         // TODO lage strengeliste eller redefinere HashMap-en til å lagre Object
         //            id2term.put(t.minID, t);
         }
         * */
        for (Term t : formliste) {
            id2term.put(t.minID, t);
        }
        for (Term t : tidsliste) {
            id2term.put(t.minID, t);
        }
        for (Term t : stedsliste) {
            id2term.put(t.minID, t);
        }
    }

    /**
     * Legger ny streng på plass i datastruktren Både i tilhørende toppterm
     * (første ledd i strengen) og i egen liste av strenger
     */
    public static void nystreng(Streng str) {
        // finner fram riktig term og legger strengen inn under denne
        // må sjekke akseptable termtyper i da, ikke bare termer, men former ...
        // strengen må også inn i id2term-strukturen?
        String s = str.da;
        str.strengtype = gettype(s);
        if (str.strengtype.equals("term")
                || str.strengtype.equals("form")
                || str.strengtype.equals("sted")) {
            ArrayList<Term> typeliste = velgliste(str.strengtype);
            boolean funnet = false;
            if (s != null) {
                for (Term t : typeliste) {
                    if (s.equals(t.minID)) {
                        // legger strengen inn inn i termen
                        t.addstreng(str);
                        // legger lenke fra strengen til tilhørende term
                        str.addtopp(t);
                        // tar vare på strengen i egen liste
                        strengliste.add(str);
                        gjortendringer = true;
                        funnet = true;
                        //lagrelogg("ny streng " + str.toStringplain());
                        break;
                    }
                }
            }
            if (!funnet) {
                Sonjavindu.melding("Feil i streng", "Fant ikke første ledd som " + str.strengtype);
            }
        } else {
            vindu.melding("Feil struktur.", "Streng kan ikke starte med tidsterm.");
        }
    }

    /**
     *
     * Metoden brukes i forbindelse med søk for å få treff på strenger med
     * aksentuerte tegn selv om man ikke har brukt det i søkestrengen
     *
     * @param s med diakriter, aksentuerte tegn
     * @return en streng med bare grunnbokstaver
     */
    public static String fjernaksenter(String s) {
        if (s.contains("å") || s.contains("Å")) {
            return s;
        } else {
            String s2 = Normalizer.normalize(s, Normalizer.Form.NFD);
            s2 = s2.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
            return s2;
        }
    }

    public static void kopierbf() {
        int antallbegreper = 0;
        int antallbfer = 0;
        if (currentuser.equals("SMR")) {
            for (Term t : termliste) {
                if (t != null && t.engelsk.size() > 1) {
                    antallbegreper++;
                    for (int i = 1; i < t.engelsk.size(); i++) {
                        t.nyttsynonym(t.engelsk.get(i));
                        antallbfer++;
                    }
                }
            }
            Sonjavindu.melding("Flyttemelding",
                    "Antall begreper:\t" + antallbegreper
                    + "\nAntall BFer:\t" + antallbfer);
        }
    }

    /**
     *
     * @param text
     * @return retval
     *
     * Sjekker om text er en korrekt termtype
     */
    static boolean korrekttermtype(String text) {
        boolean retval = false;
        if (text.equalsIgnoreCase("term")
                || text.equalsIgnoreCase("form")
                || text.equalsIgnoreCase("tid")
                || text.equalsIgnoreCase("sted")) {
            retval = true;
        }
        return retval;
    }

    static Term[] finsfratidligere(String text) {
        // søker eksakt både på termer og synonymer
        text = "=" + text + "=";
        Term[] funn = search(text, "term");
        if (funn != null) {
            return funn;
        }
        funn = search(text, "form");
        if (funn != null) {
            return funn;
        }
        funn = search(text, "tid");
        if (funn != null) {
            return funn;
        }
        funn = search(text, "sted");
        if (funn != null) {
            return funn;
        }
        return null;
    }

    public static boolean lagreCSV() {
        boolean retval = false;
        String filnavn = null;
        if (Sonja.vokabular.equals("SMR")) {
            filnavn = BASEFOLDER + "eksport/menneskerettighetstermer.csv";
        } else {
            filnavn = BASEFOLDER + "eksport/realfagstermer.csv";
        }
        try {
            // termene
            PrintWriter utfil = new PrintWriter(
                    new File(filnavn), "UTF-8");

            for (Term t : termliste) {
                utfil.print(t.csv());
            }
            for (Term t : formliste) {
                utfil.print(t.csv());
            }
            for (Term t : stedsliste) {
                utfil.print(t.csv());
            }
            for (Term t : tidsliste) {
                utfil.print(t.csv());
            }

            utfil.close();
            retval = true;
            vindu.addtologg("Lagret CSV-fil:", false);
            vindu.addtologg("Totalt: Emnetermer: " + termliste.size(), false);
            vindu.addtologg("Totalt: Formtermer: " + formliste.size(), false);
            vindu.addtologg("Totalt: Tidstermer: " + tidsliste.size(), false);
            vindu.addtologg("Totalt: Stedstermer: " + stedsliste.size(), false);
            lagrelogg("lagret realfagstermer.csv ");
        } catch (IOException ex) {
            vindu.melding("Sonja : lagreCSV", "Feil ved skriving av fil");
            lagrelogg("mislykket lagring av realfagstermer.csv ");
        }
        return retval;
    }

    public static boolean lagreCSVsmr() {
        boolean retval = false;
        String filnavn = BASEFOLDER + "eksport/smr.csv";

        try {
            // termene
            PrintWriter utfil = new PrintWriter(
                    new File(filnavn), "UTF-8");

            for (Term t : termliste) {
                utfil.print(t.csvsmr());
            }
            for (Term t : formliste) {
                utfil.print(t.csvsmr());
            }
            for (Term t : stedsliste) {
                utfil.print(t.csvsmr());
            }
//            for (Term t : tidsliste) {
//                utfil.print(t.csv());
//            }

            utfil.close();
            retval = true;
            vindu.addtologg("Lagret CSV-fil:", false);
            vindu.addtologg("Totalt: Emnetermer: " + termliste.size(), false);
            vindu.addtologg("Totalt: Formtermer: " + formliste.size(), false);
            vindu.addtologg("Totalt: Stedstermer: " + stedsliste.size(), false);
            lagrelogg("lagret realfagstermer.csv ");
        } catch (IOException ex) {
            vindu.melding("Sonja : lagreCSVsmr", "Feil ved skriving av fil\n" + ex.toString());
            lagrelogg("mislykket lagring av realfagstermer.csv ");
        }
        return retval;
    }

    public static boolean lagreXML() {
        boolean retval = false;
        String filnavn = null;
        if (Sonja.vokabular.equals("SMR")) {
            filnavn = BASEFOLDER + "eksport/menneskerettighetstermer.xml";
        } else {
            filnavn = BASEFOLDER + "eksport/realfagstermer.xml";
        }
        try {
            // termene
            PrintWriter utfil = new PrintWriter(
                    new File(filnavn), "UTF-8");

            // skriver ut prefiksteksten, se øverst i koden
            utfil.print(xmlprefiks);
            for (Term t : termliste) {
                utfil.print(t.rdfxml());
            }
            for (Term t : formliste) {
                utfil.print(t.rdfxml());
            }
            for (Term t : stedsliste) {
                utfil.print(t.rdfxml());
            }
            for (Term t : tidsliste) {
                utfil.print(t.rdfxml());
            }
            for (Streng s : strengliste) {
                utfil.print(s.rdfxml());
            }

            utfil.print(xmlpostfiks);

            utfil.close();
            retval = true;
            vindu.addtologg("RDF/XML-lagring:\n", false);
            vindu.addtologg("Totalt: Emnetermer: " + termliste.size(), false);
            vindu.addtologg("Totalt: Formtermer: " + formliste.size(), false);
            vindu.addtologg("Totalt: Tidstermer: " + tidsliste.size(), false);
            vindu.addtologg("Totalt: Stedstermer: " + stedsliste.size(), false);
            lagrelogg("lagret realfagstermer.xml ");
        } catch (IOException ex) {
            vindu.melding("Sonja : lagreXML", "Feil ved skriving av fil");
            lagrelogg("mislykket lagring av realfagstermer.xml ");
        }
        return retval;
    }

    public static String xmlEscapeText(String t) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < t.length(); i++) {
            char c = t.charAt(i);
            switch (c) {
                case '<':
                    sb.append("&lt;");
                    break;
                case '>':
                    sb.append("&gt;");
                    break;
                case '\"':
                    sb.append("&quot;");
                    break;
                case '&':
                    sb.append("&amp;");
                    break;
                case '\'':
                    sb.append("&apos;");
                    break;
                default:
//                    if (c > 0x7e) {
//                        sb.append("&#" + ((int) c) + ";");
//                    } else {
                    sb.append(c);
//                    }
            }
        }
        return sb.toString();
    }

    static void sletterm(String ID) {
        // Id-en må slettes fra alle steder den forekommer
        // SO-termer
        // strenger
        // Fjernet ID må legges i en logg som kan plukkes fram og med
        // mulighet for reaktivering av termen
    }

    public static String storbokstav(String tekst) {
        String retval = "";
        if (tekst != null && tekst.length() > 0) {
            retval = tekst.substring(0, 1).toUpperCase() + tekst.substring(1);
        }
        return retval;
    }

    static boolean slettstreng(Streng currentStreng) {
        boolean retval = false;
        String forsteledd = currentStreng.forsteledd();
        if (forsteledd != null) {
            Term t = id2term.get(forsteledd);
            if (t != null) {
                t.fjernstreng(currentStreng.minID);
                slettstreng(currentStreng.minID);
                retval = true;
                gjortendringer = true;

            }
        }
        return retval;
    }

    static void strengermeddb() {
        int antall = 0;
        for (Streng str : strengliste) {
            if (str.meddb()) {
                vindu.addtologg(str.toStringplain(), false);
                antall++;
            }
        }
        vindu.melding("Dollarb-strenger", "Antall i alt: "
                + strengliste.size()
                + "\nAntall med db: " + antall);
    }

    static void slettstreng(String sID) {
        for (int i = 0; i < strengliste.size(); i++) {
            if (sID.equals(strengliste.get(i).minID)) {
                Streng st = strengliste.get(i);
                strengliste.remove(i);
                //vindu.addtologg("DL STRN " + st.logg(), false);
                //bibsyshuskeliste.add("Strengen " + st.toStringplain() + " er fjernet");
                vindu.addtologg("Strengen " + st.toStringplain() + " er fjernet", false);
                lagrelogg("slettet streng " + st.toStringplain());
                if (st.db != null) {
                    lokarslett("streng", st.toStringekte(), st.minID);
                }
                break;
            }
        }
        gjortendringer = true;

    }

    static String fiksdato(Date dato) {
        String fiksetdato = null;
        String[] months = {
            "Jan", "Feb", "Mar",
            "Apr", "May", "Jun",
            "Jul", "Aug", "Sep",
            "Oct", "Nov", "Dec"};
        String[] mnder = {
            "01", "02", "03",
            "04", "05", "06",
            "07", "08", "09",
            "10", "11", "12"};
        String mnd = "00";
        if (dato != null) {
            String[] elem = (dato.toString()).split(" ");
            for (int i = 0; i < 12; i++) {
                if (months[i].equals(elem[1])) {
                    mnd = mnder[i];
                    break;
                }
            }
            StringBuilder sb = new StringBuilder(elem[5]); // år
            sb.append("-")
                    .append(mnd) // måned
                    .append("-")
                    .append(elem[2]) // dag
                    .append("T")
                    .append(elem[3]) // klokkeslett
                    .append("Z");
            fiksetdato = sb.toString();
        }
        return fiksetdato;
    }

    public static String strekdato(String kompakt) {
        String retval = null;
        StringBuilder sb = new StringBuilder("");
        if (kompakt != null && kompakt.length() == 8) {
            sb.append(kompakt.substring(0, 4)).append("-");
            sb.append(kompakt.substring(4, 6)).append("-");
            sb.append(kompakt.substring(6));
            retval = sb.toString();
        }
        return retval;
    }

    static boolean backupavalledata() {
        boolean retval = true;
        Date dato = new Date();
        String prefiks = fiksdato(dato);
        prefiks = prefiks.substring(0,10);
        String source = BASEFOLDER + "idtermer.txt";
        String target = BASEFOLDER + "backup/" + prefiks + "idtermer.txt";
        if (!backupfil(source, target)) {
            retval = false;
        } else {
            source = BASEFOLDER + "idformer.txt";
            target = BASEFOLDER + "backup/" + prefiks + "idformer.txt";
            if (!backupfil(source, target)) {
                retval = false;
            } else {
                source = BASEFOLDER + "idsteder.txt";
                target = BASEFOLDER + "backup/" + prefiks + "idsteder.txt";
                if (!backupfil(source, target)) {
                    retval = false;
                } else {
                    source = BASEFOLDER + "idtider.txt";
                    target = BASEFOLDER + "backup/" + prefiks + "idtider.txt";
                    if (!backupfil(source, target)) {
                        retval = false;
                    } else {
                        source = BASEFOLDER + "idstrenger.txt";
                        target = BASEFOLDER + "backup/" + prefiks + "idstrenger.txt";
                        if (!backupfil(source, target)) {
                            retval = false;
                        }
                    }
                }
            }
        }
        return retval;
    }

    static boolean backupfil(String source, String target) {
        boolean retval = false;
        try {
            BufferedReader innfil = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(source)),
                            "UTF-8"));
            PrintWriter utfil = new PrintWriter(
                    new File(target), "UTF-8");
            String linje = innfil.readLine();
            while (linje != null) {
                utfil.println(linje);
                linje = innfil.readLine();
            }
            innfil.close();
            utfil.close();
            retval = true;
        } catch (IOException exp) {
            System.out.println("Source: " + source + "\n"
                    + "Target: " + target + "\n"
                    + exp.toString());
            retval = false;
        }
        return retval;
    }

    static boolean lagrealledata() {
        boolean retval = false;
        String mld = null;
        // tar kopi av gamle filer og lagrer backup
        if (backupavalledata()) {
            // så overskrives de gamle filene
            try {
                // termene
                String filnavn = BASEFOLDER + "idtermer.txt";
                PrintWriter utfil = new PrintWriter(
                        new File(filnavn), "UTF-8");

                for (Term t : termliste) {
                    utfil.print(t.filutskrift());
                }
                utfil.close();

                filnavn = BASEFOLDER + "idformer.txt";
                utfil = new PrintWriter(
                        new File(filnavn), "UTF-8");
                for (Term t : formliste) {
                    utfil.print(t.filutskrift());
                }
                utfil.close();

                filnavn = BASEFOLDER + "idtider.txt";
                utfil = new PrintWriter(
                        new File(filnavn), "UTF-8");
                for (Term t : tidsliste) {
                    utfil.print(t.filutskrift());
                }
                utfil.close();

                filnavn = BASEFOLDER + "idsteder.txt";
                utfil = new PrintWriter(
                        new File(filnavn), "UTF-8");
                for (Term t : stedsliste) {
                    utfil.print(t.filutskrift());
                }
                utfil.close();

                filnavn = BASEFOLDER + "idstrenger.txt";
                utfil = new PrintWriter(
                        new File(filnavn), "UTF-8");
                for (Streng s : strengliste) {
                    utfil.print(s.filutskrift());
                }
                utfil.close();
                vindu.addtologg("Lagrer grunnlagsfiler:", false);
                vindu.addtologg("Totalt: Emnetermer: " + termliste.size(), false);
                vindu.addtologg("Totalt: Formtermer: " + formliste.size(), false);
                vindu.addtologg("Totalt: Tidstermer: " + tidsliste.size(), false);
                vindu.addtologg("Totalt: Stedstermer: " + stedsliste.size(), false);
                vindu.addtologg("Totalt: Strenger: " + strengliste.size(), false);
                gjortendringer = false;
                retval = true;
                lagrelogg("lagret grunnlagsfiler ");
                /*
                if (!lagregrunnfil()) {
                    mld = "Lagring av Webdata gikk galt";
                    vindu.melding("Webdata", mld + ",\nbackup må håndlegges på plass");
                    vindu.addtologg(mld, false);
                    lagrelogg(mld);
                } else {
                    mld = "Lagring av webdata gikk greit";
                    vindu.addtologg(mld, false);
                    lagrelogg(mld);

                }
                
                if (!lagreXML()) {
                    mld = "Lagring av rdf/xml-data gikk galt";
                    vindu.melding("SKOS-data", "Lagring av SKOS-data gikk galt,\nbackup må håndlegges på plass");
                    vindu.addtologg(mld, false);
                    lagrelogg(mld);
                } else {
                    mld = "Lagring av rdf/xml-data gikk greit";
                    vindu.addtologg(mld, false);
                    lagrelogg(mld);
                }
                 */

            } catch (IOException ex) {
                vindu.melding("Sonja : lagrealledata",
                        "Feil ved skriving av fil\n"
                        + ex.toString());
                lagrelogg("mislykket lagring av grunnlagsfiler ");
            }
        } else {
            mld = "Mislykket backup";
            vindu.melding("Mislykket backup", "Nye data ikke lagret. :-)");
            lagrelogg(mld);
        }
        return retval;
    }
    
    static void saveSQL() {        
	try (
		PrintWriter conceptFile = new PrintWriter(new File(BASEFOLDER + "export_concepts.sql"), "UTF-8");
//		PrintWriter conceptFile = new PrintWriter(System.out);
		PrintWriter termFile = new PrintWriter(new File(BASEFOLDER + "export_terms.sql"), "UTF-8");
		PrintWriter stringFile = new PrintWriter(new File(BASEFOLDER + "export_strings.sql"), "UTF-8")) {

	    termFile.print("TRUNCATE TABLE terms;\n" + 
	    	"TRUNCATE TABLE relationships;\n" + 
	    	"TRUNCATE TABLE mappings;\n");
	    
	    for (Term t : termliste) {
		t.toSQL(conceptFile, termFile,  "general");
	    }

	    for (Term t : formliste) {
		t.toSQL(conceptFile, termFile, "form");
	    }

	    for (Term t : tidsliste) {
		t.toSQL(conceptFile, termFile, "time");
	    }

	    for (Term t : stedsliste) {
		t.toSQL(conceptFile, termFile, "place");
	    }

	    for (Streng s : strengliste) {
		s.toSQL(stringFile);
	    }
	} catch (IOException ex) {
	    vindu.melding("Sonja : export_sql",
		    "Feil ved skriving av fil\n"
			    + ex.toString());
	    lagrelogg("mislykket lagring av SQL");
	}
	return;
    }

    static void lagrelogg(String melding) {
        if (!startup) {
            String loggfil = BASEFOLDER + "admin/logg.txt";
            Date now = new Date();
            String tidspunkt = fiksdato(now);
            StringBuilder sb = new StringBuilder("");
            sb.append(tidspunkt);
            sb.append("\t");
            sb.append(melding);
            sb.append("\n");
            try {
                BufferedWriter fil = new BufferedWriter(
                        new OutputStreamWriter(
                                new FileOutputStream(loggfil, true), "UTF-8"));
                fil.write(sb.toString());
                fil.close();
            } catch (IOException ioe) {
                Sonjavindu.melding("Sonja : lagrelogg", "Skrivefeil: loggføring mislyktes!");
                System.out.println(ioe.toString());
            }
        }
    }

    static void lokarslett(String type, String tekst, String id) {
        if (tekst != null) {
            tekst = replace(tekst, "'", "\\'");

            if (!startup) {
                String tag = "650";
                if (type.equals("form")) {
                    tag = "655";
                }
                if (type.equals("tid")) {
                    tag = "648";
                }
                if (type.equals("sted")) {
                    tag = "651";
                }
                String loggfil = BASEFOLDER + "admin/lokar.txt";
                Date now = new Date();
                String tidspunkt = fiksdato(now);
                StringBuilder sb = new StringBuilder(tidspunkt);
                sb.append("\t").append(currentuser)
                        .append("\tpython lokar.py -e nz_prod")
                        .append(" -t ").append(tag)
                        .append(" \'").append(tekst).append("\'")
                        //                    .append(" -i ").append(id)
                        .append("\n");
                try {
                    BufferedWriter fil = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(loggfil, true), "UTF-8"));
                    fil.write(sb.toString());
                    fil.close();
                } catch (IOException ioe) {
                    Sonjavindu.melding("Sonja : lokarslett", "Skrivefeil: lokarkommando forsvant!\n" + ioe);
                    //System.out.println(ioe.toString());
                }
            }
        }
    }

    static void lokarbytt(String type, String fra, String til, String id) {
        if (fra != null && til != null) {
            fra = replace(fra, "'", "\\'");
            til = replace(til, "'", "\\'");
            if (!startup) {
                String tag = "650";
                if (type.equals("form")) {
                    tag = "655";
                }
                if (type.equals("tid")) {
                    tag = "648";
                }
                if (type.equals("sted")) {
                    tag = "651";
                }
                String loggfil = BASEFOLDER + "admin/lokar.txt";
                Date now = new Date();
                String tidspunkt = fiksdato(now);
                StringBuilder sb = new StringBuilder(tidspunkt);
                sb.append("\t").append(currentuser)
                        .append("\tpython lokar.py -e nz_prod")
                        .append(" -t ").append(tag)
                        .append(" \'")
                        .append(fra).append("\' \'").append(til).append("\'")
                        //.append(" -i ").append(id)
                        .append("\n");
                try {
                    BufferedWriter fil = new BufferedWriter(
                            new OutputStreamWriter(
                                    new FileOutputStream(loggfil, true), "UTF-8"));
                    fil.write(sb.toString());
                    fil.close();
                } catch (IOException ioe) {
                    Sonjavindu.melding("Sonja : lokarbytt", "Skrivefeil: lokarkommando forsvant!\n" + ioe);
                    //System.out.println(ioe.toString());
                }
            }
        }
    }

    static boolean lagregrunnfil() {
        boolean retval = false;
        boolean filok = false;
        String grunnfilnavn = WEBDATA + "grunnfil.txt";
        int antalltermer = 0;
        int antallstrenger = 0;
        String sistete = null;
        try {
            Term[] termene = new Term[termliste.size() + strengliste.size()];
            for (int i = 0; i < termliste.size(); i++) {
                termene[i] = termliste.get(i);
            }
            int j = 0;
            for (int i = termliste.size(); i < termliste.size() + strengliste.size(); i++) {
                termene[i] = strengliste.get(j);
                j++;
            }
            Arrays.sort(termene);
            // termene
            PrintWriter utfil = new PrintWriter(
                    new File(grunnfilnavn), "UTF-8");
            for (int i = 0; i < termene.length; i++) {
                if (!(termene[i] instanceof Streng)) {
                    String tmp = termene[i].utskriftforweb();
                    if (tmp != null) {
                        utfil.print(tmp);
                        sistete = termene[i].term;
                        antalltermer++;
                    }
                } else {

                    String startledd = getTerm(((Streng) termene[i]).da).term;
                    if (startledd.equalsIgnoreCase(sistete)) {
                        startledd = "";
                    }
                    String streng = ((Streng) termene[i]).filutskriftkyrre(startledd);
                    if (streng != null) {
                        utfil.print(streng);
                        antallstrenger++;
                        //System.out.println(streng);
                    }
                }
            }
            utfil.close();
            retval = true;
            Sonjavindu.melding("Resultat",
                    "Antall termer:   " + antalltermer + "\n"
                    + "Antall strenger: " + antallstrenger);
            lagrelogg("lagret grunnfil.txt for web ");

        } catch (IOException ex) {
            Sonjavindu.melding("Sonja : lagregrunnfil", "Feil ved skriving av fil\n" + grunnfilnavn);
            lagrelogg("lagret grunnfil.txt for web ");

        }
        /*
         * // strengene filnavn = katalog + "strengerforwebutf8.txt"; try {
         * Bygg[] strengene = new Bygg[strengliste.size()]; for (int i = 0;
         * i < strengliste.size(); i++) { strengene[i] = strengliste.get(i);
         * } Arrays.sort(strengene); // termene PrintWriter utfil = new
         * PrintWriter( new File(filnavn), "UTF-8"); for (int i = 0; i <
         * strengene.length; i++) { utfil.print(strengene[i].filutskrift());
         * } utfil.close(); } catch (IOException ex) {
         * Remneflate.melding("Main : skrivforweb", "Feil ved skriving av
         * fil\t" + filnavn); }
         *
         */
        return retval;
    }

    static void lagrealt() {
        try {
            // termene
            String filnavn = REVISJON + "idtermer.txt";
            PrintWriter utfil = new PrintWriter(
                    new File(filnavn), "UTF-8");

            for (Term t : id2term.values()) {
                if (t.type.equalsIgnoreCase("term")) {
                    utfil.print(t.filutskrift());
                }
            }
            utfil.close();

            filnavn = REVISJON + "idformer.txt";
            utfil = new PrintWriter(
                    new File(filnavn), "UTF-8");
            for (Term t : formliste) {

                utfil.print(t.filutskrift());
            }
            utfil.close();

            filnavn = REVISJON + "idtider.txt";
            utfil = new PrintWriter(
                    new File(filnavn), "UTF-8");
            for (Term t : tidsliste) {
                utfil.print(t.filutskrift());
            }
            utfil.close();

            filnavn = REVISJON + "idsteder.txt";
            utfil = new PrintWriter(
                    new File(filnavn), "UTF-8");
            for (Term t : stedsliste) {
                utfil.print(t.filutskrift());
            }
            utfil.close();

            filnavn = REVISJON + "idstrenger.txt";
            utfil = new PrintWriter(
                    new File(filnavn), "UTF-8");
            for (Streng s : strengliste) {
                utfil.print(s.filutskrift());
            }
            utfil.close();

            vindu.addtologg("Totalt: Emnetermer: " + termliste.size(), false);
            vindu.addtologg("Totalt: Formtermer: " + formliste.size(), false);
            vindu.addtologg("Totalt: Tidstermer: " + tidsliste.size(), false);
            vindu.addtologg("Totalt: Stedstermer: " + stedsliste.size(), false);
            vindu.addtologg("Totalt: Strenger: " + strengliste.size(), false);

        } catch (IOException ex) {
            vindu.melding("Sonja : lagrealledata",
                    "Feil ved skriving av fil\n"
                    + ex.toString());
        }

    }

    static void fletttotermer(Term begrep1, Term begrep2) {
        ArrayList<String> mld = new ArrayList<String>();
        
	try (Database db = new Database()) {
	    // kopierer data fra begrep2 til begrep1
	    // Update database first, then local data
	    db.setAutoCommit(false);// rollback on exception

	    if (!begrep1.synonymer.contains(begrep2.term)) {
		db.addTerm(begrep1, begrep2.term, TermStatus.non_pref, getDefaultLanguage());
	    }

	    db.setDefinition(begrep1, concatenate(begrep1.definisjon, begrep2.definisjon));
	    db.setNote(begrep1, concatenate(begrep1.note, begrep2.note));

	    // Copy all terms
	    for (Locale locale : Sonjavindu.locales) {
		if (locale == getDefaultLanguage()) {
		    for (String term : difference(begrep2.synonymer, begrep1.synonymer)) {
			db.addTerm(begrep1, term, TermStatus.non_pref, getDefaultLanguage());
		    }
		} else {
		    int numTerms = begrep1.getTerms(locale).size();

		    for (String term : difference(begrep2.getTerms(locale), begrep1.getTerms(locale))) {
			if (numTerms == 0) {
			    db.addTerm(begrep1, term, TermStatus.preferred, locale);
			} else {
			    db.addTerm(begrep1, term, TermStatus.non_pref, locale);
			}
			numTerms++;
		    }
		}
	    }

	    // related concepts
	    for (String id : difference(begrep2.seog, begrep1.seog)) {
		db.addRelation(begrep1, getTerm(id), related);
	    }

	    // Broader concepts
	    for (String id : difference(begrep2.overordnet, begrep1.overordnet)) {
		db.addRelation(begrep1, getTerm(id), broader);
		db.removeRelation(begrep2, getTerm(id), broader);
	    }

	    // Narrower
	    for (String id : difference(begrep2.underordnet, begrep1.underordnet)) {
		db.addRelation(getTerm(id), begrep1, broader); // add inverse
		db.removeRelation(getTerm(id), begrep2, broader);
	    }

	    for (String id : difference(begrep2.msc, begrep1.msc)) {
		db.addMapping(begrep1, id, ExternalVocabulary.msc1970);
	    }

	    for (String id : difference(begrep2.dewey, begrep1.dewey)) {
		db.addMapping(begrep1, id, ExternalVocabulary.ddc23);
	    }

	    db.setReplacedBy(begrep2, begrep1);
	    db.commit();
	    db.setAutoCommit(true);

	    // term2 skal inn som se-henvisning
	    begrep1.nyttsynonym(begrep2.term);
	    mld.add(begrep2.term + " lagt inn som synonym til " + begrep1.term);
	    // definisjon
	    if (begrep1.definisjon != null && begrep2.definisjon != null) {
		begrep1.definisjon = begrep1.definisjon + " " + begrep2.definisjon;
		mld.add(begrep1.term + " har fått definisjonstillegg " + begrep2.definisjon);
	    } else if (begrep2.definisjon != null) {
		begrep1.definisjon = begrep2.definisjon;
		mld.add(begrep1.term + " har fått definisjonen " + begrep2.definisjon);
	    }
	    // note
	    if (begrep1.note != null && begrep2.note != null) {
		begrep1.note = begrep1.note + " " + begrep2.note;
		mld.add(begrep1.term + " har fått notetillegg " + begrep2.note);
	    } else if (begrep2.note != null) {
		begrep1.note = begrep2.note;
		mld.add(begrep1.term + " har fått noten " + begrep2.note);
	    }

	    // se-henvisninger
	    if (begrep2.synonymer.size() > 0) {
		for (int i = 0; i < begrep2.synonymer.size(); i++) {
		    begrep1.nyttsynonym(begrep2.synonymer.get(i));
		    mld.add(begrep1.term + " har fått synonymet " + begrep2.synonymer.get(i));
		}
	    }
	    // se-også relasjoner
	    if (begrep2.seog.size() > 0) {
		for (int i = 0; i < begrep2.seog.size(); i++) {
		    begrep1.nyseog(begrep2.seog.get(i));
		    mld.add(begrep1.term + " har fått se-også til " + begrep2.seog.get(i));
		}
	    }
	    // overordnete termer overføres til den termen som blir stående
	    if (begrep2.overordnet.size() > 0) {
		for (int i = 0; i < begrep2.overordnet.size(); i++) {
		    begrep1.nyoverordnet(begrep2.overordnet.get(i));
		    mld.add(begrep1.term + " har fått ny overordnet " + begrep2.overordnet.get(i));
		}
	    }

	    // underordnete termer overføres til den termen som blir stående
	    if (begrep2.underordnet.size() > 0) {
		for (int i = 0; i < begrep2.underordnet.size(); i++) {
		    begrep1.nyunderordnet(begrep2.underordnet.get(i));
		    mld.add(begrep1.term + " har fått ny underordnet " + begrep2.underordnet.get(i));
		}
	    }

	    // engelsk
	    if (begrep2.engelsk.size() > 0) {
		for (int i = 0; i < begrep2.engelsk.size(); i++) {
		    begrep1.nyengelsk(begrep2.engelsk.get(i));
		    mld.add(begrep1.term + " har fått engelsktermen " + begrep2.engelsk.get(i));
		}
	    }
	    // latin
	    if (begrep2.latin.size() > 0) {
		for (int i = 0; i < begrep2.latin.size(); i++) {
		    begrep1.nylatin(begrep2.latin.get(i));
		    mld.add(begrep1.term + " har fått latintermen " + begrep2.latin.get(i));
		}
	    }
	    // nynorsk
	    if (begrep2.nynorsk.size() > 0) {
		for (int i = 0; i < begrep2.nynorsk.size(); i++) {
		    begrep1.nynynorsk(begrep2.nynorsk.get(i));
		    mld.add(begrep1.term + " har fått nynorsktermen " + begrep2.nynorsk.get(i));
		}
	    }
	    // forkortelser
	    if (begrep2.akronymer.size() > 0) {
		for (int i = 0; i < begrep2.akronymer.size(); i++) {
		    begrep1.nyttakronym(begrep2.akronymer.get(i));
		    mld.add(begrep1.term + " har fått forkortelsen " + begrep2.akronymer.get(i));
		}
	    }
	    // MSC første begrep har forrang
	    if (begrep1.msc == null && begrep2.msc != null) {
		begrep1.msc = begrep2.msc;
		mld.add(begrep1.term + " har fått MSC-koden " + begrep2.msc);
	    }
	    // DDC første begrep har forrang
	    if (begrep1.dewey == null && begrep2.dewey != null) {
		begrep1.dewey = begrep2.dewey;
		mld.add(begrep1.term + " har fått DDC-koden " + begrep2.dewey);
	    }
	    // gå gjennom strengene og bytt ut minid2 med minid1
	    byttidistrenger(begrep2.minID, begrep1.minID);

	    // flytt strengene fra begrep2 til begrep1
	    flyttstrenger(begrep2, begrep1);

	    // gå gjennom termene og bytt ut minid2 med minid1 i evt so-relasjoner
	    byttidiseog(begrep2.minID, begrep1.minID);

	    // sette inn begrep1 sin ID i historikk til begrep2
	    begrep2.flyttettilID = begrep1.minID;

	    // fjerne begrep2 fra datagrunnlag
	    fjerneterm(begrep2);

	    // lager lokarstreng
	    lokar(begrep2.term, begrep1.term);
	    lokarbytt(begrep2.type, begrep2.term, begrep1.term, begrep1.minID);

	    String melding = begrep1.type + ": "
		    + begrep2.term
		    + " endret til "
		    + begrep1.term;
	    // bibsyshuskeliste.add(melding);
	    vindu.addtologg(melding, false);
	    // mld.add(melding);
	    if (mld.size() > 0) {
		for (int i = 0; i < mld.size(); i++) {
		    lagrelogg(mld.get(i));
		}
	    }
	} catch (SQLException e) {
	    Sonjavindu.melding("Feil ved lagring:", e.getMessage());
	}
    }

    /**
     * Find Collection (set) difference
     * 
     * @param coll1 basis collection
     * @param coll2 elements to be removed
     * @return Set consisting of coll1 - coll2
     */
    private static <T> Set<T> difference(Collection<T> coll1, Collection<T> coll2) {
	Set<T> difference = new HashSet<>(coll1);
	difference.removeAll(coll2);
	return difference;
    }

    /**
     * Concatenate 2 strings, ignoring null values
     * 
     * @param string1 a String
     * @param string2 a String
     * @return the concatenation
     */
    private static String concatenate(String string1, String string2) {
	if (string1 != null && string2 != null) {
	    return (string1 + " " + string2).trim();
	} else if (string2 != null) {
	    return string2;
	} else {
	    return string1;
	}
    }

    public static void flyttstrenger(Term b2, Term b1) {
        if (b2.strenger.size() > 0) {
            for (int i = 0; i < b2.strenger.size(); i++) {
                Streng b2streng = b2.strenger.get(i);
                b1.addstreng(b2streng);
            }
        }
    }

    public static void fjerneterm(Term t) {
        // sett slettedato i termen
        t.slettetdato(fiksdato(new Date()));

        // fjerne minID fra evt strenger
        fjernidistrenger(t.minID);

        // fjerne minID fra evt so-relasjoner
        // ot-relasjoner og ut-relasjoner
        for (Term t1 : id2term.values()) {
            t1.fjernso(t.minID);
            t1.fjernot(t.minID);
            t1.fjernut(t.minID);
        }

        // fjerne termen fra id2term, term2id og relevant termliste 
        // (term, form, tid sted)
        // id2term.remove(t);
//        term2id.remove(t);
        if (t.type.equals("term")) {
            //termliste.remove(t);
        }
        if (t.type.equals("form")) {
            //formliste.remove(t);
        }
        if (t.type.equals("tid")) {
            //tidsliste.remove(t);
        }
        if (t.type.equals("sted")) {
            //stedsliste.remove(t);
        }
        gjortendringer = true;
        //lokar(t.term, "*");
        String melding = t.type + ": " + t.term
                + " må slettes ";
        //bibsyshuskeliste.add(melding);
        vindu.addtologg(melding, false);
        melding = t.type + " " + t.term + " slettet ";
        lagrelogg(melding);
        //lokarslett(t.type, t.term);
    }

    private static void byttidistrenger(String minIDfra, String minIDtil) {
        // TODO må sjekke om det genererer en strng som fins fra før
        // TODO må sjekke om utgående id ligger i forslagskassa og evt bytte

        //Streng slettestreng = null;
        for (Streng str : strengliste) {
            str.byttid(minIDfra, minIDtil);
        }

        gjortendringer = true;

    }

    private static void fjernidistrenger(String minID) {
        ArrayList<Streng> fjernes = new ArrayList<Streng>();

        String melding = "";
        for (Streng str : strengliste) {
            if (str.harID(minID)) {
                melding = "fjernet strengen: " + str.toStringplain();
                lagrelogg(melding);
                fjernes.add(str);
            }
        }
        for (int i = 0; i < fjernes.size(); i++) {
            strengliste.remove(fjernes.get(i));
        }
        gjortendringer = true;

    }

    private static void byttidiseog(String minIDfra, String minIDtil) {
        for (Term t : id2term.values()) {
            t.byttseogid(minIDfra, minIDtil);
            gjortendringer = true;
        }
    }

    static boolean strengfins(Streng strengbygg) {
        boolean retval = false;
        if (strengbygg != null) {
            for (Streng str : strengliste) {
                if (str.erlik(strengbygg)) {
                    retval = true;
                    break;
                }
            }
        }
        return retval;
    }

    static void searchbibsys(Object tmp) {
        if (tmp != null) {
            String searchstring = null;
            if (tmp instanceof Streng) {
                Streng s = (Streng) tmp;
                searchstring = s.searchstring();
            } else if (tmp instanceof Term) {
                Term t = (Term) tmp;
                searchstring = t.getsearchstring();
            }
            sokbibsys(searchstring);
        }
    }

    public static void searchokapi(Term t) {
        if (t != null) {
            String searchstring = t.getsearchstringokapi();
            //System.out.println(searchstring);
            searchstring = utf8(searchstring);
            //System.out.println(searchstring);
            BrowserLaunch.openURL(
                    "https://katapi.biblionaut.net/documents?show=sh&q=real:"
                    + searchstring);
        }
    }

    public static void sokbibsys(String searchstring) {
        String avdavgrensning = null;
        if (!sokavd.equals("")) {
            avdavgrensning = "(bs.avdeling = \"" + sokavd + "\")";
        } else {
            avdavgrensning = "";
        }
        if (!avdavgrensning.equals("")) {
            searchstring = searchstring
                    + " AND "
                    + avdavgrensning;
        }
        searchstring = utf8(searchstring);
        if (sokbibl.equals("UBO")) {
            BrowserLaunch.openURL(
                    "http://ask.bibsys.no/ask/action/result?cql="
                    + searchstring
                    + "&aktivKilde=biblio%3AUBO");
        } else if (sokbibl.equals("UBB")) {
            BrowserLaunch.openURL(
                    "http://ask.bibsys.no/ask/action/result?cql="
                    + searchstring
                    + "&aktivKilde=biblio%3AUBB");
        } else {
            BrowserLaunch.openURL(
                    "http://ask.bibsys.no/ask/action/result?cql="
                    + searchstring
                    + "&aktivKilde=biblio");
        }
    }

    public static String utf8(String s) {
        String d = new String(s);
        d = replace(d, "æ", "%C3%A6");
        d = replace(d, "Æ", "%C3%86");
        d = replace(d, "ø", "%C3%B8");
        d = replace(d, "Ø", "%C3%98");
        d = replace(d, "å", "%C3%A5");
        d = replace(d, "Å", "%C3%85");
        d = replace(d, "(", "%28");
        d = replace(d, ")", "%29");
        d = replace(d, " ", "+");
        d = replace(d, "\"", "%22");
        d = replace(d, "=", "%3D");
        return d;
    }

    public static String utf8oria(String s) {
        String d = new String(s);
        d = replace(d, "æ", "%C3%A6");
        d = replace(d, "Æ", "%C3%86");
        d = replace(d, "ø", "%C3%B8");
        d = replace(d, "Ø", "%C3%98");
        d = replace(d, "å", "%C3%A5");
        d = replace(d, "Å", "%C3%85");
        d = replace(d, "(", "%28");
        d = replace(d, ")", "%29");
        d = replace(d, " ", "+");
        d = replace(d, "\"", "%22");
        return d;
    }

    public static String replace(String str, String pattern, String repl) {
        int s = 0;
        int e = 0;
        StringBuilder result = new StringBuilder();
        while ((e = str.indexOf(pattern, s)) >= 0) {
            result.append(str.substring(s, e));
            result.append(repl);
            s = e + pattern.length();
        }

        result.append(str.substring(s));
        return result.toString();
    }

    static ArrayList<Streng> lesstrengeforslag() {
        String arbeidsfil = FORSLAG + "arbeidskasse.txt";
        String forslagsfil = FORSLAG + "strengeforslag.txt";
        Streng str = null;
        sandkasse = new ArrayList<Streng>();
        // Leser inn sandkassa
        try {
            // termene
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(arbeidsfil)),
                            "UTF-8"));
            String linje = br.readLine();
            Streng streng = null;

            while (linje != null) {
                if (linje.startsWith("id=")) {
                    if (streng != null) {
                        sandkasse.add(streng);
                    }
                    streng = new Streng();
                    //streng.addtype("streng");
                } else {
                    puttfelt(linje, streng);
                }
                linje = br.readLine();
            }
            if (streng != null) {
                sandkasse.add(streng);
            }
            br.close();
            if (sandkasse.size() == 0) {
                vindu.melding("Gamle forslag", "Ingen gamle strengeforslag");
            }

        } catch (IOException exp) {
            vindu.melding("Sonja: lesforslag", "Klarte ikke lese de gamle forslagene.");
        }
        // leser forslagene
        try {
            boolean nyeforslag = false;
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(forslagsfil)),
                            "UTF-8"));
            String linje = br.readLine();
            while (linje != null) {
                if (linje.startsWith("id= ")) {
                    if (str != null) {
                        sandkasse.add(str);
                        nyeforslag = true;
                    }
                    str = new Streng();
                    //str.addID(linje.substring(3).trim());
                    //str.addtype("streng");
                } else {
                    puttfelt(linje, str);
                }
                linje = br.readLine();
            }
            if (str != null) {
                sandkasse.add(str);
                nyeforslag = true;
            }
            br.close();
            if (!nyeforslag) {
                vindu.melding("Nye forslag", "Ingen nye strengeforslag");
            }
            // nullstiller forslagene
            // sikrer gamle og nye forslag

        } catch (IOException exp) {
            vindu.melding("Sonja: lesstrengeforslag", "Greidde ikke lese filen\n" + exp.toString());
        }
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(arbeidsfil), "UTF-8");
            for (Streng t : sandkasse) {
                if (t != null) {
                    utfil.print(t.filutskrift());
                }
            }
            utfil.close();
            // nullstiller forslagskassa
            try {

                utfil = new PrintWriter(
                        new File(forslagsfil), "UTF-8");
                utfil.println(" ");
                utfil.close();
            } catch (IOException ioexp) {
                vindu.melding("Sonja : lesstrengeforslag", "Fikk ikke nullstilt forslagskassa.");
            }
        } catch (IOException ioexp) {
            vindu.melding("Sinja : lesstrengeforslag", "Fikk ikke skrevet forslagsarbeidsfil.");
        }
        return sandkasse;
    }

    static void puttfelt(String linje, Streng str) {
        if (linje.startsWith("da= ")) {
            str.addterm(linje.substring(3).trim());
        }
        if (linje.startsWith("db= ")) {
            str.addunderterm(linje.substring(3).trim());
        }
        if (linje.startsWith("dc= ")) {
            str.addkvalifikator(linje.substring(3).trim());
        }
        if (linje.startsWith("dx= ")) {
            str.addform(linje.substring(3).trim());
        }
        if (linje.startsWith("dy= ")) {
            str.addtid(linje.substring(3).trim());
        }
        if (linje.startsWith("dz= ")) {
            // str.addsted(storforbokstavisted(linje.substring(3).trim()));
            str.addsted(linje.substring(3).trim());
        }
        if (linje.startsWith("ms= ")) {
            str.addmsc(linje.substring(3).trim());
        }
        if (linje.startsWith("dw= ")) {
            str.adddewey(linje.substring(3).trim());
        }
        if (linje.startsWith("fa= ")) {
            str.addbruker(linje.substring(3).trim());
        }
        if (linje.startsWith("al= ")) {
            str.addalmaid(linje.substring(3).trim());
        }
    }

    static void sjekkinversseog(String minIDfra, String minIDtil) {
        Term til = getTerm(minIDtil);
        Term fra = getTerm(minIDfra);
        if (!til.harseog(minIDfra)) {
            int svar = JOptionPane.showConfirmDialog(null,
                    "Skal programmet legge inn invers se også-relasjon?",
                    "Invers relasjon", JOptionPane.YES_NO_OPTION);
            if (svar == JOptionPane.YES_OPTION) {
		try (Database db = new Database()) {
		    db.addRelation(til, fra, RelationType.related);
		    til.nyseog(minIDfra);
		    til.endret();
		} catch (SQLException e) {
		    Sonjavindu.melding("Feil ved lagring:", e.getMessage());
		}
            }
        }
    }

    static void sjekkinversot(String minIDfra, String minIDtil) {
        Term til = getTerm(minIDtil);
        Term fra = getTerm(minIDfra);
        if (!til.harot(minIDfra)) {
            til.nyunderordnet(minIDfra);
            til.endret();
        }
    }

    static void sjekkinversut(String minIDfra, String minIDtil) {
        Term til = getTerm(minIDtil);
        Term fra = getTerm(minIDfra);
        if (!til.harut(minIDfra)) {
            til.nyoverordnet(minIDfra);
            til.endret();
        }
    }

    static void avslutt() {
	System.exit(0); //todo: fix exit checks
        boolean dataok = false;
        boolean bibsysok = false;
        boolean kopierhuskeliste = false;
        int svar = 0;
        if (gjortendringer) {
            svar = JOptionPane.showConfirmDialog(null,
                    "Vil du lagre endringene?",
                    "Data oppdatert", JOptionPane.YES_NO_OPTION);
            if (svar == JOptionPane.YES_OPTION) {
                if (lagrealledata()) {
                    vindu.melding("Lagring og backup var vellykket.", "Sjekk status i logg-fanen.");
                    dataok = true;
                } else {
                    vindu.melding("Lagring og backup var mislykket.", "Meld fra.");
                }
            }
        } else {
            dataok = true;
        }
        if (bibsyshuskeliste.size() > 0) {
            svar = JOptionPane.showConfirmDialog(null,
                    "Retteliste for BIBSYS må lagres",
                    "BIBSYS må rettes", JOptionPane.YES_NO_OPTION);
            if (svar == JOptionPane.YES_OPTION) {
                if (lagrehuskeliste()) {
                    bibsysok = true;
                } else {
                    vindu.melding("Fikk ikke lagret huskeliste", "Kopier retteliste for BIBSYS fra Logg-fanekortet");
                    gjortendringer = false;
                }
            } else {
                vindu.melding("Kopier huskeliste", "Kopier retteliste for BIBSYS fra Logg-fanekortet og Avslutt på ny");
                gjortendringer = false;
                bibsyshuskeliste.clear();
                kopierhuskeliste = true;
            }
        } else {
            bibsysok = true;
        }

        if (dataok) {
            settopptattfil("ledig");
            System.exit(0);
        } else if (!kopierhuskeliste) {
            svar = JOptionPane.showConfirmDialog(null,
                    "Unormale ting har skjedd. Vil du likevel avslutte?",
                    "", JOptionPane.YES_NO_OPTION);
            if (svar == JOptionPane.YES_OPTION) {
                settopptattfil("ledig");
                System.exit(0);
            }
        }
    }

    static int slutt() {
        int retval = 1;
        avslutt();
        return retval;
    }

    static void lagreforslagsfil(ArrayList<Streng> forslag) {
        String arbeidsfil = FORSLAG + "arbeidskasse.txt";
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(arbeidsfil), "UTF-8");
            for (Streng t : forslag) {
                if (t != null) {
                    utfil.print(t.filutskrift());
                }
            }
            utfil.close();
        } catch (IOException ioexp) {
            vindu.melding("Sonja : lagreforslagsfil", "Fikk ikke skrevet forslagsarbeidsfil.");
        }
    }

    public static String gettype(String s) {
        String retval = null;
        Term t = getTerm(s);
        if (t != null) {
            retval = t.type;
        }
        return retval;
    }

    static void ordnestreng(Streng strengbygg) {
        if (strengbygg.da == null) {
            if (strengbygg.sted != null) {
                strengbygg.da = strengbygg.sted;
                strengbygg.sted = null;
            } else if (strengbygg.form != null) {
                strengbygg.da = strengbygg.form;
                strengbygg.form = null;
            }
        }
    }

    public static boolean sjekkopptattfil() {
        boolean retval = false;
        String opptattfil = BASEFOLDER + "admin/ledigstatusfil.txt";
        try {
            // termene
            BufferedReader br = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(
                                    new File(opptattfil)),
                            "UTF-8"));
            String linje = br.readLine().trim();
            br.close();
            if (linje.equalsIgnoreCase("ledig")) {
                retval = true;
            } else {
                String brukerfil = BASEFOLDER + "admin/sonjabrukerfil.txt";
                br = new BufferedReader(
                        new InputStreamReader(
                                new FileInputStream(
                                        new File(brukerfil)),
                                "UTF-8"));
                Sonjavindu.bruker = br.readLine().trim();
                br.close();
            }
        } catch (IOException exp) {
            settopptattfil("ledig");
            settbrukerfil("ukjent");
            vindu.melding("Fant ikke statusfil", "Statusfil ble opprettet.");
            //System.out.println(exp.toString());
        }

        return retval;

    }

    public static void settopptattfil(String status) {
        String opptattfil = BASEFOLDER + "admin/ledigstatusfil.txt";
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(opptattfil), "UTF-8");
            utfil.println(status);
            utfil.close();
        } catch (IOException ioexp) {
            vindu.melding("Sonja : settopptattfil", "Fikk ikke oppdatert tilgangsstatus");
        }
    }

    public static void settbrukerfil(String status) {
        String opptattfil = BASEFOLDER + "admin/sonjabrukerfil.txt";
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(opptattfil), "UTF-8");
            utfil.println(status);
            utfil.close();
        } catch (IOException ioexp) {
            vindu.melding("Main : settbrukerfil", "Fikk ikke oppdatert brukerstatus");
        }
    }

    public static boolean sjekkbruker(String bruker, char[] passord) {
        boolean retval = false;
        String ord = new String(passord);

        for (int i = 0; i < users.length; i++) {
            if (bruker.equals(users[i]) && ord.equals(pwds[i])) {
                currentuser = bruker;
                settbrukerfil(bruker);
                retval = true;
                break;
            }
        }
        /*
         if (currentuser.equals("SMR")) {
         vindu.knut(true);
         }
         */
        return retval;
    }

    private static boolean lagrehuskeliste() {
        boolean retval = false;
        String utfilnavn = fiksutfil("Oppgi fil for BIBSYS-huskeliste");
        if (utfilnavn != null) {
            try {
                PrintWriter utfil = new PrintWriter(
                        new File(utfilnavn), "UTF-8");
                for (int i = 0; i < bibsyshuskeliste.size(); i++) {
                    utfil.println(bibsyshuskeliste.get(i));
                }
                utfil.close();
                retval = true;
            } catch (IOException ioexp) {
                vindu.melding("Sonja : lagrehuskeliste", "Fikk ikke skrevet huskelista");
            }
        }
        return retval;
    }

    public static String fiksutfil(String ledetekst) {
        String retval = null;
        JFileChooser fc = new JFileChooser(currentPath);
        fc.setDialogTitle(ledetekst);
        int returnVal = fc.showSaveDialog(null);
        if (returnVal == JFileChooser.APPROVE_OPTION) {
            retval = fc.getSelectedFile().getPath();
            currentPath = retval;
        }
        return retval;
    }

    static String fjernidprefiks(String minID) {
        String renid = null;
        if (minID.startsWith(idprefiks)) {
            renid = "c" + minID.substring(idprefiks.length());
        } else {
            renid = minID;
        }
        return renid;
    }

    static void laglatexordbok() {
        ArrayList<Bokord> ordliste = new ArrayList<Bokord>();
        // bygge opp ordlista
        for (Term t : termliste) {
            ordliste.add(new Bokord(t.term, t.minID, "term"));
            if (t.synonymer.size() > 0) {
                for (int i = 0; i < t.synonymer.size(); i++) {
                    ordliste.add(new Bokord(t.synonymer.get(i), t.minID, "henv"));
                }
            }
        }
//        for (Term t : formliste) {
//            ordliste.add(new Bokord(t.term, t.minID, "form"));
//            if (t.synonymer.size() > 0) {
//                for (int i = 0; i < t.synonymer.size(); i++) {
//                    ordliste.add(new Bokord(t.synonymer.get(i), t.minID, "henv"));
//                }
//            }
//        }
//        for (Term t : stedsliste) {
//            ordliste.add(new Bokord(t.term, t.minID, "sted"));
//            if (t.synonymer.size() > 0) {
//                for (int i = 0; i < t.synonymer.size(); i++) {
//                    ordliste.add(new Bokord(t.synonymer.get(i), t.minID, "henv"));
//                }
//            }
//        }
//        for (Term t : tidsliste) {
//            ordliste.add(new Bokord(t.term, t.minID, "tid"));
//            if (t.synonymer.size() > 0) {
//                for (int i = 0; i < t.synonymer.size(); i++) {
//                    ordliste.add(new Bokord(t.synonymer.get(i), t.minID, "henv"));
//                }
//            }
//        }
//        for (Term t : strengliste) {
//            Streng s = (Streng) t;
//            ordliste.add(new Bokord(s.toString(), s.minID, "streng"));
//        }

        // lage array
        Bokord[] ordlistematrise = new Bokord[ordliste.size()];
        for (int i = 0; i < ordliste.size(); i++) {
            ordlistematrise[i] = ordliste.get(i);
        }

        // sortere den
        Arrays.sort(ordlistematrise);

        // skrive ut
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(LATEXFOLDER + "emneord.tex"), "UTF-8");
            utfil.print(latexprefiks1);
            utfil.print("\\title{Realfagstermer\\\\med se-henvisninger}");
            utfil.print(latexprefiks2);
            for (Bokord bo : ordlistematrise) {
                utfil.print(bo.latex());
            }
            utfil.print(latexpostfiks);
            utfil.close();
        } catch (Exception exp) {

        }

    }

    static void strengermeddblatex() {
        ArrayList<Bokord> ordliste = new ArrayList<Bokord>();
        for (Term t : strengliste) {
            Streng s = (Streng) t;
            if (s.db != null) {
                ordliste.add(new Bokord(s.toString(), s.minID, "streng"));
            }
        }
        // lage array
        Bokord[] ordlistematrise = new Bokord[ordliste.size()];
        for (int i = 0; i < ordliste.size(); i++) {
            ordlistematrise[i] = ordliste.get(i);
        }

        // sortere den
        Arrays.sort(ordlistematrise);

        // skrive ut
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(LATEXFOLDER + "strengermeddb.tex"), "UTF-8");
            utfil.print(latexprefiks1);
            utfil.print("\\title{Realfagstermer\\\\strenger med dollarb\\\\(underemneord)}");
            utfil.print(latexprefiks2);
            for (Bokord bo : ordlistematrise) {
                utfil.print(bo.latex());
            }
            utfil.print(latexpostfiks);
            utfil.close();
        } catch (Exception exp) {

        }

    }

    static void termermedakronymerlatex() {
        ArrayList<Bokord> ordliste = new ArrayList<Bokord>();
        for (Term t : termliste) {
            if (t.akronymer.size() > 0) {
                for (int i = 0; i < t.akronymer.size(); i++) {
                    Bokord tmp = new Bokord(t.akronymoppslag(i), t.minID, "term");
                    tmp.akrolinjer = t.akrolinjer;
                    ordliste.add(tmp);
                }
            }
        }
        Bokord[] ordlistematrise = new Bokord[ordliste.size()];
        for (int i = 0; i < ordliste.size(); i++) {
            ordlistematrise[i] = ordliste.get(i);
        }

        // sortere den
        Arrays.sort(ordlistematrise);

        // skrive ut
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(LATEXFOLDER + "termermedakronymer.tex"), "UTF-8");
            utfil.print(latexprefiks01);
            utfil.print("\\title{Forkortelser og akronymer}");
            utfil.print(latexprefiks02);
            int makslinjer = 40;
            int antallinjer = 0;
            for (Bokord bo : ordlistematrise) {
                if (antallinjer > makslinjer) {
                    utfil.print(latextabellkutt);
                    antallinjer = 0;
                }
                String rad = bo.oppslag + "\\\\\\hline\n";
                utfil.print(rad);
                antallinjer = antallinjer + bo.akrolinjer;
            }
            utfil.print(latexpostfiks2);
            utfil.close();
        } catch (Exception exp) {

        }

    }

    static void strengermeddblatexvrengt() {
        ArrayList<Bokord> ordliste = new ArrayList<Bokord>();
        for (Term t : strengliste) {
            Streng s = (Streng) t;
            if (s.db != null) {
                ordliste.add(new Bokord(s.vrengtax(), s.minID, "streng"));
            }
        }
        // lage array
        Bokord[] ordlistematrise = new Bokord[ordliste.size()];
        for (int i = 0; i < ordliste.size(); i++) {
            ordlistematrise[i] = ordliste.get(i);
        }

        // sortere den
        Arrays.sort(ordlistematrise);

        // skrive ut
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(LATEXFOLDER + "strengermeddbvrengt.tex"), "UTF-8");
            utfil.print(latexprefiks1);
            utfil.print("\\title{Realfagstermer\\\\strenger med dollarb -- vrengt}");
            utfil.print(latexprefiks2);
            for (Bokord bo : ordlistematrise) {
                utfil.print(bo.latex());
            }
            utfil.print(latexpostfiks);
            utfil.close();
        } catch (Exception exp) {

        }

    }

    static void strengerutendblatex() {
        ArrayList<Bokord> formordliste = new ArrayList<Bokord>();
        ArrayList<Bokord> stedordliste = new ArrayList<Bokord>();
        ArrayList<Bokord> tidordliste = new ArrayList<Bokord>();
        for (Term t : strengliste) {
            Streng s = (Streng) t;
            if (s.db == null) {
                if (s.form != null) {
                    formordliste.add(new Bokord(s.toString(), s.minID, "streng"));
                }
                if (s.sted != null) {
                    stedordliste.add(new Bokord(s.toString(), s.minID, "streng"));
                }
                if (s.tid != null) {
                    tidordliste.add(new Bokord(s.toString(), s.minID, "streng"));
                }
            }
        }
        // lage array
        Bokord[] formordlistematrise = new Bokord[formordliste.size()];
        for (int i = 0; i < formordliste.size(); i++) {
            formordlistematrise[i] = formordliste.get(i);
        }

        // sortere den
        Arrays.sort(formordlistematrise);

        Bokord[] stedordlistematrise = new Bokord[stedordliste.size()];
        for (int i = 0; i < stedordliste.size(); i++) {
            stedordlistematrise[i] = stedordliste.get(i);
        }

        // sortere den
        Arrays.sort(stedordlistematrise);

        Bokord[] tidordlistematrise = new Bokord[tidordliste.size()];
        for (int i = 0; i < tidordliste.size(); i++) {
            tidordlistematrise[i] = tidordliste.get(i);
        }

        // sortere den
        Arrays.sort(tidordlistematrise);

        // skrive ut
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(LATEXFOLDER + "strengerutendb.tex"), "UTF-8");
            utfil.print(latexprefiks1);
            utfil.print("\\title{Realfagstermer\\\\strenger uten dollar b\\\\men med form, tid eller sted}");
            utfil.print(latexprefiks2);
            utfil.print("\\section{Strenger med former}\n");
            for (Bokord bo : formordlistematrise) {
                utfil.print(bo.latex());
            }
            utfil.print("\\section{Strenger med sted}\n");
            for (Bokord bo : stedordlistematrise) {
                utfil.print(bo.latex());
            }
            utfil.print("\\section{Strenger med tider}\n");
            for (Bokord bo : tidordlistematrise) {
                utfil.print(bo.latex());
            }
            utfil.print(latexpostfiks);
            utfil.close();
        } catch (Exception exp) {

        }

    }

    static void engelsketermerlatex() {
        ArrayList<Bokord> ordliste = new ArrayList<Bokord>();
        // bygge opp ordlista
        for (Term t : termliste) {
            if (t.engelsk.size() > 0) {
                ordliste.add(new Bokord(t.engelsk.get(0), t.minID, "term"));
                for (int i = 1; i < t.engelsk.size(); i++) {
                    ordliste.add(new Bokord(t.engelsk.get(i), t.minID, "henv"));
                }
            }
        }

        // lage array
        Bokord[] ordlistematrise = new Bokord[ordliste.size()];
        for (int i = 0; i < ordliste.size(); i++) {
            ordlistematrise[i] = ordliste.get(i);
        }

        // sortere den
        Arrays.sort(ordlistematrise);

        // skrive ut
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(LATEXFOLDER + "engelskeemneord.tex"), "UTF-8");
            utfil.print(latexprefiks1);
            utfil.print("\\title{Realfagstermer\\\\engelske emneord med oversettelse til norsk}");
            utfil.print(latexprefiks2);
            for (Bokord bo : ordlistematrise) {
                utfil.print(bo.englatex());
            }
            utfil.print(latexpostfiks);
            utfil.close();
        } catch (Exception exp) {

        }

    }

    static void norskmedenengelsklatex() {
        ArrayList<Bokord> ordliste = new ArrayList<Bokord>();
        // bygge opp ordlista
        for (Term t : termliste) {
            if (t.engelsk.size() > 0) {
                ordliste.add(new Bokord(t.term, t.minID, "term"));
            }
        }

        // lage array
        Bokord[] ordlistematrise = new Bokord[ordliste.size()];
        for (int i = 0; i < ordliste.size(); i++) {
            ordlistematrise[i] = ordliste.get(i);
        }

        // sortere den
        Arrays.sort(ordlistematrise);

        // skrive ut
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(LATEXFOLDER + "norengemneord.tex"), "UTF-8");
            utfil.print(latexprefiks1);
            utfil.print("\\title{Realfagstermer\\\\norske emneord som har engelsk versjon}");
            utfil.print(latexprefiks2);
            for (Bokord bo : ordlistematrise) {
                utfil.print(bo.norenglatex());
            }
            utfil.print(latexpostfiks);
            utfil.close();
        } catch (Exception exp) {

        }
    }

    static void norskmedbfogenengelsklatex() {
        ArrayList<Bokord> ordliste = new ArrayList<Bokord>();
        // bygge opp ordlista
        for (Term t : termliste) {
            ordliste.add(new Bokord(t.term, t.minID, "term"));
            if (t.synonymer.size() > 0) {
                for (String syn : t.synonymer) {
                    ordliste.add(new Bokord(syn, t.minID, "bf"));
                }
            }
        }

        // lage array
        Bokord[] ordlistematrise = new Bokord[ordliste.size()];
        System.out.println("Antall ord:\t" + ordlistematrise.length);
        for (int i = 0; i < ordliste.size(); i++) {
            ordlistematrise[i] = ordliste.get(i);
        }

        // sortere den
        Arrays.sort(ordlistematrise);
        int antallit = 0;
        int antallbf = 0;
        // skrive ut
        try {

            PrintWriter utfil = new PrintWriter(
                    new File(LATEXFOLDER + "norbfeng.tex"), "UTF-8");
            utfil.print(latexprefiks1);
            utfil.print("\\title{Realfagstermer\\\\norske emneord og synonymer med engelsk}\n");
            utfil.print(latexprefiks2);
            for (Bokord bo : ordlistematrise) {
                if (bo.type.equals("term")) {
                    utfil.print(bo.norbfenglatex());
                    antallit++;
                } else if (bo.type.equals("bf")) {
                    utfil.print(bo.norbfenglatex());
                    antallbf++;
                }
            }
            utfil.print(latexpostfiks);
            utfil.close();
            System.out.println("Antall: \t" + antallit + "\t" + antallbf);
        } catch (Exception exp) {
            System.out.println(exp.toString());
        }
    }

    static void norskutenenengelsklatex() {
        ArrayList<Bokord> ordliste = new ArrayList<Bokord>();
        // bygge opp ordlista
        for (Term t : termliste) {
            if (t.engelsk.size() == 0) {
                ordliste.add(new Bokord(t.term, t.minID, "term"));
            }
        }

        // lage array
        Bokord[] ordlistematrise = new Bokord[ordliste.size()];
        for (int i = 0; i < ordliste.size(); i++) {
            ordlistematrise[i] = ordliste.get(i);
        }

        // sortere den
        Arrays.sort(ordlistematrise);

        // skrive ut
        try {
            PrintWriter utfil = new PrintWriter(
                    new File(LATEXFOLDER + "norutenengemneord.tex"), "UTF-8");
            utfil.print(latexprefiks1);
            utfil.print("\\title{Realfagstermer\\\\norske emneord som har engelsk versjon}");
            utfil.print(latexprefiks2);
            for (Bokord bo : ordlistematrise) {
                utfil.print(bo.norutenenglatex());
            }
            utfil.print(latexpostfiks);
            utfil.close();
        } catch (Exception exp) {

        }
    }

    static String fjernmultipleblanke(String tekst) {
        String retval = tekst;
        if (tekst != null) {
            tekst = tekst.trim();
            StringBuilder sb = new StringBuilder("");
            if (tekst != null) {
                String[] deler = tekst.split(" ");
                sb = new StringBuilder(deler[0]);
                for (int i = 1; i < deler.length; i++) {
                    if (!deler[i].equals("")) {
                        sb.append(" ").append(deler[i]);
                    }
                }
                //System.out.println("Antall deler:\t" + deler.length);
                retval = sb.toString();
            } else {
                retval = tekst;
            }
        }
        return retval;
    }

    static void lokar(String fra, String til) {
        String klipp = fra + "\t" + til;
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        StringSelection ss = new StringSelection(klipp);
        clip.setContents(ss, null);
    }

    static void leslogg() {
        vindu.tomloggvindu();
        vindu.addtologg(oppstartsstatus, false);
        String loggfil = BASEFOLDER + "admin/logg.txt";

        try {
            BufferedReader fil = new BufferedReader(
                    new InputStreamReader(
                            new FileInputStream(loggfil), "UTF-8"));
            String linje = fil.readLine();
            while (linje != null) {
                vindu.addtologg(linje, false);
                linje = fil.readLine();
            }
            fil.close();
        } catch (IOException ioe) {
            Sonjavindu.melding("Sonja : leslogg", "Lesefeil: logglesing mislyktes!");
            System.out.println(ioe.toString());
        }
    }

    static void popoppmenyaksjon(String kilde, String valg) {
        //System.out.println("Kilde: \t" + kilde + "\tValg:\t" + valg);
        if (kilde.equals("seog")) {
            if (valg.equals("Legge til")) {
                vindu.leggetilseog();
            } else if (valg.equals("Fjerne")) {
                vindu.fjerneseog();
            }
        }
        if (kilde.equals("bruktfor")) {
            if (valg.equals("Legge til")) {
                vindu.leggetilsehenvisning();
            } else if (valg.equals("Fjerne")) {
                vindu.fjernesehenvisning();
            }
        }
        if (kilde.equals("overordnet")) {
            if (valg.equals("Legge til")) {
                vindu.leggetiloverordnet();
            } else if (valg.equals("Fjerne")) {
                vindu.fjerneoverordnet();
            }
        }
        if (kilde.equals("underordnet")) {
            if (valg.equals("Legge til")) {
                vindu.leggetilunderordnet();
            } else if (valg.equals("Fjerne")) {
                vindu.fjerneunderordnet();
            }
        }
        if (kilde.equals("strengbruktfor")) {
            if (valg.equals("Legge til")) {
                vindu.leggetilstrengbruktfor();
            } else if (valg.equals("Fjerne")) {
                vindu.fjernestrengbruktfor();
            }
        }
        if (kilde.equals("strengdewey")) {
            if (valg.equals("Legge til")) {
                vindu.leggetilstrengdewey();
            } else if (valg.equals("Fjerne")) {
                vindu.fjernestrengdewey();
            }
        }
        if (kilde.equals("strengmsc")) {
            if (valg.equals("Legge til")) {
                vindu.leggetilstrengmsc();
            } else if (valg.equals("Fjerne")) {
                vindu.fjernestrengmsc();
            }
        }
        if (kilde.equals("multi")) {
            if (valg.equals("Legge til nynorsk")) {
        	vindu.addTerm(Sonjavindu.nn);
            } else if (valg.equals("Fjerne nynorsk")) {
                vindu.removeTerm(Sonjavindu.nn);
            } else if (valg.equals("Endre nynorsk")) {
                vindu.bestempreflabel("nynorsk");
            } else if (valg.equals("Legge til engelsk")) {
                vindu.addTerm(Sonjavindu.en);
            } else if (valg.equals("Fjerne engelsk")) {
                vindu.removeTerm(Sonjavindu.en);
            } else if (valg.equals("Endre engelsk")) {
                vindu.bestempreflabel("engelsk");
            } else if (valg.equals("Legge til latin")) {
                vindu.addTerm(Sonjavindu.la);
            } else if (valg.equals("Fjerne latin")) {
        	vindu.removeTerm(Sonjavindu.la);
            } else if (valg.equals("Endre latin")) {
                vindu.bestempreflabel("latin");
            } else if (valg.equals("Legge til forkortelse")) {
                vindu.leggetilakronym();
            } else if (valg.equals("Fjerne forkortelse")) {
                vindu.fjerneakronym();
            } else if (valg.equals("Legge til MSC")) {
                vindu.leggetilmsc();
            } else if (valg.equals("Fjerne MSC")) {
                vindu.fjernemsc();
            } else if (valg.equals("Legge til Dewey")) {
                vindu.leggetilddc();
            } else if (valg.equals("Fjerne Dewey")) {
                vindu.fjerneddc();
            }
//            vindu.fylltermskjema(vindu.currentTerm);
        }
    }

    static Term[] searchID(String sokebegrep, String listetype) {
        Term[] retval = null;
        ArrayList<Term> treff = new ArrayList<Term>();
        ArrayList<Term> sokliste;
        // det kan være MSC-søk da skal vi bare ha høyst ett treff

        // finner fram termer som oppfyller kriteriene
        // i relevant liste
        sokliste = velgliste(listetype);
        //System.out.println(listetype + " " + sokliste.size());
        for (int i = 0; i < sokliste.size(); i++) {
            Term t = sokliste.get(i);
            if (t != null) {
                if (t.minID.contains(sokebegrep)) {
                    treff.add(t);
                }
            }
        }
        // leter gjennom strengene
        //if (listetype.equals("term")) {
        if (Sonjavindu.strengesok) {
            for (int i = 0; i < strengliste.size(); i++) {
                Streng str = strengliste.get(i);
                if (str != null) {
                    if (str.minID.contains(sokebegrep)) {
                        treff.add(str);
                    }
                }
            }
        }

        //}
        // lager en array av trefflist som returverdi
        if (treff.size() > 0) {
            retval = new Term[treff.size()];
            for (int j = 0; j < treff.size(); j++) {
                retval[j] = treff.get(j);
            }
        }
        return retval;

    }

    static void norskbibkodeliste() {
        try {

            PrintWriter utfil = new PrintWriter(
                    new File(LATEXFOLDER + "norskebibkoder.txt"), "UTF-8");

            for (Term t : termliste) {
                if (t.bibkoder.size() > 0) {
                    utfil.println(t.term + "\t" + t.bibkodeliste());
                }
            }
            utfil.close();
        } catch (Exception exp) {

        }
    }

    static void norskbibkodeavgrensetlatex() {
        antallegne = 0;
        antallengelsk = 0;
        String kode = (String) JOptionPane.showInputDialog(null, "Oppgi bibkode");
        ArrayList<Bokord> ordliste = new ArrayList<Bokord>();
        // bygge opp ordlista
        for (Term t : termliste) {
            if (t.harbibkode(kode)) {
                ordliste.add(new Bokord(t.term, t.minID, "term"));
                if (t.synonymer.size() > 0) {
                    for (String syn : t.synonymer) {
                        ordliste.add(new Bokord(syn, t.minID, "bf"));
                    }
                }
            }
        }

        // lage array
        Bokord[] ordlistematrise = new Bokord[ordliste.size()];
        System.out.println("Antall ord:\t" + ordlistematrise.length);
        for (int i = 0; i < ordliste.size(); i++) {
            ordlistematrise[i] = ordliste.get(i);
        }

        // sortere den
        Arrays.sort(ordlistematrise);
        int antallit = 0;
        int antallbf = 0;
        // skrive ut
        try {

            PrintWriter utfil = new PrintWriter(
                    new File(LATEXFOLDER + "norbibkode" + kode + ".tex"), "UTF-8");
            utfil.print(latexprefiks1);
            utfil.print("\\title{Realfagstermer\\\\norske emneord og synonymer for bibkode: " + kode + "}\n");
            utfil.print(latexprefiks2);
            for (Bokord bo : ordlistematrise) {
                if (bo.type.equals("term")) {
                    utfil.print(bo.norbibkodelatex(kode));
                    antallit++;
                } else if (bo.type.equals("bf")) {
                    utfil.print(bo.norbibkodelatex(kode));
                    antallbf++;
                }
            }
            StringBuilder status = new StringBuilder("\n\\vspace{1cm}\n\\noindent{}\\textbf{Oppsummering}\\newline");
            status.append("\\noindent{}Antall termer: ")
                    .append(antallit)
                    .append("\\newline\n")
                    .append("Antall se-henvisninger: ")
                    .append(antallbf)
                    .append("\\newline\n")
                    .append("Antall egne: ")
                    .append(antallegne)
                    .append("\\newline\n")
                    .append("Antall engelsk: ")
                    .append(antallengelsk)
                    .append("\\newline\n");
            utfil.print(status.toString());
            utfil.print(latexpostfiks);
            utfil.close();
            //System.out.println("Antall: \t" + antallit + "\t" + antallbf);
        } catch (Exception exp) {
            System.out.println(exp.toString());
        }

    }

    static void importernynorsk() {
        // Stryk alt tidligere nynorsk
        /*
        for (Term t: termliste){
            t.slettnynorsk();
        }
         */
        //String grunnurl = "https://github.com/realfagstermer/prosjekt-nynorsk/blob/master/sonja.txt";
        String grunnurl = "http://folk.uio.no/knuthe/nynorsk/sonja3.txt";
        try {
            //System.out.println(grunnurl + "idtermer.txt");
            URL urldata = new URL(grunnurl);
            URLConnection con = (URLConnection) urldata.openConnection();

            BufferedReader innfil = new BufferedReader(
                    new InputStreamReader(urldata.openStream(), StandardCharsets.UTF_8));

            String forrige = "";
            int antall = 0;
            int prefantall = 0;
            int altantall = 0;
            String linje = innfil.readLine();
            while (linje != null) {
                if (linje.equals(forrige)) {
                    System.out.println("Dublett:" + linje);
                }
                String[] nynorsk = linje.split("\t");

//                System.out.println(
//                        "id= " + nynorsk[0]
//                        + "\tType= " + nynorsk[1]
//                        + "\tNynorsk= " + nynorsk[2]);
                Term t = Sonja.getTerm(nynorsk[0]);
                if (t != null) {
                    antall++;
                    if (nynorsk[1].equals("prefLabel")) {
                        t.addnynorskpref(nynorsk[2]);
                        prefantall++;
                    } else if (nynorsk[1].equals("altLabel")) {
                        t.addnynorskalt(nynorsk[2]);
                        altantall++;
                    }
                } else {
                    System.out.println("Ikke funnet ID: " + nynorsk[0]);
                }
                forrige = linje;
                linje = innfil.readLine();

            }
            innfil.close();
            Sonjavindu.melding("Ferdig", "Lest \n"
                    + "prefLabel:\t" + prefantall
                    + "altLabel:\t" + altantall
                    + "Totalt:\t" + antall);

        } catch (Exception exp) {
            System.out.println(exp.toString());
        }
    }

    static String renskforhtml(String t) {
        String retval = null;
        if (t.startsWith("<")) {
            int i = t.indexOf(">");
            if (i >= 0) {
                t = t.substring(i + 1);
                i = t.indexOf("<");
                if (i >= 0) {
                    t = t.substring(0, i);
                    retval = t;
                }
            }
        }
        return retval;
    }

    public static String getLanguage(Locale locale) {
	switch (locale.getLanguage()) {
	case "nb":
	    return "bokmål";
	case "nn":
	    return "nynorsk";
	case "en":
	    return "engelsk";
	case "la":
	    return "latin";
	default:
	    System.out.printf("Error: unknown language: '%s'\n", locale.getLanguage());
	    return "";
	}
    }
    
    static Locale getDefaultLanguage() {
	return defaultLanguage;
    }

}
