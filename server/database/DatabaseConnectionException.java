package database;

/**
 * Segnala un errore durante la connessione con il database.
 * 
 * @author Gianluca Pagliara
 *
 */
public class DatabaseConnectionException extends Exception {

	@Override
	public String getMessage() {
		return "Connection to database error.";
	}
}
