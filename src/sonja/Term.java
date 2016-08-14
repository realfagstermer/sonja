/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package sonja;

import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 *
 * @author knuthe
 */
public class Term implements Comparable {

    String term = null;
    String introdato = null;
    String endredato = null;
    String slettdato = null;
    //String akronym = null;
    String note = null;
    String definisjon = null;
    String signatur = null;
    String fokus = null;
    String type = null;
    //String msc = null;
    //String dewey = null;
    String minID = null;
    String flyttettilID = null;
    String lokalid = null;
    //String akroID = null;
    String otID = null;
    ArrayList<String> akronymer = new ArrayList<String>();
    ArrayList<String> bibkoder = new ArrayList<String>();
    ArrayList<String> engelsk = new ArrayList<String>();
    ArrayList<String> latin = new ArrayList<String>();
    ArrayList<String> nynorsk = new ArrayList<String>();
    ArrayList<String> overordnet = new ArrayList<String>();
    ArrayList<String> underordnet = new ArrayList<String>();
    ArrayList<String> seog = new ArrayList<String>();
    ArrayList<Streng> strenger = new ArrayList<Streng>();
    ArrayList<String> synonymer = new ArrayList<String>();
    ArrayList<String> msc = new ArrayList<String>();
    ArrayList<String> dewey = new ArrayList<String>();
    //String[] akroIDer;
    boolean somemne = false;
    int akrolinjer = 0;
    private int conceptId; //SQL primary key

    Term() {
    }

    Term(Term t) {
        //kopierer dataene fra t over til this
        term = t.term;
        type = t.type;
        msc = t.msc;
        dewey = t.dewey;
        somemne = t.somemne;
    }

    Term(String t) {
        if (t.startsWith("te=") || t.startsWith("da=")) {
            term = Sonja.storforbokstav(t.substring(3).trim());
        } else if (t.startsWith("id=")) {
            minID = t.substring(3).trim();
            lokalid = Sonja.fjernidprefiks(minID);
        } else {
            term = t;
        }
    }

    Term(String t, String bibkode) {
        term = Sonja.storforbokstav(t);
        bibkoder.add(bibkode);
    }

    public Term(int conceptId, int id, ConceptType type, String note, Timestamp created, Timestamp modified, Timestamp deprecated, String definition, int replaced_by) {
	this.conceptId = conceptId;
	minID = makeId(id);
	this.type = type.toString();
	lokalid = Sonja.fjernidprefiks(minID);
	nynote(note);
	introdato = created.toString();
	
	if (modified != null) {
	    endredato = modified.toString();
	}

	if (deprecated != null) {
	    slettdato = deprecated.toString();
	}
	definisjon = definition;
	flyttettilID = makeId(replaced_by);
    }

    public int compareTo(Object t) {
        Term tst = (Term) t;

        //String denne = term.toLowerCase();
        //String andre = tst.term.toLowerCase();
        String denne = this.toString().toLowerCase();
        String andre = tst.toString().toLowerCase();
        return denne.compareTo(andre);
    }

    public String toString() {
        if (Sonja.vokabular.equals("SMR") && engelsk.size() > 0) {
            return engelsk.get(0);
        } else {
            return term;
        }
    }

    public String utskriftforweb() {
        // skriver bare ut termer som ikke er slettet
        if (slettdato == null) {
            StringBuilder sb = new StringBuilder();
            sb.append("te= ");
            sb.append(term);
            sb.append("\n");
            for (int i = 0; i < synonymer.size(); i++) {
                sb.append("bf= ");
                sb.append(synonymer.get(i));
                sb.append("\n");
            }
            for (int i = 0; i < akronymer.size(); i++) {
                sb.append("ak= ");
                sb.append(akronymer.get(i));
                sb.append("\n");
            }
            for (int i = 0; i < seog.size(); i++) {
                sb.append("so= ");
                sb.append(Sonja.getTerm(seog.get(i)).term);
                sb.append("\n");
            }
            for (int i = 0; i < overordnet.size(); i++) {
                sb.append("ot= ");
                sb.append(Sonja.getTerm(overordnet.get(i)).term);
                sb.append("\n");
            }
            for (int i = 0; i < underordnet.size(); i++) {
                sb.append("ut= ");
                sb.append(Sonja.getTerm(underordnet.get(i)).term);
                sb.append("\n");
            }
            for (int i = 0; i < engelsk.size(); i++) {
                sb.append("en= ");
                sb.append(engelsk.get(i));
                sb.append("\n");
            }
            for (int i = 0; i < latin.size(); i++) {
                sb.append("la= ");
                sb.append(latin.get(i));
                sb.append("\n");
            }
            for (int i = 0; i < nynorsk.size(); i++) {
                sb.append("nn= ");
                sb.append(nynorsk.get(i));
                sb.append("\n");
            }
            if (note != null) {
                sb.append("no= ");
                sb.append(note);
                sb.append("\n");
            }
//            if (nynorsk != null) {
//                sb.append("nn= ");
//                sb.append(nynorsk);
//                sb.append("\n");
//            }
//            if (engelsk != null) {
//                sb.append("en= ");
//                sb.append(engelsk);
//                sb.append("\n");
//            }
            for (int i = 0; i < msc.size(); i++) {
                sb.append("ms= ");
                sb.append(msc.get(i));
                sb.append("\n");

            }

//            if (msc != null) {
//                sb.append("ms= ");
//                sb.append(msc);
//                sb.append("\n");
//            }
            for (int i = 0; i < dewey.size(); i++) {
                sb.append("dw= ");
                sb.append(dewey.get(i));
                sb.append("\n");

            }
//            if (dewey != null) {
//                sb.append("dw= ");
//                sb.append(dewey);
//                sb.append("\n");
//            }
////            if (latin != null) {
//                sb.append("la= ");
//                sb.append(latin);
//                sb.append("\n");
//            }
            if (definisjon != null) {
                String def = definisjon.replaceAll("<br>", " ");
                sb.append("de= ");
                sb.append(def);
                sb.append("\n");
            }
            return sb.toString() + "\n";
        } else {
            return null;
        }

    }

    /**
     * gir termen en id dersom den ikke har fra før og legger termen inn i en
     * tabell (HashMap) med ID som nøkkel
     */
    public void settID() {
        if (minID == null) {
            minID = Sonja.nesteID();
            lokalid = Sonja.fjernidprefiks(minID);
        }
        Sonja.id2term.put(minID, this);
    }

    /**
     * bytter ut termer med tilsvarende ID-er for Se-også termene
     */
    public void settsoID() {
        for (int i = 0; i < seog.size(); i++) {
            String s = Sonja.getID(seog.get(i));
            if (s != null) {
                seog.set(i, s);
            }
        }
    }

