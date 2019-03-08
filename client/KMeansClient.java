package client;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.FlowLayout;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;

/**
 * Classe che modella la GUI dell'applicazione.
 * 
 * @author Gianluca Pagliara
 *
 */
public class KMeansClient {

	private Socket socket;
	private ObjectOutputStream output;
	private ObjectInputStream input;
	private JFrame window;

	/**
	 * Estensione della classe {@link JTabbedPane} che implementa il tabbed pane da
	 * visualizzare nell'applicazione.
	 * 
	 * @author Gianluca Pagliara
	 *
	 */
	private class TabbedPane extends JTabbedPane {
		private JPanelClusterDB panelDB;
		private JPanelClusterFile panelFile;

		/**
		 * Estensione della classe {@link JPanel} che implementa il pannello per la
		 * richiesta di un nuovo processo di clustering da database.
		 * 
		 * @author Gianluca Pagliara
		 *
		 */
		private class JPanelClusterDB extends JPanel {
			private JTextField tableText;
			private JTextField kText;
			private JTextField fileText;
			private JTextArea clusterOutput;
			private JButton executeButton;

			/**
			 * Inizializza gli elementi della GUI e li inserisce nei contenitori corretti.
			 * 
			 * @param btnListener
			 *            Listener da associare al bottone
			 */
			JPanelClusterDB(MouseListener btnListener) {
				tableText = new JTextField(20);
				kText = new JTextField(10);
				fileText = new JTextField(20);
				clusterOutput = new JTextArea();
				JScrollPane scrollingArea = new JScrollPane(clusterOutput);
				executeButton = new JButton("Mine");
				executeButton.addMouseListener(btnListener);

				JPanel upPanel = new JPanel(new FlowLayout());
				JLabel tableLable = new JLabel("Table");
				upPanel.add(tableLable);
				upPanel.add(tableText);
				JLabel ncLable = new JLabel("Number of clusters");
				upPanel.add(ncLable);
				upPanel.add(kText);
				JLabel fileLable = new JLabel("File");
				upPanel.add(fileLable);
				upPanel.add(fileText);

				JPanel centralPanel = new JPanel(new BorderLayout());
				centralPanel.add(scrollingArea);

				JPanel downPanel = new JPanel();
				downPanel.add(executeButton);

				setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				add(upPanel);
				add(centralPanel);
				add(downPanel);
			}
		}

		/**
		 * Estensione della classe {@link JPanel} che implementa il pannello per la
		 * richiesta di un caricamento dei cluster da file.
		 * 
		 * @author Gianluca Pagliara
		 *
		 */
		private class JPanelClusterFile extends JPanel {
			private JTextField fileText;
			private JTextArea clusterOutput;
			private JButton executeButton;

			/**
			 * Inizializza gli elementi della GUI e li inserisce nei contenitori corretti.
			 * 
			 * @param btnName
			 *            Stringa da visualizzare nel bottone
			 * @param btnListener
			 *            Listener da associare al bottone
			 */
			JPanelClusterFile(MouseListener btnListener) {
				fileText = new JTextField(20);
				clusterOutput = new JTextArea();
				JScrollPane scrollingArea = new JScrollPane(clusterOutput);
				executeButton = new JButton("Load");
				executeButton.addMouseListener(btnListener);

				JPanel upPanel = new JPanel(new FlowLayout());
				JLabel fileLable = new JLabel("File");
				upPanel.add(fileLable);
				upPanel.add(fileText);

				JPanel centralPanel = new JPanel(new BorderLayout());
				centralPanel.add(scrollingArea);

				JPanel downPanel = new JPanel();
				downPanel.add(executeButton);

				setLayout(new BoxLayout(this, BoxLayout.Y_AXIS));
				add(upPanel);
				add(centralPanel);
				add(downPanel);
			}
		}

