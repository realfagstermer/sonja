package sonja;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

import static sonja.TermStatus.*;

/**
 * class for handling database connections
 * 
 * @author ewinge
 */
public class Database implements AutoCloseable {
    private final Connection connection;

    Database() throws SQLException {
	connection = DriverManager.getConnection(Sonja.config.getProperty("jdbc.url"), Sonja.config);
    }

    @Override
    public void close() throws SQLException {
	if (connection != null) {
	    connection.close();
	}
    }

    public Term insertConcept(String vokabular, String type, String lexicalValue) throws SQLException {
	try (PreparedStatement statement = connection.prepareStatement("INSERT INTO concepts (vocab_id,concept_type) VALUES (?,?) RETURNING *");) {
	    statement.setString(1, Sonja.vokabular);
	    statement.setString(2, type);

	    connection.setAutoCommit(false);// concept and term must both be committed

	    try (ResultSet inserted = statement.executeQuery();) {
		if (inserted.next()) {
		    final Term term = getTerm(inserted);
		    addTerm(term, lexicalValue, TermStatus.preferred, Sonja.getDefaultLanguage());
		    term.term = lexicalValue;
		    connection.commit();
		    return term;
		} else {
		    throw new SQLException("insert concept failed, no Term obtained.");
		}
	    } finally {
		connection.setAutoCommit(true);
	    }
	}
    }

    public void setPreferred(Term concept, String from, String to, Locale locale) throws SQLException {
	connection.setAutoCommit(false);

	try (PreparedStatement statement = connection
		.prepareStatement("UPDATE terms SET status = ? WHERE concept_id = ? AND lang_id = ? AND lexical_value = ?;")) {
	    // Set new preferred
	    statement.setString(1, preferred.toString());
	    statement.setInt(2, concept.getConceptId());
	    statement.setString(3, locale.getLanguage());
	    statement.setString(4, to);
	    statement.executeUpdate();

	    // Set the non_pref
	    statement.setString(1, non_pref.toString());
	    statement.setString(4, from);
	    statement.executeUpdate();
	    updateModified(concept);
	    connection.commit();
	} finally {
	    connection.setAutoCommit(true);
	}
    }

    public void getConcept(ResultSet rs, PreparedStatement termsStmt) throws SQLException {
	Term t = getTerm(rs);

	t.initTermsSql(termsStmt);
	Sonja.leggnytermiliste(t);
	Sonja.conceptsById.put(t.getConceptId(), t);
    }

    private Term getTerm(ResultSet rs) throws SQLException {
	Term t = new Term(
		rs.getInt("concept_id"),
		rs.getInt("external_id"),
		ConceptType.fromEnglish(rs.getString("concept_type")),
		rs.getString("editorial_note"),
		rs.getTimestamp("created"),
		rs.getTimestamp("modified"),
		rs.getTimestamp("deprecated"),
		rs.getString("definition"),
		rs.getInt("replaced_by"));
	return t;
    }

    public void addTerm(Term concept, String lexicalValue, TermStatus status, Locale locale) throws SQLException {
	try (PreparedStatement statement = connection.prepareStatement("INSERT INTO terms (concept_id,status,lexical_value,lang_id) VALUES (?,?,?,?);");) {
	    statement.setInt(1, concept.getConceptId());
	    statement.setString(2, status.toString());
	    statement.setString(3, lexicalValue);
	    statement.setString(4, locale.getLanguage());
	    statement.executeUpdate();
	    updateModified(concept);
	}
    }

    public void removeTerm(Term concept, String lexicalValue, Locale locale) throws SQLException {
	try (PreparedStatement statement = connection.prepareStatement("DELETE FROM terms WHERE concept_id=? AND lexical_value=? AND lang_id=?;");) {
	    statement.setInt(1, concept.getConceptId());
	    statement.setString(2, lexicalValue);
	    statement.setString(3, locale.getLanguage());
	    statement.executeUpdate();
	    updateModified(concept);
	}
    }