    /**
     * Metoder som tar i mot nye data til termen fra fil der dataene har den
     * generelle formen
     * <feltkode>= <data>
     * Hvilke feltkoder som behandles går fram av if-testene, men gjentas her ak
     * (akronym) ba (bibliotekkode) bf (brukt for-term) ddc dewey) dw (dewey) de
     * (definisjon) df (definisjon) en (engelsk) fo (fokus?) ft (forslagstype)
     * id (identifikator) la (latin) ms (MSC - mathematics subject
     * classification) nn (nynorsk) no (intern note) so (se-også-termer) tio
     * (dato opprettet) tie (dato endret) tis (dato slettet)
     *
     * Først en fordelermetode, Deretter inputmetode for hver enkelt feltkode
     */
    public void puttfelt(String linje) {
        if (linje != null) {
            String t = null;
            if (linje.length() > 3) {
                t = Sonja.storforbokstav(linje.substring(3).trim());
            }
            if (linje.startsWith("ak=")) {
                nyttakronym(t);
            }
            if (linje.startsWith("ba=")) {
                nybibkode(t.toLowerCase());
            }
            if (linje.startsWith("bf=")) {
                nyttsynonym(t);
            }
            if (linje.startsWith("da=")) {
                nyterm(t);
            }
            if (linje.startsWith("ddc=")) {
                nydw(linje.substring(4).trim());
            }

            if (linje.startsWith("de=")) {
                nydefinisjon(t);
            }
            if (linje.startsWith("df=")) {
                nydefinisjon(t);
            }
            if (linje.startsWith("dw=")) {
                nydw(linje.substring(3).trim());
            }
            if (linje.startsWith("en=")) {
                String[] enge = t.split(";");
                for (int i = 0; i < enge.length; i++) {
                    nyengelsk(Sonja.storforbokstav(enge[i].trim()));
                }
            }
            if (linje.startsWith("fo=")) {
                nyttfokus(t);
            }
            if (linje.startsWith("ft=")) {
                nyforslagstype(t);
            }
            if (linje.startsWith("id=")) {
                nyid(t);
            }
            if (linje.startsWith("la=")) {
                nylatin(t);
            }
            if (linje.startsWith("ms=")) {
                nymsc(linje.substring(3).trim());
            }
            if (linje.startsWith("nn=")) {
                String[] nyn = t.split(";");
                for (int i = 0; i < nyn.length; i++) {
                    nynynorsk(Sonja.storforbokstav(nyn[i].trim()));
                }
            }
            if (linje.startsWith("no=")) {
                nynote(t);
            }
            if (linje.startsWith("so=")) {
                nyseog(t);
            }
            if (linje.startsWith("ot=")) {
                nyoverordnet(t);
            }
            if (linje.startsWith("ut=")) {
                nyunderordnet(t);
            }
            if (linje.startsWith("te=")) {
                nyterm(t);
            }
            if (linje.startsWith("ty=")) {
                nytype(t);
            }
            if (linje.startsWith("tio=")) {
                nydato(linje.substring(4).trim());
            }
            if (linje.startsWith("tie=")) {
                endretdato(linje.substring(4).trim());
            }
            if (linje.startsWith("tis=")) {
                slettetdato(linje.substring(4).trim());
            }
            if (linje.startsWith("fly=")) {
                flyttettilID = linje.substring(4).trim();
            }

        }
    }

    public void nyttakronym(String s) {
        if (s != null && s.length() > 0) {
            boolean funnet = false;
            for (int i = 0; i < akronymer.size(); i++) {
                if (s.equals(akronymer.get(i))) {
                    funnet = true;
                    break;
                }
            }
            if (!funnet) {
                akronymer.add(s);
                Sonja.lagrelogg("Nytt akronym " + s + " til " + term);
            }
        }
    }

    public void nybibkode(String s) {
        if (s != null && s.length() > 0) {
            boolean funnet = false;
            for (int i = 0; i < bibkoder.size(); i++) {
                if (s.equalsIgnoreCase(bibkoder.get(i))) {
                    funnet = true;
                    break;
                }
            }
            if (!funnet) {
                bibkoder.add(s);
            }
        }
    }

    public void nydato(String d) {
        if (d != null && d.length() > 0) {
            introdato = fiksdato(d);
        }
    }

    public String fiksdato(String d) {
        String retval = null;
        if (d.length() == 8) {
            StringBuilder sb = new StringBuilder(d.substring(0, 4));
            sb.append("-")
                    .append(d.substring(4, 6))
                    .append("-")
                    .append(d.substring(6))
                    .append("T00:00:00Z");
            retval = sb.toString();
        } else {
            retval = d;
        }
        return retval;
    }

    public void endretdato(String d) {
        if (d != null && d.length() > 0) {
            endredato = fiksdato(d);
        }
    }

    public void endret() {
        String d = Sonja.fiksdato(new Date());
        if (d != null && d.length() > 0) {
            endredato = d;
        }
    }

    public void slettetdato(String d) {
        if (d != null && d.length() > 0) {
            slettdato = fiksdato(d);
            nullstill();
        }
    }

    public void nullstill() {
        //note = null;
        //definisjon = null;
        signatur = null;
        fokus = null;
        otID = null;
        akronymer = new ArrayList<String>();
        bibkoder = new ArrayList<String>();
        engelsk = new ArrayList<String>();
        latin = new ArrayList<String>();
        nynorsk = new ArrayList<String>();
        overordnet = new ArrayList<String>();
        underordnet = new ArrayList<String>();
        seog = new ArrayList<String>();
        strenger = new ArrayList<Streng>();
        synonymer = new ArrayList<String>();
        msc = new ArrayList<String>();
        dewey = new ArrayList<String>();
    }

    public void nydefinisjon(String s) {
        if (s != null && s.length() > 0) {
            definisjon = s;
            Sonja.lagrelogg("Ny definisjon " + s + " til " + term);
        }
    }

    public void nydw(String s) {
        if (s != null && s.length() > 0) {
            dewey.add(s);
            Sonja.lagrelogg("Ny dewey " + s + " til " + term);
        }

    }

    public void nyengelsk(String s) {
        if (s != null && s.length() > 0) {
            boolean funnet = false;
            for (int i = 0; i < engelsk.size(); i++) {
                if (s.equalsIgnoreCase(engelsk.get(i))) {
                    funnet = true;
                    break;
                }
            }
            if (!funnet) {
                engelsk.add(s);
                Sonja.lagrelogg("Ny engelsk " + s + " til " + term);
            }
        }

    }

    public void nyttfokus(String s) {

        if (s != null && s.length() > 0) {
            fokus = s.toLowerCase();
        } else {
            fokus = null;
        }
    }

    public void nyforslagstype(String d) {
        type = d;
    }

    public void nyid(String d) {
        minID = d;
        lokalid = Sonja.fjernidprefiks(minID);
    }

    public void nylatin(String s) {
        if (s != null && s.length() > 0) {
            boolean funnet = false;
            for (int i = 0; i < latin.size(); i++) {
                if (s.equalsIgnoreCase(latin.get(i))) {
                    funnet = true;
                    break;
                }
            }
            if (!funnet) {
                latin.add(s);

                Sonja.lagrelogg("Ny latin " + s + " til " + term);
            }
        }

    }

    public void nymsc(String s) {
        if (s != null && s.length() > 0) {
            msc.add(s);
            Sonja.lagrelogg("Ny MSC " + s + " til " + term);

        }

    }

    public void nynote(String s) {
        if (s != null && s.length() > 0) {
            note = s;
            Sonja.lagrelogg("Ny note " + s + " til " + term);

        }
    }

    public void nynynorsk(String s) {
        if (s != null && s.length() > 0) {
            boolean funnet = false;
            for (int i = 0; i < nynorsk.size(); i++) {
                if (s.equalsIgnoreCase(nynorsk.get(i))) {
                    funnet = true;
                    break;
                }
            }
            if (!funnet) {
                nynorsk.add(s);
                //Sonja.lagrelogg("Ny nynorsk " + s + " til " + term);

            }
        }

    }