		/**
		 * Inizializza i membri e li aggiunge all'istanza della classe.
		 */
		TabbedPane() {
			panelDB = new JPanelClusterDB(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					try {
						learningFromDBAction();
					} catch (ClassNotFoundException | IOException | ServerException ex) {
						printExceptionError(ex);
						resetConnection();
					}
				}
			});
			panelFile = new JPanelClusterFile(new MouseAdapter() {
				public void mouseClicked(MouseEvent e) {
					try {
						learningFromFileAction();
					} catch (ClassNotFoundException | IOException | ServerException ex) {
						printExceptionError(ex);
						resetConnection();
					}
				}
			});
			addTab("DB", panelDB);
			addTab("File", panelFile);
		}

		/**
		 * Esegue le azioni necessarie per effettuare il clustering caricando i dati dal
		 * database. Richiede all'utente l'inserimento del numero di cluster da cercare,
		 * la tabella da cui caricare dati e il file in cui salvare i cluster trovati.
		 * Invia questi dati al server e stampa a video i dati ricevuti dal server. Se
		 * si verificano degli errori durante la comunicazione con il server, vengono
		 * notificati all'utente tramite {@link JTabbedPane}.
		 * 
		 * @throws IOException
		 * @throws ClassNotFoundException
		 * @throws ServerException
		 *             se riceve un messaggio di errore dal server
		 * 
		 */
		private void learningFromDBAction() throws IOException, ClassNotFoundException, ServerException {
			if(!isConnected())
				throw new ServerException();
			
			// Acquisizione dei dati necessari
			int numberOfCluster = 0;
			try {
				numberOfCluster = new Integer(panelDB.kText.getText()).intValue();
			} catch (NumberFormatException e) {
				JOptionPane.showMessageDialog(this, e.toString());
				return;
			}
			String tableName = panelDB.tableText.getText();
			String fileName = panelDB.fileText.getText();

			// Comunicazione con il server
			output.writeObject("DB");
			output.writeObject(tableName);
			output.writeObject(numberOfCluster);
			output.writeObject(fileName);

			if (waitOK() == false) {
				throw new ServerException();
			}

			String message = getMessage(); // Numero iterazioni
			panelDB.clusterOutput.append(message);
			message = getMessage(); // Cluster
			panelDB.clusterOutput.append(message);
			message = getMessage(); // Salvataggio
			panelDB.clusterOutput.append(message);
			panelDB.clusterOutput.append("\n\n");

			JOptionPane.showMessageDialog(this, "Completed.");
		}

		/**
		 * Esegue le azioni necessarie per caricare i dati da file. Richiede all'utente
		 * l'inserimento del numero di cluster da cercare e il file da cui caricare i
		 * cluster. Invia questi dati al server e stampa a video i dati ricevuti dal
		 * server. Se si verificano degli errori durante la comunicazione con il server,
		 * vengono notificati all'utente tramite {@link JTabbedPane}.
		 * 
		 * @throws IOException
		 * @throws ClassNotFoundException
		 * @throws ServerException
		 *             se riceve un messaggio di errore dal server
		 * 
		 */
		private void learningFromFileAction() throws IOException, ClassNotFoundException, ServerException {
			if(!isConnected())
				throw new ServerException();
			
			// Acquisizione dei dati necessari
			String fileName = panelFile.fileText.getText();

			// Comunicazione con il server
			output.writeObject("FILE");
			output.writeObject(fileName);

			if (waitOK() == false) {
				throw new ServerException();
			}

			String message = getMessage(); // Cluster
			panelFile.clusterOutput.append(message);
			panelFile.clusterOutput.append("\n\n");

			JOptionPane.showMessageDialog(this, "Completed.");
		}

		/**
		 * Attende la ricezione di un messaggio dal server.
		 * 
		 * @return messaggio ricevuto dal server
		 * 
		 * @throws ClassNotFoundException
		 * @throws IOException
		 */
		private String getMessage() throws ClassNotFoundException, IOException {
			String string;
			while (true) {
				string = (String) input.readObject();
				if (string != null)
					break;
			}
			return string;
		}

		/**
		 * Attende la ricezione del messaggio "OK" dal server.
		 * 
		 * @return vero se il messaggio ricevuto dal server è "OK", falso altrimenti
		 * 
		 * @throws ClassNotFoundException
		 * @throws IOException
		 */
		private boolean waitOK() throws ClassNotFoundException, IOException {
			String reply = getMessage();
			if (!reply.equals("OK"))
				return false;
			else
				return true;
		}
	}

	/**
	 * Inizializza la componente grafica dell'applicazione. Avvia la richiesta di
	 * connessione al server e inizializza i flussi di comunicazione.
	 */
	public void init() {
		window = new JFrame("Client");
		Container container = window.getContentPane();
		TabbedPane tab = new TabbedPane();
		JButton connectButton = new JButton("Connect");
		connectButton.addMouseListener(new MouseAdapter() {
			public void mouseClicked(MouseEvent e) {
				if (!isConnected())
					initConnection();
				else
					resetConnection();
			}
		});
		container.add(connectButton, BorderLayout.PAGE_START);
		container.add(tab);

		window.setSize(800, 500);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setVisible(true);
		window.addWindowListener(new WindowAdapter() {
			public void windowClosing(WindowEvent e) {
				int i = JOptionPane.showConfirmDialog(null, "Are you sure you want to quit?");
				if (i == 0) {
					closeConnection();
					System.exit(0); // chiusura applicazione
				}
			}
		});

		initConnection();
	}

	/**
	 * Restituisce vero se il client è connesso al server, falso altrimenti.
	 * 
	 * @return true se connesso, false altrimenti
	 */
	private boolean isConnected() {
		return socket != null ? socket.isConnected() && !socket.isClosed() : false;
	}

	/**
	 * Effettua la connessione al server, se non si è già connessi.
	 */
	private void initConnection() {
		if (!isConnected()) {
			// Connessione al server
			InetAddress addr = null;
			socket = null;

			try {
				addr = InetAddress.getByName("127.0.0.1");
				socket = new Socket(addr, 8080);
			} catch (IOException e) {
				printExceptionError(e);
			}

			initStream();

			if (isConnected()) {
				JOptionPane.showMessageDialog(window, "Connected.");
			}
		} else {
			resetConnection();
		}

	}

	/**
	 * Inzializza gli stream per la comunicazione con il server.
	 */
	private void initStream() {
		if (isConnected()) {
			try {
				closeStream();
				output = new ObjectOutputStream(socket.getOutputStream());
				input = new ObjectInputStream(socket.getInputStream());
			} catch (IOException e) {
				printExceptionError(e);
			}
		}
	}

	/**
	 * Effettua una nuova connessione, chiudendo quella precedente.
	 */
	private void resetConnection() {
		closeConnection();
		initConnection();
	}

	/**
	 * Chiude la connessione.
	 */
	private void closeConnection() {
		if (isConnected()) {
			closeStream();
			try {
				socket.close();
			} catch (IOException e) {
				printExceptionError(e);
			}
		}
	}

	/**
	 * Chiude gli stream.
	 */
	private void closeStream() {
		try {
			if (output != null) {
				output.close();
			}
		} catch (IOException e) {
			printExceptionError(e);
		}
		try {
			if (input != null) {
				input.close();
			}
		} catch (IOException e) {
			printExceptionError(e);
		}
	}

	/**
	 * Visualizza un {@link JOptionPane} con il messaggio di errore dell'eccezione
	 * e.
	 * 
	 * @param e
	 *            eccezione della quale stampare l'errore
	 */
	private void printExceptionError(Exception e) {
		JOptionPane.showMessageDialog(window, "Error: " + e.getMessage());
	}

	public static void main(String[] args) {
		KMeansClient k = new KMeansClient();
		k.init();
	}
}
