/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sonja;

import java.util.ArrayList;

/**
 *
 * @author knuthe
 */
public class Streng extends Term {

    String minID = null;
    String lokalid = null;
    String almaid = null;
    String da = null;
    String db = null;
    String dc = null;
    String form = null;
    String tid = null;
    String sted = null;
    //String msc = null;
    //String dewey = null;
    //String dato = null;
//    String introdato = null;
//    String endredato = null;
//    String slettdato = null;
    String strengtype = null;
    String bruker = "";
    Term topp = null;
    // bruktfor er langt fra implementert, spesielt ikke i søk
    // men i kontrollutskrift, filutskrift og web-utskrift, men ikke
    // xml-utskrift
    ArrayList<String> bruktfor = new ArrayList<String>();
    ArrayList<String> msc = new ArrayList<String>();
    ArrayList<String> dewey = new ArrayList<String>();

    /*
     * initiering av data i de forskjellige feltene,
     * noen av dem ligger i Term
     */
    public void addterm(String t) {
        da = Sonja.storforbokstav(t.trim());
    }

    public void addalmaid(String id) {
        almaid = id;
    }

    public void addunderterm(String t) {
        if (t != null) {
            if (db == null) {
                if (!da.equalsIgnoreCase(t)) {
                    db = Sonja.storforbokstav(t);
                }
            } else {
                addkvalifikator(t);
            }
        }
    }

    public void addkvalifikator(String t) {
        if (t != null) {
            if (!da.equalsIgnoreCase(t)
                    && !db.equalsIgnoreCase(t)) {
                dc = Sonja.storforbokstav(t);
            }
        }
    }

    public void addID(String t) {
        minID = t;
        lokalid = Sonja.fjernidprefiks(minID);
    }

    public void addform(String t) {
        form = Sonja.storforbokstav(t);
    }

    public void addtid(String t) {
        tid = Sonja.storforbokstav(t);
    }

    public void addsted(String t) {
        sted = Sonja.storforbokstav(t);
    }

    public void addmsc(String t) {
        msc.add(t);
    }

    public void adddewey(String t) {
        dewey.add(t);
    }

    public void addtopp(Term t) {
        topp = t;
    }

    public void addtype(String s) {
        strengtype = s;
    }

    public void addbruker(String s) {
        bruker = s;
    }

    public void addbruktfor(String s) {
        if (s != null) {
            bruktfor.add(s);
        }
    }

    public void settID() {
        if (minID == null) {
            minID = Sonja.nesteID();
            lokalid = Sonja.fjernidprefiks(minID);
        }
        // TODO må gjøre av strengen
//        Sonja.id2term.put(minID, this);
    }

    public void fjernbruktfor(String bf) {
        for (int i = 0; i < bruktfor.size(); i++) {
            if (bf.equalsIgnoreCase(bruktfor.get(i))) {
                bruktfor.remove(i);
                // Sonja.vindu.addtologg("RM EN " + str + " FRM " + minID, false);
                Sonja.lagrelogg("fjernet brukt for " + bf + " fra " + toString());
                break;
            }
        }

    }

    public void fjernmsc(String str) {
        for (int i = 0; i < msc.size(); i++) {
            if (str.equalsIgnoreCase(msc.get(i))) {
                msc.remove(i);
                // Sonja.vindu.addtologg("RM EN " + str + " FRM " + minID, false);
                Sonja.lagrelogg("fjernet msc " + str + " fra " + toString());
                break;
            }
        }

    }

    public void fjernddc(String str) {
        for (int i = 0; i < dewey.size(); i++) {
            if (str.equalsIgnoreCase(dewey.get(i))) {
                dewey.remove(i);
                //Sonja.vindu.addtologg("RM EN " + str + " FRM " + minID, false);
                Sonja.lagrelogg("fjernet DDC " + str + " fra " + toString());
                break;
            }
        }

    }


