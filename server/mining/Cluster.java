package mining;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import data.Data;
import data.Tuple;

public class Cluster implements Serializable {
	private Tuple centroid;
	private Set<Integer> clusteredData;

	/**
	 * Istanzia l'oggeto e inizializza il centroide con centroid.
	 * 
	 * @param centroid
	 *            centroide del cluster
	 */
	Cluster(Tuple centroid) {
		this.centroid = centroid;
		clusteredData = new HashSet<Integer>();
	}

	/**
	 * Restituisce il centroide del cluster.
	 * 
	 * @return centroide del cluster
	 */
	Tuple getCentroid() {
		return centroid;
	}

	/**
	 * Determina il centroide del cluster rispetto ai dati di data.
	 * 
	 * @param data
	 *            dati rispetto ai quali calcolare il centroide
	 */
	void computeCentroid(Data data) {
		for (int i = 0; i < centroid.getLength(); i++) {
			centroid.get(i).update(data, clusteredData);
		}
	}

	/**
	 * Aggiunge id della tupla al cluster corrente. Restituisce vero se la tupla è
	 * stata aggiunta, falso altrimenti.
	 * 
	 * @param id
	 *            identificatore della tupla
	 * @return true se id è stato aggiunto all'insieme, false altrimenti
	 */
	boolean addData(int id) {
		return clusteredData.add(id);
	}

	/**
	 * Verifica se una tupla è clusterizzata nel cluster corrente.
	 * 
	 * @param id
	 *            id della tupla di cui si vuole verificare la clusterizzazione
	 * @return true se la tupla è clusterizzata nel cluster corrente, false
	 *         altrimenti
	 */
	boolean contain(int id) {
		return clusteredData.contains(id);
	}

	/**
	 * Rimuove la tupla con uno specifico id.
	 * 
	 * @param id
	 *            identificatore della tupla
	 */
	void removeTuple(int id) {
		clusteredData.remove(id);
	}

	/**
	 * Restituisce una striga rappresentante lo stato del cluster.
	 */
	public String toString() {
		String str = "Centroid = (";
		for (int i = 0; i < centroid.getLength(); i++)
			str += centroid.get(i) + " ";
		str += ")";
		return str;
	}

	/**
	 * Restituisce una striga rappresentante lo stato del cluster e delle tuple
	 * contenute nel cluster.
	 * 
	 * @param data
	 *            dati in cui sono contenute le tuple del cluster
	 * @return stringa rappresentante lo stato del cluster e delle sue tuple
	 */
	public String toString(Data data) {
		String str = "Centroid = ( ";
		for (int i = 0; i < centroid.getLength(); i++)
			str += centroid.get(i) + " ";
		str += ")\nTuples:\n";

		Iterator<Integer> e = clusteredData.iterator();
		while (e.hasNext()) {
			int currentInt = e.next();
			str += "[ ";
			for (int j = 0; j < data.getNumberOfAttributes(); j++)
				str += data.getAttributeValue(currentInt, j) + " ";
			str += "] dist = " + getCentroid().getDistance(data.getItemSet(currentInt)) + "\n";

		}
		str += "AvgDistance = " + getCentroid().avgDistance(data, clusteredData) + "\n";
		return str;
	}

}
