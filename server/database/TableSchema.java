package database;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Questa classe permette di rappresentare lo schema di una tabella, attraverso
 * l'interrogazione della stessa.
 * 
 * @author Gianluca Pagliara
 *
 */
public class TableSchema {

	/**
	 * Questa classe permette di rappresentare una singola colonna di una tabella.
	 * 
	 * @author Gianluca Pagliara
	 *
	 */
	public class Column {
		private String name;
		private String type;

		/**
		 * Inizializza gli attributi della colonna, l'identificatore e il tipo dei dati
		 * memorizzati.
		 * 
		 * @param name
		 *            nome della colonna
		 * @param type
		 *            tipo di dati memorizzati nella colonna
		 */
		Column(String name, String type) {
			this.name = name;
			this.type = type;
		}

		/**
		 * Restituisce il nome della colonna.
		 * 
		 * @return nome della colonna
		 */
		public String getColumnName() {
			return name;
		}

		/**
		 * Restituisce se la colonna memorizza dati di tipo {@link Number}.
		 * 
		 * @return <code>true</code> se la colonna contiene valori numerici,
		 *         <code>false</code> altrimenti.
		 */
		public boolean isNumber() {
			return type.equals("number");
		}

		/**
		 * Ritorna una stringa che rappresenta la colonna.
		 * 
		 * @return stringa che rappresenta la colonna
		 */
		public String toString() {
			return name + ":" + type;
		}
	}

	private List<Column> tableSchema = new ArrayList<Column>();

	/**
	 * Estrae lo schema dalla tabella, mappando i tipi SQL con quelli di Java.
	 * 
	 * @param tableName
	 *            nome della tabella
	 * @throws SQLException
	 *             se ci sono errori nella connessione al database
	 */
	public TableSchema(String tableName) throws SQLException {
		HashMap<String, String> mapSQL_JAVATypes = new HashMap<String, String>();
		// http://java.sun.com/j2se/1.3/docs/guide/jdbc/getstart/mapping.html
		mapSQL_JAVATypes.put("CHAR", "string");
		mapSQL_JAVATypes.put("VARCHAR", "string");
		mapSQL_JAVATypes.put("LONGVARCHAR", "string");
		mapSQL_JAVATypes.put("BIT", "string");
		mapSQL_JAVATypes.put("SHORT", "number");
		mapSQL_JAVATypes.put("INT", "number");
		mapSQL_JAVATypes.put("LONG", "number");
		mapSQL_JAVATypes.put("FLOAT", "number");
		mapSQL_JAVATypes.put("DOUBLE", "number");

		Connection con = DbAccess.getConnection();
		DatabaseMetaData meta = con.getMetaData();
		ResultSet res = meta.getColumns(null, null, tableName, null);

		while (res.next()) {
			if (mapSQL_JAVATypes.containsKey(res.getString("TYPE_NAME")))
				tableSchema.add(
						new Column(res.getString("COLUMN_NAME"), mapSQL_JAVATypes.get(res.getString("TYPE_NAME"))));
		}
		res.close();

	}

	/**
	 * Restituisce lo schema della tabella.
	 * 
	 * @return schema della tabella
	 */
	public List<Column> getSchema() {
		return tableSchema;
	}

	/**
	 * Restituisce il numero di attributi (o colonne) della tabella.
	 * 
	 * @return numero di attributi
	 */
	public int getNumberOfAttributes() {
		return tableSchema.size();
	}

	/**
	 * Restituisce la i-esima colonna dello schema.
	 * 
	 * @param i
	 *            indice della colonna da restituire
	 * @return i-esima colonna
	 */
	public Column getColumn(int i) {
		return tableSchema.get(i);
	}

}