    /*
     * Utskriftsmetoder
     */
    public String oriasearchstring() {
        StringBuilder sb = new StringBuilder("&query=lsr20,exact,");
        String termlokal = Sonja.getTerm(da).term;
        sb.append(termlokal);
        //sb.append(" ");

        if (db != null) {
            sb.append("&query=lsr20,exact,");
            sb.append(Sonja.getTerm(db));
        }

        if (form != null) {
            sb.append("&query=sub,exact,");
            sb.append(Sonja.getTerm(form));
            //sb.append(" ");
        }
        if (tid != null) {
            sb.append("&query=sub,exact,");
            sb.append(Sonja.getTerm(tid));
            //sb.append(" ");
        }
        if (sted != null) {
            sb.append("&query=lsr17,exact,");
            sb.append(Sonja.getTerm(sted));
            //sb.append(" ");
        }

        String resultat = sb.toString();
        return resultat;

    }

    /**
     *
     * @return returner en kolonredigert streng
     */
    public String toString() {
        StringBuilder sb = new StringBuilder();
        StringBuilder debug = new StringBuilder();
        String tmp = null;
        if (da != null) {
            //System.out.println(term);
            Term dum = Sonja.getTerm(da);
            if (dum != null) {
                tmp = dum.term;
            }
            sb.append(tmp);
            debug.append(tmp);
        }
        if (db != null) {
            sb.append(" : ").append(Sonja.getTerm(db).term);
            debug.append(" : ").append(db);
        }
        if (dc != null) {
            sb.append(" : ").append(Sonja.getTerm(dc).term);
            debug.append(" : ").append(dc);
        }

        if (sted != null) {
            sb.append(" : ").append(Sonja.getTerm(sted).term);
            debug.append(" 1: ").append(sted);
        }
        if (tid != null) {
            sb.append(" : ").append(Sonja.getTerm(tid).term);
            debug.append(" 8: ").append(tid);
        }
        if (form != null) {
            sb.append(" : ").append(Sonja.getTerm(form).term);
            debug.append(" 5: ").append(form);
        }
        return sb.toString();
    }

    /**
     *
     * @return returner en kolonredigert streng
     */
    public String vrengtax() {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        if (db != null) {
            sb.append(Sonja.getTerm(db).term);
        }
        if (da != null) {
            sb.append(" : ").append(Sonja.getTerm(da).term);
        }
        if (dc != null) {
            sb.append(" : ").append(Sonja.getTerm(dc).term);
        }

        if (sted != null) {
            sb.append(" : ").append(Sonja.getTerm(sted).term);
        }
        if (tid != null) {
            sb.append(" : ").append(Sonja.getTerm(tid).term);
        }
        if (form != null) {
            sb.append(" : ").append(Sonja.getTerm(form).term);
        }
        return sb.toString();
    }

    public boolean finnmsc(String s) {
        boolean retval = false;
        // sjekker oppslagsordets MSC
        if (msc.size() > 0) {
            for (int i = 0; i < msc.size(); i++) {

                if (s.equals(msc.get(i))) {
                    retval = true;
                    break;
                }
            }
        }
        return retval;
    }

    public String searchstring() {
        StringBuilder sb = new StringBuilder("(bs.lokoeo-frase= \"");
        String termlokal = Sonja.getTerm(da).term;
        sb.append(termlokal);
        sb.append("\") ");

        if (db != null) {
            sb.append("AND (bs.lokoeo-frase = \"");
            sb.append(Sonja.getTerm(db));
            sb.append("\") ");
        }
        if (dc != null) {
            sb.append("AND (bs.lokoeo-frase = \"");
            sb.append(Sonja.getTerm(dc));
            sb.append("\") ");
        }
        if (form != null) {
            sb.append("AND (bs.framst-form = \"");
            sb.append(Sonja.getTerm(form));
            sb.append("\") ");
        }
        if (tid != null) {
            sb.append("AND (bs.lokoeo-frase = \"");
            sb.append(Sonja.getTerm(tid));
            sb.append("\") ");
        }
        if (sted != null) {
            sb.append("AND (bs.geografisk-emneord = \"");
            sb.append(Sonja.getTerm(sted));
            sb.append("\") ");
        }

        String resultat = sb.toString();
        if (resultat.equals(termlokal)) {
            return null;
        } else {
            return resultat;
        }
    }

