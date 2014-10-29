package proxyserver;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

/**
 * Klasse zur Kommunikation mit Mailclients. Bearbeitet die Anfragen der Clients.
 * 
 * @author Fabian Reiber, Francis Opoku
 * 
 */
public class ProxyServer extends Thread {

	private Socket connection;
	/**
	 * 
	 * @param c
	 *            Der Socket auf dem der MainServer einen Client akzeptiert hat.
	 */
	public ProxyServer(Socket c) {
		this.connection = c;
		
		
		//erkennung des korrekten accounts, da user und pass ggf. identisch fuer unterschiedliche server sein kann
		//->hashcode?
		
		//wenn mails von abholaccoutn geholt werden, abfragen ob != null
		
		
	}

	/**
	 * Die Kommunikationsreihenfolge ist folgende: Solange der Socket auf dem
	 * der RequestHandler mit dem Client kommuniziert nicht geschlossen ist 1.
	 * Wartet er auf dem Socket auf eine ankommende Zeile von maximal 256
	 * Zeichen. 2. Übergibt diese der Methode handleClientRequest die die
	 * Eingabezeichenkette analysiert. 3. Löscht sich aus der Liste der von
	 * MainServer verwalteten RequestHandler.
	 */
	public void run() {
		while (!connection.isClosed()) {
			try {

				Scanner readFromClient = new Scanner(
						connection.getInputStream(),
						StandardCharsets.UTF_8.name());
				PrintWriter writeToClient = new PrintWriter(
						new OutputStreamWriter(connection.getOutputStream(),
								StandardCharsets.UTF_8), true);

				String clientRequest = readFromClient.findInLine(".{0,254}");
				handleClientRequest(clientRequest, writeToClient);
				// readFromClient.close();
			} catch (Exception e) {
				try {
					connection.close();
				} catch (IOException e1) {
					System.out.println("RequestHandler: connection not closed");
				}
			}

		}

		POP3Proxy.deleteMe(this);

		try {
			connection.close();
		} catch (IOException e) {
			System.out
					.println("Fehler: RequestHandler.run(): connection.close");
			e.printStackTrace();
		}
	}

	/**
	 * Der Befehl wird von der Eingabezeichenfolge des Clients separiert. Er
	 * wird Analysiert und entsprechend seiner Gültigkeit dann auf der
	 * restlichen Zeichenkette durch einen entsprechenden Methodenaufruf
	 * ausgeführt. Falls es kein gültiger Befehl ist, wird der Client darüber
	 * informiert.
	 * 
	 * @param command
	 *            Eingabezeichenfolge des Clients.
	 * @param writeToClient
	 *            Der OutputStream des Sockets als PrintWriter um das Ergebnis
	 *            zum Cliet zu schreiben.
	 */
	private void handleClientRequest(String command,
			PrintWriter writeToClient) {
		
	}

	/**
	 * Wenn MailClient die Verbindung mit "QUIT" schließen will, wird Thread geschlossen
	 */
	private void doQuit() {
		//close connection
	}

}
