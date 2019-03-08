package data;

import java.io.Serializable;
import java.util.Set;

/**
 * Questa classe rappresenta un generico item, ossia una coppia
 * attributo-valore.
 * 
 * @author Gianluca Pagliara
 *
 */
public abstract class Item implements Serializable {
	protected Attribute attribute; // attributo coinvolto nell'item
	protected Object value; // valore assegnato all'attributo

	/**
	 * Istanzia l'oggetto e inizializza l'attributo e il valore.
	 * 
	 * @param attribute
	 *            attributo dell'item
	 * @param value
	 *            valore dell'item
	 */
	public Item(Attribute attribute, Object value) {
		this.attribute = attribute;
		this.value = value;
	}

	/**
	 * Restituisce l'attributo dell'item.
	 * 
	 * @return attributo dell'item
	 */
	Attribute getAttribute() {
		return attribute;
	}

	/**
	 * Restituisce il valore dell'item.
	 * 
	 * @return valore dell'item
	 */
	Object getValue() {
		return value;
	}

	/**
	 * Restituisce la stringa rappresentante lo stato dell'item.
	 */
	public String toString() {
		return value.toString();
	}

	/**
	 * Calcola la distanza dell'item da l'oggetto obj.
	 * 
	 * @param obj
	 *            oggetto rispetto il quale calcolare la distanza
	 * @return distanza
	 */
	abstract double distance(Object obj);

	/**
	 * Rispetto ad un oggetto di tipo {@link Data}, modifica il valore dell'item
	 * assegnando il valore restituito da
	 * {@link Data#computePrototype(Set, Attribute)}.
	 * 
	 * @param data
	 *            dati rispetto ai quali aggiornare i valori dell'item
	 * @param clusteredData
	 *            insieme di indici delle righe della matrice in data che formano i
	 *            cluster
	 */
	public void update(Data data, Set<Integer> clusteredData) {
		value = data.computePrototype(clusteredData, attribute);
	}
}