    public String toStringplain() {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        if (da != null) {
            //System.out.println(term);
            Term dum = Sonja.getTerm(da);
            if (dum != null) {
                tmp = dum.term;
            }
            sb.append(tmp);
        }
        if (db != null) {
            sb.append(" : ").append(Sonja.getTerm(db));
        }
        if (dc != null) {
            sb.append(" : ").append(dc);
        }
        if (sted != null) {
            sb.append(" : ").append(Sonja.getTerm(sted));
        }
        if (tid != null) {
            sb.append(" : ").append(Sonja.getTerm(tid));
        }
        if (form != null) {
            sb.append(" : ").append(Sonja.getTerm(form));
        }
        return sb.toString();
    }

    public String toStringekte() {
        StringBuilder sb = new StringBuilder();
        String tmp = null;
        if (da != null) {
            //System.out.println(term);
            Term dum = Sonja.getTerm(da);
            if (dum != null) {
                tmp = dum.term;
            }
            sb.append(tmp);
        }
        if (db != null) {
            sb.append(" : ").append(Sonja.getTerm(db));
            if (sted != null) {
                sb.append(" : ").append(Sonja.getTerm(sted));
            }
            if (tid != null) {
                sb.append(" : ").append(Sonja.getTerm(tid));
            }
            if (form != null) {
                sb.append(" : ").append(Sonja.getTerm(form));
            }
        }

        return sb.toString();
    }

    public String searchstringokapi() {
        StringBuilder sb = new StringBuilder("");
        String termlokal = Sonja.getTerm(da).term;
        sb.append(termlokal);
        //sb.append(" ");

        if (db != null) {
            sb.append("--");
            sb.append(Sonja.getTerm(db));
            //sb.append(" ");
        }
        if (dc != null) {
            sb.append("--");
            sb.append(Sonja.getTerm(dc));
            //sb.append(" ");
        }
        if (form != null) {
            sb.append("--");
            sb.append(Sonja.getTerm(form));
            //sb.append(" ");
        }
        if (tid != null) {
            sb.append("--");
            sb.append(Sonja.getTerm(tid));
            //sb.append(" ");
        }
        if (sted != null) {
            sb.append("--");
            sb.append(Sonja.getTerm(sted));
            //sb.append(" ");
        }

        String resultat = sb.toString();
        if (resultat.equals(termlokal)) {
            return null;
        } else {
            return resultat;
        }
    }

    public String kontrollutskrift() {
        StringBuilder sb = new StringBuilder(toString()).append(" (").append(minID).append(")");
        if (bruktfor.size() > 0) {
            sb.append(" BF ")
                    .append(bruktfor.get(0))
                    .append("\n");
            if (bruktfor.size() > 1) {
                for (int i = 1; i < bruktfor.size(); i++) {
                    sb.append("    ")
                            .append(bruktfor.get(i))
                            .append("\n");
                }
            }
        }
        if (msc.size() > 0) {
            sb.append(" MSC ").append(msc.get(0)).append("\n");
            if (msc.size() > 1) {
                for (int i = 1; i < msc.size(); i++) {
                    sb.append("     ").append(msc.get(i)).append("\n");
                }
            }
        }
        if (dewey.size() > 0) {
            sb.append(" DDC ").append(dewey.get(0)).append("\n");
            if (dewey.size() > 1) {
                for (int i = 1; i < dewey.size(); i++) {
                    sb.append("     ").append(dewey.get(i)).append("\n");
                }
            }
        }
//        if ((msc != null) && !msc.equals("")) {
//            sb.append("\n")
//                    .append(" MSC ")
//                    .append(msc)
//                    .append("\n");
//        }
//        if (dewey != null && !dewey.equals("")) {
//            sb.append(" DDC ");
//            sb.append(dewey);
//            sb.append("\n");
//        }

        return sb.toString();
    }

