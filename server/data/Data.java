package data;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.TreeSet;

import database.EmptySetException;
import database.NoValueException;
import database.QUERY_TYPE;
import database.Row;
import database.TableData;
import database.TableSchema;
import database.TableSchema.Column;

/**
 * Questa classe rappresenta i dati estratti da un database.
 * 
 * @author Gianluca Pagliara
 *
 */
public class Data {

	// Una lista di tipo Row dove ogni elemento modella una transazione
	private List<Row> data;
	// cardinalità dell’insieme di tuple (numero di righe in data)
	private int numberOfTuples;
	// un vettore degli attributi in ciascuna tupla (schema della tabella di dati)
	private List<Attribute> explanatorySet;

	/**
	 * Istanzia l'oggetto e carica i dati dalla tabella.
	 * 
	 * @param table
	 *            nome della tabella da cui caricare i dati
	 */
	public Data(String table) {
		try {
			// Carica i dati dalla tabella, li inserisce in un TreeSet così da ordinarli e
			// quindi inizializza data e numberOfExamples
			TreeSet<Row> tempData = new TreeSet<Row>(TableData.getDistinctTransaction(table));
			data = new ArrayList<Row>(tempData);
			numberOfTuples = data.size();

			// Inizializza explanatorySet
			TableSchema tbSchema = new TableSchema(table);
			int numberOfAttributes = tbSchema.getNumberOfAttributes();
			explanatorySet = new LinkedList<Attribute>();
			for (int i = 0; i < numberOfAttributes; i++) {
				Attribute currentAttribute;
				Column currentColumn = tbSchema.getColumn(i);
				String currentColumnName = currentColumn.getColumnName();
				// Controlla il tipo dell'attributo corrente
				if (currentColumn.isNumber()) {
					double min = (double) TableData.getAggregateColumnValue(table, currentColumn, QUERY_TYPE.MIN);
					double max = (double) TableData.getAggregateColumnValue(table, currentColumn, QUERY_TYPE.MAX);
					currentAttribute = new ContinuousAttribute(currentColumnName, i, min, max);
				} else {
					TreeSet<Object> currentAttributeObj = (TreeSet<Object>) TableData.getDistinctColumnValues(table,
							currentColumn);
					// Converte il TreeSet di Object in un TreeSet di String
					TreeSet<String> currentAttributeValues = new TreeSet<>();
					for (Object o : currentAttributeObj) {
						currentAttributeValues.add(o.toString());
					}
					currentAttribute = new DiscreteAttribute(currentColumnName, i, currentAttributeValues);
				}
				explanatorySet.add(currentAttribute);
			}

		} catch (SQLException | EmptySetException | NoValueException e) {
			System.out.println("Data loading error: " + e.getMessage());
		}
	}

	/**
	 * Restituisce il numero di tuple presenti nella tabella.
	 * 
	 * @return numero di tuple
	 */
	public int getNumberOfTuples() {
		return numberOfTuples;
	}

	/**
	 * Restituisce il numero di attributi della tabella.
	 * 
	 * @return numero di attributi
	 */
	public int getNumberOfAttributes() {
		return explanatorySet.size();
	}

	/**
	 * Restituisce lo schema della tabella.
	 * 
	 * @return lista di attributi rappresentante lo schema della tabella
	 */
	List<Attribute> getAttributeSchema() {
		return explanatorySet;
	}

	/**
	 * Restituisce il valore presente nella riga row della colonna column.
	 * 
	 * @param row
	 *            riga del valore richiesto
	 * @param column
	 *            colonna del valore richiesto
	 * @return valore nella riga row della colonna column
	 */
	public Object getAttributeValue(int row, int column) {
		return data.get(row).get(column);
	}

	/**
	 * Restituisce la stringa rappresentante i dati caricati dalla tabella.
	 */
	public String toString() {
		String header = "";
		for (int i = 0; i < getNumberOfAttributes(); i++) {
			header += explanatorySet.get(i);
			if (i != getNumberOfAttributes() - 1)
				header += ", ";
		}
		header += "\n";

		String dataValues = "";
		Iterator<Row> e = data.iterator();
		int row = 0;
		while (e.hasNext()) {
			dataValues += ++row + ": " + e.next().toString() + "\n";
		}

		return header + dataValues;
	}

