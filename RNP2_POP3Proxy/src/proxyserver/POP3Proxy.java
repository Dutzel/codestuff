package proxyserver;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * 
 * @author Fabian Reiber, Francis Opoku Die Klasse erzeugt einen Haupt-Thread,
 *         der auf einem Socket auf ankommende Clients wartet und für diese dann
 *         einen ProxyServer erzeugt, der die Anfragen der Clients bearbeitet.
 * 
 */
public class POP3Proxy extends Thread {

	public ServerSocket socket;
	private ProxyClient proxy_cl;
	private static int port;
	private static int maxClients;
	private static int countClients;
	private static Scanner adminReader;
	public boolean serverIsAlive = true;
	private static List<ProxyServer> activeMailClients;

	/**
	 * 
	 * @param max
	 *            Maximale Anzahl an Clients die parallel mit dem Server
	 *            arbeiten dürfen.
	 */
	public POP3Proxy(int max) {
		maxClients = max;
		countClients = 0;
		activeMailClients = new ArrayList<ProxyServer>();
	}

	/**
	 * Aufforderung zur Angabe des Listenig-Ports für den MainServer
	 */
	private void configure() {
		System.out.println("Enter the port: ");
		adminReader = new Scanner(System.in, StandardCharsets.UTF_8.name());
		if (adminReader.hasNextInt()) {
			port = Integer.parseInt(adminReader.nextLine());
			this.proxy_cl = new ProxyClient();
			this.proxy_cl.start();
		} else {
			System.out.println("Bitte geben sie einen gültigen Port (int Wert) ein.");
			configure();
		}
	}

	/**
	 * Warten auf Socket auf ankommende Clients. Dann Socket an RequestHandler
	 * übergeben damit dieser Mit dem Client kommunizieren kann. Abschließend
	 * RequestHandler-Thread starten.
	 */
	private void waitForMailClient() {
		try {
			socket = new ServerSocket(port);
			while (serverIsAlive) {
				if (countClients <= maxClients) {
					Socket connection = socket.accept();
					
					ProxyServer proxy_srv = new ProxyServer(connection);
					storeClientConnections(proxy_srv);
					proxy_srv.start();
			
				}
			}
		} catch (IOException e) {
			if (!serverIsAlive) {
				System.out.println("OK_BYE");
			} else {
				System.out
						.println("IOException in MainServer.waitForRequests()");
			}
		}
	}

	/**
	 * Erst Server konfigurieren (Listening-Port von Server-Admin erfragen),
	 * dann auf ankommende Clients warten.
	 */
	public void run() {
		configure();
		waitForMailClient();
	}

	/**
	 * Aktive ProxyServer in Liste verwalten um Referenzen auf diese zu
	 * halten, damit auch eine Kommunikation zu diesen gewährleistet ist.
	 * 
	 * @param proxy_srv
	 *            Der ProxyServer der mit einem Client kommuniziert.
	 * @param proxy_cl
	 * 			  Der ProxyClient der mit dem POP3-MailServer kommuniziert
	 */
	private void storeClientConnections(ProxyServer proxy_srv) {
		activeMailClients.add(proxy_srv);
	}

	/**
	 * Wird von einer RequestHandler-Instanz in run() aufgerufen bei BYE- oder
	 * SHUTDOWN-Befehl. Dann wird der Socket auf dem der RequestHandler mit dem
	 * Client kommuniziert geschlossen und der RequestHandler terminiert
	 * (erreicht ende der run-Methode), weshalb er sich aus der Liste der
	 * verwalteten RequestHandler "austragen" muss.
	 * 
	 * @param r
	 */
	public static void deleteMe(ProxyServer r) {
		activeMailClients.remove(r);
		countClients = countClients - 1;

	}

}