    /**
     *
     * @return returnerer MARC-felter i tråd med valgte data
     */
    public String marcutskrift(int marc) {
        boolean bibsys = false;
        if (marc == 1) {
            bibsys = true;
        }
        StringBuilder sb = new StringBuilder("");
        String feltkode = null;
        String linjeskift = "\n";
        String feltavrunding = linjeskift;

        if (bibsys) {
            linjeskift = "\t";
            feltavrunding = " $2 " + Sonja.vokabularID + linjeskift;
        }

        if (strengtype.equals("form")) {
            feltkode = "655 ";
        } else if (strengtype.equals("tid")) {
            feltkode = "648 ";
        } else if (strengtype.equals("sted")) {
            feltkode = "651 ";
        } else {
            feltkode = "687 ";
        }

        // generer marcfelt med delfeltkoder
        sb.append(feltkode).append(Sonja.getTerm(da));
        if (db != null) {
            sb.append(" $b ").append(Sonja.getTerm(db));
        }
        if (dc != null) {
            sb.append(" $c ").append(Sonja.getTerm(dc));
        }
        sb.append(feltavrunding);

        // former, tider og steder legges i egne felt
        if (form != null) {
            sb.append("655 ")
                    .append(Sonja.getTerm(form))
                    .append(feltavrunding);

        }
        // tider legges både i 687 og 648 på grunn av søkbarhet
        if (tid != null) {
            sb.append("687 ")
                    .append(Sonja.getTerm(tid))
                    .append(feltavrunding);
            sb.append("648 ")
                    .append(Sonja.getTerm(tid))
                    .append(feltavrunding);
        }
        if (sted != null) {
            sb.append("651 ")
                    .append(Sonja.getTerm(sted))
                    .append(feltavrunding);
        }

        // dewey og msc legges i sine respektive felt
        if (dewey.size() > 0) {
            sb.append("082 ")
                    .append(dewey.get(0));
            if (dewey.size() > 1) {
                for (int i = 1; i < dewey.size(); i++) {
                    sb.append(" $b ")
                            .append(dewey.get(i));
                }

            }
            sb.append(" $2 DDC-23")
                    .append(linjeskift);
        }
        if (msc.size() > 0) {
            sb.append("084 ")
                    .append(msc.get(0));
            if (msc.size() > 1) {
                for (int i = 1; i < msc.size(); i++) {
                    sb.append(" $b ")
                            .append(msc.get(i));
                }
            }
            sb.append(linjeskift);
        }

//        if (dewey != null && !dewey.equals("")) {
//            sb.append("082 ")
//                    .append(dewey)
//                    .append(" $2 DDC-23")
//                    .append(linjeskift);
//        }
//        if ((msc != null) && !msc.equals("")) {
//            sb.append("084 ")
//                    .append(msc)
//                    .append(" $2 msc ")
//                    .append(linjeskift);
//        }
        return sb.toString();
    }

    public String filutskrift() {
        // genererer en tekststreng som kan brukes som lagring på fil
        // med feltkoder
        StringBuilder sb = new StringBuilder();
        sb.append("id= ").append(minID).append("\n");
        sb.append("da= ").append(da).append("\n");
        //sb.append("ty= ").append("687 ").append("\n");
        //sb.append("to= ").append(topp.minID).append("\n");

        if (db != null && db.length() > 0) {
            sb.append("db= ").append(db).append("\n");
        }
        if (dc != null && dc.length() > 0) {
            sb.append("dc= ").append(dc).append("\n");
        }
        if (form != null && form.length() > 0) {
            sb.append("dx= ").append(form).append("\n");
        }
        if (tid != null && tid.length() > 0) {
            sb.append("dy= ").append(tid).append("\n");
        }
        if (sted != null && sted.length() > 0) {
            sb.append("dz= ").append(sted).append("\n");
        }
        if (bruktfor.size() > 0) {
            for (int i = 0; i < bruktfor.size(); i++) {
                sb.append("bf= ")
                        .append(bruktfor.get(i))
                        .append("\n");
            }
        }

        if (msc.size() > 0) {
            for (int i = 0; i < msc.size(); i++) {
                sb.append("ms= ").append(msc.get(i)).append("\n");
            }
        }
//        if (msc != null && msc.length() > 0) {
//            sb.append("ms= ").append(msc).append("\n");
//        }
        if (dewey.size() > 0) {
            for (int i = 0; i < dewey.size(); i++) {
                sb.append("dw= ").append(dewey.get(i)).append("\n");
            }
        }
//        if (dewey != null && dewey.length() > 0) {
//            sb.append("dw= ").append(dewey).append("\n");
//        }
        if (introdato != null && introdato.length() > 0) {
            sb.append("tio= ").append(introdato).append("\n");
        }
        if (endredato != null && endredato.length() > 0) {
            sb.append("tie= ").append(endredato).append("\n");
        }
        if (slettdato != null && slettdato.length() > 0) {
            sb.append("tis= ").append(slettdato).append("\n");
        }
        if (bruker != null && bruker.length() > 0) {
            sb.append("fa= ").append(bruker).append("\n");
        }

        if (almaid != null && almaid.length() > 0) {
            sb.append("al= ").append(almaid).append("\n");
        }

        return sb.toString() + "\n";
    }

