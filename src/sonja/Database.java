package sonja;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Wrapper class for handling database connections
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

    void getConcept(ResultSet rs, PreparedStatement relationships, PreparedStatement termsStmt) throws SQLException {
	Term t = getTerm(rs);

	t.initTermsSql(termsStmt, relationships);
	Sonja.leggnytermiliste(t);
	Sonja.conceptsById.put(t.getConceptId(), t);
    }

    private Term getTerm(ResultSet rs) throws SQLException {
	Term t = new Term(
		rs.getInt("concept_id"),
		rs.getInt("external_id"),
		ConceptType.fromEnglish(rs.getString("concept_type")),
		rs.getString("note"),
		rs.getTimestamp("created"),
		rs.getTimestamp("modified"),
		rs.getTimestamp("deprecated"),
		rs.getString("definition"),
		rs.getInt("replaced_by"));
	return t;
    }

    public void addTerm(Term concept, String lexicalValue, TermStatus status, String lang) throws SQLException {
	PreparedStatement statement = connection.prepareStatement("INSERT INTO terms (concept_id,status,lexical_value,lang_id) VALUES (?,?,?,?);");
	statement.setInt(1, concept.getConceptId());
	statement.setString(2, status.toString());
	statement.setString(3, lexicalValue);
	statement.setString(4, lang);

	statement.executeUpdate();
    }

    public void removeTerm(Term concept, String lexicalValue, String language) throws SQLException {
	PreparedStatement statement = connection.prepareStatement("DELETE FROM terms WHERE concept_id=? AND lexical_value=? AND lang_id=?;");
	statement.setInt(1, concept.getConceptId());
	statement.setString(2, lexicalValue);
	statement.setString(3, language);
	statement.executeUpdate();
    }

    public void addRelation(Term concept1, Term concept2, RelationType type) throws SQLException {
	PreparedStatement statement = connection.prepareStatement("INSERT INTO relationships (concept1, concept2, rel_type) VALUES (?,?,?);");
	statement.setInt(1, concept1.getConceptId());
	statement.setInt(2, concept2.getConceptId());
	statement.setString(3, type.toString());
	statement.executeUpdate();
    }

    public PreparedStatement prepareStatement(String query) throws SQLException {
	return connection.prepareStatement(query);
    }

    public Statement createStatement() throws SQLException {
	return connection.createStatement();
    }

}
