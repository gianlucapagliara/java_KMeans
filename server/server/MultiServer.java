package server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Questa classe implementa il server per l'applicazione. Si occupa di accettare
 * le richieste e creare i thread per la loro esecuzione.
 * 
 * @author Gianluca Pagliara
 *
 */
public class MultiServer {
	/**
	 * Porta su cui il server accetterà le connessioni.
	 */
	private int port;

	/**
	 * Istanzia un oggetto di tipo {@link MultiServer} e avvia il server.
	 * 
	 * @param args
	 *            argomenti passati nel momento dell'avvio del programma
	 */
	public static void main(String[] args) {
		MultiServer ms = new MultiServer();
		ms.run();
	}

	/**
	 * Crea un server con porta predefinita 8080.
	 */
	public MultiServer() {
		port = 8080;
	}

	/**
	 * Crea un server in ascolto su una specifica porta.
	 * 
	 * @param port
	 *            numero della porta
	 */
	public MultiServer(int port) {
		this.port = port;
	}

	/**
	 * Crea il socket e si mette in ascolto per richieste di connessione da parte
	 * dei client. Crea un {@link ServerOneClient} per ogni richiesta accettata.
	 * Gestisce gli errori di connessione con stampa dei relativi messaggi di
	 * errore.
	 */
	private void run() {
		ServerSocket serverSocket = null;
		try {
			serverSocket = new ServerSocket(port);
			while (true) {
				System.out.println("In attesa di connessione.");
				Socket socket = serverSocket.accept();
				try {
					new ServerOneClient(socket);
					System.out.println("Connessione accettata.");
				} catch (IOException e) {
					System.out.println("IOException: " + e.getMessage() + "\nClosing socket.");
					socket.close();
				}
			}
		} catch (IOException e) {
			System.out.println("Socket error: " + e.getMessage());
		} finally {
			try {
				serverSocket.close();
			} catch (IOException e) {
				System.out.println("Socket closing error: " + e.getMessage());
			}
		}
	}
}