    public String filutskrift3() {
        StringBuilder sb = new StringBuilder("");
        if (db != null) {
            sb.append("db= ");
            sb.append(Sonja.getTerm(db).term);
            sb.append("\n");
        }
        if (dc != null) {
            sb.append("dc= ");
            sb.append(Sonja.getTerm(dc).term);
            sb.append("\n");
        }
        if (form != null) {
            sb.append("dx= ");
            sb.append(Sonja.getTerm(form).term);
            sb.append("\n");
        }
        if (tid != null) {
            sb.append("dy= ");
            sb.append(Sonja.getTerm(tid).term);
            sb.append("\n");
        }
        if (sted != null) {
            sb.append("dz= ");
            sb.append(Sonja.getTerm(sted).term);
            sb.append("\n");
        }
        if (bruktfor.size() > 0) {
            for (int i = 0; i < bruktfor.size(); i++) {
                sb.append("bf= ")
                        .append(bruktfor.get(i))
                        .append("\n");
            }
        }
        if (msc.size() > 0) {
            for (int i = 0; i < msc.size(); i++) {
                sb.append("ms= ").append(msc.get(i)).append("\n");
            }
        }

//        if (msc != null) {
//            sb.append("ms= ");
//            sb.append(msc);
//            sb.append("\n");
//        }
        if (dewey.size() > 0) {
            for (int i = 0; i < dewey.size(); i++) {
                sb.append("dw= ").append(dewey.get(i)).append("\n");
            }
        }
//        if (dewey != null) {
//            sb.append("dw= ");
//            sb.append(dewey);
//            sb.append("\n");
//        }

        String resultat = sb.toString();
        if (resultat.equals(da)) {
            return null;
        } else {
            return resultat + "\n";
        }
    }

    public String filutskriftkyrre(String startledd) {
        String st = "";
        if (!startledd.equals("")) {
            st = "te= " + startledd + "\n";
        }
        StringBuilder sb = new StringBuilder(st);
        sb.append(filutskrift3());
        return sb.toString();
    }