	/**
	 * Restituisce la tupla della riga row.
	 * 
	 * @param row
	 *            riga della tupla da restituire
	 * @return tupla della riga row
	 */
	public Tuple getItemSet(int row) {
		Tuple tuple = new Tuple(getNumberOfAttributes());
		Iterator<Attribute> e = getAttributeSchema().iterator();
		while (e.hasNext()) {
			Attribute currentAttribute = e.next();
			Item currentItem = null;
			if (currentAttribute instanceof DiscreteAttribute)
				currentItem = new DiscreteItem(currentAttribute,
						(String) getAttributeValue(row, currentAttribute.getId()));
			else if (currentAttribute instanceof ContinuousAttribute)
				currentItem = new ContinuousItem(currentAttribute,
						(Double) getAttributeValue(row, currentAttribute.getId()));
			tuple.add(currentItem, currentAttribute.getId());
		}
		return tuple;
	}

	/**
	 * Sceglie casualmente k tuple come centroidi. Restituisce un array contenente
	 * gli indici delle tuple scelte.
	 * 
	 * @param k
	 *            numero di centroidi da scegliere
	 * @return array di indici delle tuple scelte
	 * @throws OutOfRangeSampleSizeException
	 *             se k è minore uguale di 0 o k maggiore del numero di tuple
	 */
	public int[] sampling(int k) throws OutOfRangeSampleSizeException {
		if (k <= 0 || k > numberOfTuples)
			throw new OutOfRangeSampleSizeException();

		int centroidIndexes[] = new int[k];
		// choose k random different centroids in data.
		Random rand = new Random();
		rand.setSeed(System.currentTimeMillis());
		for (int i = 0; i < k; i++) {
			boolean found = false;
			int c;
			do {
				found = false;
				c = rand.nextInt(getNumberOfTuples());
				// verify that centroid[c] is not equal to a centroid already stored in
				// CentroidIndexes
				for (int j = 0; j < i; j++)
					if (compare(centroidIndexes[j], c)) {
						found = true;
						break;
					}
			} while (found);
			centroidIndexes[i] = c;
		}
		return centroidIndexes;
	}

	/**
	 * Restituisce vero se le due righe di data contengono gli stessi valori, falso
	 * altrimenti.
	 * 
	 * @param row1
	 *            prima riga da confrontare
	 * @param row2
	 *            seconda riga da confrontare
	 * @return true se le due righe sono uguali, false altrimenti.
	 */
	private boolean compare(int row1, int row2) {
		boolean equals = true;
		for (int j = 0; j < getNumberOfAttributes(); j++) {
			if (!getAttributeValue(row1, j).equals(getAttributeValue(row2, j)))
				equals = false;
		}
		return equals;
	}

	/**
	 * Determina il valore che occorre più frequentemente per l'attributo attribute
	 * nel sottoinsieme di dati individuato da idList.
	 * 
	 * @param idList
	 *            sottoinsime di dati da considerare
	 * @param attribute
	 *            attributo per cui cercare il valore più frequente
	 * @return valore più frequente
	 */
	Object computePrototype(Set<Integer> idList, Attribute attribute) {
		if (attribute instanceof DiscreteAttribute)
			return computePrototype(idList, (DiscreteAttribute) attribute);
		else if (attribute instanceof ContinuousAttribute)
			return computePrototype(idList, (ContinuousAttribute) attribute);

		return null;
	}

	/**
	 * Determina il valore che occorre più frequentemente per l'attributo discreto
	 * attribute nel sottoinsieme di dati individuato da idList.
	 * 
	 * @param idList
	 *            sottoinsime di dati da considerare
	 * @param attribute
	 *            attributo per cui cercare il valore più frequente
	 * @return valore più frequente
	 */
	String computePrototype(Set<Integer> idList, DiscreteAttribute attribute) {
		String mostFrequentValue = "";
		int frequencyOfMostFrequentValue = 0;
		Iterator<String> e = attribute.iterator();

		while (e.hasNext()) {
			String currentValue = e.next();
			int currentFrequency = attribute.frequency(this, idList, currentValue);
			if (currentFrequency > frequencyOfMostFrequentValue) {
				mostFrequentValue = currentValue;
				frequencyOfMostFrequentValue = currentFrequency;
			}
		}

		return mostFrequentValue;
	}

	/**
	 * Determina il valore che occorre più frequentemente per l'attributo continuto
	 * attribute nel sottoinsieme di dati individuato da idList.
	 * 
	 * @param idList
	 *            sottoinsime di dati da considerare
	 * @param attribute
	 *            attributo per cui cercare il valore più frequente
	 * @return valore più frequente
	 */
	Double computePrototype(Set<Integer> idList, ContinuousAttribute attribute) {
		double total = 0;
		int numberOfValues = 0;

		for (int i = 0; i < getNumberOfTuples(); i++) {
			if (idList.contains(i)) {
				total += (Double) getAttributeValue(i, attribute.getId());
				numberOfValues++;
			}
		}

		return total / numberOfValues;
	}
}