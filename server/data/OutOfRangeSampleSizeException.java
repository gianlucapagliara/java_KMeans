package data;

/**
 * Segnala che il numero di cluster da cercare è maggiore del numero di
 * centroidi generabili o minore di 1.
 * 
 * @author Gianluca Pagliara
 *
 */
public class OutOfRangeSampleSizeException extends Exception {

	@Override
	public String getMessage() {
		return "Number of cluster out of range.";
	}

}