    /**
     * Metode for å finne strenger som tilfredsstiller søkebegrepet s. Søket tar
     * ikke hensyn til store/små bokstaver.
     *
     * Funn er avhengig av valgt listetype , det søkes bare i relevante deler av
     * strengen etter følgende logikk
     *
     * Listetype delfelter som søkes ------------------------------ term term,
     * db, dc form form tid tid sted sted
     */
    public boolean finndel(String s, String listetype) {
        boolean retval = false;
        s = s.toLowerCase();
        String sterm = null;
        if (!strengok()) {
            System.out.println("finndel:\t" + toStringplain());
        }
        if (da != null) {
            sterm = Sonja.fjernaksenter(Sonja.getTerm(da).term.toLowerCase());
        }
        String sdb = null;
        if (db != null) {
            sdb = Sonja.fjernaksenter(Sonja.getTerm(db).term.toLowerCase());
        }
        String sdc = null;
        if (dc != null) {
            sdc = Sonja.fjernaksenter(Sonja.getTerm(dc).term.toLowerCase());
        }
        String sform = null;
        if (form != null) {
            sform = Sonja.fjernaksenter(Sonja.getTerm(form).term.toLowerCase());
        }
        String stid = null;
        if (tid != null) {
            stid = Sonja.fjernaksenter(Sonja.getTerm(tid).term.toLowerCase());
        }
        String ssted = null;
        if (sted != null) {
            ssted = Sonja.fjernaksenter(Sonja.getTerm(sted).term.toLowerCase());
        }

        // eksakt match
        if (s.startsWith("=") && s.endsWith("=")) {
            s = s.substring(1, s.length() - 1);
            // sjekker termen
            if (listetype.equals("term")) {
                if (sterm != null) {
                    if (s.equalsIgnoreCase(sterm)) {
                        return true;
                    }
                }
                // sjekker underemne

                if (db != null) {
                    if (s.equalsIgnoreCase(sdb)) {
                        return true;
                    }
                }
                // sjekker kvalifikator
                if (dc != null) {
                    if (s.equalsIgnoreCase(sdc)) {
                        return true;
                    }
                }
            }
            // sjekker form
            if (listetype.equals("form")) {
                if (form != null) {
                    if (s.equalsIgnoreCase(sform)) {
                        return true;

                    }
                }
            }
            // sjekker tid
            if (listetype.equals("tid")) {
                if (tid != null) {
                    if (s.equalsIgnoreCase(stid)) {
                        return true;
                    }
                }
            }
            // sjekker sted
            if (listetype.equals("sted")) {
                if (sted != null) {
                    if (s.equalsIgnoreCase(ssted)) {
                        return true;
                    }
                }
            }
            /*
             * STARTER med
             */
        } else if (s.startsWith("=")) {
            s = s.substring(1);
            // sjekker termen
            if (listetype.equals("term")) {
                if (sterm != null) {
                    if (sterm.startsWith(s)) {
                        return true;
                    }
                }
                // sjekker underemne
                if (db != null) {
                    if ((sdb.startsWith(s))) {
                        return true;
                    }
                }
                // sjekker kvalifikator
                if (dc != null) {
                    if ((sdc.startsWith(s))) {
                        return true;
                    }
                }
            }
            // sjekker form
            if (listetype.equals("form")) {
                if (form != null) {
                    if (sform.startsWith(s)) {
                        return true;
                    }
                }
            }
            // sjekker tid
            if (listetype.equals("tid")) {
                if (stid != null && stid.startsWith(s)) {
                    return true;
                }
            }
            // sjekker sted
            if (listetype.equals("sted")) {
                if (ssted != null && ssted.startsWith(s)) {
                    return true;
                }
            }
            // slutter med
        } else if (s.endsWith("=")) {
            s = s.substring(0, s.length() - 1);
            // sjekker termen
            if (listetype.equals("term")) {
                if (sterm != null && sterm.endsWith(s)) {
                    return true;
                }
                // sjekker underemne
                if (sdb != null && sdb.endsWith(s)) {
                    return true;
                }
                // sjekker kvalifikator
                if (sdc != null && sdc.endsWith(s)) {
                    return true;
                }
            }
            // sjekker form
            if (listetype.equals("form")) {
                if (sform != null && sform.endsWith(s)) {
                    return true;
                }
            }
            // sjekker tid
            if (listetype.equals("tid")) {
                if (stid != null && stid.endsWith(s)) {
                    return true;
                }
            }
            // sjekker sted
            if (listetype.equals("sted")) {
                if (ssted != null && ssted.endsWith(s)) {
                    return true;
                }
            }
            /*
             * INNEHOLDER
             */
        } else {
            // sjekker termen
            if (listetype.equals("term")) {
                if (sterm != null && sterm.contains(s)) {
                    return true;
                }
                // sjekker underemne
                if (sdb != null && sdb.contains(s)) {
                    return true;
                }
                // sjekker kvalifikator
                if (sdc != null && sdc.contains(s)) {
                    return true;
                }
            }
            // sjekker form
            if (listetype.equals("form")) {
                if (sform != null && sform.contains(s)) {
                    return true;
                }
            }
            // sjekker tid
            if (listetype.equals("tid")) {
                if (stid != null && stid.contains(s)) {
                    return true;
                }
            }
            // sjekker sted
            if (listetype.equals("sted")) {
                if (ssted != null && ssted.contains(s)) {
                    return true;
                }
            }
        }
        return retval;
    }