    public void addnynorskpref(String s) {
        int funnetindeks = -1;
        if (s != null && s.length() > 0) {
            boolean funnet = false;
            for (int i = 0; i < nynorsk.size(); i++) {
                if (s.equalsIgnoreCase(nynorsk.get(i))) {
                    funnet = true;
                    funnetindeks = i;
                    break;
                }
            }
            if (!funnet) {
                nynorsk.add(0, s);
            } else {
                nynorsk.remove(funnetindeks);
                nynorsk.add(0, s);
            }
        }
    }

    public void addnynorskalt(String s) {
        if (s != null && s.length() > 0) {
            boolean funnet = false;
            for (int i = 0; i < nynorsk.size(); i++) {
                if (s.equalsIgnoreCase(nynorsk.get(i))) {
                    funnet = true;
                    break;
                }
            }
            if (!funnet) {
                nynorsk.add(s);
            }
        }
    }

    public void slettnynorsk() {
        nynorsk = new ArrayList<String>();
    }

    public void nysignatur(String s) {
        if (s != null && s.length() > 0) {
            signatur = s;
            Sonja.lagrelogg("Ny signatur " + s + " til " + term);

        }
    }

    public void nyseog(String s) {
        if (s != null && s.length() > 0) {
            if (!s.equals(minID)) {
                boolean funnet = false;
                for (int i = 0; i < seog.size(); i++) {
                    if (s.equalsIgnoreCase(seog.get(i))) {
                        funnet = true;
                        break;
                    }
                }
                if (!funnet) {
                    seog.add(s);
                    if (!Sonja.startup) {
                        Sonja.lagrelogg("Ny seogså fra  "
                                + term + " til " + Sonja.getTerm(s).term);
                    }

                }
            }
        }
    }

    public void nyoverordnet(String s) {
        if (s != null && s.length() > 0) {
            if (!s.equals(minID)) {
                boolean funnet = false;
                for (int i = 0; i < overordnet.size(); i++) {
                    if (s.equalsIgnoreCase(overordnet.get(i))) {
                        funnet = true;
                        break;
                    }
                }
                if (!funnet) {
                    overordnet.add(s);
                    if (!Sonja.startup) {
                        Sonja.lagrelogg("Ny overordnet "
                                + Sonja.getTerm(s).term
                                + " lagt til " + term);
                    }

                }
            }
        }
    }

    public void nyunderordnet(String s) {
        if (s != null && s.length() > 0) {
            if (!s.equals(minID)) {
                boolean funnet = false;
                for (int i = 0; i < underordnet.size(); i++) {
                    if (s.equalsIgnoreCase(underordnet.get(i))) {
                        funnet = true;
                        break;
                    }
                }
                if (!funnet) {
                    underordnet.add(s);
                    if (!Sonja.startup) {
                        Sonja.lagrelogg("Ny underordnet "
                                + Sonja.getTerm(s).term
                                + " lagt til " + term);
                    }
                }
            }
        }
    }

    public void nyttsynonym(String s) {
        if (s != null && s.length() > 0) {
            boolean funnet = false;
            for (int i = 0; i < synonymer.size(); i++) {
                if (s.equalsIgnoreCase(synonymer.get(i))) {
                    funnet = true;
                    break;
                }
            }
            if (!funnet) {
                synonymer.add(s);
                Sonja.lagrelogg("Ny sehenvisning " + s + " til " + term);

            }
        }

    }

    public void nytype(String s) {
        type = s;
    }

    public void nyterm(String s) {
        term = Sonja.storforbokstav(s);

    }

    public void addstreng(Streng str) {
        // her kan man differensiere i strenger med underemner. 
        // former, tider og steder ved å analysere strengen
        strenger.add(str);
    }

    /**
     * metoder som gir opplysninger om innholdet i termen
     */
    public int getantallbf() {
        return synonymer.size();
    }

    public int getantallso() {
        return seog.size();
    }

    public int getantallny() {
        return nynorsk.size();
    }

    public int getantallengelsk() {
        return engelsk.size();
    }

    public int getantalllatin() {
        return latin.size();
    }

    public boolean harlatin() {
        return latin.size() > 0;
    }

    public boolean harengelsk() {
        return engelsk.size() > 0;
    }
    
    public boolean harNynorsk() {
        return nynorsk.size() > 0;
    }

