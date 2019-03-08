package database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Questa classe permette attraverso i suoi metodi di connettersi ad uno
 * specifico database (jdbc:mysql://localhost:3306/MapDB).
 * 
 * @author Gianluca Pagliara
 *
 */
public class DbAccess {

	private static final String DRIVER_CLASS_NAME = "org.gjt.mm.mysql.Driver";

	private static final String DBMS = "jdbc:mysql";
	private static final String SERVER_ID = "localhost";
	private static final String DBMS_PORT = "3306"; // La porta su cui il DBMS MySQL accetta le connessioni
	private static final String DATABASE_NAME = "MapDB";
	private static final String USER_ID = "MapUser";
	private static final String USER_PASSWORD = "map";

	private static Connection connection; // gestisce una connessione

	private DbAccess() {

	}

	/**
	 * Impartisce al classloader l’ordine di caricare il driver mysql, inizializza
	 * la connessione riferita da {@link DbAccess#connection}.
	 * 
	 * @throws DatabaseConnectionException
	 *             se fallisce la connessione al database.
	 */
	public static void initConnection() throws DatabaseConnectionException {
		// Chiude la connessione se già presente
		closeConnection();

		String connectionString = DBMS + "://" + SERVER_ID + ":" + DBMS_PORT + "/" + DATABASE_NAME;

		// Verifica la correttezza del driver
		try {
			Class.forName(DRIVER_CLASS_NAME).newInstance();
		} catch (Exception e) {
			System.out.println("MySQL Driver error: " + e.getMessage());
		}

		// Esegue la connessione
		try {
			connection = DriverManager.getConnection(connectionString, USER_ID, USER_PASSWORD);
		} catch (SQLException e) {
			throw new DatabaseConnectionException();
		}
	}

	/**
	 * Restituisce la connessione se già creata e se essa è valida, altrimenti prima
	 * la inizializza.
	 * 
	 * @return restituisce {@link DbAccess#connection}
	 */
	public static Connection getConnection() {
		// Verifica che la connessione sia valida, altrimenti la inizializza
		try {
			boolean connectionIsValid = (connection != null) && connection.isValid(0) ? true : false;
			if (!connectionIsValid)
				try {
					initConnection();
				} catch (DatabaseConnectionException e) {
					System.out.println("Connection initialization error: " + e.getMessage());
				}
		} catch (SQLException e) {
			// Ignorata, viene lanciata da Connection.isValid(int) se timeout < 0
		}

		return connection;
	}

	/**
	 * Chiude la connessione.
	 */
	public static void closeConnection() {
		if (connection != null) {
			try {
				connection.close();
			} catch (SQLException e) {
				System.out.println("Close connection error: " + e.getMessage());
			}
		}
	}

}