    /*
     * Sjekker at leddene i strengen bare består av identifikatorer 
     * som starter med "REAL" etterfulgt av 6 siffer
     */
    public boolean strengok() {
        boolean retval = false;
        if (leddok(da)
                && leddok(db)
                && leddok(dc)
                && leddok(form)
                && leddok(tid)
                && leddok(sted)) {
            retval = true;
        } else {
            System.out.println(minID + " " + da);
        }
        return retval;
    }

    public boolean leddok(String l) {
        boolean retval = false;
        if (l != null) {
            if (l.length() == 10) {
                if (l.startsWith("REAL")) {
                    String tmp = l.substring(4);
                    try {
                        int i = Integer.parseInt(tmp);
                        retval = true;
                    } catch (Exception exp) {
                    }
                }
            }
        } else {
            retval = true;
        }
        return retval;
    }

    //returner ID til første ledd i strengen
    public String firstledd() {
        String first = null;
        if (da != null && da.length() > 0) {
            first = da;
        } else if (db != null && db.length() > 0) {
            first = db;
        } else if (sted != null && sted.length() > 0) {
            first = sted;
        } else if (tid != null && tid.length() > 0) {
            first = tid;
        } else if (form != null && form.length() > 0) {
            first = form;
        }
        return first;
    }

    public String byggledd(String skilletekst) {
        StringBuilder sb = new StringBuilder();
        //String skilletekst = " : ";
        boolean first = false;
        if (da != null && da.length() > 0) {
            sb.append(Sonja.getTerm(da));
            first = true;
        }
        if (db != null && db.length() > 0) {
            if (first) {
                sb.append(skilletekst);
                sb.append(Sonja.getTerm(db));
            } else {
                sb.append(Sonja.getTerm(db));
                first = true;
            }
        }
        if (sted != null && sted.length() > 0) {
            if (first) {
                sb.append(skilletekst);
                sb.append(Sonja.getTerm(sted));
            } else {
                sb.append(Sonja.getTerm(sted));
                first = true;
            }
        }
        if (tid != null && tid.length() > 0) {
            if (first) {
                sb.append(skilletekst);
                sb.append(Sonja.getTerm(tid));
            } else {
                sb.append(Sonja.getTerm(tid));
                first = true;
            }
        }
        if (form != null && form.length() > 0) {
            if (first) {
                sb.append(skilletekst);
                sb.append(Sonja.getTerm(form));
            } else {
                sb.append(Sonja.getTerm(form));
                first = true;
            }
        }
        return sb.toString();

    }

    public String logg() {
        StringBuilder sb = new StringBuilder("id=");
        sb.append(minID);

        if (da != null) {
            sb.append(";da=").append(da);
        }
        if (db != null) {
            sb.append(";db=").append(db);
        }
        if (sted != null) {
            sb.append(";dz=").append(sted);
        }
        if (tid != null) {
            sb.append(";dy=").append(tid);
        }
        if (form != null) {
            sb.append(";dx=").append(form);
        }
        return sb.toString();
    }

    public boolean meddb() {
        boolean retval = false;
        if (db != null && db.length() > 0) {
            retval = true;
        }
        return retval;
    }

    public String forsteledd() {
        String retval = null;

        if (da != null) {
            retval = da;
        } else if (db != null) {
            retval = db;
        } else if (sted != null) {
            retval = sted;
        } else if (tid != null) {
            retval = tid;
        } else if (form != null) {
            retval = form;
        }
        return retval;
    }

