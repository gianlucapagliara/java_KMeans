package database;

/**
 * Segnala che il valore calcolato da un query � NULL.
 * 
 * @author Gianluca Pagliara
 *
 */
public class NoValueException extends Exception {

	@Override
	public String getMessage() {
		return "Unexpected query result: NULL value.";
	}
}