    public boolean harseog(String seogid) {
        boolean retval = false;
        for (int i = 0; i < seog.size(); i++) {
            if (seogid.equals(seog.get(i))) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    public boolean harot(String otid) {
        boolean retval = false;
        for (int i = 0; i < overordnet.size(); i++) {
            if (otid.equals(overordnet.get(i))) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    public boolean harut(String utid) {
        boolean retval = false;
        for (int i = 0; i < underordnet.size(); i++) {
            if (utid.equals(underordnet.get(i))) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    /**
     * Metode som leter i strengen etter søkebegrepet s som eventuelt kan
     * prefikses og/eller postfikses med tegnet =
     *
     * Søkesyntaks og semantikk:
     *
     * - =s= (matcher s eksakt) - =s (starter med s) - s= (slutter med s) - s
     * (inneholder s)
     */
    public boolean finn(String s) {
        boolean retval = false;
        s = s.toLowerCase();
        String te = Sonja.fjernaksenter(term.toLowerCase());
        // eksakt match
        if (s.startsWith("=") && s.endsWith("=")) {
            s = s.substring(1, s.length() - 1);
            // sjekker termen
            if (s.equalsIgnoreCase(te)) {
                return true;
            }
            // sjekker synonymene
            for (int i = 0; i < synonymer.size(); i++) {
                if (s.equalsIgnoreCase(synonymer.get(i))) {
                    return true;
                }
            }
            // sjekker akronym
            for (int i = 0; i < akronymer.size(); i++) {
                if (s.equalsIgnoreCase(akronymer.get(i))) {
                    return true;
                }
            }
            // sjekker engelsk
            for (int i = 0; i < engelsk.size(); i++) {
                if (s.equalsIgnoreCase(engelsk.get(i))) {
                    return true;
                }
            }
            // sjekker latin
            for (int i = 0; i < latin.size(); i++) {
                if (s.equalsIgnoreCase(latin.get(i))) {
                    return true;
                }
            }

            /*
             * STARTER med
             */
        } else if (s.startsWith("=")) {
            s = s.substring(1);
            // sjekker termen
            if (te.startsWith(s)) {
                return true;
            }
            // sjekker synonymene
            for (int i = 0; i < synonymer.size(); i++) {
                String syn = synonymer.get(i).toLowerCase();
                if ((syn.startsWith(s))) {
                    return true;
                }
            }
            // sjekker akronym
            for (int i = 0; i < akronymer.size(); i++) {
                String akr = akronymer.get(i).toLowerCase();
                if ((akr.startsWith(s))) {
                    return true;
                }
            }
            // sjekker engelsk
            for (int i = 0; i < engelsk.size(); i++) {
                String eng = engelsk.get(i).toLowerCase();
                if (eng.startsWith(s)) {
                    return true;
                }
            }
            // sjekker latin
            for (int i = 0; i < latin.size(); i++) {
                String lat = latin.get(i).toLowerCase();
                if (lat.startsWith(s)) {
                    return true;
                }
            }

            /* 
             * SLUTTER med
             */
        } else if (s.endsWith("=")) {
            s = s.substring(0, s.length() - 1);

            // sjekker termen
            if (te.endsWith(s)) {
                return true;
            }
            // sjekker synonymene
            for (int i = 0; i < synonymer.size(); i++) {
                String syn = synonymer.get(i).toLowerCase();
                if ((syn.endsWith(s))) {
                    return true;
                }
            }
            // sjekker akronym
            for (int i = 0; i < akronymer.size(); i++) {
                String akr = akronymer.get(i).toLowerCase();
                if ((akr.endsWith(s))) {
                    return true;
                }
            }
            // sjekker engelsk
            for (int i = 0; i < engelsk.size(); i++) {
                String eng = engelsk.get(i).toLowerCase();
                if (eng.endsWith(s)) {
                    return true;
                }
            }
            // sjekker latin
            for (int i = 0; i < latin.size(); i++) {
                String lat = latin.get(i).toLowerCase();
                if (lat.endsWith(s)) {
                    return true;
                }
            }
            /*
             * INNEHOLDER
             */

        } else {
            // sjekker termen
            if (te.contains(s)) {
                return true;
            }
            // sjekker synonymene
            for (int i = 0; i < synonymer.size(); i++) {
                String syn = synonymer.get(i).toLowerCase();
                if ((syn.contains(s))) {
                    return true;
                }
            }
            // sjekker akronym
            for (int i = 0; i < akronymer.size(); i++) {
                String akr = akronymer.get(i).toLowerCase();
                if ((akr.contains(s))) {
                    return true;
                }
            }
            // sjekker engelsk
            for (int i = 0; i < engelsk.size(); i++) {
                String eng = engelsk.get(i).toLowerCase();
                if (eng.contains(s)) {
                    return true;
                }
            }
            // sjekker latin
            for (int i = 0; i < latin.size(); i++) {
                String lat = latin.get(i).toLowerCase();
                if (lat.contains(s)) {
                    return true;
                }
            }
        }

        return retval;

    }

    /*
     * Søker etter eksakt MSC-kode, også i strenger
     */
    public boolean finnmsc(String s) {
        boolean retval = false;
        // sjekker oppslagsordets MSC
        for (String t : msc) {
            if (s.equals(t)) {
                retval = true;
                break;
            }
        }
        // sjekker evt streng sin MSC
        for (Streng str : strenger) {
            for (String t : str.msc) {
                if (s.equals(t)) {
                    retval = true;
                    break;
                }
            }
        }

        return retval;
    }

    /*
     * Diverse verdireturer
     */
    public String getoriasearchstring() {
        String retval = null;
        // if (!type.equals("streng")) {
        if (!(this instanceof Streng)) {
            retval = "&query=lsr20,exact," + term;
        } else {
            retval = ((Streng) this).oriasearchstring();
        }
        return retval;
    }

    public String getfokus() {
        return fokus;
    }

//    public String getsearchstring() {
//        String retval = null;
//        if (!type.equals("streng")) {
//            retval = "(bs.lokoeo-frase = \""
//                    + term
//                    + "\") ";
//        } else {
//            retval = ((Streng) this).searchstring();
//        }
//        return retval;
//    }
    public String getsearchstring() {
        String retval = null;
        // if (!type.equals("streng")) {
        if (!(this instanceof Streng)) {
            if (type.equals("term") || type.equals("tid")) {
                retval = "(bs.lokoeo-frase = \""
                        + term
                        + "\") ";
            } else if (type.equals("form")) {
                retval = "(bs.framst-form = \""
                        + term
                        + "\") ";
            } else if (type.equals("sted")) {
                retval = "(bs.geografisk-emneord = \""
                        + term
                        + "\") ";
            }
        } else {
            retval = ((Streng) this).searchstring();
        }
        return retval;
    }

    public String getsearchstringokapi() {
        String retval = null;
        // if (!type.equals("streng")) {
        if (!(this instanceof Streng)) {
            retval = term;
        } else {
            retval = ((Streng) this).searchstringokapi();
        }
        return retval;
    }

    /*
     * Diverse utskriftsmetoder
     */
    /**
     * Metode for å skrive ut termen med tilhørende relasjoner og andre data
     * knyttet til teksten Returnerer en redigert tekst med fortekster og data
     */
    public String kontrollutskrift() {
        // skriver ut alle opplysninger i kontrollvindu
        StringBuilder sb = new StringBuilder();

        sb.append(term).append(" (").append(minID).append(")");

        sb.append("\n");

        if (nynorsk.size() > 0) {
            StringBuilder sy = new StringBuilder("  NN ");
            if (!nynorsk.get(0).equals("")) {
                sy.append(nynorsk.get(0)).append("\n");
                for (int i = 1; i < nynorsk.size(); i++) {
                    sy.append("     ").append(nynorsk.get(i)).append("\n");
                }
                sb.append(sy.toString());
            }

        }

        if (latin.size() > 0) {
            StringBuilder sy = new StringBuilder("  LA ");
            if (!latin.get(0).equals("")) {
                sy.append(latin.get(0)).append("\n");
                for (int i = 1; i < latin.size(); i++) {
                    sy.append("     ").append(latin.get(i)).append("\n");
                }
                sb.append(sy.toString());
            }
        }

        if (note != null && !note.equals("")) {
            sb.append("  NO ").append(note).append("\n");
        }
        if (fokus != null && !fokus.equals("")) {
            sb.append("  FO ").append(fokus).append("\n");
        }

        if (definisjon != null && !definisjon.equals("")) {
            String[] delinjer = definisjon.split("<br>");
            sb.append("  DE ").append(delinjer[0]).append("\n");
            for (int i = 1; i < delinjer.length; i++) {
                sb.append("     ").append(delinjer[i]).append("\n");
            }
        }
        if (engelsk.size() > 0) {
            StringBuilder sy = new StringBuilder("  EN ");
            if (!engelsk.get(0).equals("")) {
                sy.append(engelsk.get(0)).append("\n");
                for (int i = 1; i < engelsk.size(); i++) {
                    sy.append("     ").append(engelsk.get(i)).append("\n");
                }
                sb.append(sy.toString());
            }

        }

        if (synonymer.size() > 0) {
            StringBuilder sy = new StringBuilder("  BF ");
            if (!synonymer.get(0).equals("")) {
                sy.append(synonymer.get(0));
                sy.append("\n");
                for (int i = 1; i < synonymer.size(); i++) {
                    sy.append("     ");
                    sy.append(synonymer.get(i));
                    sy.append("\n");
                }
                sb.append(sy.toString());
            }
        }

        if (akronymer.size() > 0) {
            StringBuilder sy = new StringBuilder("  AK ");
            if (!akronymer.get(0).equals("")) {
                sy.append(akronymer.get(0));
                sy.append("\n");
                for (int i = 1; i < akronymer.size(); i++) {
                    sy.append("     ");
                    sy.append(akronymer.get(i));
                    sy.append("\n");
                }
                sb.append(sy.toString());
            }
        }

        if (seog.size() > 0) {
            StringBuilder sy = new StringBuilder("  SO ");
            if (!seog.get(0).equals("")) {
                sy.append(Sonja.getTerm(seog.get(0)).term);
                sy.append("\n");
                for (int i = 1; i < seog.size(); i++) {
                    sy.append("     ");
                    sy.append(Sonja.getTerm(seog.get(i)));
                    sy.append("\n");
                }
                sb.append(sy.toString());
            }
        }

        if (overordnet.size() > 0) {
            StringBuilder sy = new StringBuilder("  OT ");
            if (!overordnet.get(0).equals("")) {
                sy.append(Sonja.getTerm(overordnet.get(0)).term);
                sy.append("\n");
                for (int i = 1; i < overordnet.size(); i++) {
                    sy.append("     ");
                    sy.append(Sonja.getTerm(overordnet.get(i)));
                    sy.append("\n");
                }
                sb.append(sy.toString());
            }
        }

        if (underordnet.size() > 0) {
            StringBuilder sy = new StringBuilder("  UT ");
            if (!underordnet.get(0).equals("")) {
                sy.append(Sonja.getTerm(underordnet.get(0)).term);
                sy.append("\n");
                for (int i = 1; i < underordnet.size(); i++) {
                    sy.append("     ");
                    sy.append(Sonja.getTerm(underordnet.get(i)));
                    sy.append("\n");
                }
                sb.append(sy.toString());
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

        if (bibkoder.size() > 0) {
            StringBuilder ba = new StringBuilder("  BA ");
            for (int i = 0; i < bibkoder.size(); i++) {
                ba.append(bibkoder.get(i));
                ba.append(" ");
            }
            ba.append("\n");
            sb.append(ba.toString());
        }

        if (type != null && !type.equals("")) {
            sb.append("  FT ").append(type).append("\n");
        }

        if (strenger.size() > 0) {
            StringBuilder st = new StringBuilder("  ST ");
            //st.append(strenger.get(0).minID).append("\n");
            st.append(strenger.get(0).toString()).append("\n");
            for (int i = 1; i < strenger.size(); i++) {
                //st.append("     ").append(strenger.get(i).minID).append("\n");
                st.append("     ").append(strenger.get(i).toString()).append("\n");
            }
            sb.append(st.toString());
        }
        if (introdato != null && !introdato.equals("")) {
            sb.append("  NY ").append(introdato).append("\n");
        }
        if (endredato != null && !endredato.equals("")) {
            sb.append("  CH ").append(endredato).append("\n");
        }
        if (slettdato != null && !slettdato.equals("")) {
            sb.append("  SL ").append(slettdato).append("\n");
        }

        return sb.toString() + "\n";
    }

    public String bibkodeliste() {
        StringBuilder sb = new StringBuilder("");
        for (int i = 0; i < bibkoder.size(); i++) {
            sb.append(bibkoder.get(i)).append(",");
        }
        String tmp = sb.toString();
        tmp = tmp.substring(0, tmp.length() - 1);
        return tmp;
    }

    /**
     * Skriver ut data som MARC-data med feltkode og vokabular-identifikator i
     * $2
     *
     * @return
     */
    public String marcutskrift(int marc) {
        boolean bibsys = false;
        if (marc == 1) {
            bibsys = true;
        }
        String linjeskift = "\n";
        String feltavrunding = linjeskift;
        if (bibsys) {
            linjeskift = "\t";
            feltavrunding = " $2 " + Sonja.vokabularID + linjeskift;
        }
        StringBuilder sb = new StringBuilder("");
        if (type.equals("sted")) {
            sb.append("651 ")
                    .append(term)
                    .append(feltavrunding);
        } else if (type.equals("form")) {
            String marctag = "655 ";
            if (somemne) {
                marctag = "687 ";
            }
            sb.append(marctag)
                    .append(term)
                    .append(feltavrunding);
        } else if (type.equals("tid")) {
            sb.append("648 ")
                    .append(term)
                    .append(feltavrunding);
            sb.append("687 ")
                    .append(term)
                    .append(feltavrunding);
        } else {
            sb.append("687 ")
                    .append(term)
                    .append(feltavrunding);
        }

        return sb.toString();
    }

    public String ddcmscutskrift(boolean bibsys) {
        String linjeskift = "\n";
        if (bibsys) {
            linjeskift = "\t";
        }
        StringBuilder sb = new StringBuilder("");
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

        return sb.toString();
    }

    public String filutskrift() {
        // genererer en tekststreng som kan brukes som lagring på fil
        // med feltkoder
        StringBuilder sb = new StringBuilder();
        sb.append("id= ").append(minID).append("\n");
        sb.append("te= ").append(term).append("\n");

        if (bibkoder.size() > 0) {
            for (int i = 0; i < bibkoder.size(); i++) {
                sb.append("ba= ").append(bibkoder.get(i)).append("\n");
            }
        }
        if (synonymer.size() > 0) {
            for (int i = 0; i < synonymer.size(); i++) {
                sb.append("bf= ").append(synonymer.get(i)).append("\n");
            }
        }
        if (akronymer.size() > 0) {
            for (int i = 0; i < akronymer.size(); i++) {
                sb.append("ak= ").append(akronymer.get(i)).append("\n");
            }
        }
        if (seog.size() > 0) {
            for (int i = 0; i < seog.size(); i++) {
                sb.append("so= ").append(seog.get(i)).append("\n");
            }
        }

        if (overordnet.size() > 0) {
            for (int i = 0; i < overordnet.size(); i++) {
                sb.append("ot= ").append(overordnet.get(i)).append("\n");
            }
        }

        if (underordnet.size() > 0) {
            for (int i = 0; i < underordnet.size(); i++) {
                sb.append("ut= ").append(underordnet.get(i)).append("\n");
            }
        }

        if (note != null && note.length() > 0) {
            sb.append("no= ").append(note).append("\n");
        }
        if (nynorsk.size() > 0) {
            for (int i = 0; i < nynorsk.size(); i++) {
                sb.append("nn= ").append(nynorsk.get(i)).append("\n");
            }
        }
        if (engelsk.size() > 0) {
            for (int i = 0; i < engelsk.size(); i++) {
                sb.append("en= ").append(engelsk.get(i)).append("\n");
            }
        }
        if (latin.size() > 0) {
            for (int i = 0; i < latin.size(); i++) {
                sb.append("la= ").append(latin.get(i)).append("\n");
            }
        }
        if (definisjon != null && definisjon.length() > 0) {
            sb.append("de= ").append(definisjon).append("\n");
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
            if (flyttettilID != null && flyttettilID.length() > 0) {
                sb.append("fly= ").append(flyttettilID).append("\n");
            }
        }
        if (strenger.size() > 0) {
            for (int i = 0; i < strenger.size(); i++) {
                sb.append("st= ").append(strenger.get(i).minID).append("\n");
            }
        }

        return sb.toString() + "\n";
    }
    
    /**
     * Export SQL data to file
     * 
     * @param concepts
     * @param terms
     * @param conceptType
     */
    public void toSQL(PrintWriter concepts, PrintWriter terms, String conceptType) {
	concepts.print("INSERT INTO concepts (external_id,vocab_id,concept_type,editorial_note, created, modified, deprecated, definition, used_by_libs) \nVALUES (");

	// Strip prefix
	int externalID = stripPrefix(minID);

	concepts.print(externalID + ",");
	concepts.print(makeSqlString(Sonja.vokabular));
	concepts.print(makeSqlString(conceptType));
	concepts.print(makeSqlString(note));
	concepts.print(makeSqlString(introdato));
	concepts.print(makeSqlString(endredato));
	concepts.print(makeSqlString(slettdato));
	concepts.print(makeSqlString(definisjon));

	if (bibkoder.size() > 0) {
	    concepts.print(quoteSQL(String.join(" ", bibkoder)));
	} else {
	    concepts.print("NULL");
	}

	// end insert concept
	concepts.println(");");

	// Because of dependencies, replaced_by must be updated after all concepts are imported
	if (flyttettilID != null) {
	    terms.printf("UPDATE concepts SET replaced_by = " + getConceptIdSql(flyttettilID) +
		    " WHERE concept_id= %s;\n", getConceptIdSql(minID));
	}

	// Preferred terms
	saveSQLTerm(terms, "preferred", term, "nb");

	for (String synonym : synonymer) {
	    saveSQLTerm(terms, "non_pref", synonym, "nb");
	}

	String[] languages = new String[] { "nb", "nn", "en" };
	@SuppressWarnings("unchecked")
	ArrayList<String>[] termLists = new ArrayList[] { synonymer, nynorsk, engelsk };

	for (String a : akronymer) {
	    for (int i = 0; i < languages.length; i++) {
		if (!(termLists[i].contains(a) || term.equals(a))) { // avoid duplicates
		    saveSQLTerm(terms, "non_pref", a, languages[i]);
		}
	    }
	}

	for (String id : seog) {
	    saveSQLRelation(terms, id, "related");
	}

	for (String id : overordnet) {
	    saveSQLRelation(terms, id, "broader");
	}

	// if (underordnet.size() > 0) {
	// for (int i = 0; i < underordnet.size(); i++) {
	// sb.append("ut= ").append(underordnet.get(i)).append("\n");
	// }
	// }

	for (int i = 0; i < nynorsk.size(); i++) {
	    saveSQLTerm(terms, (i == 0 ? "preferred" : "non_pref"), nynorsk.get(i), "nn");
	}

	for (int i = 0; i < engelsk.size(); i++) {
	    saveSQLTerm(terms, (i == 0 ? "preferred" : "non_pref"), engelsk.get(i), "en");
	}

	for (int i = 0; i < latin.size(); i++) {
	    saveSQLTerm(terms, (i == 0 ? "preferred" : "non_pref"), latin.get(i), "la");
	}

	for (String ms : msc) {
	    saveSQLMapping(terms, ms, "msc1970", "close");
	}

	for (String d : dewey) {
	    saveSQLMapping(terms, d, "ddc23", "close");
	}

	// if (strenger.size() > 0) {
	// for (int i = 0; i < strenger.size(); i++) {
	// sb.append("st= ").append(strenger.get(i).minID).append("\n");
	// }
	// }
    }
    
    protected Object getConceptIdSql(String ID) {
	return ID == null ? "NULL" : String.format("get_concept_id('%s', %d)", Sonja.vokabular, stripPrefix(ID));
    }

    private void saveSQLMapping(PrintWriter out, String targetConcept, String targetVocabulary, String mappingRelation) {
	out.printf("INSERT INTO mappings (source_concept_id, target_concept_id, target_vocabulary_id, mapping_relation) "
		+ "VALUES (%s, %s, '%s', '%s');\n", getConceptIdSql(minID), quoteSQL(targetConcept), targetVocabulary, mappingRelation);
    }

    private void saveSQLRelation(PrintWriter out, String external2, String type) {
	out.printf("INSERT INTO relationships (concept1, concept2, rel_type) VALUES (%s, %s, '%s');\n",
		getConceptIdSql(minID), getConceptIdSql(external2), type);
    }

    private void saveSQLTerm(PrintWriter out, String status, String term, String lang) {
	out.printf("INSERT INTO terms (concept_id,status,lexical_value,lang_id) VALUES (" +
		getConceptIdSql(minID) + ", " + makeSqlString(status) + makeSqlString(term) + quoteSQL(lang) + ");\n");
    }

    protected int stripPrefix(String ID) {
	return Integer.parseUnsignedInt(ID.substring(Sonja.vokabular.length()));
    }

    protected String makeSqlString(String s) {
	return (s == null ? "NULL" : quoteSQL(s)) + ",";
    }

    private String quoteSQL(String value) {
	return "'" + value.replace("'", "''") + "'";
    }

    public String rdfxml() {
        String rt = "http://data.ub.uio.no/realfagstermer/";
        // først bygges indekstermen opp
        StringBuilder sb = new StringBuilder("<skos:Concept rdf:about=\"");
        sb.append(rt);
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

        String topic = null;
        if (type.equalsIgnoreCase("term")) {
            topic = "Topic";
        } else if (type.equalsIgnoreCase("form")) {
            topic = "GenreForm";
        } else if (type.equalsIgnoreCase("tid")) {
            topic = "Temporal";
        } else if (type.equalsIgnoreCase("sted")) {
            topic = "Geographic";
        }
        sb.append("  <rdf:type rdf:resource=\"http://www.loc.gov/mads/rdf/v1#");
        sb.append(topic);
        sb.append("\"/>\n");
        sb.append("  <skos:inScheme rdf:resource=\"http://data.ub.uio.no/realfagstermer\"/>\n");

        // sb.append("  <rdf:type rdf:resource=\"http://www.w3.org/2004/02/skos/core#Concept\"/>\n");
        sb.append("  <skos:prefLabel xml:lang=\"nb\">");
        sb.append(Sonja.xmlEscapeText(Sonja.storforbokstav(term)));
        sb.append("</skos:prefLabel>\n");

        if (synonymer.size() > 0) {
            for (int i = 0; i < synonymer.size(); i++) {
                sb.append("  <skos:altLabel xml:lang=\"nb\">");
                sb.append(Sonja.xmlEscapeText(Sonja.storbokstav(synonymer.get(i))));
                sb.append("</skos:altLabel>\n");
            }
        }

        // de enkelte feltene
        if (nynorsk.size() > 0) {
            sb.append("  <skos:prefLabel xml:lang=\"nn\">");
            sb.append(Sonja.xmlEscapeText(Sonja.storforbokstav(nynorsk.get(0))));
            sb.append("</skos:prefLabel>\n");
            for (int i = 1; i < nynorsk.size(); i++) {
                sb.append("  <skos:altLabel xml:lang=\"nn\">");
                sb.append(Sonja.xmlEscapeText(Sonja.storforbokstav(nynorsk.get(i))));
                sb.append("</skos:altLabel>\n");
            }
        } else {
//            sb.append("  <skos:prefLabel xml:lang=\"nn\">");
//                sb.append(Sonja.storforbokstav(term));
//                sb.append("</skos:prefLabel>\n");
        }

        if (engelsk.size() > 0) {
            sb.append("  <skos:prefLabel xml:lang=\"en\">");
            sb.append(Sonja.xmlEscapeText(Sonja.storforbokstav(engelsk.get(0))));
            sb.append("</skos:prefLabel>\n");
            for (int i = 1; i < engelsk.size(); i++) {
                sb.append("  <skos:altLabel xml:lang=\"en\">");
                sb.append(Sonja.storforbokstav(engelsk.get(i)));
                sb.append("</skos:altLabel>\n");
            }

        }

        if (latin.size() > 0) {
            sb.append("  <skos:prefLabel xml:lang=\"la\">");
            sb.append(Sonja.xmlEscapeText(Sonja.storforbokstav(latin.get(0))));
            sb.append("</skos:prefLabel>\n");
            for (int i = 1; i < latin.size(); i++) {
                sb.append("  <skos:altLabel xml:lang=\"la\">");
                sb.append(Sonja.xmlEscapeText(Sonja.storforbokstav(latin.get(i))));
                sb.append("</skos:altLabel>\n");
            }
        }
        if (definisjon != null && definisjon.length() > 0) {
            sb.append("  <skos:definition xml:lang=\"nb\">");
            sb.append(Sonja.xmlEscapeText(definisjon));
            sb.append("</skos:definition>\n");
        }

        if (note != null && note.length() > 0) {
            sb.append("  <skos:editorialNote xml:lang=\"nb\">");
            sb.append(Sonja.xmlEscapeText(note));
            sb.append("</skos:editorialNote>\n");
        }

        if (akronymer.size() > 0) {
            for (int i = 0; i < akronymer.size(); i++) {
                sb.append("  <skos:altLabel>");
                sb.append(Sonja.xmlEscapeText(akronymer.get(i)));
                sb.append("</skos:altLabel>\n");
            }
        }

        if (seog.size() > 0) {
            for (int i = 0; i < seog.size(); i++) {
                sb.append("  <skos:related rdf:resource=\"");
                sb.append(rt);
                sb.append(Sonja.getTerm(seog.get(i)).lokalid);
                sb.append("\"/>\n");
            }
        }

        if (overordnet.size() > 0) {
            for (int i = 0; i < overordnet.size(); i++) {
                sb.append("  <skos:broader rdf:resource=\"");
                sb.append(rt);
                sb.append(Sonja.getTerm(overordnet.get(i)).lokalid);
                sb.append("\"/>\n");
            }
        }

        if (underordnet.size() > 0) {
            for (int i = 0; i < underordnet.size(); i++) {
                sb.append("  <skos:narrower rdf:resource=\"");
                sb.append(rt);
                sb.append(Sonja.getTerm(underordnet.get(i)).lokalid);
                sb.append("\"/>\n");
            }
        }

        if (dewey.size() > 0) {
            for (int i = 0; i < dewey.size(); i++) {
                sb.append("  <dcterms:DDC>");
                sb.append(Sonja.xmlEscapeText(dewey.get(i)));
                sb.append("</dcterms:DDC>\n");
            }
        }

        // eventuell overordnet (viss det er et bygg)
        if (otID != null && otID.length() > 0) {
            sb.append("  <skos:broader rdf:resource=\"");
            sb.append(rt);
            sb.append(otID);
            sb.append("\"/>\n");
        }

        // eventuell liste over underordnete byggid-liste
        if (strenger.size() > 0) {
            for (int i = 0; i < strenger.size(); i++) {
                sb.append("  <skos:narrower rdf:resource=\"");
                sb.append(rt);
                sb.append(strenger.get(i).lokalid);
                sb.append("\"/>\n");
            }
        }
        /* venter på avklaring av behandling av MSC
         if (msc != null && msc.length() > 0) {
         sb.append("  <dcterms:msc>");
         sb.append(msc);
         sb.append("</dcterms:msc>\n");
         }
         *
         */
        sb.append("</skos:Concept>\n\n");

        /*
         * så må bruktfortermene legges inn som egne entiteter med henvisning
         * til denne termen via dens ID (stemmer ikke med SKOS-definisjonen
         */
 /*
         if (synonymer.size() > 0) {
         for (int i = 0; i < synonymer.size(); i++) {
         sb.append("<skos:Concept rdf:about=\"#");
         sb.append(Main.finnID(synonymer.get(i)));
         sb.append("\">\n");

         sb.append("  <rdf:type rdf:resource=\"http://www.w3.org/2004/02/skos/core#Concept\" />\n");
         sb.append("  <skos:altLabel xml:lang=\"no\">");
         sb.append(synonymer.get(i));
         sb.append("</skos:altLabel>\n");
         sb.append("  <skos:prefLabel rdf:resource=\"#");
         sb.append(minID);
         sb.append("\" />\n</skos:Concept>\n\n");
         }
         }

         if (akronymer.size() > 0) {
         for (int i = 0; i < akronymer.size(); i++) {
         sb.append("<skos:Concept rdf:about=\"#");
         sb.append(akroIDer[i]);
         sb.append("\">\n");

         sb.append("  <rdf:type rdf:resource=\"http://www.w3.org/2004/02/skos/core#Concept\" />\n");
         sb.append("  <skos:altLabel lang=\"en\">");
         sb.append(akronymer.get(i));
         sb.append("</skos:altLabel>\n");
         sb.append("  <skos:prefLabel rdf:resource=\"#");
         sb.append(minID);
         sb.append("\" />\n</skos:Concept>\n\n");
         }
         }

         *
         */
        String retur = sb.toString();
        // retur = retur.replace("<br>", " ");
        return retur;
    }

    public String csv() {
        // først bygges indekstermen opp
        String kolskille = "@";
        String radskille = ";";
        StringBuilder sb = new StringBuilder(minID).append(kolskille);
        sb.append(term).append(kolskille);

        if (synonymer.size() > 0) {
            for (int i = 0; i < synonymer.size(); i++) {
                sb.append(Sonja.storbokstav(synonymer.get(i))).append(radskille);
            }
        }
        sb.append(kolskille);

        if (engelsk.size() > 0) {
            for (int i = 0; i < engelsk.size(); i++) {
                sb.append(Sonja.storforbokstav(engelsk.get(i))).append(radskille);
            }
        }
        sb.append(kolskille);
        /*
         if (latin.size() > 0) {
         for (int i = 0; i < latin.size(); i++) {
         sb.append(Sonja.storforbokstav(latin.get(i))).append(radskille);
         }
         }
         sb.append(kolskille);

         if (akronymer.size() > 0) {
         for (int i = 0; i < akronymer.size(); i++) {
         sb.append(akronymer.get(i)).append(radskille);
         }
         }
         sb.append(kolskille);
         */
 /*
         if (seog.size() > 0) {
         for (int i = 0; i < seog.size(); i++) {
         sb.append("  <skos:related rdf:resource=\"#");
         sb.append(seog.get(i));
         sb.append("\"/>\n");
         }
         }

         if (dewey != null && dewey.length() > 0) {
         sb.append("  <dcterms:DDC>");
         sb.append(dewey);
         sb.append("</dcterms:DDC>\n");
         }
         // eventuell overordnet (viss det er et bygg)
         if (otID != null && otID.length() > 0) {
         sb.append("  <skos:broader rdf:resource=\"#");
         sb.append(otID);
         sb.append("\"/>\n");
         }

         // eventuell liste over underordnete byggid-liste
         if (strenger.size() > 0) {
         for (int i = 0; i < strenger.size(); i++) {
         sb.append("  <skos:narrower rdf:resource=\"#");
         sb.append(strenger.get(i).minID);
         sb.append("\"/>\n");
         }
         }

         sb.append("</skos:Concept>\n\n");

         */
        sb.append("\n");
        String retur = sb.toString();
        return retur;
    }

    public String akronymoppslag(int i) {
        int bmlinjer = 1;
        int nnlinjer = 1;
        StringBuilder sb = new StringBuilder(akronymer.get(i))
                .append("&\\textbf{" + term + "}{\\footnotesize");
        for (int j = 0; j < synonymer.size(); j++){
            sb.append("\\newline ").
                    append(synonymer.get(j));
            bmlinjer++;
        }
        sb.append("}&\\textbf{")
                .append(nynorsk.get(0))
                .append("}{\\footnotesize ");
        for (int j = 1; j < nynorsk.size(); j++) {
            sb.append("\\newline ")
                    .append(nynorsk.get(j));
            nnlinjer++;
        }
        sb.append("}");
        String retval = sb.toString();
        retval = Sonja.replace(retval, "#", "\\#");
        akrolinjer = Math.max(bmlinjer,nnlinjer);
        return retval;
    }

    public String csvsmr() {
        // først bygges indekstermen opp
        String kolskille = "\t ";
        String radskille = "\n";
        StringBuilder sb = new StringBuilder(minID).append(kolskille);
        if (engelsk.size() > 0) {
            sb.append(engelsk.get(0)).append(kolskille).append(radskille);
            if (engelsk.size() > 1) {
                for (int i = 1; i < engelsk.size() - 1; i++) {
                    sb.append(minID)
                            .append(kolskille)
                            .append(kolskille)
                            .append(engelsk.get(i))
                            .append(radskille);
                }
            }
        } else {
            sb.append(term).append(kolskille).append(radskille);
        }
        sb.append("\n");
        String retur = sb.toString();
        return retur;
    }

    public void fjernstreng(String sID) {
        for (int i = 0; i < strenger.size(); i++) {
            if (sID.equals(strenger.get(i).minID)) {
                String st = strenger.get(i).toStringplain();
                strenger.remove(i);
                Sonja.vindu.addtologg("RM STRN " + sID + " FRM " + minID, false);
                Sonja.lagrelogg("fjernet streng " + st + " fra " + term);
                //strenger.trimToSize();
                break;
            }
        }
    }

    public void fjernso(String soID) {
        for (int i = 0; i < seog.size(); i++) {
            if (soID.equals(seog.get(i))) {
                seog.remove(i);
                //Sonja.vindu.addtologg("RM SEOG " + soID + " FRM " + minID, false);
                String so = Sonja.getTerm(soID).term;
                Sonja.lagrelogg("fjernet seogså " + so + " fra " + term);
                break;
            }
        }
    }

    public void fjernot(String otID) {
        for (int i = 0; i < overordnet.size(); i++) {
            if (otID.equals(overordnet.get(i))) {
                overordnet.remove(i);
                //Sonja.vindu.addtologg("RM SEOG " + soID + " FRM " + minID, false);
                String ot = Sonja.getTerm(otID).term;
                Sonja.lagrelogg("fjernet overordnet term " + ot + " fra " + term);
                break;
            }
        }
    }

    public void fjernut(String utID) {
        for (int i = 0; i < underordnet.size(); i++) {
            if (utID.equals(underordnet.get(i))) {
                underordnet.remove(i);
                //Sonja.vindu.addtologg("RM SEOG " + soID + " FRM " + minID, false);
                String ut = Sonja.getTerm(utID).term;
                Sonja.lagrelogg("fjernet underordnet term " + ut + " fra " + term);
                break;
            }
        }
    }

    public void fjernsehenvisning(String str) {
        for (int i = 0; i < synonymer.size(); i++) {
            if (str.equalsIgnoreCase(synonymer.get(i))) {
                synonymer.remove(i);
                Sonja.vindu.addtologg("RM SE " + str + " FRM " + minID, false);
                Sonja.lagrelogg("fjernet sehenvisning " + str + " fra " + term);
                break;
            }
        }

    }

    public void fjernengelsk(String str) {
        for (int i = 0; i < engelsk.size(); i++) {
            if (str.equalsIgnoreCase(engelsk.get(i))) {
                engelsk.remove(i);
                Sonja.vindu.addtologg("RM EN " + str + " FRM " + minID, false);
                Sonja.lagrelogg("fjernet engelsk " + str + " fra " + term);
                break;
            }
        }

    }

    public void fjernmsc(String str) {
        for (int i = 0; i < msc.size(); i++) {
            if (str.equalsIgnoreCase(msc.get(i))) {
                msc.remove(i);
                // Sonja.vindu.addtologg("RM EN " + str + " FRM " + minID, false);
                Sonja.lagrelogg("fjernet msc " + str + " fra " + term);
                break;
            }
        }

    }

    public void fjernddc(String str) {
        for (int i = 0; i < dewey.size(); i++) {
            if (str.equalsIgnoreCase(dewey.get(i))) {
                dewey.remove(i);
                //Sonja.vindu.addtologg("RM EN " + str + " FRM " + minID, false);
                Sonja.lagrelogg("fjernet DDC " + str + " fra " + term);
                break;
            }
        }

    }

    public void fjernnynorsk(String str) {
        for (int i = 0; i < nynorsk.size(); i++) {
            if (str.equalsIgnoreCase(nynorsk.get(i))) {
                nynorsk.remove(i);
                Sonja.vindu.addtologg("RM NN " + str + " FRM " + minID, false);
                Sonja.lagrelogg("fjernet nynorsk " + str + " fra " + term);
                break;
            }
        }

    }

    public void fjernlatin(String str) {
        for (int i = 0; i < latin.size(); i++) {
            if (str.equalsIgnoreCase(latin.get(i))) {
                latin.remove(i);
                Sonja.vindu.addtologg("RM LA " + str + " FRM " + minID, false);
                Sonja.lagrelogg("fjernet latin " + str + " fra " + term);
                break;
            }
        }

    }

    public void fjernakronym(String str) {
        for (int i = 0; i < akronymer.size(); i++) {
            if (str.equalsIgnoreCase(akronymer.get(i))) {
                akronymer.remove(i);
                Sonja.vindu.addtologg("RM AK " + str + " FRM " + minID, false);
                Sonja.lagrelogg("fjernet akronym " + str + " fra " + term);
                break;
            }
        }

    }

    void byttseogid(String minIDfra, String minIDtil) {
        for (String id : seog) {
            if (id.equals(minIDfra)) {
                // legg til den nye (dublett unngås)
                nyseog(minIDtil);
                // fjern den gamle
                fjernso(minIDfra);
                break;
            }
        }

    }

    public void fjernduplikatstrenger() {
        if (strenger.size() > 1) {
            ArrayList<Streng> fjernes = new ArrayList<Streng>();

            for (int i = 0; i < strenger.size() - 1; i++) {
                Streng tmp = strenger.get(i);
                for (int j = i + 1; j < strenger.size(); j++) {
                    if (tmp.erlik(strenger.get(j))) {
                        fjernes.add(strenger.get(j));
                    }
                }
            }

            for (int i = 0; i < fjernes.size(); i++) {
                //strenger.remove(fjernes.get(i));
                fjernstreng(fjernes.get(i).minID);
                Sonja.slettstreng(fjernes.get(i));
            }
        }
    }

    public boolean harbibkode(String kode) {
        boolean retval = false;
        for (String bk : bibkoder) {
            if (bk.equalsIgnoreCase(kode)) {
                retval = true;
                break;
            }
        }
        return retval;
    }

    void initTermsSql(PreparedStatement terms) {
	try {
	    terms.setInt(1, conceptId);
	    
	    try (ResultSet results = terms.executeQuery()) {
		while (results.next()) {
		    String label = results.getString("lexical_value");
		    String status = results.getString("status").trim();
		    final String lang = results.getString("lang_id").trim();
		    
		    switch (lang) {
		    case "nb":
			if ("preferred".equals(status)) {
			    term = label;
			} else {
			    // nyttsynonym(label);
			    synonymer.add(label);
			}
			break;
		    case "nn":
			nynorsk.add(label);
			break;
		    case "en":
			engelsk.add(label);
			break;
		    case "la":
			latin.add(label);
			break;
		    default:
			System.out.printf("Error: unknown language: '%s'\n", lang);
		    }
		}
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public List<String> getTerms(Locale lang) {
	switch (lang.getLanguage()) {
	case "nb":
	    return synonymer;
	case "nn":
	    return nynorsk;
	case "en":
	    return engelsk;
	case "la":
	    return latin;
	default:
	    System.out.printf("Error: unknown language: '%s'\n", lang.getLanguage());
	    return new ArrayList<>();
	}
    }
    
    static String makeId(final int id) {
	int padding = (Sonja.vokabular.equals("REAL") ? 6 : 5);// todo: fetch from database, support ujur
	return String.format("%s%0" + padding + "d", Sonja.vokabular, id);
    }

    public int getConceptId() {
        return conceptId;
    }

    public boolean isPreferred(String lexicalValue, Locale lang) {
	return getTerms(lang).indexOf(lexicalValue) == 0;
    }

}
