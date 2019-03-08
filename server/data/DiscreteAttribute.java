package data;

import java.util.Set;
import java.util.Iterator;
import java.util.TreeSet;

/**
 * Questa classe estende {@linkplain Attribute} e rappresenta un attributo
 * discreto.
 * 
 * @author Gianluca Pagliara
 *
 */
public class DiscreteAttribute extends Attribute implements Iterable<String> {

	/*
	 * Set di oggetti String, uno per ciascun valore del dominio discreto. I valori
	 * del dominio sono memorizzati in values seguendo un ordine lessicografico.
	 */
	private TreeSet<String> values;

	/**
	 * Istanzia l'oggetto e inzializza il nome, l'id e l'insieme di valori che
	 * l'attributo può assumere.
	 * 
	 * @param name
	 *            nome dell'attributo
	 * @param id
	 *            id dell'attributo
	 * @param values
	 *            insieme dei valori che l'attributo può assumere
	 */
	public DiscreteAttribute(String name, int id, TreeSet<String> values) {
		super(name, id);
		this.values = values;
	}

	/**
	 * Restituisce il numero di valori diversi (ossia la cardinalità dell'insieme)
	 * che l'attributo può assumere.
	 * 
	 * @return numero di valori distinti
	 */
	int getNumberOfDistinctValues() {
		return values.size();
	}

	/**
	 * Determina il numero di volte che il valore v compare in corrispondenza
	 * dell'attributo corrente negli tuple memorizzate in data e indicizzate (per
	 * riga) da idList.
	 * 
	 * @param data
	 *            dati in cui cercare v
	 * @param idList
	 *            indici delle tuple da considerare
	 * @param v
	 *            valore di cui contare le occorrenze
	 * @return numero di occorrenze di v
	 */
	int frequency(Data data, Set<Integer> idList, String v) {
		int frequency = 0;
		for (int i = 0; i < data.getNumberOfTuples(); i++) {
			if (idList.contains(i) == true) {
				if (data.getAttributeValue(i, this.getId()).equals(v))
					frequency++;
			}
		}

		return frequency;
	}

	@Override
	/**
	 * Restituisce l'iterator sugli elementi dell'insieme di valori.
	 */
	public Iterator<String> iterator() {
		return values.iterator();
	}
}