    public void addRelation(Term concept1, Term concept2, RelationType type) throws SQLException {
	try (PreparedStatement statement = connection.prepareStatement("INSERT INTO relationships (concept1, concept2, rel_type) VALUES (?,?,?);");) {
	    statement.setInt(1, concept1.getConceptId());
	    statement.setInt(2, concept2.getConceptId());
	    statement.setString(3, type.toString());
	    statement.executeUpdate();
	    updateModified(concept1);
	    updateModified(concept2);
	}
    }

    public void removeRelation(Term concept1, Term concept2, RelationType type) throws SQLException {
	try (PreparedStatement statement = connection.prepareStatement("DELETE FROM relationships WHERE concept1 = ? AND concept2 = ? AND rel_type = ?;");) {
	    statement.setInt(1, concept1.getConceptId());
	    statement.setInt(2, concept2.getConceptId());
	    statement.setString(3, type.toString());
	    statement.executeUpdate();
	    updateModified(concept1);
	    updateModified(concept2);
	}
    }

    public void addMapping(Term source, String target, ExternalVocabulary vocab) throws SQLException {
	try (PreparedStatement statement = prepareStatement(
		"INSERT INTO mappings (source_concept_id, target_concept_id, target_vocabulary_id, mapping_relation) VALUES (?,?,?,?);");) {
	    statement.setInt(1, source.getConceptId());
	    statement.setString(2, target);
	    statement.setString(3, vocab.toString());
	    statement.setString(4, "close"); // todo: support other mapping relations
	    statement.executeUpdate();
	    updateModified(source);
	}
    }

    public void removeMapping(Term source, String target, ExternalVocabulary vocab) throws SQLException {
	try (PreparedStatement statement = prepareStatement(
		"DELETE FROM mappings WHERE source_concept_id=? AND target_concept_id=? AND target_vocabulary_id=? AND mapping_relation=?;");) {
	    statement.setInt(1, source.getConceptId());
	    statement.setString(2, target);
	    statement.setString(3, vocab.toString());
	    statement.setString(4, "close"); // todo: support other mapping relations
	    statement.executeUpdate();
	    updateModified(source);
	}
    }

    public void setNote(Term concept, String note) throws SQLException {
	try (PreparedStatement statement = prepareStatement("UPDATE concepts SET editorial_note=? WHERE concept_id=?;");) {
	    statement.setString(1, note);
	    statement.setInt(2, concept.getConceptId());
	    statement.executeUpdate();
	    updateModified(concept);
	}
    }

    public void setDefinition(Term concept, String definition) throws SQLException {
	try (PreparedStatement statement = prepareStatement("UPDATE concepts SET definition=? WHERE concept_id=?;");) {
	    statement.setString(1, definition);
	    statement.setInt(2, concept.getConceptId());
	    statement.executeUpdate();
	    updateModified(concept);
	}
    }

    private void updateModified(Term concept) throws SQLException {
	try (PreparedStatement statement = connection.prepareStatement("UPDATE concepts SET modified = CURRENT_TIMESTAMP WHERE concept_id = ?;");) {
	    statement.setInt(1, concept.getConceptId());
	    statement.executeUpdate();
	}
    }

    public void setReplacedBy(Term deprecated, Term replacement) throws SQLException {
	try (PreparedStatement statement = prepareStatement("UPDATE concepts SET replaced_by=? WHERE concept_id=?;")) {
	    statement.setInt(1, replacement.getConceptId());
	    statement.setInt(2, deprecated.getConceptId());
	    statement.executeUpdate();
	    deprecate(deprecated); //also does updateModified()
	}
    }

    private void deprecate(Term concept) throws SQLException {
	try (PreparedStatement statement = connection.prepareStatement("UPDATE concepts SET deprecated = CURRENT_TIMESTAMP WHERE concept_id = ?;");) {
	    statement.setInt(1, concept.getConceptId());
	    statement.executeUpdate();
	    updateModified(concept);
	}
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
	return connection.prepareStatement(query);
    }

    public Statement createStatement() throws SQLException {
	return connection.createStatement();
    }

    public void commit() throws SQLException {
	connection.commit();
    }

    public void setAutoCommit(boolean b) throws SQLException {
	connection.setAutoCommit(b);
    }

}
