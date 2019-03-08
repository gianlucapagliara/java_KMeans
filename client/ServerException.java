package client;

/**
 * Segnala che durante la comunicazione con il server c'� stato un errore.
 * 
 * @author Gianluca Pagliara
 *
 */
public class ServerException extends Exception {

	@Override
	public String getMessage() {
		return "Communication server error.";
	}
}
