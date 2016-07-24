/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sonja;

/**
 *
 * @author knuthe
 */
public class Bokord implements Comparable {

    String oppslag = null;
    String type = null;
    String id = null;
    int akrolinjer = 0;

    Bokord(String mittoppslag, String minID, String mintype) {
        oppslag = mittoppslag;
        id = minID;
        type = mintype;
    }

    public int compareTo(Object b) {
        Bokord tst = (Bokord) b;
        String denne = this.toString().toLowerCase();
        String denandre = tst.toString().toLowerCase();
        return denne.compareTo(denandre);
    }

    public String toString() {
        return oppslag;
    }

    public String latex() {
        StringBuilder sb = new StringBuilder("\\startoppslag\n");
        if (type.equals("term")
                || type.equals("streng")
                || type.equals("form")
                || type.equals("tid")
                || type.equals("sted")) {
            sb.append("\\oppslag{").append(oppslag).append("}\n");
        } else {
            sb.append("\\henvisning{")
                    .append(oppslag).append("}{")
                    .append(Sonja.getTerm(id).term)
                    .append("}\n");
        }
        return sb.toString();
    }

    public String englatex() {
        StringBuilder sb = new StringBuilder("\\startoppslag\n");
        if (type.equals("term")) {
            sb.append("\\engnor{")
                    .append(oppslag)
                    .append("}{")
                    .append(Sonja.getTerm(id).term)
                    .append("}\n");
        } else {
            sb.append("\\henvisning{")
                    .append(oppslag).append("}{")
                    .append(Sonja.getTerm(id).engelsk.get(0))
                    .append("}\n");
        }
        return sb.toString();

    }

    public String norenglatex() {
        StringBuilder sb = new StringBuilder("\\startoppslag\n");
        if (type.equals("term")) {
            sb.append("\\noreng{")
                    .append(oppslag)
                    .append("}{")
                    .append(Sonja.getTerm(id).engelsk.get(0))
                    .append("}\n");
        } else {
            sb.append("\\henvisning{")
                    .append(oppslag).append("}{")
                    .append(Sonja.getTerm(id).engelsk.get(0))
                    .append("}\n");
        }
        return sb.toString();
    }

    public String norutenenglatex() {
        StringBuilder sb = new StringBuilder("\\startoppslag\n");
        if (type.equals("term")) {
            sb.append("\\noruteneng{")
                    .append(oppslag)
                    .append("}\n");
        }
        return sb.toString();
    }

    public String norbfenglatex() {
        StringBuilder sb = new StringBuilder("\\startoppslag\n");
        if (type.equals("term")) {
            Term t = Sonja.getTerm(id);
            String english = " ";

            if (t.engelsk.size() > 0) {
                english = Sonja.getTerm(id).engelsk.get(0);
            }
            String stjerne = "";
            if (Sonja.getTerm(id).msc != null) {
                stjerne = "* ";
            }
            sb.append("\\noreng{")
                    .append(stjerne)
                    .append(oppslag)
                    .append("}{")
                    .append(english)
                    .append("}\n");

        } else if (type.equals("bf")) {
            sb.append("\\henvisning{")
                    .append(oppslag).append("}{")
                    .append(Sonja.getTerm(id).term)
                    .append("}\n");
        }
        return sb.toString();
    }

    public String norbibkodelatex(String kode) {
        StringBuilder sb = new StringBuilder("\\startoppslag\n");
        if (type.equals("term")) {
            Term t = Sonja.getTerm(id);
            String english = " ";

            if (t.engelsk.size() > 0) {
                english = Sonja.getTerm(id).engelsk.get(0);
                Sonja.antallengelsk++;
            }
            String stjerne = "";
            if (Sonja.getTerm(id).msc != null) {
                stjerne = "* ";
            }
            sb.append("\\norengkode{")
                    .append(stjerne)
                    .append(oppslag)
                    .append("}{")
                    .append(english)
                    .append("}");
            if (t.bibkoder.size() > 1) {
                sb.append("{");
                for (String bk : t.bibkoder) {
                    if (!bk.equalsIgnoreCase(kode)) {
                        sb.append(bk).append(", ");
                    }
                }
                sb.append("}");
            } else {
                Sonja.antallegne++;
            }
            sb.append("\n");

        } else if (type.equals("bf")) {
            sb.append("\\henvisning{")
                    .append(oppslag).append("}{")
                    .append(Sonja.getTerm(id).term)
                    .append("}\n");
        }
        return sb.toString();
    }
}
