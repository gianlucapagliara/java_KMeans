package database;

import java.sql.ResultSet;

/**
 * Segnala che il {@link ResultSet} di una query è vuoto.
 * 
 * @author Gianluca Pagliara
 *
 */
public class EmptySetException extends Exception {

	@Override
	public String getMessage() {
		return "No result found for the query.";
	}
}
