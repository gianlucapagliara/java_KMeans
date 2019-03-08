package database;

import java.util.ArrayList;
import java.util.List;

/**
 * Classe che implementa il concetto di riga di una tabella come una lista di
 * oggetti.
 * 
 * @author Gianluca Pagliara
 *
 */
public class Row implements Comparable<Row> {
	private List<Object> example;

	/**
	 * Istanzia un oggetto, inizializzando la lista di oggetti della riga.
	 */
	public Row() {
		example = new ArrayList<Object>();
	}

	/**
	 * Istanzia un oggetto, inizializzando la lista di oggetti della riga ad una
	 * dimensione specificata.
	 * 
	 * @param rowSize
	 *            dimensione della lista
	 */
	public Row(int rowSize) {
		example = new ArrayList<Object>(rowSize);
	}

	/**
	 * Aggiunge un oggetto alla lista di oggetti della riga che si sta
	 * rappresentando.
	 * 
	 * @param obj
	 *            oggetto da aggiungere
	 */
	public void add(Object obj) {
		example.add(obj);
	}

	/**
	 * Restituisce un oggetto in una posizione specifica nella riga.
	 * 
	 * @param index
	 *            indice dell'oggetto da restituire
	 * @return oggetto richiesto
	 */
	public Object get(int index) {
		return example.get(index);
	}

	/**
	 * Confronta la riga rappresentata dall'istanza dell'oggetto con la riga row.
	 * Restituisce 0, -1, 1 sulla base del risultato del confronto. 0 se le due
	 * righe includono gli stessi valori. Altrimenti il risultato del compareTo()
	 * invocato sulla prima coppia di valori in disaccordo.
	 * 
	 * @return 0 se le due righe contengono gli stessi valori, -1 o 1 in base al
	 *         risultato del compareTo sulla prima coppia di valori diversi
	 */
	public int compareTo(Row row) {

		int i = 0;
		for (Object o : row.example) {
			if (!o.equals(this.example.get(i)))
				return ((Comparable) o).compareTo(example.get(i));
			i++;
		}
		return 0;
	}

	/**
	 * Restituisce la stringa rappresentante la riga.
	 * 
	 * @return stringa rappresentante la riga
	 */
	public String toString() {
		String str = "";
		for (Object o : example)
			str += o.toString() + " ";
		return str;
	}

}