    public String rdfxml() {
        String otid = null;
        if (Sonja.idprefiks.length() > 0) {
            otid = "c" + firstledd().substring(Sonja.idprefiks.length());
        }
        String rt = "http://data.ub.uio.no/realfagstermer/";
//        String skilletegn = "--";
//        String skilletegn = " - ";
        String skilletegn = " : ";
//        sb.append(minID);

        StringBuilder sb = new StringBuilder("<skos:Concept rdf:about=\"http://data.ub.uio.no/realfagstermer/");
        sb.append(lokalid);
        sb.append("\">\n");

        sb.append("  <dcterms:identifier>");
        sb.append(minID);
        sb.append("</dcterms:identifier>\n");

        if (introdato != null) {
            sb.append("  <dcterms:created rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">");
            sb.append(introdato);
            sb.append("</dcterms:created>\n");
        }

        if (endredato != null) {
            sb.append("  <dcterms:modified rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">");
            sb.append(endredato);
            sb.append("</dcterms:modified>\n");
        } else if (introdato != null) {
            sb.append("  <dcterms:modified rdf:datatype=\"http://www.w3.org/2001/XMLSchema#dateTime\">");
            sb.append(introdato);
            sb.append("</dcterms:modified>\n");
        }

        sb.append("  <rdf:type rdf:resource=\"http://www.loc.gov/mads/rdf/v1#ComplexSubject\"/>\n");
        sb.append("  <skos:inScheme rdf:resource=\"http://data.ub.uio.no/realfagstermer\"/>\n");
        //sb.append("  <rdf:type rdf:resource=\"http://www.w3.org/2004/02/skos/core#Concept\"/>\n");
        sb.append("  <skos:prefLabel xml:lang=\"nb\">");
        sb.append(byggledd(skilletegn));
        sb.append("</skos:prefLabel>\n");
        sb.append("  <skos:broader rdf:resource=\"");
        sb.append(rt);
        sb.append(otid);
        sb.append("\"/>\n");
        sb.append("</skos:Concept>\n\n");
        return sb.toString();
    }

    void byttid(String minIDfra, String minIDtil) {
        if (da != null && da.equals(minIDfra)) {
            da = minIDtil;
        }
        if (db != null && db.equals(minIDfra)) {
            db = minIDtil;
        }
        if (dc != null && dc.equals(minIDfra)) {
            dc = minIDtil;
        }
        if (form != null && form.equals(minIDfra)) {
            form = minIDtil;
        }
        if (tid != null && tid.equals(minIDfra)) {
            tid = minIDtil;
        }
        if (sted != null && sted.equals(minIDfra)) {
            sted = minIDtil;
        }
    }

    boolean harID(String minID) {
        boolean retval = false;
        if (da != null && da.equals(minID)) {
            retval = true;
        }
        if (db != null && db.equals(minID)) {
            retval = true;
        }
        if (dc != null && dc.equals(minID)) {
            retval = true;
        }
        if (form != null && form.equals(minID)) {
            retval = true;
        }
        if (tid != null && tid.equals(minID)) {
            retval = true;
        }
        if (sted != null && sted.equals(minID)) {
            retval = true;
        }
        return retval;
    }

    boolean erlik(Streng strengbygg) {
        boolean retval = false;
        boolean dalik = false;
        boolean dblik = false;
        boolean stedlik = false;
        boolean tidlik = false;
        boolean formlik = false;

        if (da == null && strengbygg.da == null) {
            dalik = true;
        } else if (da != null && strengbygg.da != null && da.equals(strengbygg.da)) {
            dalik = true;
        }
        if (db == null && strengbygg.db == null) {
            dblik = true;
        } else if (db != null && strengbygg.db != null && db.equals(strengbygg.db)) {
            dblik = true;
        }
        if (sted == null && strengbygg.sted == null) {
            stedlik = true;
        } else if (sted != null && strengbygg.sted != null && sted.equals(strengbygg.sted)) {
            stedlik = true;
        }
        if (tid == null && strengbygg.tid == null) {
            tidlik = true;
        } else if (tid != null && strengbygg.tid != null && tid.equals(strengbygg.tid)) {
            tidlik = true;
        }
        if (form == null && strengbygg.form == null) {
            formlik = true;
        } else if (form != null && strengbygg.form != null && form.equals(strengbygg.form)) {
            formlik = true;
        }
        // oppsummering
        if (dalik && dblik && stedlik && tidlik && formlik) {
            retval = true;
        }

        return retval;
    }
}
