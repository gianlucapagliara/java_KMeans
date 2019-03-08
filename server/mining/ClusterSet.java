package mining;

import java.io.Serializable;

import data.Data;
import data.OutOfRangeSampleSizeException;
import data.Tuple;

/**
 * Questa classe rappresenta un insieme di cluster determinati dall'algoritmo
 * k-means.
 * 
 * @author Gianluca Pagliara
 *
 */
public class ClusterSet implements Serializable {
	private Cluster[] C;
	private int i = 0; // posizione valida per la memorizzazione di un nuovo cluster in C

	/**
	 * Istanza un oggetto, inizializzando la dimensione dell'insieme di cluster a k,
	 * ossia il numero di cluster che verranno generati.
	 * 
	 * @param k
	 *            dimensione dell'insieme
	 */
	ClusterSet(int k) {
		C = new Cluster[k];
	}

	/**
	 * Aggiunge un cluster all'insieme.
	 * 
	 * @param c
	 *            cluster da aggiungere
	 */
	void add(Cluster c) {
		C[i++] = c;
	}

	/**
	 * Restituisce il cluster che si trova in una posizione specifica.
	 * 
	 * @param i
	 *            indice del cluster da restituire
	 * @return cluster nella i-esima posizione
	 */
	Cluster get(int i) {
		return C[i];
	}

	/**
	 * Sceglie i centroidi e crea un cluster per ogni centroide. Ogni cluster viene
	 * aggiunto all'insieme.
	 * 
	 * @param data
	 *            dati da cui calcolare i centroidi
	 * @throws OutOfRangeSampleSizeException
	 *             {@link Data#sampling(int)}
	 */
	void initializeCentroids(Data data) throws OutOfRangeSampleSizeException {
		int centroidIndexes[] = data.sampling(C.length);
		for (int i = 0; i < centroidIndexes.length; i++) {
			Tuple centroidI = data.getItemSet(centroidIndexes[i]);
			add(new Cluster(centroidI));
		}
	}

	/**
	 * Restituisce il cluster con distanza minima da una specifica tupla.
	 * 
	 * @param tuple
	 *            tupla dalla quale calcolare la distanza dei cluster
	 * @return cluster meno distante da tuple
	 */
	Cluster nearestCluster(Tuple tuple) {
		double distanceOfNearest = tuple.getDistance(C[0].getCentroid());
		Cluster nearest = C[0];
		for (int i = 1; i < C.length; i++) {
			double currentDistance = tuple.getDistance(C[i].getCentroid());
			if (currentDistance < distanceOfNearest) {
				nearest = C[i];
				distanceOfNearest = currentDistance;
			}
		}

		return nearest;
	}

	/**
	 * Restituisce il cluster in cui è presente la tupla identificata da id. Se la
	 * tupla non è presente in nessun cluster, restituisce null.
	 * 
	 * @param id
	 *            id della tupla
	 * @return cluster contenente la tupla, null altrimenti
	 */
	Cluster currentCluster(int id) {
		Cluster current = null;
		boolean found = false;

		for (int i = 0; i < C.length && !found; i++) {
			if (C[i].contain(id)) {
				current = C[i];
				found = true;
			}
		}

		return current;
	}

	/**
	 * Calcola il nuovo centroide per ciascun cluster nell'insieme.
	 * 
	 * @param data
	 *            dati da cui calcolare i centroidi
	 */
	void updateCentroids(Data data) {
		for (int i = 0; i < C.length; i++) {
			C[i].computeCentroid(data);
		}
	}

	/**
	 * Restituisce la stringa rappresentante lo stato dei centroidi dei cluster
	 * dell'insieme.
	 * 
	 * @return stringa rappresentante lo stato dei centroidi
	 */
	public String toString() {
		String str = "";
		for (int i = 0; i < C.length; i++) {
			if (C[i] != null) {
				str += i + ": " + C[i].toString() + "\n";
			}
		}
		return str;
	}

	/**
	 * Restituisce la stringa rappresentante lo stato dei cluster dell'insieme.
	 * 
	 * @param data
	 *            dati dei cluster dell'insieme
	 * @return stringa rappresentante lo stato dei cluster
	 */
	public String toString(Data data) {
		String str = "";
		for (int i = 0; i < C.length; i++) {
			if (C[i] != null) {
				str += i + ": " + C[i].toString(data) + "\n";
			}
		}
		return str;
	}

}
