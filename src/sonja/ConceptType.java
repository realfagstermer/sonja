package sonja;

/**
 * Valid concepts types, and their English translations as used
 * in the SQL schema
 * 
 * @author ewinge
 */
public enum ConceptType {
    term(English.general), form(English.form), tid(English.time), sted(English.place);

    final English english;

    ConceptType(English eng) {
	english = eng;
    }

    public static ConceptType fromEnglish(String s) {
	switch (English.valueOf(s)) {
	case form:
	    return form;
	case general:
	    return term;
	case place:
	    return sted;
	case time:
	    return tid;
	default:
	    throw new IllegalArgumentException("Unknown type: " + s);
	}
    }

    public static String toEnglish(String str) {
	return valueOf(str).english.toString();
    }

    static enum English {
	general, form, time, place;

    }
}