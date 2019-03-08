package database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import database.TableSchema.Column;

/**
 * Questa classe contiene metodi che, connessi al database tramite
 * {@link DbAccess}, permettono di ottenere un insieme di dati dal database.
 * 
 * @author Gianluca Pagliara
 *
 */
public class TableData {

	private TableData() {
	}

	/**
	 * Ricava lo schema della tabella con nome table ed esegue un'interrogazione per
	 * estrarre le tuple distinte da tale tabella. Per ogni tupla del resultset, si
	 * crea un oggetto, istanza della classe {@link Row}, il cui riferimento va
	 * incluso nella lista da restituire.
	 * 
	 * @param table
	 *            Tabella da interrogare
	 * 
	 * @return Lista di tuple distinte
	 * 
	 * @throws SQLException
	 *             se ci sono errori nell'esecuzione della query
	 * @throws EmptySetException
	 *             se il resultset è vuoto
	 */
	public static List<Row> getDistinctTransaction(String table) throws SQLException, EmptySetException {
		DbAccess.getConnection();

		// Trova lo schema della tabella
		TableSchema tableSchema = new TableSchema(table);

		Statement statement = DbAccess.getConnection().createStatement();
		String query = "SELECT DISTINCT * FROM " + table + ";";
		ResultSet resultSet = statement.executeQuery(query);

		// Verifica se il resultset è vuoto
		if (resultSet.next() == false) {
			throw new EmptySetException();
		} else {
			resultSet.beforeFirst();
		}

		// Popola la lista da restituire
		List<Row> exampleList = new ArrayList<Row>();
		int numberOfAttributes = tableSchema.getNumberOfAttributes();
		while (resultSet.next()) {
			Row currentExample = new Row(numberOfAttributes);
			for (int i = 1; i <= numberOfAttributes; i++) {
				if (tableSchema.getColumn(i - 1).isNumber()) {
					currentExample.add(resultSet.getDouble(i));
				} else {
					currentExample.add(resultSet.getString(i));
				}
			}
			exampleList.add(currentExample);
		}

		return exampleList;
	}

	/**
	 * Formula ed esegue un'interrogazione SQL per estrarre i valori distinti
	 * ordinati di column (nella tabella table) e popolare l'insieme che viene
	 * restituito.
	 * 
	 * @param table
	 *            nome della tabella da interrogare
	 * @param column
	 *            nome della colonna della tabella da cui estrarre i valori
	 * @return insieme di valori distinti ordinati in modalità ascendente
	 * @throws SQLException
	 *             {@link Connection#createStatement()}
	 *             {@link Statement#executeQuery(String)}
	 *             {@link ResultSet#getObject(String)}
	 */
	public static Set<Object> getDistinctColumnValues(String table, Column column) throws SQLException {
		Set<Object> set = new TreeSet<Object>();

		// Query
		Statement statement = DbAccess.getConnection().createStatement();
		String columnName = column.getColumnName();
		String query = "SELECT DISTINCT " + columnName + " FROM " + table + " ORDER BY " + columnName + ";";
		ResultSet resultSet = statement.executeQuery(query);

		// Popola l'insieme ordinato da restituire
		while (resultSet.next()) {
			set.add(resultSet.getObject(columnName));
		}

		return set;
	}

	/**
	 * Formula ed esegue un'interrogazione SQL per estrarre il valore aggregato
	 * (minimo o massimo) cercato nella colonna di nome column della tabella di nome
	 * table.
	 * 
	 * @param table
	 *            nome della tabella da interrogare
	 * @param column
	 *            nome della colonna della tabella da cui estrarre i valori
	 * @param aggregate
	 *            tipo di valore aggregato cercato, {@link QUERY_TYPE}
	 * @return aggregato cercato
	 * 
	 * @throws SQLException
	 *             {@link Connection#createStatement()}
	 *             {@link Statement#executeQuery(String)}
	 *             {@link ResultSet#getDouble(int)}
	 * @throws NoValueException
	 *             se il valore calcolato è NULL.
	 * @throws EmptySetException
	 *             se il resultset è vuoto
	 */
	public static Object getAggregateColumnValue(String table, Column column, QUERY_TYPE aggregate)
			throws SQLException, NoValueException, EmptySetException {
		Object aggregateObj = null;

		// Query
		Statement statement = DbAccess.getConnection().createStatement();
		String columnName = column.getColumnName();
		String query = "SELECT " + aggregate + "(" + columnName + ") FROM " + table + ";";
		ResultSet resultSet = statement.executeQuery(query);

		// Verifica che sia stato trovato un risultato e che non sia NULL
		boolean error = false;
		if (resultSet.next()) {
			aggregateObj = resultSet.getDouble(1);
			error = (aggregateObj == null) ? true : false;
		} else {
			throw new EmptySetException();
		}

		// Lancia l'eccezione se c'è stato un errore
		if (error)
			throw new NoValueException();

		return aggregateObj;
	}

}
