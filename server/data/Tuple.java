package data;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Set;

/**
 * Questa classe rappresenta una tupla come sequenza di coppie attributo-valore.
 * 
 * @author Gianluca Pagliara
 *
 */
public class Tuple implements Serializable {
	Item[] tuple;

	/**
	 * Istanzia l'oggetto inzializzando l'array con il numero di item che costituirà
	 * la tupla.
	 * 
	 * @param size
	 *            numero di item della tupla
	 */
	Tuple(int size) {
		tuple = new Item[size];
	}

	/**
	 * Restituisce la lunghezza della tupla.
	 * 
	 * @return lunghezza della tupla
	 */
	public int getLength() {
		return tuple.length;
	}

	/**
	 * Restituisce l'item in una posizione specifica.
	 *
	 * @param i
	 *            posizione dell'item nella tupla
	 * @return item nella i-esima posizione
	 */
	public Item get(int i) {
		return tuple[i];
	}

	/**
	 * Aggiunge un item in una posizione specifica.
	 * 
	 * @param item
	 *            item da inserire
	 * @param i
	 *            posizione in cui inserire l'item
	 */
	void add(Item item, int i) {
		tuple[i] = item;
	}

	/**
	 * Restituisce la distanza tra la tupla riferita da obj e la tupla corrente. La
	 * distanza è ottenuta come somma delle distanze tra gli item in posizioni
	 * uguali nelle due tuple.
	 * 
	 * @param tuple
	 *            tupla con cui confrontare la tupla corrente
	 * @return distanza tra le due tuple
	 */
	public double getDistance(Tuple tuple) {
		int distance = 0;
		for (int i = 0; i < getLength(); i++) {
			distance += get(i).distance(tuple.get(i));
		}
		return distance;
	}

	/**
	 * Restituisce la media delle distanze tra la tupla corrente e quelle ottenibili
	 * dalle righe della matrice in data aventi indice contenuto in clusteredData.
	 * 
	 * @param data
	 *            dati da cui prendere le tuple
	 * @param clusteredData
	 *            indici delle tuple da considerare
	 * @return distanza media
	 */
	public double avgDistance(Data data, Set<Integer> clusteredData) {
		double p = 0.0, sumD = 0.0;
		Iterator<Integer> e = clusteredData.iterator();
		while (e.hasNext()) {
			double d = getDistance(data.getItemSet(e.next()));
			sumD += d;
		}
		p = sumD / clusteredData.size();
		return p;

	}
}
