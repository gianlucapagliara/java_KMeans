package mining;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import data.Data;
import data.OutOfRangeSampleSizeException;

/**
 * Classe che implementa l'esecuzione dell'algoritmo KMeans.
 * 
 * @author Gianluca Pagliara
 *
 */
public class KMeansMiner {
	ClusterSet C;

	/**
	 * Istanzia l'oggetto e inizializza l'insieme di cluster ad una dimensione
	 * specifica.
	 * 
	 * @param size
	 *            dimensione dell'insieme di cluster
	 */
	public KMeansMiner(int size) {
		C = new ClusterSet(size);
	}

	/**
	 * Carica l'insieme di cluster dal file con nome fileName.
	 * 
	 * @param fileName
	 *            percorso del file da cui caricare i dati
	 * @throws FileNotFoundException
	 *             {@link FileInputStream#FileInputStream(String)}
	 * @throws ClassNotFoundException
	 *             {@link ObjectInputStream#readObject()}
	 * @throws IOException
	 *             {@link ObjectInputStream#ObjectInputStream(java.io.InputStream)}
	 *             {@link ObjectInputStream#readObject()}
	 *             {@link ObjectInputStream#close()} {@link FileInputStream#close()}
	 */
	public KMeansMiner(String fileName) throws FileNotFoundException, IOException, ClassNotFoundException {
		FileInputStream inputFile = new FileInputStream(fileName);
		ObjectInputStream inputStream = new ObjectInputStream(inputFile);
		C = (ClusterSet) inputStream.readObject();
		inputStream.close();
		inputFile.close();
	}

	/**
	 * Salva l'insieme di cluster sul file con nome fileName.
	 * 
	 * @param fileName
	 *            percorso del file in cui salvare i dati
	 * @throws FileNotFoundException
	 *             {@link FileOutputStream#FileOutputStream(String)}
	 * @throws IOException
	 *             {@link ObjectOutputStream#ObjectOutputStream(java.io.OutputStream)}
	 *             {@link ObjectOutputStream#writeObject(Object)}
	 *             {@link ObjectOutputStream#close()}
	 *             {@link FileOutputStream#close()}
	 */
	public void save(String fileName) throws FileNotFoundException, IOException {
		FileOutputStream outFile = new FileOutputStream(fileName);
		ObjectOutputStream outStream = new ObjectOutputStream(outFile);
		outStream.writeObject(C);
		outStream.close();
		outFile.close();
	}

	/**
	 * Restituisce l'insieme di cluster ({@link ClusterSet}).
	 * 
	 * @return insieme di cluster
	 */
	public ClusterSet getC() {
		return C;
	}

	/**
	 * Esegue l'algoritmo KMeans e restituisce il numero di iterazioni eseguite.
	 * 
	 * @param data
	 *            dati su cui eseguire l'algoritmo
	 * @return numero di iterazioni eseguite
	 * @throws OutOfRangeSampleSizeException
	 *             {@link ClusterSet#initializeCentroids(Data)}
	 */
	public int kmeans(Data data) throws OutOfRangeSampleSizeException {
		int numberOfIterations = 0;
		// STEP 1
		C.initializeCentroids(data);
		boolean changedCluster = false;
		do {
			numberOfIterations++;
			// STEP 2
			changedCluster = false;
			for (int i = 0; i < data.getNumberOfTuples(); i++) {
				Cluster nearestCluster = C.nearestCluster(data.getItemSet(i));
				Cluster oldCluster = C.currentCluster(i);
				boolean currentChange = nearestCluster.addData(i);
				if (currentChange)
					changedCluster = true;
				// rimuovo la tupla dal vecchio cluster
				if (currentChange && oldCluster != null)
					// il nodo va rimosso dal suo vecchio cluster
					oldCluster.removeTuple(i);

			}
			// STEP 3
			C.updateCentroids(data);
		} while (changedCluster);
		return numberOfIterations;
	}

}
