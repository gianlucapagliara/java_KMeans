package server;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import data.Data;
import data.OutOfRangeSampleSizeException;
import mining.KMeansMiner;

/**
 * Questa classe estende {@link Thread}. Implementa l'esecuzione di una
 * richiesta del client. Ogni richiesta viene eseguita in un thread separato.
 * 
 * @author Gianluca Pagliara
 *
 */
public class ServerOneClient extends Thread {
	private Socket socket;
	private ObjectInputStream in;
	private ObjectOutputStream out;
	private KMeansMiner kmeans;

	/**
	 * Inizializza gli attributi e avvia il thread.
	 * 
	 * @param socket
	 *            socket da cui ottenere gli stream.
	 * @throws IOException
	 *             {@link Socket#getInputStream()} e
	 *             {@link Socket#getOutputStream()}
	 */
	public ServerOneClient(Socket socket) throws IOException {
		this.socket = socket;
		System.out.println("Connection accepted: " + socket);

		in = new ObjectInputStream(socket.getInputStream());
		out = new ObjectOutputStream(socket.getOutputStream());

		start();
	}

	/**
	 * Esegue il thread.
	 */
	@Override
	public void run() {
		try {
			processRequest();

			System.out.println("Closing connection.");
		} catch (IOException e) {
			System.out.println("Connection error: " + e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println("Communication error: " + e.getMessage());
		} finally {
			try {
				socket.close();
			} catch (IOException e) {
				System.out.println("Socket closing error: " + e.getMessage());
			}
		}
	}

	/**
	 * Attende la ricezione di un messaggio dal client.
	 * 
	 * @return messaggio ricevuto dal client
	 * 
	 * @throws ClassNotFoundException
	 *             {@link ObjectInputStream#readObject()}
	 * @throws IOException
	 *             {@link ObjectInputStream#readObject()}
	 */
	private String getMessage() throws ClassNotFoundException, IOException {
		String string;
		while (true) {
			string = (String) in.readObject();
			if (string != null)
				break;
		}
		return string;
	}

	/**
	 * Attende la ricezione di un numero dal client.
	 * 
	 * @return messaggio ricevuto dal client
	 * 
	 * @throws ClassNotFoundException
	 *             {@link ObjectInputStream#readObject()}
	 * @throws IOException
	 *             {@link ObjectInputStream#readObject()}
	 */
	private int getNumber() throws ClassNotFoundException, IOException {
		Integer number;
		while (true) {
			number = (Integer) in.readObject();
			if (number != null) {
				break;
			}
		}
		return number;
	}

	/**
	 * Esegue la richiesta del client.
	 * 
	 * @throws IOException
	 *             {@link ServerOneClient#getMessage()} e
	 *             {@link ObjectOutputStream#writeObject(Object)}
	 * @throws ClassNotFoundException
	 *             {@link ServerOneClient#getMessage()}
	 */
	private void processRequest() throws IOException, ClassNotFoundException {
		String choice = getMessage();

		switch (choice) {
		case "DB":
			String tableName = getMessage();
			int numberOfClusters = getNumber();
			String fileName = getMessage();
			out.writeObject("OK");
			learningFromDB(numberOfClusters, tableName, fileName);
			break;
		case "FILE":
			fileName = getMessage();
			out.writeObject("OK");
			learningFromFile(fileName);
			break;
		}

		out.writeObject("END");
	}

	/**
	 * Esegue il processo di clustering da database.
	 * 
	 * @param numberOfClusters
	 *            numero di cluster cercati
	 * @param tableName
	 *            nome della tabella in cui cercare i cluster
	 * @param fileName
	 *            nome del file in cui salvare i cluster
	 * @throws IOException
	 *             {@link ObjectOutputStream#writeObject(Object)}
	 */
	private void learningFromDB(int numberOfClusters, String tableName, String fileName) throws IOException {
		kmeans = new KMeansMiner(numberOfClusters);
		Data data = new Data(tableName);

		try {
			int numIter = kmeans.kmeans(data);
			out.writeObject("Numero di iterazioni: " + numIter + "\n");
			out.writeObject(kmeans.getC().toString(data));
			out.writeObject("Salvataggio in " + fileName);
			try {
				kmeans.save(fileName);
			} catch (FileNotFoundException e) {
				System.out.println(e.getMessage());
				out.writeObject(e.getMessage());
			} catch (IOException e) {
				System.out.println(e.getMessage());
				out.writeObject(e.getMessage());
			}
		} catch (OutOfRangeSampleSizeException e) {
			System.out.println(e.getMessage());
			out.writeObject(e.getMessage());
		}
	}

	/**
	 * Carica i cluster da file.
	 * 
	 * @param fileName
	 *            nome del file da cui caricare i cluster
	 * @throws IOException
	 *             {@link ObjectOutputStream#writeObject(Object)}
	 */
	private void learningFromFile(String fileName) throws IOException {
		try {
			kmeans = new KMeansMiner(fileName);
			out.writeObject(kmeans.getC().toString());
		} catch (FileNotFoundException e) {
			System.out.println(e.getMessage());
			out.writeObject(e.getMessage());
		} catch (IOException e) {
			System.out.println(e.getMessage());
			out.writeObject(e.getMessage());
		} catch (ClassNotFoundException e) {
			System.out.println(e.getMessage());
			out.writeObject(e.getMessage());
		}
	}

}
